package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.AdoptionApplication;
import com.qwb.petmanage.mapper.AdoptionApplicationMapper;
import com.qwb.petmanage.service.AdoptionApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 领养申请Service实现类
 */
@Service
public class AdoptionApplicationServiceImpl extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication> implements AdoptionApplicationService {

    @Override
    public Page<AdoptionApplication> pageList(Integer current, Integer size, String keyword, String status) {
        Page<AdoptionApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(AdoptionApplication::getPetName, keyword)
                   .or()
                   .like(AdoptionApplication::getApplicantName, keyword)
                   .or()
                   .like(AdoptionApplication::getPhone, keyword);
        }

        if (StringUtils.hasText(status)) {
            wrapper.eq(AdoptionApplication::getStatus, status);
        }

        wrapper.orderByDesc(AdoptionApplication::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean submitApplication(AdoptionApplication application) {
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        application.setStatus("待审核");
        return save(application);
    }

    @Override
    public boolean updateApplication(AdoptionApplication application) {
        application.setUpdateTime(LocalDateTime.now());
        return updateById(application);
    }

    @Override
    public boolean deleteApplication(Long id) {
        return removeById(id);
    }

    @Override
    public boolean reviewApplication(Long id, String status, String reviewer, String rejectReason) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            return false;
        }
        application.setStatus(status);
        application.setReviewTime(LocalDateTime.now());
        application.setReviewer(reviewer);
        application.setRejectReason(rejectReason);
        application.setUpdateTime(LocalDateTime.now());
        return updateById(application);
    }

    @Override
    public boolean completeAdoption(Long id) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            return false;
        }
        application.setStatus("已领养");
        application.setAdoptionTime(LocalDateTime.now());
        // 设置回访日期为领养后一个月
        application.setReturnVisitDate(LocalDate.now().plusMonths(1));
        application.setUpdateTime(LocalDateTime.now());
        return updateById(application);
    }

    @Override
    public boolean cancelApplication(Long id) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            return false;
        }
        application.setStatus("已取消");
        application.setUpdateTime(LocalDateTime.now());
        return updateById(application);
    }

    @Override
    public boolean addReturnVisit(Long id, String status, String notes) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            return false;
        }
        application.setReturnVisitStatus(status);
        application.setReturnVisitNotes(notes);
        application.setUpdateTime(LocalDateTime.now());
        return updateById(application);
    }
}
