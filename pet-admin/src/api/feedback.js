
import request from '@/utils/request'

/**
 * 用户提交反馈
 */
export function submitFeedback(data) {
  return request({
    url: '/feedback/submit',
    method: 'post',
    data
  })
}

/**
 * 用户查询自己的反馈列表
 */
export function getFeedbackByUser(userId, params) {
  return request({
    url: `/feedback/listByUser/${userId}`,
    method: 'get',
    params
  })
}

/**
 * 管理员分页查询反馈列表
 */
export function getFeedbackPage(params) {
  return request({
    url: '/feedback/page',
    method: 'get',
    params
  })
}

/**
 * 管理员回复反馈
 */
export function replyFeedback(data) {
  return request({
    url: '/feedback/reply',
    method: 'post',
    data
  })
}

/**
 * 管理员更新反馈状态
 */
export function updateFeedbackStatus(data) {
  return request({
    url: '/feedback/updateStatus',
    method: 'post',
    data
  })
}

/**
 * 删除反馈
 */
export function deleteFeedback(id) {
  return request({
    url: `/feedback/delete/${id}`,
    method: 'post'
  })
}
