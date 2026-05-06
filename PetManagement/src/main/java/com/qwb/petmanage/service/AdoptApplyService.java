
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.AdoptApply;

public interface AdoptApplyService extends IService<AdoptApply> {
    /**
     * 分页查询领养申请列表
     * @param current 当前页
     * @param size 每页大小
     * @param auditStatus 审核状态
     * @param userId 用户ID
     * @param petId 宠物ID
     * @return 分页结果
     */
    Page<AdoptApply> pageList(Integer current, Integer size, String auditStatus, Long userId, Long petId);
    
    /**
     * 提交领养申请
     * @param adoptApply 领养申请信息
     * @return 提交成功返回true，否则返回false
     */
    boolean submitApply(AdoptApply adoptApply);
    
    /**
     * 审核领养申请
     * @param applyId 申请ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param auditAdminId 审核管理员ID
     * @return 审核成功返回true，否则返回false
     */
    boolean auditApply(Long applyId, String auditStatus, String auditRemark, Long auditAdminId);
    
    /**
     * 根据用户ID查询领养申请列表
     * @param userId 用户ID
     * @return 领养申请列表
     */
    java.util.List<AdoptApply> listByUserId(Long userId);
    
    /**
     * 根据宠物ID查询领养申请列表
     * @param petId 宠物ID
     * @return 领养申请列表
     */
    java.util.List<AdoptApply> listByPetId(Long petId);
}
