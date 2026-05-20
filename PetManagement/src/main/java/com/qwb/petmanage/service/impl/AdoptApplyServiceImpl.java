
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.AdoptApply;
import com.qwb.petmanage.mapper.AdoptApplyMapper;
import com.qwb.petmanage.service.AdoptApplyService;
import com.qwb.petmanage.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdoptApplyServiceImpl extends ServiceImpl<AdoptApplyMapper, AdoptApply> implements AdoptApplyService {

    @Lazy
    @Autowired
    private PetService petService;

    @Override
    public Page<AdoptApply> pageList(Integer current, Integer size, String auditStatus, String realName, Long userId, Long petId) {
        Page<AdoptApply> page = new Page<>(current, size);
        List<AdoptApply> records = baseMapper.selectApplyPage(page, auditStatus, realName);
        page.setRecords(records);
        return page;
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
        String previousStatus = adoptApply.getAuditStatus();
        adoptApply.setAuditStatus(auditStatus);
        adoptApply.setAuditRemark(auditRemark);
        adoptApply.setAuditAdmin(auditAdminId);
        adoptApply.setAuditTime(LocalDateTime.now());
        boolean updated = updateById(adoptApply);
        // 审核通过时，联动更新宠物状态为"已领养"
        if (updated && "通过".equals(auditStatus) && adoptApply.getPetId() != null) {
            petService.updateAdoptStatus(adoptApply.getPetId().intValue(), "已领养");
        }
        // 审核拒绝时，如果之前是审核通过状态，需要将宠物状态恢复为"待领养"
        if (updated && "拒绝".equals(auditStatus) && adoptApply.getPetId() != null
                && "通过".equals(previousStatus)) {
            petService.updateAdoptStatus(adoptApply.getPetId().intValue(), "待领养");
        }
        return updated;
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
