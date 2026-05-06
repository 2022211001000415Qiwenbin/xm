
package com.qwb.petmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

    private String auditStatus;

    private String auditRemark;

    private Long auditAdmin;

    private LocalDateTime applyTime;

    private LocalDateTime auditTime;
}
