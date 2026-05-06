package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.AbandonedPet;
import com.qwb.petmanage.mapper.AbandonedPetMapper;
import com.qwb.petmanage.service.AbandonedPetService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 弃养宠物Service实现类
 */
@Service
public class AbandonedPetServiceImpl extends ServiceImpl<AbandonedPetMapper, AbandonedPet> implements AbandonedPetService {

    @Override
    public Page<AbandonedPet> pageList(Integer current, Integer size, String keyword, String status) {
        Page<AbandonedPet> page = new Page<>(current, size);
        LambdaQueryWrapper<AbandonedPet> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(AbandonedPet::getName, keyword)
                   .or()
                   .like(AbandonedPet::getType, keyword)
                   .or()
                   .like(AbandonedPet::getBreed, keyword)
                   .or()
                   .like(AbandonedPet::getContactPerson, keyword);
        }

        if (StringUtils.hasText(status)) {
            wrapper.eq(AbandonedPet::getStatus, status);
        }

        wrapper.orderByDesc(AbandonedPet::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean addAbandonedPet(AbandonedPet abandonedPet) {
        abandonedPet.setCreateTime(LocalDateTime.now());
        abandonedPet.setUpdateTime(LocalDateTime.now());
        abandonedPet.setStatus("待审核");
        return save(abandonedPet);
    }

    @Override
    public boolean updateAbandonedPet(AbandonedPet abandonedPet) {
        abandonedPet.setUpdateTime(LocalDateTime.now());
        return updateById(abandonedPet);
    }

    @Override
    public boolean deleteAbandonedPet(Long id) {
        return removeById(id);
    }

    @Override
    public boolean reviewAbandonedPet(Long id, String status, String reviewer) {
        AbandonedPet abandonedPet = getById(id);
        if (abandonedPet == null) {
            return false;
        }
        abandonedPet.setStatus(status);
        abandonedPet.setReviewTime(LocalDateTime.now());
        abandonedPet.setReviewer(reviewer);
        abandonedPet.setUpdateTime(LocalDateTime.now());
        return updateById(abandonedPet);
    }
}
