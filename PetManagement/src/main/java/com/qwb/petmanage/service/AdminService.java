
package com.qwb.petmanage.service;

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
}
