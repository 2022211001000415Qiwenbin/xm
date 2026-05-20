
import request from '@/utils/request'

/**
 * 管理员发布知识文章
 */
export function addKnowledge(data) {
  return request({
    url: '/pet-knowledge/add',
    method: 'post',
    data
  })
}

/**
 * 管理员更新知识文章
 */
export function updateKnowledge(data) {
  return request({
    url: '/pet-knowledge/update',
    method: 'post',
    data
  })
}

/**
 * 管理员删除知识文章
 */
export function deleteKnowledge(id) {
  return request({
    url: `/pet-knowledge/delete/${id}`,
    method: 'post'
  })
}

/**
 * 管理员分页查询知识文章
 */
export function getKnowledgePage(params) {
  return request({
    url: '/pet-knowledge/page',
    method: 'get',
    params
  })
}

/**
 * 用户端获取知识文章列表
 */
export function getKnowledgeList(params) {
  return request({
    url: '/pet-knowledge/list',
    method: 'get',
    params
  })
}

/**
 * 获取知识文章详情
 * @param {boolean} noCount - 管理员查看传true，不增加浏览量
 */
export function getKnowledgeDetail(id, noCount = false) {
  return request({
    url: `/pet-knowledge/detail/${id}`,
    method: 'get',
    params: noCount ? { noCount: true } : {}
  })
}

/**
 * 用户提交评论
 */
export function addComment(data) {
  return request({
    url: '/pet-knowledge/comment/add',
    method: 'post',
    data
  })
}

/**
 * 获取文章评论列表
 */
export function getCommentList(knowledgeId) {
  return request({
    url: `/pet-knowledge/comment/list/${knowledgeId}`,
    method: 'get'
  })
}

/**
 * 删除评论
 */
export function deleteComment(id) {
  return request({
    url: `/pet-knowledge/comment/delete/${id}`,
    method: 'post'
  })
}
