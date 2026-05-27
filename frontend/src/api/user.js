import request from '@/utils/request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/api/v1/users/login',
    method: 'post',
    data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/api/v1/users/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export const getUserInfo = (id) => {
  // 如果没有传 id，获取当前用户信息
  const url = id ? `/api/v1/users/${id}` : '/api/v1/users/current'
  return request({
    url: url,
    method: 'get'
  })
}

// 获取用户列表
export const getUserList = (params) => {
  return request({
    url: '/api/v1/users/page',
    method: 'get',
    params
  })
}

// 条件查询用户
export const getUserListByCondition = (params) => {
  return request({
    url: '/api/v1/users/page/condition',
    method: 'get',
    params
  })
}

// 新增用户
export const addUser = (data) => {
  return request({
    url: '/api/v1/users',
    method: 'post',
    data
  })
}

// 更新用户
export const updateUser = (data) => {
  return request({
    url: '/api/v1/users',
    method: 'put',
    data
  })
}

// 删除用户
export const deleteUser = (id) => {
  return request({
    url: `/api/v1/users/${id}`,
    method: 'delete'
  })
}

// 修改密码
export const updatePassword = (data) => {
  return request({
    url: `/api/v1/users/${data.id}/password`,
    method: 'put',
    data
  })
}

// 修改当前用户密码
export const changeUserPassword = (data) => {
  return request({
    url: '/api/v1/users/change-password',
    method: 'put',
    data
  })
}

// 更新当前用户信息
export const updateUserInfo = (data) => {
  return request({
    url: '/api/v1/users/profile',
    method: 'put',
    data
  })
}



