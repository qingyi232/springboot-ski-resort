import request from '@/utils/request'

/**
 * 教练预约API
 */

// 获取预约列表
export function getBookingList() {
  return request({
    url: '/api/v1/bookings',
    method: 'get'
  })
}

// 根据ID获取预约
export function getBookingById(id) {
  return request({
    url: `/api/v1/bookings/${id}`,
    method: 'get'
  })
}

// 根据用户ID获取预约列表
export function getBookingsByUserId(userId) {
  return request({
    url: `/api/v1/bookings/user/${userId}`,
    method: 'get'
  })
}

// 根据教练ID获取预约列表
export function getBookingsByCoachId(coachId) {
  return request({
    url: `/api/v1/bookings/coach/${coachId}`,
    method: 'get'
  })
}

// 根据教练用户ID获取预约列表（自动转换为教练ID）
export function getBookingsByCoachUserId(userId) {
  return request({
    url: `/api/v1/bookings/coach/user/${userId}`,
    method: 'get'
  })
}

// 创建预约
export function createCoachBooking(data) {
  return request({
    url: '/api/v1/bookings',
    method: 'post',
    data
  })
}

// 更新预约
export function updateBooking(id, data) {
  return request({
    url: `/api/v1/bookings/${id}`,
    method: 'put',
    data
  })
}

// 取消预约
export function cancelBooking(id, cancelReason) {
  return request({
    url: `/api/v1/bookings/${id}/cancel`,
    method: 'put',
    data: { cancelReason }
  })
}

// 确认预约
export function confirmBooking(id) {
  return request({
    url: `/api/v1/bookings/${id}/confirm`,
    method: 'put'
  })
}

// 完成预约
export function completeBooking(id) {
  return request({
    url: `/api/v1/bookings/${id}/complete`,
    method: 'put'
  })
}

// 评价预约
export function rateBooking(id, rating, comment) {
  return request({
    url: `/api/v1/bookings/${id}/rate`,
    method: 'put',
    data: { rating, comment }
  })
}

// 删除预约
export function deleteBooking(id) {
  return request({
    url: `/api/v1/bookings/${id}`,
    method: 'delete'
  })
}

// 更新预约状态
export function updateCoachBookingStatus(id, status) {
  return request({
    url: `/api/v1/bookings/${id}`,
    method: 'put',
    data: { status }
  })
}

// 获取所有预约（管理员用）
export function getAllBookings(params) {
  return request({
    url: '/api/v1/bookings',
    method: 'get',
    params
  })
}

// 别名导出（为了兼容性）
// 管理员使用：获取所有教练预约（支持分页和筛选）
export const getCoachBookings = getAllBookings
export const cancelCoachBooking = cancelBooking
