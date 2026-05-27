import request from '@/utils/request'

/**
 * 场地管理API
 */

// 获取场地列表
export function getVenues(params) {
  return request({
    url: '/api/v1/venues/page',
    method: 'get',
    params
  })
}

// 获取场地列表（别名）
export function getVenueList(params) {
  return getVenues(params)
}

// 获取场地详情
export function getVenueById(id) {
  return request({
    url: `/api/v1/venues/${id}`,
    method: 'get'
  })
}

// 创建场地（管理员）
export function createVenue(data) {
  return request({
    url: '/api/v1/venues',
    method: 'post',
    data
  })
}

// 更新场地（管理员）
export function updateVenue(id, data) {
  return request({
    url: `/api/v1/venues/${id}`,
    method: 'put',
    data
  })
}

// 删除场地（管理员）
export function deleteVenue(id) {
  return request({
    url: `/api/v1/venues/${id}`,
    method: 'delete'
  })
}

// 更新场地状态
export function updateVenueStatus(id, status) {
  return request({
    url: `/api/v1/venues/${id}/status`,
    method: 'put',
    data: { status }
  })
}
