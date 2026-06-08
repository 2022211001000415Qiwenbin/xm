
package com.qwb.petmanage.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qwb.petmanage.entity.Feedback;
import com.qwb.petmanage.mapper.FeedbackMapper;
import com.qwb.petmanage.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Autowired
    private com.qwb.petmanage.service.UserService userService;

    @Override
    public void submitFeedback(Feedback feedback) {
        feedback.setStatus("待处理");
        feedback.setCreateTime(java.time.LocalDateTime.now());
        save(feedback);
    }

    @Override
    public Page<Feedback> pageByUser(Long userId, Integer current, Integer size) {
        Page<Feedback> page = new Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Feedback> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return page(page, queryWrapper);
    }

    @Override
    public Page<Feedback> pageList(Integer current, Integer size, String status, String title) {
        Page<Feedback> page = new Page<>(current, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Feedback> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (title != null && !title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("create_time");

        Page<Feedback> result = page(page, queryWrapper);
        // 填充用户名
        for (Feedback f : result.getRecords()) {
            if (f.getUserId() != null) {
                com.qwb.petmanage.entity.User user = userService.getById(f.getUserId());
                if (user != null) {
                    f.setUserName(user.getRealName());
                }
            }
        }
        return result;
    }

    @Override
    public void replyFeedback(Long feedbackId, String reply, Long replyAdminId) {
        Feedback feedback = getById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("反馈不存在");
        }
        feedback.setReply(reply);
        feedback.setReplyAdminId(replyAdminId);
        feedback.setStatus("已处理");
        feedback.setReplyTime(java.time.LocalDateTime.now());
        updateById(feedback);
    }

    @Override
    public void updateFeedbackStatus(Long feedbackId, String status) {
        Feedback feedback = getById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("反馈不存在");
        }
        feedback.setStatus(status);
        updateById(feedback);
    }
}
