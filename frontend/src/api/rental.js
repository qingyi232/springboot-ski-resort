import request from '@/utils/request'

/**
 * 租赁订单管理API
 */

// 获取租赁订单列表（分页）
export function getRentalOrders(params) {
  return request({
    url: '/api/v1/rental-order/page',
    method: 'get',
    params
  })
}

// 获取租赁订单详情
export function getRentalOrderById(id) {
  return request({
    url: `/api/v1/rental-order/${id}`,
    method: 'get'
  })
}

// 创建租赁订单
export function createRentalOrder(data) {
  return request({
    url: '/api/v1/rental-order',
    method: 'post',
    data
  })
}

// 更新租赁订单
export function updateRentalOrder(data) {
  return request({
    url: '/api/v1/rental-order',
    method: 'put',
    data
  })
}

// 删除租赁订单
export function deleteRentalOrder(id) {
  return request({
    url: `/api/v1/rental-order/${id}`,
    method: 'delete'
  })
}

// 支付租赁订单
export function payRentalOrder(id, paymentMethod) {
  return request({
    url: `/api/v1/rental-order/${id}/pay`,
    method: 'put',
    data: { paymentMethod }
  })
}

// 归还雪具
export function returnRentalOrder(id) {
  return request({
    url: `/api/v1/rental-order/${id}/return`,
    method: 'put'
  })
}

// 取消租赁订单
export function cancelRentalOrder(id) {
  return request({
    url: `/api/v1/rental-order/${id}/cancel`,
    method: 'put'
  })
}

// 根据用户ID获取租赁订单
export function getRentalOrdersByUserId(userId, params) {
  return request({
    url: `/api/v1/rental-order/user/${userId}`,
    method: 'get',
    params
  })
}

// 根据状态获取租赁订单
export function getRentalOrdersByStatus(status, params) {
  return request({
    url: `/api/v1/rental-order/status/${status}`,
    method: 'get',
    params
  })
}







