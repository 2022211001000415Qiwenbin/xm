
package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {

    List<Pet> selectPetPage(Page<Pet> page, @Param("petName") String petName,
                            @Param("adoptStatus") String adoptStatus, @Param("breedType") String breedType);

    Pet selectPetDetail(@Param("petId") Integer petId);
}
