
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.PetBreed;

public interface PetBreedService extends IService<PetBreed> {

    /**
     * 分页查询品种列表
     * @param current 当前页
     * @param size 每页大小
     * @param breedName 品种名称
     * @param breedType 品种类型
     * @return 分页结果
     */
    Page<PetBreed> pageList(Integer current, Integer size, String breedName, String breedType);

    /**
     * 新增品种（含查重）
     * @param petBreed 品种信息
     * @return 新增后的品种ID
     */
    Integer addBreed(PetBreed petBreed);

    /**
     * 获取所有品种（按类型和名称排序）
     * @return 品种列表
     */
    java.util.List<PetBreed> listAll();
}
