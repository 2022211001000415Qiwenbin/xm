
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet")
public class PetInfo {
    @TableId(type = IdType.AUTO)
    private Long petId;

    private Long breedId;

    private String petName;

    private Integer age;

    private String gender;

    private String healthStatus;

    private String personality;

    private String experience;

    private String petPhoto;

    private String adoptStatus;

    private String remark;

    private Long createAdmin;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
