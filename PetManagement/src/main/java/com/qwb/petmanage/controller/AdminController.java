
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Admin;
import com.qwb.petmanage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
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
        Page<Admin> page = adminService.pageList(current, size, adminName);
        return Result.success(page);
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
        adminService.addAdmin(admin);
        return Result.success();
    }

    /**
     * 修改管理员
     */
    @PutMapping
    public Result<Void> update(@RequestBody Admin admin) {
        adminService.updateAdmin(admin);
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
        adminService.changePassword(adminId, oldPassword, newPassword);
        return Result.success();
    }
}
