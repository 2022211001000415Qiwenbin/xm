
package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.entity.PetReserve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetReserveMapper extends BaseMapper<PetReserve> {

    List<PetReserve> selectReservePage(Page<PetReserve> page,
                                       @Param("reserveStatus") String reserveStatus,
                                       @Param("userId") Long userId,
                                       @Param("petId") Long petId,
                                       @Param("realName") String realName);
}
