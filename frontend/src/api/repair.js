import request from '@/utils/request'

export const createRepairOrder = (data) => {
  return request({ url: '/api/v1/repairs', method: 'post', data })
}

export const getRepairById = (id) => {
  return request({ url: `/api/v1/repairs/${id}`, method: 'get' })
}

export const getRepairsByUserId = (userId) => {
  return request({ url: `/api/v1/repairs/user/${userId}`, method: 'get' })
}

export const getAllRepairs = () => {
  return request({ url: '/api/v1/repairs/list', method: 'get' })
}

export const assignRepairStaff = (id, data) => {
  return request({ url: `/api/v1/repairs/${id}/assign`, method: 'put', data })
}

export const completeRepair = (id, data) => {
  return request({ url: `/api/v1/repairs/${id}/complete`, method: 'put', data })
}

export const updateRepairStatus = (id, status) => {
  return request({ url: `/api/v1/repairs/${id}/status/${status}`, method: 'put' })
}

export const deleteRepair = (id) => {
  return request({ url: `/api/v1/repairs/${id}`, method: 'delete' })
}
