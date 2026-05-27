import request from '@/utils/request'

/**
 * 课程管理API
 */

// 获取课程列表
export function getCourses(params) {
  return request({
    url: '/api/v1/courses/page',
    method: 'get',
    params
  })
}

// 获取课程列表（别名）
export function getCourseList(params) {
  return getCourses(params)
}

// 获取热门课程
export function getHotCourses(limit = 10) {
  return request({
    url: '/api/v1/courses/list/hot',
    method: 'get',
    params: { limit }
  })
}

// 获取课程详情
export function getCourseById(id) {
  return request({
    url: `/api/v1/courses/${id}`,
    method: 'get'
  })
}

// 创建课程（管理员）
export function createCourse(data) {
  return request({
    url: '/api/v1/courses',
    method: 'post',
    data
  })
}

// 更新课程（管理员）
export function updateCourse(id, data) {
  // 确保数据中包含 id
  const courseData = {
    ...data,
    id: id
  }
  return request({
    url: '/api/v1/courses',
    method: 'put',
    data: courseData
  })
}

// 删除课程（管理员）
export function deleteCourse(id) {
  return request({
    url: `/api/v1/courses/${id}`,
    method: 'delete'
  })
}

// 更新课程状态
export function updateCourseStatus(id, status) {
  return request({
    url: `/api/v1/courses/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取课程学员列表
export function getCourseStudents(courseId, params) {
  return request({
    url: `/api/v1/courses/${courseId}/students`,
    method: 'get',
    params
  })
}

// 根据教练ID获取课程列表
export function getCoursesByCoachId(coachId) {
  return request({
    url: `/api/v1/courses/coach/${coachId}`,
    method: 'get'
  })
}
