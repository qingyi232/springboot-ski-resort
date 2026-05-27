import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 10000
})

// 是否正在跳转登录页（防止重复跳转和提示）
let isRedirectingToLogin = false

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    if (res.code !== 200) {
      // 401: 未授权
      if (res.code === 401) {
        handleUnauthorized()
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          handleUnauthorized()
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    
    return Promise.reject(error)
  }
)

// 处理401未授权（防止重复处理）
function handleUnauthorized() {
  if (isRedirectingToLogin) return
  
  isRedirectingToLogin = true
  ElMessage.error('登录已过期，请重新登录')
  
  const userStore = useUserStore()
  userStore.logout()
  router.push('/login')
  
  // 2秒后重置标志，允许下次处理
  setTimeout(() => {
    isRedirectingToLogin = false
  }, 2000)
}

export default request



