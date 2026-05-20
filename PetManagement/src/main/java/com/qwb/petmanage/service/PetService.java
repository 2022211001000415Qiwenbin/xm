
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.Pet;

public interface PetService extends IService<Pet> {

    /**
     * 分页查询宠物列表
     */
    Page<Pet> pageList(Integer current, Integer size, String petName, String adoptStatus, String breedType);

    /**
     * 获取宠物详情（含品种信息）
     */
    Pet getPetDetail(Integer petId);

    /**
     * 添加宠物
     */
    boolean addPet(Pet pet);

    /**
     * 更新宠物信息
     */
    boolean updatePet(Pet pet);

    /**
     * 删除宠物
     */
    boolean deletePet(Integer id);
    
    /**
     * 更新宠物领养状态
     * @param petId 宠物ID
     * @param adoptStatus 领养状态
     * @return 更新成功返回true，否则返回false
     */
    boolean updateAdoptStatus(Integer petId, String adoptStatus);
    
    /**
     * 根据品种ID查询宠物列表
     * @param breedId 品种ID
     * @return 宠物列表
     */
    java.util.List<Pet> listByBreedId(Integer breedId);
    
    /**
     * 根据领养状态查询宠物列表
     * @param adoptStatus 领养状态
     * @return 宠物列表
     */
    java.util.List<Pet> listByAdoptStatus(String adoptStatus);
}
