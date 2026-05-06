package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.AbandonedPet;

/**
 * 弃养宠物Service接口
 */
public interface AbandonedPetService extends IService<AbandonedPet> {

    /**
     * 分页查询弃养宠物列表
     */
    Page<AbandonedPet> pageList(Integer current, Integer size, String keyword, String status);

    /**
     * 添加弃养宠物
     */
    boolean addAbandonedPet(AbandonedPet abandonedPet);

    /**
     * 更新弃养宠物信息
     */
    boolean updateAbandonedPet(AbandonedPet abandonedPet);

    /**
     * 删除弃养宠物
     */
    boolean deleteAbandonedPet(Long id);

    /**
     * 审核弃养宠物
     */
    boolean reviewAbandonedPet(Long id, String status, String reviewer);
}
