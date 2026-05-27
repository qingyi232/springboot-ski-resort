import request from '@/utils/request'

/**
 * 统计数据API
 */

// 获取管理员统计数据
export function getAdminStatistics() {
  return request({
    url: '/api/v1/statistics/admin',
    method: 'get'
  })
}

// 获取教练统计数据
export function getCoachStatistics(coachId) {
  return request({
    url: `/api/v1/statistics/coach/${coachId}`,
    method: 'get'
  })
}






