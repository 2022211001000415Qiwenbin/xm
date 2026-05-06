
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
}
