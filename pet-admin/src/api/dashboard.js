import request from '@/utils/request'

/**
 * 获取统计数据
 */
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

/**
 * 获取领养趋势数据
 */
export function getAdoptionTrend(params) {
  return request({
    url: '/dashboard/adoption-trend',
    method: 'get',
    params
  })
}

/**
 * 获取宠物类型分布数据
 */
export function getPetTypeDistribution() {
  return request({
    url: '/dashboard/pet-type-distribution',
    method: 'get'
  })
}

/**
 * 获取最新动态
 */
export function getRecentActivities() {
  return request({
    url: '/dashboard/recent-activities',
    method: 'get'
  })
}
