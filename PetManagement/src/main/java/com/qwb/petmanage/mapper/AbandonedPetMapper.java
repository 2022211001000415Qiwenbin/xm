package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qwb.petmanage.entity.AbandonedPet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 弃养宠物Mapper接口
 */
@Mapper
public interface AbandonedPetMapper extends BaseMapper<AbandonedPet> {
}
