
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        Page<User> page = userService.pageList(current, size, realName, phone);
        return Result.success(page);
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
        userService.addUser(user);
        return Result.success();
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 删除用户（同时删除关联的领养申请和预约记录）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 管理员重置用户密码
     * @param params 包含userId和newPassword
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody java.util.Map<String, String> params) {
        Long userId = Long.parseLong(params.get("userId"));
        String newPassword = params.get("newPassword");
        userService.resetPassword(userId, newPassword);
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
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
