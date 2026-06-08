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

