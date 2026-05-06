import request from '@/utils/request'

/**
 * 获取预约列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页
 * @param {number} params.size - 每页数量
 * @param {string} params.realName - 预约人姓名（可选）
 * @param {string} params.reserveStatus - 预约状态（可选）
 */
export function getAppointmentPage(params) {
  return request({
    url: '/pet-reserve/page',
    method: 'get',
    params
  })
}

/**
 * 提交预约
 * @param {Object} data - 预约信息
 */
export function submitAppointment(data) {
  return request({
    url: '/pet-reserve/submit',
    method: 'post',
    data
  })
}

/**
 * 更新预约信息
 * @param {Object} data - 预约信息
 */
export function updateAppointment(data) {
  return request({
    url: '/pet-reserve/update',
    method: 'post',
    data
  })
}

/**
 * 删除预约
 * @param {number} id - 预约ID
 */
export function deleteAppointment(id) {
  return request({
    url: `/pet-reserve/delete/${id}`,
    method: 'post'
  })
}

/**
 * 获取预约详情
 * @param {number} id - 预约ID
 */
export function getAppointmentDetail(id) {
  return request({
    url: `/pet-reserve/detail/${id}`,
    method: 'get'
  })
}

/**
 * 确认预约
 * @param {Object} data - 确认信息
 * @param {number} data.reserveId - 预约ID
 * @param {number} data.confirmAdmin - 确认管理员ID
 */
export function confirmAppointment(data) {
  return request({
    url: '/pet-reserve/confirm',
    method: 'post',
    data
  })
}

/**
 * 完成预约
 * @param {Object} data - 完成信息
 * @param {number} data.reserveId - 预约ID
 */
export function completeAppointment(id) {
  return request({
    url: `/pet-reserve/complete/${id}`,
    method: 'post'
  })
}

/**
 * 取消预约
 * @param {Object} data - 取消信息
 * @param {number} data.id - 预约ID
 * @param {string} data.cancelReason - 取消原因
 * @param {string} data.cancelPerson - 取消操作人
 */
export function cancelAppointment(data) {
  return request({
    url: '/pet-reserve/cancel',
    method: 'post',
    params: data
  })
}
