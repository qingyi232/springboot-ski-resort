import request from '@/utils/request'

/**
 * 场地预订API
 */

// 获取所有场地预订列表
export function getVenueBookings(params) {
  return request({
    url: '/api/v1/venue-bookings',
    method: 'get',
    params
  })
}

// 根据用户ID获取场地预订列表
export function getVenueBookingsByUserId(userId) {
  return request({
    url: `/api/v1/venue-bookings/user/${userId}`,
    method: 'get'
  })
}

// 获取单个场地预订
export function getVenueBookingById(id) {
  return request({
    url: `/api/v1/venue-bookings/${id}`,
    method: 'get'
  })
}

// 创建场地预订
export function createVenueBooking(data) {
  return request({
    url: '/api/v1/venue-bookings',
    method: 'post',
    data
  })
}

// 取消场地预订
export function cancelVenueBooking(id) {
  return request({
    url: `/api/v1/venue-bookings/${id}/cancel`,
    method: 'put'
  })
}

// 更新预订状态
export function updateBookingStatus(id, status) {
  return request({
    url: `/api/v1/venue-bookings/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 更新场地预订状态（别名，保持命名一致性）
export function updateVenueBookingStatus(id, status) {
  return request({
    url: `/api/v1/venue-bookings/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 确认场地预订
export function confirmVenueBooking(id) {
  return request({
    url: `/api/v1/venue-bookings/${id}/confirm`,
    method: 'put'
  })
}

// 完成场地预订
export function completeVenueBooking(id) {
  return request({
    url: `/api/v1/venue-bookings/${id}/complete`,
    method: 'put'
  })
}

// 获取所有场地预订（管理员用）
export function getAllVenueBookings(params) {
  return request({
    url: '/api/v1/venue-bookings',
    method: 'get',
    params
  })
}
