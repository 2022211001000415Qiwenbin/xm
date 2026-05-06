package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.Appointment;

/**
 * 预约Service接口
 */
public interface AppointmentService extends IService<Appointment> {

    /**
     * 分页查询预约列表
     */
    Page<Appointment> pageList(Integer current, Integer size, String keyword, String status);

    /**
     * 提交预约申请
     */
    boolean submitAppointment(Appointment appointment);

    /**
     * 更新预约信息
     */
    boolean updateAppointment(Appointment appointment);

    /**
     * 删除预约
     */
    boolean deleteAppointment(Long id);

    /**
     * 确认预约
     */
    boolean confirmAppointment(Long id, String confirmPerson);

    /**
     * 完成预约
     */
    boolean completeAppointment(Long id, String completePerson, String feedback, Integer visitDuration);

    /**
     * 取消预约
     */
    boolean cancelAppointment(Long id, String cancelReason, String cancelPerson);
}
