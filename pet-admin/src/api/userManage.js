import request from '@/utils/request'

/**
 * 获取用户列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页
 * @param {number} params.size - 每页数量
 * @param {string} params.realName - 用户姓名（可选）
 * @param {string} params.phone - 手机号（可选）
 */
export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 */
export function getUserDetail(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {Object} data - 用户信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.realName - 真实姓名
 * @param {string} data.phone - 手机号
 * @param {string} data.idCard - 身份证号
 * @param {string} data.address - 地址
 * @param {string} data.petExperience - 养宠经历
 */
export function addUser(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 */
export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/**
 * 重置用户密码
 * @param {Object} data - 重置信息
 * @param {number} data.userId - 用户ID
 * @param {string} data.newPassword - 新密码
 */
export function resetPassword(data) {
  return request({
    url: '/user/reset-password',
    method: 'post',
    data
  })
}
