
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long petId;

    private String petName;

    private String visitorName;

    private String phone;

    private String idCard;

    private String email;

    private LocalDateTime appointmentTime;

    private String timeSlot; // 上午、下午、晚上

    private String status; // 待确认、已确认、已完成、已取消

    private String notes;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime confirmTime;

    private String confirmPerson;

    private LocalDateTime completeTime;

    private String completePerson;

    private String feedback; // 参观反馈

    private Integer visitDuration; // 参观时长（分钟）

    private String cancelReason; // 取消原因

    private LocalDateTime cancelTime; // 取消时间

    private String cancelPerson; // 取消操作人
}
