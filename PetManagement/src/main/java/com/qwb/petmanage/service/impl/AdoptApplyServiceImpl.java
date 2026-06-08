
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.AdoptApply;
import com.qwb.petmanage.mapper.AdoptApplyMapper;
import com.qwb.petmanage.service.AdoptApplyService;
import com.qwb.petmanage.entity.PetReserve;
import com.qwb.petmanage.service.PetReserveService;
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

    @Lazy
    @Autowired
    private PetReserveService petReserveService;

    @Override
    public Page<AdoptApply> pageList(Integer current, Integer size, String auditStatus, String realName, Long userId, Long petId) {
        Page<AdoptApply> page = new Page<>(current, size);
        List<AdoptApply> records = baseMapper.selectApplyPage(page, auditStatus, realName, userId, petId);
        page.setRecords(records);
        return page;
    }

    @Override
    public boolean submitApply(AdoptApply adoptApply) {
        Long userId = adoptApply.getUserId();
        Long petId = adoptApply.getPetId();

        // 1. 同一用户不能对同一宠物重复提交待审核的申请
        LambdaQueryWrapper<AdoptApply> duplicateWrapper = new LambdaQueryWrapper<>();
        duplicateWrapper.eq(AdoptApply::getUserId, userId);
        duplicateWrapper.eq(AdoptApply::getPetId, petId);
        duplicateWrapper.eq(AdoptApply::getAuditStatus, "待审核");
        long duplicateCount = count(duplicateWrapper);
        if (duplicateCount > 0) {
            throw new RuntimeException("您已对该宠物提交过领养申请，请勿重复提交");
        }

        // 2. 同一用户不能对同一宠物有已通过的申请时再次申请
        LambdaQueryWrapper<AdoptApply> approvedWrapper = new LambdaQueryWrapper<>();
        approvedWrapper.eq(AdoptApply::getUserId, userId);
        approvedWrapper.eq(AdoptApply::getPetId, petId);
        approvedWrapper.eq(AdoptApply::getAuditStatus, "通过");
        long approvedCount = count(approvedWrapper);
        if (approvedCount > 0) {
            throw new RuntimeException("您已成功领养该宠物，无需再次申请");
        }

        // 3. 同一用户最多同时有3个待审核的申请
        LambdaQueryWrapper<AdoptApply> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(AdoptApply::getUserId, userId);
        pendingWrapper.eq(AdoptApply::getAuditStatus, "待审核");
        long pendingCount = count(pendingWrapper);
        if (pendingCount >= 3) {
            throw new RuntimeException("您已有3个待审核的领养申请，请等待审核后再提交");
        }

        // 4. 用户已有通过的领养申请（已成功领养宠物）时不能再申请
        LambdaQueryWrapper<AdoptApply> hasAdoptedWrapper = new LambdaQueryWrapper<>();
        hasAdoptedWrapper.eq(AdoptApply::getUserId, userId);
        hasAdoptedWrapper.eq(AdoptApply::getAuditStatus, "通过");
        long hasAdoptedCount = count(hasAdoptedWrapper);
        if (hasAdoptedCount > 0) {
            throw new RuntimeException("您已有成功领养的宠物，暂时无法再次申请领养");
        }

        // 5. 用户必须至少完成一次预约才能申请领养
        LambdaQueryWrapper<PetReserve> reserveWrapper = new LambdaQueryWrapper<>();
        reserveWrapper.eq(PetReserve::getUserId, userId);
        reserveWrapper.eq(PetReserve::getReserveStatus, "已完成");
        long completedReserveCount = petReserveService.count(reserveWrapper);
        if (completedReserveCount == 0) {
            throw new RuntimeException("请先预约看宠并完成至少一次预约，才能申请领养");
        }

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
        // 审核通过时，联动更新宠物状态为"已领养"，并自动拒绝同一宠物的其他待审核申请
        if (updated && "通过".equals(auditStatus) && adoptApply.getPetId() != null) {
            petService.updateAdoptStatus(adoptApply.getPetId().intValue(), "已领养");
            // 自动拒绝同一宠物的其他待审核申请
            LambdaQueryWrapper<AdoptApply> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdoptApply::getPetId, adoptApply.getPetId());
            wrapper.eq(AdoptApply::getAuditStatus, "待审核");
            wrapper.ne(AdoptApply::getApplyId, applyId);
            List<AdoptApply> otherPendingApplies = list(wrapper);
            for (AdoptApply otherApply : otherPendingApplies) {
                otherApply.setAuditStatus("拒绝");
                otherApply.setAuditRemark("该宠物已被其他申请人领养");
                otherApply.setAuditAdmin(auditAdminId);
                otherApply.setAuditTime(LocalDateTime.now());
                updateById(otherApply);
            }
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
