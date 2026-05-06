
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.User;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功返回true，否则返回false
     */
    boolean register(User user);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User getByUsername(String username);
    
    /**
     * 根据身份证号查询用户
     * @param idCard 身份证号
     * @return 用户对象
     */
    User getByIdCard(String idCard);
}
