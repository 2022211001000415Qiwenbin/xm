import request from '@/utils/request'

/**
 * 获取品种列表
 */
export function getBreedList() {
  return request({
    url: '/petBreed/list',
    method: 'get'
  })
}

/**
 * 新增品种
 */
export function addBreed(data) {
  return request({
    url: '/petBreed',
    method: 'post',
    data
  })
}

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
 * 新增宠物
 * @param {Object} data - 宠物信息
 */
export function addPet(data) {
  return request({
    url: '/pet/add',
    method: 'post',
    data
  })
}

/**
 * 更新宠物信息
 * @param {Object} data - 宠物信息
 */
export function updatePet(data) {
  return request({
    url: '/pet/update',
    method: 'post',
    data
  })
}

/**
 * 删除宠物
 * @param {number} id - 宠物ID
 */
export function deletePet(id) {
  return request({
    url: `/pet/delete/${id}`,
    method: 'post'
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
