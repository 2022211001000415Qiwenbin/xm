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

