
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("adoption_application")
public class AdoptionApplication {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long petId;

    private String petName;

    private String applicantName;

    private String idCard;

    private String phone;

    private String email;

    private String occupation;

    private BigDecimal monthlyIncome;

    private String housingType; // 自有住房、租房、与父母同住等

    private String housingCondition; // 平房、楼房、带院子等

    private Boolean hasExperience; // 是否有养宠经验

    private String experienceDescription;

    private Integer familyMembers; // 家庭成员数量

    private Boolean hasChildren; // 是否有小孩

    private Integer childrenAge;

    private Boolean hasOtherPets; // 是否有其他宠物

    private String otherPetInfo;

    private String workSchedule; // 工作时间安排

    private String carePlan; // 照料计划

    private String reason; // 领养原因

    private String status; // 待审核、审核通过、审核拒绝、待领养、已领养、已取消

    private String rejectReason;

    private String notes;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime reviewTime;

    private String reviewer;

    private LocalDateTime adoptionTime;

    private LocalDate returnVisitDate; // 回访日期

    private String returnVisitStatus; // 回访状态

    private String returnVisitNotes; // 回访记录
}
