import request from '@/utils/request'

export const getRentalOrderList = (params) => {
  return request({ url: '/api/v1/rental-order/page', method: 'get', params })
}

export const getRentalOrderById = (id) => {
  return request({ url: `/api/v1/rental-order/${id}`, method: 'get' })
}

export const getRentalOrderByOrderNo = (orderNo) => {
  return request({ url: `/api/v1/rental-order/orderNo/${orderNo}`, method: 'get' })
}

export const addRentalOrder = (data) => {
  return request({ url: '/api/v1/rental-order', method: 'post', data })
}

export const updateRentalOrder = (data) => {
  return request({ url: '/api/v1/rental-order', method: 'put', data })
}

export const deleteRentalOrder = (id) => {
  return request({ url: `/api/v1/rental-order/${id}`, method: 'delete' })
}

export const payOrder = (id, paymentMethod) => {
  return request({ url: `/api/v1/rental-order/${id}/pay`, method: 'put', params: { paymentMethod } })
}

export const returnOrder = (id) => {
  return request({ url: `/api/v1/rental-order/${id}/return`, method: 'put' })
}

export const cancelOrder = (id) => {
  return request({ url: `/api/v1/rental-order/${id}/cancel`, method: 'put' })
}

// 别名导出，兼容不同命名
export const createRentalOrder = addRentalOrder
export const cancelRentalOrder = cancelOrder
export const returnRentalOrder = returnOrder
export const getRentalOrders = getRentalOrderList

export const getUserOrders = (userId) => {
  return request({ url: `/api/v1/rental-order/list/user/${userId}`, method: 'get' })
}



