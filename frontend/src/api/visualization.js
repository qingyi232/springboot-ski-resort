import request from '@/utils/request'

/**
 * 数据可视化API
 */

// 获取大屏统计数据
export function getDashboardData() {
  return request({
    url: '/api/v1/visualization/dashboard',
    method: 'get'
  })
}






