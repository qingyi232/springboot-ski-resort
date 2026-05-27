import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  
  // 安全地解析 userInfo
  let parsedUserInfo = {}
  try {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo && storedUserInfo !== 'undefined' && storedUserInfo !== 'null') {
      parsedUserInfo = JSON.parse(storedUserInfo)
    }
  } catch (error) {
    console.error('解析用户信息失败，已清空:', error)
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }
  const userInfo = ref(parsedUserInfo)

  // 登录状态
  const isLoggedIn = computed(() => {
    return !!token.value
  })

  // 用户角色
  const userRole = computed(() => {
    return userInfo.value?.role || ''
  })

  // 登录
  const login = async (username, password) => {
    try {
      const res = await loginApi({ username, password })
      if (res.code === 200) {
        token.value = res.data.token
        userInfo.value = res.data.userInfo
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const res = await getUserInfo()
      if (res.code === 200) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    userRole,
    login,
    logout,
    fetchUserInfo
  }
})



