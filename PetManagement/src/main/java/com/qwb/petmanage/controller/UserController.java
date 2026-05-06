
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户（领养人）控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") Integer current,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(required = false) String realName,
                                    @RequestParam(required = false) String phone) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (realName != null && !realName.isEmpty()) {
            queryWrapper.like("real_name", realName);
        }
        if (phone != null && !phone.isEmpty()) {
            queryWrapper.like("phone", phone);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(userService.page(page, queryWrapper));
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        // 检查账号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (userService.count(queryWrapper) > 0) {
            return Result.error("账号已存在");
        }
        // 检查身份证号是否已存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card", user.getIdCard());
        if (userService.count(queryWrapper) > 0) {
            return Result.error("身份证号已注册");
        }
        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Password);
        userService.save(user);
        return Result.success();
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        // 如果修改了密码，则进行MD5加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(md5Password);
        }
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody java.util.Map<String, String> params) {
        Long userId = Long.parseLong(params.get("userId"));
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        String md5OldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!md5OldPassword.equals(user.getPassword())) {
            return Result.error("原密码错误");
        }

        // 更新新密码
        String md5NewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        user.setPassword(md5NewPassword);
        userService.updateById(user);
        return Result.success();
    }
}
