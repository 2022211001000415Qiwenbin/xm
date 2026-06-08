
package com.qwb.petmanage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qwb.petmanage.entity.Feedback;

public interface FeedbackService extends IService<Feedback> {

    /**
     * 用户提交反馈
     * @param feedback 反馈信息
     */
    void submitFeedback(Feedback feedback);

    /**
     * 用户查询自己的反馈列表
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Feedback> pageByUser(Long userId, Integer current, Integer size);

    /**
     * 管理员分页查询反馈列表（含用户名）
     * @param current 当前页
     * @param size 每页大小
     * @param status 状态
     * @param title 标题
     * @return 分页结果
     */
    Page<Feedback> pageList(Integer current, Integer size, String status, String title);

    /**
     * 管理员回复反馈
     * @param feedbackId 反馈ID
     * @param reply 回复内容
     * @param replyAdminId 回复管理员ID
     */
    void replyFeedback(Long feedbackId, String reply, Long replyAdminId);

    /**
     * 更新反馈状态
     * @param feedbackId 反馈ID
     * @param status 状态
     */
    void updateFeedbackStatus(Long feedbackId, String status);
}
