import request from '@/utils/request'

// 创建课程预约
export const createBooking = (data) => {
  return request({
    url: '/api/v1/bookings',
    method: 'post',
    data
  })
}

// 获取用户的预约列表
export const getUserBookings = (userId) => {
  return request({
    url: `/api/v1/bookings/user/${userId}`,
    method: 'get'
  })
}

// 获取预约详情
export const getBookingById = (id) => {
  return request({
    url: `/api/v1/bookings/${id}`,
    method: 'get'
  })
}

// 取消预约
export const cancelBooking = (id, cancelReason) => {
  return request({
    url: `/api/v1/bookings/${id}/cancel`,
    method: 'put',
    data: { cancelReason }
  })
}

// 评价预约
export const rateBooking = (id, rating, comment) => {
  return request({
    url: `/api/v1/bookings/${id}/rate`,
    method: 'put',
    data: { rating, comment }
  })
}

// 获取所有预约
export const getAllBookings = () => {
  return request({
    url: '/api/v1/bookings',
    method: 'get'
  })
}

// 确认预约（教练/管理员）
export const confirmBooking = (id) => {
  return request({
    url: `/api/v1/bookings/${id}/confirm`,
    method: 'put'
  })
}

// 完成预约（教练/管理员）
export const completeBooking = (id) => {
  return request({
    url: `/api/v1/bookings/${id}/complete`,
    method: 'put'
  })
}





