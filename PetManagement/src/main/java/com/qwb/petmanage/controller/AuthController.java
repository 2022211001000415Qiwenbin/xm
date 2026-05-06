
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Admin;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.service.AdminService;
import com.qwb.petmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * 只通过用户名和密码区分管理员和用户，不再需要role参数
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 先尝试管理员登录
        QueryWrapper<Admin> adminQuery = new QueryWrapper<>();
        adminQuery.eq("admin_name", username)
                  .eq("password", md5Password);
        Admin admin = adminService.getOne(adminQuery);

        if (admin != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "admin-token-" + admin.getAdminName());
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
            Map<String, Object> data = new HashMap<>();
            data.put("token", "user-token-" + user.getUsername());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("role", "user");
            data.put("userId", user.getUserId());
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
            // 只允许普通用户注册
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
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 这里应该进行真实的登出逻辑，如清除token等
        return Result.success();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> data = new HashMap<>();

        // 根据token判断用户类型
        if (token != null && token.startsWith("Bearer admin-token-")) {
            // 管理员信息
            String adminName = token.substring("Bearer admin-token-".length());
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_name", adminName);
            Admin admin = adminService.getOne(queryWrapper);

            if (admin != null) {
                data.put("username", admin.getAdminName());
                data.put("realName", admin.getRealName());
                data.put("role", "admin");
                data.put("adminId", admin.getAdminId());
                data.put("phone", admin.getPhone());
                data.put("avatar", "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
            }
        } else if (token != null && token.startsWith("Bearer user-token-")) {
            // 普通用户信息
            String username = token.substring("Bearer user-token-".length());
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User user = userService.getOne(queryWrapper);

            if (user != null) {
                data.put("username", user.getUsername());
                data.put("realName", user.getRealName());
                data.put("role", "user");
                data.put("userId", user.getUserId());
                data.put("idCard", user.getIdCard());
                data.put("phone", user.getPhone());
                data.put("address", user.getAddress());
                data.put("petExperience", user.getPetExperience());
                data.put("avatar", "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
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
