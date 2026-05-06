
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.AdoptApply;
import com.qwb.petmanage.mapper.AdoptApplyMapper;
import com.qwb.petmanage.service.AdoptApplyService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdoptApplyServiceImpl extends ServiceImpl<AdoptApplyMapper, AdoptApply> implements AdoptApplyService {

    @Override
    public Page<AdoptApply> pageList(Integer current, Integer size, String auditStatus, Long userId, Long petId) {
        Page<AdoptApply> page = new Page<>(current, size);
        LambdaQueryWrapper<AdoptApply> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null && !auditStatus.isEmpty()) {
            wrapper.eq(AdoptApply::getAuditStatus, auditStatus);
        }
        if (userId != null) {
            wrapper.eq(AdoptApply::getUserId, userId);
        }
        if (petId != null) {
            wrapper.eq(AdoptApply::getPetId, petId);
        }
        wrapper.orderByDesc(AdoptApply::getApplyTime);
        return page(page, wrapper);
    }

    @Override
    public boolean submitApply(AdoptApply adoptApply) {
        adoptApply.setAuditStatus("待审核");
        adoptApply.setApplyTime(LocalDateTime.now());
        return save(adoptApply);
    }

    @Override
    public boolean auditApply(Long applyId, String auditStatus, String auditRemark, Long auditAdminId) {
        AdoptApply adoptApply = getById(applyId);
        if (adoptApply == null) {
            throw new RuntimeException("申请记录不存在");
        }
        adoptApply.setAuditStatus(auditStatus);
        adoptApply.setAuditRemark(auditRemark);
        adoptApply.setAuditAdmin(auditAdminId);
        adoptApply.setAuditTime(LocalDateTime.now());
        return updateById(adoptApply);
    }

    @Override
    public List<AdoptApply> listByUserId(Long userId) {
        LambdaQueryWrapper<AdoptApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptApply::getUserId, userId);
        wrapper.orderByDesc(AdoptApply::getApplyTime);
        return list(wrapper);
    }

    @Override
    public List<AdoptApply> listByPetId(Long petId) {
        LambdaQueryWrapper<AdoptApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptApply::getPetId, petId);
        wrapper.orderByDesc(AdoptApply::getApplyTime);
        return list(wrapper);
    }
}
