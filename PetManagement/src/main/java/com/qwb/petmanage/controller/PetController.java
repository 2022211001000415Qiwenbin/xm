
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Pet;
import com.qwb.petmanage.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宠物管理", description = "宠物信息管理接口")
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 分页查询宠物列表
     */
    @Operation(summary = "分页查询宠物列表")
    @GetMapping("/page")
    public Result<Page<Pet>> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String petName,
                                   @RequestParam(required = false) String adoptStatus,
                                   @RequestParam(required = false) String breedType) {
        Page<Pet> page = petService.pageList(current, size, petName, adoptStatus, breedType);
        return Result.success(page);
    }

    /**
     * 添加宠物
     */
    @Operation(summary = "添加宠物")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Pet pet) {
        boolean success = petService.addPet(pet);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新宠物信息
     */
    @Operation(summary = "更新宠物信息")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Pet pet) {
        boolean success = petService.updatePet(pet);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除宠物
     */
    @Operation(summary = "删除宠物")
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = petService.deletePet(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取宠物详情
     */
    @Operation(summary = "获取宠物详情")
    @GetMapping("/detail/{id}")
    public Result<Pet> detail(@PathVariable Integer id) {
        Pet pet = petService.getPetDetail(id);
        return Result.success(pet);
    }
    
    /**
     * 更新宠物领养状态
     */
    @Operation(summary = "更新宠物领养状态")
    @PostMapping("/updateStatus")
    public Result<Void> updateAdoptStatus(@RequestParam Integer petId, @RequestParam String adoptStatus) {
        boolean success = petService.updateAdoptStatus(petId, adoptStatus);
        return success ? Result.success() : Result.error("更新失败");
    }
    
    /**
     * 根据品种ID查询宠物列表
     */
    @Operation(summary = "根据品种ID查询宠物列表")
    @GetMapping("/listByBreed/{breedId}")
    public Result<java.util.List<Pet>> listByBreedId(@PathVariable Integer breedId) {
        java.util.List<Pet> list = petService.listByBreedId(breedId);
        return Result.success(list);
    }
    
    /**
     * 根据领养状态查询宠物列表
     */
    @Operation(summary = "根据领养状态查询宠物列表")
    @GetMapping("/listByStatus/{adoptStatus}")
    public Result<java.util.List<Pet>> listByAdoptStatus(@PathVariable String adoptStatus) {
        java.util.List<Pet> list = petService.listByAdoptStatus(adoptStatus);
        return Result.success(list);
    }
}
