import request from '@/utils/request'

/**
 * 课程报名API
 */

// 获取课程报名列表
export function getCourseEnrollments(params) {
  return request({
    url: '/api/v1/course-enrollments',
    method: 'get',
    params
  })
}

// 获取单个课程报名
export function getCourseEnrollmentById(id) {
  return request({
    url: `/api/v1/course-enrollments/${id}`,
    method: 'get'
  })
}

// 创建课程报名
export function createCourseEnrollment(data) {
  return request({
    url: '/api/v1/course-enrollments',
    method: 'post',
    data
  })
}

// 取消课程报名
export function cancelCourseEnrollment(id) {
  return request({
    url: `/api/v1/course-enrollments/${id}/cancel`,
    method: 'put'
  })
}

// 更新报名状态
export function updateEnrollmentStatus(id, status) {
  return request({
    url: `/api/v1/course-enrollments/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取我的报名列表
export function getMyEnrollments(params) {
  return request({
    url: '/api/v1/course-enrollments/my',
    method: 'get',
    params
  })
}







