
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("adopt_apply")
public class AdoptApply {
    @TableId(type = IdType.AUTO)
    private Long applyId;

    private Long userId;

    private Long petId;

    private String applyInfo;

    private String applicantName;

    private String applicantPhone;

    private String applicantOccupation;

    private String applicantAddress;

    private String applicantExperience;

    private String auditStatus;

    private String auditRemark;

    private Long auditAdmin;

    private LocalDateTime applyTime;

    private LocalDateTime auditTime;

    // 关联查询字段
    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String phone;

    @TableField(exist = false)
    private String petName;

    @TableField(exist = false)
    private String breedName;

    @TableField(exist = false)
    private String petPhoto;

    @TableField(exist = false)
    private String adoptStatus;

    @TableField(exist = false)
    private String adminName;
}
