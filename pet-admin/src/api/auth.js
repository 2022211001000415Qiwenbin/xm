import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 刷新Token
 * @param {string} refreshToken - 刷新令牌
 */
export function refreshTokenApi(refreshToken) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data: { refreshToken }
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.realName - 真实姓名
 * @param {string} data.phone - 手机号
 * @param {string} data.idCard - 身份证号（必填）
 * @param {string} data.address - 地址（必填）
 * @param {string} data.petExperience - 养宠经历（必填）
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 * @param {string} data.realName - 真实姓名
 * @param {string} data.phone - 手机号
 * @param {string} data.idCard - 身份证号
 * @param {string} data.address - 地址
 * @param {string} data.petExperience - 养宠经历
 */
export function updateUserInfo(data) {
  return request({
    url: '/auth/update',
    method: 'post',
    data
  })
}
