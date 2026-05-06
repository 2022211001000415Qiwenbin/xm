import request from '@/utils/request'

/**
 * 获取宠物列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页
 * @param {number} params.size - 每页数量
 * @param {string} params.petName - 宠物名称（可选）
 * @param {string} params.adoptStatus - 领养状态（可选）
 */
export function getPetPage(params) {
  return request({
    url: '/pet/page',
    method: 'get',
    params
  })
}

/**
 * 获取宠物详情
 * @param {number} id - 宠物ID
 */
export function getPetDetail(id) {
  return request({
    url: `/pet/detail/${id}`,
    method: 'get'
  })
}

/**
 * 提交领养申请
 * @param {Object} data - 申请信息
 */
export function submitAdoption(data) {
  return request({
    url: '/adopt-apply/submit',
    method: 'post',
    data
  })
}

/**
 * 获取我的领养申请列表
 * @param {Object} params - 查询参数
 */
export function getMyAdoptions(params) {
  return request({
    url: '/adopt-apply/page',
    method: 'get',
    params
  })
}

/**
 * 提交预约
 * @param {Object} data - 预约信息
 */
export function submitReservation(data) {
  return request({
    url: '/pet-reserve/submit',
    method: 'post',
    data
  })
}

/**
 * 获取我的预约列表
 * @param {Object} params - 查询参数
 */
export function getMyReservations(params) {
  return request({
    url: '/pet-reserve/page',
    method: 'get',
    params
  })
}

/**
 * 取消预约
 * @param {number} id - 预约ID
 */
export function cancelReservation(id) {
  return request({
    url: `/pet-reserve/cancel/${id}`,
    method: 'post'
  })
}
