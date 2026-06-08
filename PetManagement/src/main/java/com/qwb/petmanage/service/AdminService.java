
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.Admin;

public interface AdminService extends IService<Admin> {
    /**
     * 管理员注册
     * @param admin 管理员信息
     * @return 注册成功返回true，否则返回false
     */
    boolean register(Admin admin);
    
    /**
     * 根据管理员账号查询管理员
     * @param adminName 管理员账号
     * @return 管理员对象
     */
    Admin getByAdminName(String adminName);

    /**
     * 分页查询管理员列表
     * @param current 当前页
     * @param size 每页大小
     * @param adminName 管理员账号（模糊查询）
     * @return 分页结果
     */
    Page<Admin> pageList(Integer current, Integer size, String adminName);

    /**
     * 新增管理员（含查重和MD5加密）
     * @param admin 管理员信息
     */
    void addAdmin(Admin admin);

    /**
     * 修改管理员（含MD5加密）
     * @param admin 管理员信息
     */
    void updateAdmin(Admin admin);

    /**
     * 修改密码
     * @param adminId 管理员ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long adminId, String oldPassword, String newPassword);
}
