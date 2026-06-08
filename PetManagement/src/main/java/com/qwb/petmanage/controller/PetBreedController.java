
package com.qwb.petmanage.controller;

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
        Page<PetBreed> page = petBreedService.pageList(current, size, breedName, breedType);
        return Result.success(page);
    }

    /**
     * 获取所有品种
     */
    @GetMapping("/list")
    public Result<java.util.List<PetBreed>> list() {
        return Result.success(petBreedService.listAll());
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
        Integer breedId = petBreedService.addBreed(petBreed);
        return Result.success(breedId);
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
