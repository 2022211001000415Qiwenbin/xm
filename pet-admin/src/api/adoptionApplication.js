import request from '@/utils/request'

/**
 * 获取领养申请列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页
 * @param {number} params.size - 每页数量
 * @param {string} params.realName - 申请人姓名（可选）
 * @param {string} params.auditStatus - 审核状态（可选）
 */
export function getAdoptionApplicationPage(params) {
  return request({
    url: '/adopt-apply/page',
    method: 'get',
    params
  })
}

/**
 * 提交领养申请
 * @param {Object} data - 申请信息
 */
export function submitAdoptionApplication(data) {
  return request({
    url: '/adopt-apply/submit',
    method: 'post',
    data
  })
}

/**
 * 更新领养申请信息
 * @param {Object} data - 申请信息
 */
export function updateAdoptionApplication(data) {
  return request({
    url: '/adopt-apply/update',
    method: 'post',
    data
  })
}

/**
 * 删除领养申请
 * @param {number} id - 申请ID
 */
export function deleteAdoptionApplication(id) {
  return request({
    url: `/adopt-apply/delete/${id}`,
    method: 'post'
  })
}

/**
 * 获取领养申请详情
 * @param {number} id - 申请ID
 */
export function getAdoptionApplicationDetail(id) {
  return request({
    url: `/adopt-apply/detail/${id}`,
    method: 'get'
  })
}

/**
 * 审核领养申请
 * @param {Object} data - 审核信息
 * @param {number} data.applyId - 申请ID
 * @param {string} data.auditStatus - 审核状态
 * @param {number} data.auditAdmin - 审核管理员ID
 * @param {string} data.auditRemark - 审核备注
 */
export function reviewAdoptionApplication(data) {
  return request({
    url: '/adopt-apply/review',
    method: 'post',
    data
  })
}

/**
 * 完成领养申请
 * @param {number} id - 申请ID
 */
export function completeAdoptionApplication(id) {
  return request({
    url: `/adopt-apply/complete/${id}`,
    method: 'post'
  })
}

/**
 * 取消领养申请
 * @param {number} id - 申请ID
 */
export function cancelAdoptionApplication(id) {
  return request({
    url: `/adopt-apply/cancel/${id}`,
    method: 'post'
  })
}

/**
 * 添加回访记录
 * @param {Object} data - 回访信息
 * @param {number} data.applicationId - 申请ID
 * @param {string} data.content - 回访内容
 * @param {string} data.visitTime - 回访时间
 */
export function addReturnVisit(data) {
  return request({
    url: '/adoption-application/return-visit',
    method: 'post',
    data
  })
}
