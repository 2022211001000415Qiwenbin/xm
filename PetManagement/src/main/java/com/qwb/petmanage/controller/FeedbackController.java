
package com.qwb.petmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qwb.petmanage.common.Result;
import com.qwb.petmanage.entity.Feedback;
import com.qwb.petmanage.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 意见反馈控制器
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 用户提交反馈
     */
    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody Feedback feedback) {
        feedbackService.submitFeedback(feedback);
        return Result.success();
    }

    /**
     * 用户查询自己的反馈列表
     */
    @GetMapping("/listByUser/{userId}")
    public Result<Page<Feedback>> listByUser(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size) {
        Page<Feedback> page = feedbackService.pageByUser(userId, current, size);
        return Result.success(page);
    }

    /**
     * 管理员分页查询反馈列表
     */
    @GetMapping("/page")
    public Result<Page<Feedback>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String title) {
        Page<Feedback> page = feedbackService.pageList(current, size, status, title);
        return Result.success(page);
    }

    /**
     * 管理员回复反馈
     */
    @PostMapping("/reply")
    public Result<Void> reply(@RequestBody Map<String, Object> params) {
        Long feedbackId = Long.parseLong(params.get("feedbackId").toString());
        String reply = params.get("reply").toString();
        Long replyAdminId = params.get("replyAdminId") != null ? Long.parseLong(params.get("replyAdminId").toString()) : null;
        feedbackService.replyFeedback(feedbackId, reply, replyAdminId);
        return Result.success();
    }

    /**
     * 管理员更新反馈状态
     */
    @PostMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestBody Map<String, String> params) {
        Long feedbackId = Long.parseLong(params.get("feedbackId"));
        String status = params.get("status");
        feedbackService.updateFeedbackStatus(feedbackId, status);
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
