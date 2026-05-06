
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet_breed")
public class PetBreed {
    @TableId(type = IdType.AUTO)
    private Long breedId;

    private String breedName;

    private String breedType;

    private LocalDateTime createTime;
}
