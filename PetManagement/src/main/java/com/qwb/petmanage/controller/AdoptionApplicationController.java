package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.AdoptionApplication;
import com.qwb.petmanage.service.AdoptionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 领养申请管理Controller
 */
@Tag(name = "领养申请管理", description = "领养申请信息管理接口")
@RestController
@RequestMapping("/adoption-application")
public class AdoptionApplicationController {

    @Autowired
    private AdoptionApplicationService adoptionApplicationService;

    /**
     * 分页查询领养申请列表
     */
    @Operation(summary = "分页查询领养申请列表")
    @GetMapping("/page")
    public Result<Page<AdoptionApplication>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String status) {
        Page<AdoptionApplication> page = adoptionApplicationService.pageList(current, size, keyword, status);
        return Result.success(page);
    }

    /**
     * 提交领养申请
     */
    @Operation(summary = "提交领养申请")
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody AdoptionApplication application) {
        boolean success = adoptionApplicationService.submitApplication(application);
        return success ? Result.success() : Result.error("提交失败");
    }

    /**
     * 更新领养申请信息
     */
    @Operation(summary = "更新领养申请信息")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody AdoptionApplication application) {
        boolean success = adoptionApplicationService.updateApplication(application);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除领养申请
     */
    @Operation(summary = "删除领养申请")
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = adoptionApplicationService.deleteApplication(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取领养申请详情
     */
    @Operation(summary = "获取领养申请详情")
    @GetMapping("/detail/{id}")
    public Result<AdoptionApplication> detail(@PathVariable Long id) {
        AdoptionApplication application = adoptionApplicationService.getById(id);
        return Result.success(application);
    }

    /**
     * 审核领养申请
     */
    @Operation(summary = "审核领养申请")
    @PostMapping("/review")
    public Result<Void> review(@RequestParam Long id,
                               @RequestParam String status,
                               @RequestParam String reviewer,
                               @RequestParam(required = false) String rejectReason) {
        boolean success = adoptionApplicationService.reviewApplication(id, status, reviewer, rejectReason);
        return success ? Result.success() : Result.error("审核失败");
    }

    /**
     * 完成领养
     */
    @Operation(summary = "完成领养")
    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        boolean success = adoptionApplicationService.completeAdoption(id);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 取消领养申请
     */
    @Operation(summary = "取消领养申请")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        boolean success = adoptionApplicationService.cancelApplication(id);
        return success ? Result.success() : Result.error("取消失败");
    }

    /**
     * 添加回访记录
     */
    @Operation(summary = "添加回访记录")
    @PostMapping("/return-visit")
    public Result<Void> addReturnVisit(@RequestParam Long id,
                                      @RequestParam String status,
                                      @RequestParam String notes) {
        boolean success = adoptionApplicationService.addReturnVisit(id, status, notes);
        return success ? Result.success() : Result.error("添加失败");
    }
}
