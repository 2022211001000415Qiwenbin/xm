
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("abandoned_pet")
public class AbandonedPet {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String breed;

    private Integer age;

    private String gender;

    private String color;

    private BigDecimal weight;

    private String healthStatus;

    private String vaccinationStatus;

    private String sterilizationStatus;

    private String personality;

    private String specialNeeds;

    private String photos;

    private String reason;

    private String contactPerson;

    private String contactPhone;

    private String address;

    private String status; // 待审核、审核通过、已领养、已下架

    private String notes;

    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime reviewTime;

    private String reviewer;
}
