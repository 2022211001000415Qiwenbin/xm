
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.AdoptApply;
import com.qwb.petmanage.entity.Pet;
import com.qwb.petmanage.entity.PetReserve;
import com.qwb.petmanage.service.AdoptApplyService;
import com.qwb.petmanage.service.PetReserveService;
import com.qwb.petmanage.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘Controller
 */
@Tag(name = "仪表盘", description = "仪表盘数据接口")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private PetService petService;

    @Autowired
    private AdoptApplyService adoptApplyService;

    @Autowired
    private PetReserveService petReserveService;

    /**
     * 获取统计数据
     */
    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 宠物总数
        long totalPets = petService.count();
        stats.put("totalPets", totalPets);

        // 已领养宠物数
        LambdaQueryWrapper<Pet> adoptedWrapper = new LambdaQueryWrapper<>();
        adoptedWrapper.eq(Pet::getAdoptStatus, "已领养");
        long adoptedPets = petService.count(adoptedWrapper);
        stats.put("adoptedPets", adoptedPets);

        // 待审核申请数
        LambdaQueryWrapper<AdoptApply> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(AdoptApply::getAuditStatus, "待审核");
        long pendingApplications = adoptApplyService.count(pendingWrapper);
        stats.put("pendingApplications", pendingApplications);

        // 今日预约数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LambdaQueryWrapper<PetReserve> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(PetReserve::getReserveTime, todayStart)
                    .lt(PetReserve::getReserveTime, todayEnd);
        long todayAppointments = petReserveService.count(todayWrapper);
        stats.put("todayAppointments", todayAppointments);

        return Result.success(stats);
    }

    /**
     * 获取最新动态
     */
    @Operation(summary = "获取最新动态")
    @GetMapping("/recent-activities")
    public Result<List<Map<String, String>>> getRecentActivities() {
        List<Map<String, String>> activities = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 最近通过的领养申请
        LambdaQueryWrapper<AdoptApply> adoptWrapper = new LambdaQueryWrapper<>();
        adoptWrapper.eq(AdoptApply::getAuditStatus, "通过")
                    .orderByDesc(AdoptApply::getAuditTime)
                    .last("LIMIT 3");
        List<AdoptApply> adoptApplies = adoptApplyService.list(adoptWrapper);
        for (AdoptApply apply : adoptApplies) {
            Map<String, String> activity = new HashMap<>();
            activity.put("time", apply.getAuditTime() != null ? apply.getAuditTime().format(formatter) : "");
            activity.put("type", "领养");
            activity.put("description", "用户(ID:" + apply.getUserId() + ")成功领养了宠物(ID:" + apply.getPetId() + ")");
            activities.add(activity);
        }

        // 最近的预约
        LambdaQueryWrapper<PetReserve> reserveWrapper = new LambdaQueryWrapper<>();
        reserveWrapper.orderByDesc(PetReserve::getCreateTime)
                      .last("LIMIT 3");
        List<PetReserve> reserves = petReserveService.list(reserveWrapper);
        for (PetReserve reserve : reserves) {
            Map<String, String> activity = new HashMap<>();
            activity.put("time", reserve.getCreateTime() != null ? reserve.getCreateTime().format(formatter) : "");
            activity.put("type", "预约");
            activity.put("description", reserve.getContactPerson() + "预约了" +
                (reserve.getReserveTime() != null ? reserve.getReserveTime().format(formatter) : "") +
                "参观宠物(ID:" + reserve.getPetId() + ")");
            activities.add(activity);
        }

        // 待审核的领养申请
        LambdaQueryWrapper<AdoptApply> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(AdoptApply::getAuditStatus, "待审核")
                      .orderByDesc(AdoptApply::getApplyTime)
                      .last("LIMIT 3");
        List<AdoptApply> pendingApplies = adoptApplyService.list(pendingWrapper);
        for (AdoptApply apply : pendingApplies) {
            Map<String, String> activity = new HashMap<>();
            activity.put("time", apply.getApplyTime() != null ? apply.getApplyTime().format(formatter) : "");
            activity.put("type", "申请");
            activity.put("description", "收到新的领养申请，用户(ID:" + apply.getUserId() + ")申请领养宠物(ID:" + apply.getPetId() + ")，待审核");
            activities.add(activity);
        }

        // 最近登记的宠物
        LambdaQueryWrapper<Pet> petWrapper = new LambdaQueryWrapper<>();
        petWrapper.orderByDesc(Pet::getCreateTime)
                  .last("LIMIT 3");
        List<Pet> pets = petService.list(petWrapper);
        for (Pet pet : pets) {
            Map<String, String> activity = new HashMap<>();
            activity.put("time", pet.getCreateTime() != null ? pet.getCreateTime().format(formatter) : "");
            activity.put("type", "登记");
            activity.put("description", "新登记了一只名为[" + pet.getPetName() + "]的宠物");
            activities.add(activity);
        }

        // 按时间倒序排列，取前10条
        activities.sort((a, b) -> {
            String timeA = a.getOrDefault("time", "");
            String timeB = b.getOrDefault("time", "");
            return timeB.compareTo(timeA);
        });

        if (activities.size() > 10) {
            activities = activities.subList(0, 10);
        }

        return Result.success(activities);
    }

    /**
     * 获取宠物类型分布
     */
    @Operation(summary = "获取宠物类型分布")
    @GetMapping("/pet-type-distribution")
    public Result<List<Map<String, Object>>> getPetTypeDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();

        // 按品种类型分组统计
        List<Pet> allPets = petService.list();
        Map<String, Long> breedTypeCount = allPets.stream()
                .filter(p -> p.getBreedType() != null)
                .collect(Collectors.groupingBy(Pet::getBreedType, Collectors.counting()));

        for (Map.Entry<String, Long> entry : breedTypeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            distribution.add(item);
        }

        return Result.success(distribution);
    }

    /**
     * 获取领养趋势数据
     */
    @Operation(summary = "获取领养趋势数据")
    @GetMapping("/adoption-trend")
    public Result<List<Map<String, Object>>> getAdoptionTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 获取最近7天的领养趋势
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<AdoptApply> adoptWrapper = new LambdaQueryWrapper<>();
        adoptWrapper.eq(AdoptApply::getAuditStatus, "通过")
                    .ge(AdoptApply::getAuditTime, today.minusDays(6).atStartOfDay());
        List<AdoptApply> adoptApplies = adoptApplyService.list(adoptWrapper);

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = adoptApplies.stream()
                    .filter(a -> a.getAuditTime() != null && a.getAuditTime().toLocalDate().equals(date))
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }

        return Result.success(trend);
    }
}
