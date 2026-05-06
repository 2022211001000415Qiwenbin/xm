
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Admin;
import com.qwb.petmanage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 分页查询管理员列表
     */
    @GetMapping("/page")
    public Result<Page<Admin>> page(@RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String adminName) {
        Page<Admin> page = new Page<>(current, size);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (adminName != null && !adminName.isEmpty()) {
            queryWrapper.like("admin_name", adminName);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(adminService.page(page, queryWrapper));
    }

    /**
     * 根据ID查询管理员
     */
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        return Result.success(adminService.getById(id));
    }

    /**
     * 新增管理员
     */
    @PostMapping
    public Result<Void> add(@RequestBody Admin admin) {
        // 检查账号是否已存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin_name", admin.getAdminName());
        if (adminService.count(queryWrapper) > 0) {
            return Result.error("账号已存在");
        }
        // 检查手机号是否已存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", admin.getPhone());
        if (adminService.count(queryWrapper) > 0) {
            return Result.error("手机号已注册");
        }
        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
        admin.setPassword(md5Password);
        adminService.save(admin);
        return Result.success();
    }

    /**
     * 修改管理员
     */
    @PutMapping
    public Result<Void> update(@RequestBody Admin admin) {
        // 如果修改了密码，则进行MD5加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String md5Password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
            admin.setPassword(md5Password);
        }
        adminService.updateById(admin);
        return Result.success();
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminService.removeById(id);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        Long adminId = Long.parseLong(params.get("adminId"));
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        Admin admin = adminService.getById(adminId);
        if (admin == null) {
            return Result.error("管理员不存在");
        }

        // 验证旧密码
        String md5OldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!md5OldPassword.equals(admin.getPassword())) {
            return Result.error("原密码错误");
        }

        // 更新新密码
        String md5NewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        admin.setPassword(md5NewPassword);
        adminService.updateById(admin);
        return Result.success();
    }
}
