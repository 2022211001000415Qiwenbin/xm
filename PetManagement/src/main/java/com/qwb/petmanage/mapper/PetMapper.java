
package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qwb.petmanage.entity.Pet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}
