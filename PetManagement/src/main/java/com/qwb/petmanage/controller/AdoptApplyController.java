
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.AdoptApply;
import com.qwb.petmanage.service.AdoptApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 领养申请控制器
 */
@RestController
@RequestMapping("/adopt-apply")
public class AdoptApplyController {

    @Autowired
    private AdoptApplyService adoptApplyService;

    /**
     * 分页查询领养申请列表
     */
    @GetMapping("/page")
    public Result<Page<AdoptApply>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String auditStatus,
                                           @RequestParam(required = false) String realName,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) Long petId) {
        Page<AdoptApply> page = adoptApplyService.pageList(current, size, auditStatus, realName, userId, petId);
        return Result.success(page);
    }

    /**
     * 根据ID查询领养申请
     */
    @GetMapping("/{id}")
    public Result<AdoptApply> getById(@PathVariable Long id) {
        return Result.success(adoptApplyService.getById(id));
    }

    /**
     * 提交领养申请
     */
    @PostMapping("/submit")
    public Result<Void> add(@RequestBody AdoptApply adoptApply) {
        boolean success = adoptApplyService.submitApply(adoptApply);
        return success ? Result.success() : Result.error("提交申请失败");
    }

    /**
     * 审核领养申请
     */
    @PostMapping("/review")
    public Result<Void> audit(@RequestBody java.util.Map<String, Object> params) {
        Long applyId = Long.parseLong(params.get("applyId").toString());
        String auditStatus = params.get("auditStatus").toString();
        String auditRemark = params.get("auditRemark") != null ? params.get("auditRemark").toString() : null;
        Long auditAdmin = params.get("auditAdmin") != null ? Long.parseLong(params.get("auditAdmin").toString()) : null;

        boolean success = adoptApplyService.auditApply(applyId, auditStatus, auditRemark, auditAdmin);
        return success ? Result.success() : Result.error("审核失败");
    }

    /**
     * 根据用户ID查询领养申请列表
     */
    @GetMapping("/listByUserId/{userId}")
    public Result<java.util.List<AdoptApply>> listByUserId(@PathVariable Long userId) {
        return Result.success(adoptApplyService.listByUserId(userId));
    }

    /**
     * 根据宠物ID查询领养申请列表
     */
    @GetMapping("/listByPetId/{petId}")
    public Result<java.util.List<AdoptApply>> listByPetId(@PathVariable Long petId) {
        return Result.success(adoptApplyService.listByPetId(petId));
    }

    /**
     * 删除领养申请
     */
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adoptApplyService.removeById(id);
        return Result.success();
    }

    /**
     * 取消领养申请
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        AdoptApply adoptApply = adoptApplyService.getById(id);
        if (adoptApply == null) {
            return Result.error("申请记录不存在");
        }
        if (!"待审核".equals(adoptApply.getAuditStatus())) {
            return Result.error("只能取消待审核的申请");
        }
        adoptApply.setAuditStatus("已取消");
        adoptApplyService.updateById(adoptApply);
        return Result.success();
    }
}
