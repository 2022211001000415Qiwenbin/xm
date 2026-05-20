
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Feedback;
import com.qwb.petmanage.entity.User;
import com.qwb.petmanage.service.FeedbackService;
import com.qwb.petmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 意见反馈控制器
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    /**
     * 用户提交反馈
     */
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Feedback feedback) {
        feedback.setStatus("待处理");
        feedbackService.save(feedback);
        return Result.success();
    }

    /**
     * 用户查询自己的反馈列表
     */
    @GetMapping("/listByUser/{userId}")
    public Result<Page<Feedback>> listByUser(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size) {
        Page<Feedback> page = new Page<>(current, size);
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return Result.success(feedbackService.page(page, queryWrapper));
    }

    /**
     * 管理员分页查询反馈列表
     */
    @GetMapping("/page")
    public Result<Page<Feedback>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String title) {
        Page<Feedback> page = new Page<>(current, size);
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (title != null && !title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("create_time");
        Page<Feedback> result = feedbackService.page(page, queryWrapper);
        // 填充用户名
        for (Feedback f : result.getRecords()) {
            if (f.getUserId() != null) {
                User user = userService.getById(f.getUserId());
                if (user != null) {
                    f.setUserName(user.getRealName());
                }
            }
        }
        return Result.success(result);
    }

    /**
     * 管理员回复反馈
     */
    @PostMapping("/reply")
    public Result<Void> reply(@RequestBody Map<String, Object> params) {
        Long feedbackId = Long.parseLong(params.get("feedbackId").toString());
        String reply = params.get("reply").toString();
        Long replyAdminId = params.get("replyAdminId") != null ? Long.parseLong(params.get("replyAdminId").toString()) : null;

        Feedback feedback = feedbackService.getById(feedbackId);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        feedback.setReply(reply);
        feedback.setReplyAdminId(replyAdminId);
        feedback.setStatus("已处理");
        feedback.setReplyTime(LocalDateTime.now());
        feedbackService.updateById(feedback);
        return Result.success();
    }

    /**
     * 管理员更新反馈状态
     */
    @PostMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestBody Map<String, String> params) {
        Long feedbackId = Long.parseLong(params.get("feedbackId"));
        String status = params.get("status");

        Feedback feedback = feedbackService.getById(feedbackId);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        feedback.setStatus(status);
        feedbackService.updateById(feedback);
        return Result.success();
    }

    /**
     * 删除反馈
     */
    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        feedbackService.removeById(id);
        return Result.success();
    }
}
