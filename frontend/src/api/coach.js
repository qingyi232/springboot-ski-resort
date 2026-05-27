import request from '@/utils/request'

export const getCoachList = (params) => {
  return request({ url: '/api/v1/coach/page', method: 'get', params })
}

export const getCoachById = (id) => {
  return request({ url: `/api/v1/coach/${id}`, method: 'get' })
}

export const addCoach = (data) => {
  return request({ url: '/api/v1/coach', method: 'post', data })
}

export const updateCoach = (data) => {
  return request({ url: '/api/v1/coach', method: 'put', data })
}

export const deleteCoach = (id) => {
  return request({ url: `/api/v1/coach/${id}`, method: 'delete' })
}

export const getActiveCoaches = () => {
  return request({ url: '/api/v1/coach/list/active', method: 'get' })
}

export const updateCoachRating = (id, rating, totalReviews) => {
  return request({ url: `/api/v1/coach/${id}/rating`, method: 'put', params: { rating, totalReviews } })
}

// 获取所有教练（不分页）
export const getCoaches = () => {
  return request({ url: '/api/v1/coach/list', method: 'get' })
}

// 创建教练（别名）
export const createCoach = (data) => {
  return request({ url: '/api/v1/coach', method: 'post', data })
}

// 查询教练某日日程
export const getCoachSchedule = (coachId, date) => {
  return request({ url: `/api/v1/coach/${coachId}/schedule`, method: 'get', params: { date } })
}

