import request from '@/utils/request'

/**
 * Dashboard统计API
 */

// 获取统计概览数据
export function getDashboardOverview() {
  return request({
    url: '/api/v1/dashboard/overview',
    method: 'get'
  })
}

// 获取收入统计数据
export function getRevenueStats(startDate, endDate) {
  return request({
    url: '/api/v1/dashboard/revenue',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 获取订单统计数据
export function getOrderStats(startDate, endDate) {
  return request({
    url: '/api/v1/dashboard/orders',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 获取热门雪具排行
export function getPopularEquipment() {
  return request({
    url: '/api/v1/dashboard/popular-equipment',
    method: 'get'
  })
}

// 获取热门教练排行
export function getPopularCoaches() {
  return request({
    url: '/api/v1/dashboard/popular-coaches',
    method: 'get'
  })
}

// 获取热门课程排行
export function getPopularCourses() {
  return request({
    url: '/api/v1/dashboard/popular-courses',
    method: 'get'
  })
}







