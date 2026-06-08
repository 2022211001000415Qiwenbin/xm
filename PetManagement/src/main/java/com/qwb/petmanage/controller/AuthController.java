
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.qwb.petmanage.common.JwtUtil;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Admin;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.service.AdminService;
import com.qwb.petmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 基于JWT + Caffeine缓存实现Token管理、单点登录、Token刷新
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Cache<String, Object> tokenCache;

    /**
     * 用户登录
     * 登录成功后签发JWT Token，并存入缓存实现单点登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error("用户名和密码不能为空");
        }

        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 先尝试管理员登录
        QueryWrapper<Admin> adminQuery = new QueryWrapper<>();
        adminQuery.eq("admin_name", username)
                  .eq("password", md5Password);
        Admin admin = adminService.getOne(adminQuery);

        if (admin != null) {
            // 签发JWT Token
            String accessToken = jwtUtil.generateAccessToken(Long.valueOf(admin.getAdminId()), admin.getAdminName(), "admin");
            String refreshToken = jwtUtil.generateRefreshToken(Long.valueOf(admin.getAdminId()), admin.getAdminName(), "admin");

            // 存入缓存，实现单点登录（同一账号只保留一个有效Token）
            String tokenKey = "auth:token:admin:" + admin.getAdminId();
            tokenCache.put(tokenKey, accessToken);

            // 存储RefreshToken
            String refreshKey = "auth:refresh:admin:" + admin.getAdminId();
            tokenCache.put(refreshKey, refreshToken);

            Map<String, Object> data = new HashMap<>();
            data.put("token", accessToken);
            data.put("refreshToken", refreshToken);
            data.put("username", admin.getAdminName());
            data.put("realName", admin.getRealName());
            data.put("role", "admin");
            data.put("adminId", admin.getAdminId());
            return Result.success(data);
        }

        // 尝试普通用户登录
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.eq("username", username)
                 .eq("password", md5Password);
        User user = userService.getOne(userQuery);

        if (user != null) {
            // 签发JWT Token
            String accessToken = jwtUtil.generateAccessToken(Long.valueOf(user.getUserId()), user.getUsername(), "user");
            String refreshToken = jwtUtil.generateRefreshToken(Long.valueOf(user.getUserId()), user.getUsername(), "user");

            // 存入缓存，实现单点登录
            String tokenKey = "auth:token:user:" + user.getUserId();
            tokenCache.put(tokenKey, accessToken);

            // 存储RefreshToken
            String refreshKey = "auth:refresh:user:" + user.getUserId();
            tokenCache.put(refreshKey, refreshToken);

            Map<String, Object> data = new HashMap<>();
            data.put("token", accessToken);
            data.put("refreshToken", refreshToken);
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("role", "user");
            data.put("userId", user.getUserId());
            data.put("avatar", user.getAvatar());
            return Result.success(data);
        }

        return Result.error("用户名或密码错误");
    }

    /**
     * 用户注册
     * 只允许注册普通用户，不能注册为管理员
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> registerData) {
        try {
            User user = new User();
            user.setUsername(registerData.get("username"));
            user.setPassword(registerData.get("password"));
            user.setRealName(registerData.get("realName"));
            user.setIdCard(registerData.get("idCard"));
            user.setPhone(registerData.get("phone"));
            user.setAddress(registerData.get("address"));
            user.setPetExperience(registerData.get("petExperience"));

            userService.register(user);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 刷新AccessToken
     * 使用RefreshToken换取新的AccessToken和RefreshToken
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestBody Map<String, String> refreshData) {
        String refreshToken = refreshData.get("refreshToken");

        if (!StringUtils.hasText(refreshToken)) {
            return Result.error("RefreshToken不能为空");
        }

        // 验证RefreshToken
        if (!jwtUtil.validateToken(refreshToken)) {
            return Result.error("RefreshToken无效或已过期，请重新登录");
        }

        // 检查Token类型必须是refresh
        String tokenType = jwtUtil.getTypeFromToken(refreshToken);
        if (!"refresh".equals(tokenType)) {
            return Result.error("Token类型错误，请使用RefreshToken");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        if (userId == null || role == null) {
            return Result.error("RefreshToken信息不完整");
        }

        // 验证缓存中的RefreshToken是否一致
        String refreshKey = "auth:refresh:" + role + ":" + userId;
        Object storedRefreshToken = tokenCache.getIfPresent(refreshKey);
        if (storedRefreshToken == null || !refreshToken.equals(storedRefreshToken.toString())) {
            return Result.error("RefreshToken已失效，请重新登录");
        }

        // 签发新的Token对
        String newAccessToken = jwtUtil.generateAccessToken(userId, username, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username, role);

        // 更新缓存中的Token
        String tokenKey = "auth:token:" + role + ":" + userId;
        tokenCache.put(tokenKey, newAccessToken);
        tokenCache.put(refreshKey, newRefreshToken);

        Map<String, Object> data = new HashMap<>();
        data.put("token", newAccessToken);
        data.put("refreshToken", newRefreshToken);
        return Result.success(data);
    }

    /**
     * 用户登出
     * 清除缓存中的Token，使当前Token失效
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if (userId != null && StringUtils.hasText(role)) {
            // 清除缓存中的AccessToken和RefreshToken
            String tokenKey = "auth:token:" + role + ":" + userId;
            String refreshKey = "auth:refresh:" + role + ":" + userId;
            tokenCache.invalidate(tokenKey);
            tokenCache.invalidate(refreshKey);
        }

        return Result.success();
    }

    /**
     * 获取用户信息
     * 从JWT Token中解析用户信息，不再依赖Token字符串格式
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if (userId == null || !StringUtils.hasText(role)) {
            return Result.error("未获取到用户信息");
        }

        Map<String, Object> data = new HashMap<>();

        if ("admin".equals(role)) {
            Admin admin = adminService.getById(userId);
            if (admin != null) {
                data.put("username", admin.getAdminName());
                data.put("realName", admin.getRealName());
                data.put("role", "admin");
                data.put("adminId", admin.getAdminId());
                data.put("phone", admin.getPhone());
                data.put("avatar", "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
            }
        } else if ("user".equals(role)) {
            User user = userService.getById(userId);
            if (user != null) {
                data.put("username", user.getUsername());
                data.put("realName", user.getRealName());
                data.put("role", "user");
                data.put("userId", user.getUserId());
                data.put("idCard", user.getIdCard());
                data.put("phone", user.getPhone());
                data.put("address", user.getAddress());
                data.put("petExperience", user.getPetExperience());
                data.put("avatar", user.getAvatar() != null ? user.getAvatar() : "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
            }
        }

        return Result.success(data);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody Map<String, String> params) {
        String role = params.get("role");

        if ("user".equals(role)) {
            Long userId = Long.parseLong(params.get("userId"));
            User user = userService.getById(userId);

            if (user == null) {
                return Result.error("用户不存在");
            }

            user.setRealName(params.get("realName"));
            user.setPhone(params.get("phone"));
            user.setIdCard(params.get("idCard"));
            user.setAddress(params.get("address"));
            user.setPetExperience(params.get("petExperience"));
            user.setAvatar(params.get("avatar"));

            userService.updateById(user);
            return Result.success();
        }

        return Result.error("暂不支持管理员信息修改");
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("后端接口正常工作");
    }
}
