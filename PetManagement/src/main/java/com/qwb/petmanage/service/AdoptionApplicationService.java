package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.AdoptionApplication;

/**
 * 领养申请Service接口
 */
public interface AdoptionApplicationService extends IService<AdoptionApplication> {

    /**
     * 分页查询领养申请列表
     */
    Page<AdoptionApplication> pageList(Integer current, Integer size, String keyword, String status);

    /**
     * 提交领养申请
     */
    boolean submitApplication(AdoptionApplication application);

    /**
     * 更新领养申请信息
     */
    boolean updateApplication(AdoptionApplication application);

    /**
     * 删除领养申请
     */
    boolean deleteApplication(Long id);

    /**
     * 审核领养申请
     */
    boolean reviewApplication(Long id, String status, String reviewer, String rejectReason);

    /**
     * 完成领养
     */
    boolean completeAdoption(Long id);

    /**
     * 取消领养申请
     */
    boolean cancelApplication(Long id);

    /**
     * 添加回访记录
     */
    boolean addReturnVisit(Long id, String status, String notes);
}
