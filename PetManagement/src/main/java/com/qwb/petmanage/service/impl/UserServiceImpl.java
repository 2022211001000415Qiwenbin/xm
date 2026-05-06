
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.mapper.UserMapper;
import com.qwb.petmanage.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查身份证号是否已存在
        if (getByIdCard(user.getIdCard()) != null) {
            throw new RuntimeException("身份证号已注册");
        }
        
        // 检查手机号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", user.getPhone());
        if (count(queryWrapper) > 0) {
            throw new RuntimeException("手机号已注册");
        }
        
        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Password);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        return save(user);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 检查用户是否存在
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        String md5OldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!md5OldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新新密码
        String md5NewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        user.setPassword(md5NewPassword);
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }

    @Override
    public User getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    @Override
    public User getByIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id_card", idCard);
        return getOne(queryWrapper);
    }
}
