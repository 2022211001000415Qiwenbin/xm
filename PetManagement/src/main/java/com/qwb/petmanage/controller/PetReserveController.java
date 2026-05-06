
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        Page<PetReserve> page = new Page<>(current, size);
        QueryWrapper<PetReserve> queryWrapper = new QueryWrapper<>();
        if (reserveStatus != null && !reserveStatus.isEmpty()) {
            queryWrapper.eq("reserve_status", reserveStatus);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (petId != null) {
            queryWrapper.eq("pet_id", petId);
        }
        if (realName != null && !realName.isEmpty()) {
            queryWrapper.like("real_name", realName);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(petReserveService.page(page, queryWrapper));
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
        petReserve.setReserveStatus("待确认");
        petReserveService.save(petReserve);
        return Result.success();
    }

    /**
     * 确认预约
     */
    @PostMapping("/confirm")
    public Result<Void> confirm(@RequestBody java.util.Map<String, Object> params) {
        Long reserveId = Long.parseLong(params.get("reserveId").toString());
        Long confirmAdmin = params.get("confirmAdmin") != null ? Long.parseLong(params.get("confirmAdmin").toString()) : null;

        PetReserve petReserve = petReserveService.getById(reserveId);
        if (petReserve == null) {
            return Result.error("预约记录不存在");
        }
        petReserve.setReserveStatus("已确认");
        petReserve.setConfirmAdmin(confirmAdmin);
        petReserveService.updateById(petReserve);
        return Result.success();
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result<Void> cancel(@RequestParam Long id,
                               @RequestParam String cancelReason,
                               @RequestParam String cancelPerson) {
        PetReserve petReserve = petReserveService.getById(id);
        if (petReserve == null) {
            return Result.error("预约记录不存在");
        }
        petReserve.setReserveStatus("已取消");
        petReserve.setReserveRemark(cancelReason);
        petReserveService.updateById(petReserve);
        return Result.success();
    }

    /**
     * 完成预约
     */
    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        PetReserve petReserve = petReserveService.getById(id);
        if (petReserve == null) {
            return Result.error("预约记录不存在");
        }
        petReserve.setReserveStatus("已完成");
        petReserveService.updateById(petReserve);
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
