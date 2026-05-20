
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.AdoptApply;
import com.qwb.petmanage.entity.Pet;
import com.qwb.petmanage.mapper.PetMapper;
import com.qwb.petmanage.entity.PetBreed;
import com.qwb.petmanage.service.AdoptApplyService;
import com.qwb.petmanage.service.PetBreedService;
import com.qwb.petmanage.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    @Autowired
    private AdoptApplyService adoptApplyService;

    @Autowired
    private PetBreedService petBreedService;

    @Override
    public Page<Pet> pageList(Integer current, Integer size, String petName, String adoptStatus, String breedType) {
        Page<Pet> page = new Page<>(current, size);
        List<Pet> records = baseMapper.selectPetPage(page, petName, adoptStatus, breedType);
        page.setRecords(records);
        return page;
    }

    @Override
    public Pet getPetDetail(Integer petId) {
        return baseMapper.selectPetDetail(petId);
    }

    @Override
    public boolean addPet(Pet pet) {
        // 根据breedType和breedName查找或创建品种
        if (StringUtils.hasText(pet.getBreedType()) && StringUtils.hasText(pet.getBreedName())) {
            LambdaQueryWrapper<PetBreed> breedWrapper = new LambdaQueryWrapper<>();
            breedWrapper.eq(PetBreed::getBreedType, pet.getBreedType());
            breedWrapper.eq(PetBreed::getBreedName, pet.getBreedName());
            PetBreed breed = petBreedService.getOne(breedWrapper);
            if (breed == null) {
                breed = new PetBreed();
                breed.setBreedType(pet.getBreedType());
                breed.setBreedName(pet.getBreedName());
                petBreedService.save(breed);
            }
            pet.setBreedId(breed.getBreedId());
        }
        pet.setCreateTime(LocalDateTime.now());
        pet.setUpdateTime(LocalDateTime.now());
        return save(pet);
    }

    @Override
    public boolean updatePet(Pet pet) {
        // 根据breedType和breedName查找或创建品种
        if (StringUtils.hasText(pet.getBreedType()) && StringUtils.hasText(pet.getBreedName())) {
            LambdaQueryWrapper<PetBreed> breedWrapper = new LambdaQueryWrapper<>();
            breedWrapper.eq(PetBreed::getBreedType, pet.getBreedType());
            breedWrapper.eq(PetBreed::getBreedName, pet.getBreedName());
            PetBreed breed = petBreedService.getOne(breedWrapper);
            if (breed == null) {
                breed = new PetBreed();
                breed.setBreedType(pet.getBreedType());
                breed.setBreedName(pet.getBreedName());
                petBreedService.save(breed);
            }
            pet.setBreedId(breed.getBreedId());
        }
        pet.setUpdateTime(LocalDateTime.now());
        return updateById(pet);
    }

    @Override
    public boolean deletePet(Integer id) {
        // 先删除该宠物的所有领养申请记录
        LambdaQueryWrapper<AdoptApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptApply::getPetId, id.longValue());
        adoptApplyService.remove(wrapper);

        // 再删除宠物记录
        return removeById(id);
    }

    @Override
    public boolean updateAdoptStatus(Integer petId, String adoptStatus) {
        Pet pet = getById(petId);
        if (pet == null) {
            throw new RuntimeException("宠物不存在");
        }
        pet.setAdoptStatus(adoptStatus);
        pet.setUpdateTime(LocalDateTime.now());
        return updateById(pet);
    }

    @Override
    public java.util.List<Pet> listByBreedId(Integer breedId) {
        if (breedId == null) {
            return java.util.Collections.emptyList();
        }
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getBreedId, breedId);
        return list(wrapper);
    }

    @Override
    public java.util.List<Pet> listByAdoptStatus(String adoptStatus) {
        if (!StringUtils.hasText(adoptStatus)) {
            return java.util.Collections.emptyList();
        }
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getAdoptStatus, adoptStatus);
        return list(wrapper);
    }
}