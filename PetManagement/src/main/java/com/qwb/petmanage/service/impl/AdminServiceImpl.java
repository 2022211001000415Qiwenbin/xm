
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.Admin;
import com.qwb.petmanage.mapper.AdminMapper;
import com.qwb.petmanage.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public boolean register(Admin admin) {
        // 检查管理员账号是否已存在
        if (getByAdminName(admin.getAdminName()) != null) {
            throw new RuntimeException("管理员账号已存在");
        }
        
        // 检查手机号是否已存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", admin.getPhone());
        if (count(queryWrapper) > 0) {
            throw new RuntimeException("手机号已注册");
        }
        
        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
        admin.setPassword(md5Password);
        admin.setCreateTime(LocalDateTime.now());
        
        return save(admin);
    }

    @Override
    public Admin getByAdminName(String adminName) {
        if (!StringUtils.hasText(adminName)) {
            return null;
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin_name", adminName);
        return getOne(queryWrapper);
    }

    @Override
    public Page<Admin> pageList(Integer current, Integer size, String adminName) {
        Page<Admin> page = new Page<>(current, size);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (adminName != null && !adminName.isEmpty()) {
            queryWrapper.like("admin_name", adminName);
        }
        queryWrapper.orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public void addAdmin(Admin admin) {
        // 检查账号是否已存在
        if (getByAdminName(admin.getAdminName()) != null) {
            throw new RuntimeException("账号已存在");
        }
        // 检查手机号是否已存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", admin.getPhone());
        if (count(queryWrapper) > 0) {
            throw new RuntimeException("手机号已注册");
        }
        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
        admin.setPassword(md5Password);
        admin.setCreateTime(LocalDateTime.now());
        save(admin);
    }

    @Override
    public void updateAdmin(Admin admin) {
        // 如果修改了密码，则进行MD5加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String md5Password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
            admin.setPassword(md5Password);
        }
        updateById(admin);
    }

    @Override
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        // 验证旧密码
        String md5OldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!md5OldPassword.equals(admin.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        // 更新新密码
        String md5NewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        admin.setPassword(md5NewPassword);
        updateById(admin);
    }
}
