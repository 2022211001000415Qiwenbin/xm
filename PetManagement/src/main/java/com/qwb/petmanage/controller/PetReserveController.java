
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.PetReserve;
import com.qwb.petmanage.service.PetReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 线下看宠预约控制器
 */
@RestController
@RequestMapping("/pet-reserve")
public class PetReserveController {

    @Autowired
    private PetReserveService petReserveService;

    /**
     * 分页查询预约列表
     */
    @GetMapping("/page")
    public Result<Page<PetReserve>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String reserveStatus,
                                           @RequestParam(required = false) String realName,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) Long petId) {
        Page<PetReserve> page = petReserveService.pageList(current, size, reserveStatus, userId, petId, realName);
        return Result.success(page);
    }

    /**
     * 根据ID查询预约
     */
    @GetMapping("/{id}")
    public Result<PetReserve> getById(@PathVariable Long id) {
        return Result.success(petReserveService.getById(id));
    }

    /**
     * 创建预约
     */
    @PostMapping("/submit")
    public Result<Void> add(@RequestBody PetReserve petReserve) {
        petReserveService.submitReserve(petReserve);
        return Result.success();
    }

    /**
     * 确认预约
     */
    @PostMapping("/confirm")
    public Result<Void> confirm(@RequestBody java.util.Map<String, Object> params) {
        Long reserveId = Long.parseLong(params.get("reserveId").toString());
        Long confirmAdmin = params.get("confirmAdmin") != null ? Long.parseLong(params.get("confirmAdmin").toString()) : null;
        petReserveService.confirmReserve(reserveId, confirmAdmin);
        return Result.success();
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result<Void> cancel(@RequestParam Long id,
                               @RequestParam String cancelReason,
                               @RequestParam String cancelPerson) {
        petReserveService.cancelReserve(id, cancelReason);
        return Result.success();
    }

    /**
     * 完成预约
     */
    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        petReserveService.completeReserve(id);
        return Result.success();
    }

    /**
     * 删除预约
     */
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petReserveService.removeById(id);
        return Result.success();
    }
}
