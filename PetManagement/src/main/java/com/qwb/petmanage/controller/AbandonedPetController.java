package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.AbandonedPet;
import com.qwb.petmanage.service.AbandonedPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 弃养宠物管理Controller
 */
@Tag(name = "弃养宠物管理", description = "弃养宠物信息管理接口")
@RestController
@RequestMapping("/abandoned-pet")
public class AbandonedPetController {

    @Autowired
    private AbandonedPetService abandonedPetService;

    /**
     * 分页查询弃养宠物列表
     */
    @Operation(summary = "分页查询弃养宠物列表")
    @GetMapping("/page")
    public Result<Page<AbandonedPet>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String status) {
        Page<AbandonedPet> page = abandonedPetService.pageList(current, size, keyword, status);
        return Result.success(page);
    }

    /**
     * 添加弃养宠物
     */
    @Operation(summary = "添加弃养宠物")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody AbandonedPet abandonedPet) {
        boolean success = abandonedPetService.addAbandonedPet(abandonedPet);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新弃养宠物信息
     */
    @Operation(summary = "更新弃养宠物信息")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody AbandonedPet abandonedPet) {
        boolean success = abandonedPetService.updateAbandonedPet(abandonedPet);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除弃养宠物
     */
    @Operation(summary = "删除弃养宠物")
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = abandonedPetService.deleteAbandonedPet(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取弃养宠物详情
     */
    @Operation(summary = "获取弃养宠物详情")
    @GetMapping("/detail/{id}")
    public Result<AbandonedPet> detail(@PathVariable Long id) {
        AbandonedPet abandonedPet = abandonedPetService.getById(id);
        return Result.success(abandonedPet);
    }

    /**
     * 审核弃养宠物
     */
    @Operation(summary = "审核弃养宠物")
    @PostMapping("/review")
    public Result<Void> review(@RequestParam Long id,
                               @RequestParam String status,
                               @RequestParam String reviewer) {
        boolean success = abandonedPetService.reviewAbandonedPet(id, status, reviewer);
        return success ? Result.success() : Result.error("审核失败");
    }
}
