import request from '@/utils/request'

/**
 * 销售订单API
 */

// 获取销售订单列表
export function getSalesOrders(params) {
  return request({
    url: '/api/v1/sales-orders',
    method: 'get',
    params
  })
}

// 根据用户ID获取销售订单列表
export function getSalesOrdersByUserId(userId) {
  return request({
    url: `/api/v1/sales-orders/user/${userId}`,
    method: 'get'
  })
}

// 获取单个销售订单
export function getSalesOrderById(id) {
  return request({
    url: `/api/v1/sales-orders/${id}`,
    method: 'get'
  })
}

// 创建销售订单
export function createSalesOrder(data) {
  return request({
    url: '/api/v1/sales-orders',
    method: 'post',
    data
  })
}

// 支付销售订单
export function paySalesOrder(id, paymentMethod) {
  return request({
    url: `/api/v1/sales-orders/${id}/pay`,
    method: 'put',
    data: { paymentMethod }
  })
}

// 取消销售订单
export function cancelSalesOrder(id) {
  return request({
    url: `/api/v1/sales-orders/${id}/cancel`,
    method: 'put'
  })
}

// 发货
export function shipSalesOrder(id) {
  return request({
    url: `/api/v1/sales-orders/${id}/ship`,
    method: 'put'
  })
}

// 确认收货
export function confirmReceiveSalesOrder(id) {
  return request({
    url: `/api/v1/sales-orders/${id}/confirm-receive`,
    method: 'put'
  })
}

// 更新订单状态
export function updateSalesOrderStatus(id, status) {
  return request({
    url: `/api/v1/sales-orders/${id}/status`,
    method: 'put',
    data: { status }
  })
}


