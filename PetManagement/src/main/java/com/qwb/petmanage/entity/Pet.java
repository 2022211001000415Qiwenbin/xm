
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet")
public class Pet {
    @TableId(type = IdType.AUTO)
    private Integer petId;

    private Integer breedId;

    private String petName;

    private Integer age;

    private String gender;

    private String healthStatus;

    private String abandonReason;

    private String rescueAddress;

    private String petPhoto;

    private String adoptStatus;

    private String remark;

    private Integer createAdmin;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
