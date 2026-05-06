
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.PetInfo;
import com.qwb.petmanage.service.PetInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物信息控制器
 */
@RestController
@RequestMapping("/petInfo")
public class PetInfoController {

    @Autowired
    private PetInfoService petInfoService;

    /**
     * 分页查询宠物信息列表
     */
    @GetMapping("/page")
    public Result<Page<PetInfo>> page(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) String petName,
                                       @RequestParam(required = false) String adoptStatus,
                                       @RequestParam(required = false) Long breedId) {
        Page<PetInfo> page = new Page<>(current, size);
        QueryWrapper<PetInfo> queryWrapper = new QueryWrapper<>();
        if (petName != null && !petName.isEmpty()) {
            queryWrapper.like("pet_name", petName);
        }
        if (adoptStatus != null && !adoptStatus.isEmpty()) {
            queryWrapper.eq("adopt_status", adoptStatus);
        }
        if (breedId != null) {
            queryWrapper.eq("breed_id", breedId);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(petInfoService.page(page, queryWrapper));
    }

    /**
     * 根据ID查询宠物信息
     */
    @GetMapping("/{id}")
    public Result<PetInfo> getById(@PathVariable Long id) {
        return Result.success(petInfoService.getById(id));
    }

    /**
     * 新增宠物信息
     */
    @PostMapping
    public Result<Void> add(@RequestBody PetInfo petInfo) {
        petInfoService.save(petInfo);
        return Result.success();
    }

    /**
     * 修改宠物信息
     */
    @PutMapping
    public Result<Void> update(@RequestBody PetInfo petInfo) {
        petInfoService.updateById(petInfo);
        return Result.success();
    }

    /**
     * 删除宠物信息
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petInfoService.removeById(id);
        return Result.success();
    }

    /**
     * 更新宠物领养状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody java.util.Map<String, Object> params) {
        Long petId = Long.parseLong(params.get("petId").toString());
        String adoptStatus = params.get("adoptStatus").toString();

        PetInfo petInfo = petInfoService.getById(petId);
        if (petInfo == null) {
            return Result.error("宠物信息不存在");
        }
        petInfo.setAdoptStatus(adoptStatus);
        petInfoService.updateById(petInfo);
        return Result.success();
    }
}
