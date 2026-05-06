package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Appointment;
import com.qwb.petmanage.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约管理Controller
 */
@Tag(name = "预约管理", description = "预约信息管理接口")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 分页查询预约列表
     */
    @Operation(summary = "分页查询预约列表")
    @GetMapping("/page")
    public Result<Page<Appointment>> page(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String status) {
        Page<Appointment> page = appointmentService.pageList(current, size, keyword, status);
        return Result.success(page);
    }

    /**
     * 提交预约申请
     */
    @Operation(summary = "提交预约申请")
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Appointment appointment) {
        boolean success = appointmentService.submitAppointment(appointment);
        return success ? Result.success() : Result.error("提交失败");
    }

    /**
     * 更新预约信息
     */
    @Operation(summary = "更新预约信息")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Appointment appointment) {
        boolean success = appointmentService.updateAppointment(appointment);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除预约
     */
    @Operation(summary = "删除预约")
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = appointmentService.deleteAppointment(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取预约详情
     */
    @Operation(summary = "获取预约详情")
    @GetMapping("/detail/{id}")
    public Result<Appointment> detail(@PathVariable Long id) {
        Appointment appointment = appointmentService.getById(id);
        return Result.success(appointment);
    }

    /**
     * 确认预约
     */
    @Operation(summary = "确认预约")
    @PostMapping("/confirm")
    public Result<Void> confirm(@RequestParam Long id,
                               @RequestParam String confirmPerson) {
        boolean success = appointmentService.confirmAppointment(id, confirmPerson);
        return success ? Result.success() : Result.error("确认失败");
    }

    /**
     * 完成预约
     */
    @Operation(summary = "完成预约")
    @PostMapping("/complete")
    public Result<Void> complete(@RequestParam Long id,
                                @RequestParam String completePerson,
                                @RequestParam(required = false) String feedback,
                                @RequestParam(required = false) Integer visitDuration) {
        boolean success = appointmentService.completeAppointment(id, completePerson, feedback, visitDuration);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 取消预约
     */
    @Operation(summary = "取消预约")
    @PostMapping("/cancel")
    public Result<Void> cancel(@RequestParam Long id,
                               @RequestParam String cancelReason,
                               @RequestParam String cancelPerson) {
        boolean success = appointmentService.cancelAppointment(id, cancelReason, cancelPerson);
        return success ? Result.success() : Result.error("取消失败");
    }
}
