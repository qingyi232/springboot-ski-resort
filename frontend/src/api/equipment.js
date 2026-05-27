import request from '@/utils/request'

export const getEquipmentList = (params) => {
  return request({ url: '/api/v1/equipment/page', method: 'get', params })
}

export const getEquipmentById = (id) => {
  return request({ url: `/api/v1/equipment/${id}`, method: 'get' })
}

export const addEquipment = (data) => {
  return request({ url: '/api/v1/equipment', method: 'post', data })
}

export const updateEquipment = (data) => {
  return request({ url: '/api/v1/equipment', method: 'put', data })
}

export const deleteEquipment = (id) => {
  return request({ url: `/api/v1/equipment/${id}`, method: 'delete' })
}

export const getAvailableEquipment = () => {
  return request({ url: '/api/v1/equipment/list/available', method: 'get' })
}

export const rentEquipment = (id, quantity) => {
  return request({ url: `/api/v1/equipment/${id}/rent/${quantity}`, method: 'put' })
}

export const returnEquipment = (id, quantity) => {
  return request({ url: `/api/v1/equipment/${id}/return/${quantity}`, method: 'put' })
}

export const getLowStockEquipment = (threshold) => {
  return request({ url: '/api/v1/equipment/list/lowStock', method: 'get', params: { threshold } })
}



