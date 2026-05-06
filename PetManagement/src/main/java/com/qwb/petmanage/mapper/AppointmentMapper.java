package com.qwb.petmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qwb.petmanage.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约Mapper接口
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
