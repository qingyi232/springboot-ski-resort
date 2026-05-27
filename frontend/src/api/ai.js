import request from '@/utils/request'

/**
 * AI服务API
 */

// AI课程推荐
export function recommendCourses(data) {
  return request({
    url: '/api/v1/ai/recommend/courses',
    method: 'post',
    data
  })
}

// AI装备推荐
export function recommendEquipment(data) {
  return request({
    url: '/api/v1/ai/recommend/equipment',
    method: 'post',
    data
  })
}

// AI智能客服
export function chatWithAI(data) {
  return request({
    url: '/api/v1/ai/chatbot',
    method: 'post',
    data
  })
}
