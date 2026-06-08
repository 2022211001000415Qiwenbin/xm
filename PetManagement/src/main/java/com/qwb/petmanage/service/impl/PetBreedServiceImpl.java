
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.PetBreed;
import com.qwb.petmanage.mapper.PetBreedMapper;
import com.qwb.petmanage.service.PetBreedService;
import org.springframework.stereotype.Service;

@Service
public class PetBreedServiceImpl extends ServiceImpl<PetBreedMapper, PetBreed> implements PetBreedService {

    @Override
    public Page<PetBreed> pageList(Integer current, Integer size, String breedName, String breedType) {
        Page<PetBreed> page = new Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PetBreed> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (breedName != null && !breedName.isEmpty()) {
            queryWrapper.like("breed_name", breedName);
        }
        if (breedType != null && !breedType.isEmpty()) {
            queryWrapper.eq("breed_type", breedType);
        }
        queryWrapper.orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public Integer addBreed(PetBreed petBreed) {
        // 检查品种名称是否已存在
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PetBreed> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("breed_name", petBreed.getBreedName());
        if (count(queryWrapper) > 0) {
            throw new RuntimeException("品种名称已存在");
        }
        petBreed.setCreateTime(java.time.LocalDateTime.now());
        save(petBreed);
        return petBreed.getBreedId();
    }

    @Override
    public java.util.List<PetBreed> listAll() {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PetBreed> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.orderByAsc("breed_type", "breed_name");
        return list(queryWrapper);
    }
}
