package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qwb.petmanage.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 领养申请Mapper接口
 */
@Mapper
public interface AdoptionApplicationMapper extends BaseMapper<AdoptionApplication> {
}
