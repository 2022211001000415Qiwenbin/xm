package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.Appointment;
import com.qwb.petmanage.mapper.AppointmentMapper;
import com.qwb.petmanage.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 预约Service实现类
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Override
    public Page<Appointment> pageList(Integer current, Integer size, String keyword, String status) {
        Page<Appointment> page = new Page<>(current, size);
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Appointment::getPetName, keyword)
                   .or()
                   .like(Appointment::getVisitorName, keyword)
                   .or()
                   .like(Appointment::getPhone, keyword);
        }

        if (StringUtils.hasText(status)) {
            wrapper.eq(Appointment::getStatus, status);
        }

        wrapper.orderByDesc(Appointment::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean submitAppointment(Appointment appointment) {
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointment.setStatus("待确认");
        return save(appointment);
    }

    @Override
    public boolean updateAppointment(Appointment appointment) {
        appointment.setUpdateTime(LocalDateTime.now());
        return updateById(appointment);
    }

    @Override
    public boolean deleteAppointment(Long id) {
        return removeById(id);
    }

    @Override
    public boolean confirmAppointment(Long id, String confirmPerson) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("已确认");
        appointment.setConfirmTime(LocalDateTime.now());
        appointment.setConfirmPerson(confirmPerson);
        appointment.setUpdateTime(LocalDateTime.now());
        return updateById(appointment);
    }

    @Override
    public boolean completeAppointment(Long id, String completePerson, String feedback, Integer visitDuration) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("已完成");
        appointment.setCompleteTime(LocalDateTime.now());
        appointment.setCompletePerson(completePerson);
        appointment.setFeedback(feedback);
        appointment.setVisitDuration(visitDuration);
        appointment.setUpdateTime(LocalDateTime.now());
        return updateById(appointment);
    }

    @Override
    public boolean cancelAppointment(Long id, String cancelReason, String cancelPerson) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("已取消");
        appointment.setCancelReason(cancelReason);
        appointment.setCancelTime(LocalDateTime.now());
        appointment.setCancelPerson(cancelPerson);
        appointment.setUpdateTime(LocalDateTime.now());
        return updateById(appointment);
    }
}
