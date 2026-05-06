
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet_reserve")
public class PetReserve {
    @TableId(type = IdType.AUTO)
    private Long reserveId;

    private Long userId;

    private Long petId;

    private LocalDateTime reserveTime;

    private String reserveAddress;

    private String contactPerson;

    private String contactPhone;

    private String reserveStatus;

    private Long confirmAdmin;

    private String reserveRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
