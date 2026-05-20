
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.PetBreed;
import com.qwb.petmanage.service.PetBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物品种控制器
 */
@RestController
@RequestMapping("/petBreed")
public class PetBreedController {

    @Autowired
    private PetBreedService petBreedService;

    /**
     * 分页查询品种列表
     */
    @GetMapping("/page")
    public Result<Page<PetBreed>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String breedName,
                                        @RequestParam(required = false) String breedType) {
        Page<PetBreed> page = new Page<>(current, size);
        QueryWrapper<PetBreed> queryWrapper = new QueryWrapper<>();
        if (breedName != null && !breedName.isEmpty()) {
            queryWrapper.like("breed_name", breedName);
        }
        if (breedType != null && !breedType.isEmpty()) {
            queryWrapper.eq("breed_type", breedType);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(petBreedService.page(page, queryWrapper));
    }

    /**
     * 获取所有品种
     */
    @GetMapping("/list")
    public Result<java.util.List<PetBreed>> list() {
        QueryWrapper<PetBreed> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("breed_type", "breed_name");
        return Result.success(petBreedService.list(queryWrapper));
    }

    /**
     * 根据ID查询品种
     */
    @GetMapping("/{id}")
    public Result<PetBreed> getById(@PathVariable Long id) {
        return Result.success(petBreedService.getById(id));
    }

    /**
     * 新增品种
     */
    @PostMapping
    public Result<Integer> add(@RequestBody PetBreed petBreed) {
        // 检查品种名称是否已存在
        QueryWrapper<PetBreed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("breed_name", petBreed.getBreedName());
        if (petBreedService.count(queryWrapper) > 0) {
            return Result.error("品种名称已存在");
        }
        petBreedService.save(petBreed);
        return Result.success(petBreed.getBreedId());
    }

    /**
     * 修改品种
     */
    @PutMapping
    public Result<Void> update(@RequestBody PetBreed petBreed) {
        petBreedService.updateById(petBreed);
        return Result.success();
    }

    /**
     * 删除品种
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petBreedService.removeById(id);
        return Result.success();
    }
}
