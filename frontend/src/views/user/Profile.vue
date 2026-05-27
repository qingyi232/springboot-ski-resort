<template>
  <div class="profile-page">
    <!-- 顶部Banner -->
    <div class="banner">
        <div class="banner-content">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userInfo.avatar" :key="userInfo.avatar">
              {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) }}
            </el-avatar>
            <input
              ref="avatarInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleAvatarChange"
            />
            <el-button text @click="uploadAvatar" class="upload-btn">
              <el-icon><Camera /></el-icon>
              更换头像
            </el-button>
          </div>
        <div class="user-info-section">
          <h1 class="user-name">{{ userInfo.realName || userInfo.username }}</h1>
          <div class="user-meta">
            <el-tag :type="getRoleType(userInfo.role)" size="large">
              {{ getRoleText(userInfo.role) }}
            </el-tag>
            <span class="join-time">
              <el-icon><Clock /></el-icon>
              加入于 {{ formatDate(userInfo.createTime) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <div class="stat-card" @click="userInfo.role === 'user' ? $router.push('/my-orders') : null" :style="{ cursor: userInfo.role === 'user' ? 'pointer' : 'default' }">
        <div class="stat-icon" style="background: linear-gradient(135deg, #8c8c8c 0%, #595959 100%);">
          <el-icon :size="32"><ShoppingBag /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <div class="stat-label">{{ userInfo.role === 'coach' ? '总订单' : '我的订单' }}</div>
        </div>
      </div>

      <div class="stat-card" @click="$router.push(userInfo.role === 'coach' ? '/admin/coach/bookings' : '/my-bookings')">
        <div class="stat-icon" style="background: linear-gradient(135deg, #a6a6a6 0%, #737373 100%);">
          <el-icon :size="32"><Calendar /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalBookings }}</div>
          <div class="stat-label">{{ userInfo.role === 'coach' ? '总预约' : '我的预约' }}</div>
        </div>
      </div>

      <div class="stat-card" @click="userInfo.role === 'coach' ? $router.push('/admin/coach/my-courses') : null" :style="{ cursor: userInfo.role === 'coach' ? 'pointer' : 'default' }">
        <div class="stat-icon" style="background: linear-gradient(135deg, #bfbfbf 0%, #8c8c8c 100%);">
          <el-icon :size="32"><TrophyBase /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalCourses }}</div>
          <div class="stat-label">{{ userInfo.role === 'coach' ? '总课程' : '报名课程' }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #d9d9d9 0%, #a6a6a6 100%);">
          <el-icon :size="32"><Coin /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.totalSpent }}</div>
          <div class="stat-label">{{ userInfo.role === 'coach' ? '总收入' : '累计消费' }}</div>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <el-tabs v-model="activeTab" class="profile-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            :model="profileForm"
            :rules="profileRules"
            ref="profileFormRef"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="profileForm.birthday"
                type="date"
                placeholder="选择生日"
                style="width: 100%;"
              />
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
              <el-input 
                v-model="profileForm.idCard" 
                placeholder="请输入18位身份证号（选填）" 
                maxlength="18"
                clearable
              />
              <template #extra>
                <span style="color: #909399; font-size: 12px;">格式：18位数字，最后一位可以是X，例如：110101199001011234</span>
              </template>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile" :loading="updating">
                保存修改
              </el-button>
              <el-button @click="resetProfileForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="120px"
            class="password-form"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（6-20位）"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="changingPassword">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 账号安全 -->
        <el-tab-pane label="账号安全" name="security">
          <div class="security-items">
            <div class="security-item">
              <div class="security-left">
                <el-icon :size="24" color="#67c23a"><Lock /></el-icon>
                <div class="security-info">
                  <div class="security-title">登录密码</div>
                  <div class="security-desc">定期更换密码，保护账号安全</div>
                </div>
              </div>
              <el-button @click="activeTab = 'password'">修改</el-button>
            </div>

            <div class="security-item">
              <div class="security-left">
                <el-icon :size="24" color="#409eff"><Iphone /></el-icon>
                <div class="security-info">
                  <div class="security-title">手机绑定</div>
                  <div class="security-desc">
                    {{ userInfo.phone ? `已绑定：${hidePhone(userInfo.phone)}` : '未绑定' }}
                  </div>
                </div>
              </div>
              <el-button @click="activeTab = 'basic'">{{ userInfo.phone ? '修改' : '绑定' }}</el-button>
            </div>

            <div class="security-item">
              <div class="security-left">
                <el-icon :size="24" color="#e6a23c"><Message /></el-icon>
                <div class="security-info">
                  <div class="security-title">邮箱绑定</div>
                  <div class="security-desc">
                    {{ userInfo.email ? `已绑定：${hideEmail(userInfo.email)}` : '未绑定' }}
                  </div>
                </div>
              </div>
              <el-button @click="activeTab = 'basic'">{{ userInfo.email ? '修改' : '绑定' }}</el-button>
            </div>

            <div class="security-item">
              <div class="security-left">
                <el-icon :size="24" color="#f56c6c"><Warning /></el-icon>
                <div class="security-info">
                  <div class="security-title">账号状态</div>
                  <div class="security-desc">
                    {{ userInfo.status === 1 ? '正常' : '已禁用' }}
                  </div>
                </div>
              </div>
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                {{ userInfo.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Camera, Clock, ShoppingBag, Calendar, TrophyBase, Coin,
  Lock, Iphone, Message, Warning
} from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, changeUserPassword } from '@/api/user'
import { formatDate } from '@/utils/performance'
import { getAdminStatistics, getCoachStatistics } from '@/api/statistics'
import { getSalesOrdersByUserId } from '@/api/sales-order'
import { getBookingsByUserId } from '@/api/coach-booking'
import { getVenueBookingsByUserId } from '@/api/venue-booking'
import { uploadImage } from '@/api/upload'
import { useUserStore } from '@/stores/user'

// 获取用户store和路由
const userStore = useUserStore()
const router = useRouter()

// 当前标签页
const activeTab = ref('basic')

// 头像上传input引用
const avatarInput = ref(null)

// 用户信息
const userInfo = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  gender: null,
  birthday: null,
  idCard: '',
  role: '',
  status: 1,
  createTime: ''
})

// 统计数据
const stats = ref({
  totalOrders: 0,
  totalBookings: 0,
  totalCourses: 0,
  totalSpent: 0
})

// 基本信息表单
const profileFormRef = ref(null)
const profileForm = reactive({
  realName: '',
  phone: '',
  email: '',
  gender: null,
  birthday: null,
  idCard: ''
})

const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  idCard: [
    { 
      validator: (rule, value, callback) => {
        if (!value || value === '') {
          // 身份证号为空时，允许通过（可选字段）
          callback()
        } else if (!/^\d{17}[\dXx]$/.test(value)) {
          // 有值但格式不正确
          callback(new Error('身份证号必须是18位（17位数字+1位数字或X），例如：110101199001011234'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

const updating = ref(false)

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const changingPassword = ref(false)

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await getUserInfo()
    if (response.code === 200) {
      userInfo.value = response.data
      
      // 处理头像URL：如果是相对路径，拼接后端地址
      if (userInfo.value.avatar && userInfo.value.avatar.startsWith('/uploads')) {
        userInfo.value.avatar = `http://localhost:8080${userInfo.value.avatar}`
      }
      
      // ✅ 同时更新全局store
      userStore.userInfo = { ...userStore.userInfo, ...userInfo.value }
      localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      
      // 填充表单
      Object.assign(profileForm, {
        realName: response.data.realName,
        phone: response.data.phone,
        email: response.data.email,
        gender: response.data.gender,
        birthday: response.data.birthday,
        idCard: response.data.idCard
      })
      
      // 加载真实统计数据
      loadStatistics(response.data)
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// 加载统计数据
const loadStatistics = async (userData) => {
  try {
    console.log('=== 加载个人中心统计数据 ===')
    console.log('用户角色:', userData.role)
    console.log('用户ID:', userData.id)
    
    if (userData.role === 'coach') {
      // 教练端：加载教练统计数据
      const statsRes = await getCoachStatistics(userData.id)
      console.log('教练统计响应:', statsRes)
      
      if (statsRes.code === 200) {
        const data = statsRes.data
        stats.value = {
          totalOrders: 0, // 教练没有订单概念
          totalBookings: data.totalBookings || 0, // 总预约数
          totalCourses: data.totalCourses || 0, // 总课程数
          totalSpent: data.totalIncome || 0 // 教练显示总收入
        }
        console.log('教练统计数据:', stats.value)
      }
    } else if (userData.role === 'user') {
      // 普通用户：加载订单、预约统计
      let totalOrders = 0
      let totalBookings = 0
      let totalSpent = 0
      
      // 获取销售订单
      try {
        const ordersRes = await getSalesOrdersByUserId(userData.id)
        if (ordersRes.code === 200) {
          const orders = ordersRes.data || []
          totalOrders = orders.length
          totalSpent = orders.reduce((sum, order) => sum + (parseFloat(order.actualAmount) || 0), 0)
          console.log('销售订单数:', totalOrders, '总消费:', totalSpent)
        }
      } catch (error) {
        console.error('加载销售订单失败:', error)
      }
      
      // 获取教练预约
      try {
        const coachBookingsRes = await getBookingsByUserId(userData.id)
        if (coachBookingsRes.code === 200) {
          const coachBookings = coachBookingsRes.data || []
          totalBookings += coachBookings.length
          totalSpent += coachBookings.reduce((sum, booking) => sum + (parseFloat(booking.totalAmount) || 0), 0)
          console.log('教练预约数:', coachBookings.length)
        }
      } catch (error) {
        console.error('加载教练预约失败:', error)
      }
      
      // 获取场地预订
      try {
        const venueBookingsRes = await getVenueBookingsByUserId(userData.id)
        if (venueBookingsRes.code === 200) {
          const venueBookings = venueBookingsRes.data || []
          totalBookings += venueBookings.length
          totalSpent += venueBookings.reduce((sum, booking) => sum + (parseFloat(booking.totalAmount) || 0), 0)
          console.log('场地预订数:', venueBookings.length)
        }
      } catch (error) {
        console.error('加载场地预订失败:', error)
      }
      
      stats.value = {
        totalOrders,
        totalBookings,
        totalCourses: 0, // 用户端暂不统计课程数
        totalSpent: Math.round(totalSpent)
      }
      console.log('用户统计数据:', stats.value)
    } else if (userData.role === 'admin') {
      // 管理员：加载全局统计
      const statsRes = await getAdminStatistics()
      if (statsRes.code === 200) {
        const data = statsRes.data
        stats.value = {
          totalOrders: data.orderCount || 0,
          totalBookings: data.bookingCount || 0,
          totalCourses: data.courseCount || 0,
          totalSpent: data.totalIncome || 0
        }
        console.log('管理员统计数据:', stats.value)
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 失败时保持默认值0
  }
}

// 上传头像 - 支持文件上传和URL两种方式
const uploadAvatar = async () => {
  try {
    const { value: choice } = await ElMessageBox.confirm(
      '请选择头像更新方式：\n\n' +
      '💾 本地上传：从电脑选择图片文件上传（支持jpg、png、gif等格式，最大5MB）\n\n' +
      '🌐 URL地址：直接输入在线图片地址（支持http://或https://开头的链接）',
      '更换头像',
      {
        distinguishCancelAndClose: true,
        confirmButtonText: '💾 本地上传',
        cancelButtonText: '🌐 URL地址',
        type: 'info',
        dangerouslyUseHTMLString: false,
        customClass: 'avatar-upload-dialog'
      }
    )
    // 用户点击"本地上传"
    avatarInput.value.click()
  } catch (action) {
    if (action === 'cancel') {
      // 用户点击"URL地址"
      showUrlInput()
    }
    // 用户点击关闭按钮，不做任何操作
  }
}

// 显示URL输入框
const showUrlInput = async () => {
  try {
    const { value: avatarUrl } = await ElMessageBox.prompt(
      '请输入头像图片URL地址（支持http://或https://开头的图片链接）\n\n💡 推荐使用免费头像服务：\n• Pravatar: https://i.pravatar.cc/300?img=1\n• UI Avatars: https://ui-avatars.com/api/?name=YourName&size=200\n• DiceBear: https://api.dicebear.com/7.x/avataaars/svg?seed=你的名字\n\n✨ 或者使用您自己的图片链接',
      '输入头像URL',
      {
        confirmButtonText: '✅ 确定',
        cancelButtonText: '❌ 取消',
        inputPattern: /^https?:\/\/.+/,
        inputErrorMessage: '⚠️ 请输入有效的URL地址（必须以http://或https://开头）',
        inputPlaceholder: 'https://example.com/avatar.jpg',
        inputValue: 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + (userInfo.value.realName || userInfo.value.username),
        customClass: 'avatar-url-dialog'
      }
    )

    if (avatarUrl && avatarUrl.trim()) {
      await updateAvatarUrl(avatarUrl.trim())
    }
  } catch (error) {
    // 用户取消
  }
}

// 通过URL更新头像
const updateAvatarUrl = async (url) => {
  try {
    const loading = ElMessage({
      message: '正在更新头像...',
      type: 'info',
      duration: 0
    })

    console.log('URL更新头像:', url)

    const updateRes = await updateUserInfo({
      avatar: url
    })

    loading.close()

    if (updateRes.code === 200) {
      // 立即更新显示 - 添加时间戳防止缓存
      const newAvatar = url.includes('?') ? url : url + '?t=' + Date.now()
      userInfo.value = { ...userInfo.value, avatar: newAvatar }
      
      // ✅ 同时更新全局store，让右上角头像也更新
      userStore.userInfo.avatar = newAvatar
      localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      
      ElMessage.success('头像更新成功！')
      console.log('✅ 头像已更新为:', newAvatar)
      console.log('✅ 全局store已更新')
      
      // 1秒后重新加载用户信息以同步数据库状态
      setTimeout(async () => {
        await loadUserInfo()
      }, 1000)
    } else {
      ElMessage.error(updateRes.message || '头像更新失败')
    }
  } catch (error) {
    console.error('更新头像失败:', error)
    ElMessage.error('更新失败，请重试')
  }
}

// 处理本地文件上传
const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }

  try {
    const loading = ElMessage({
      message: '正在上传头像...',
      type: 'info',
      duration: 0
    })
    
    // 上传图片到服务器
    console.log('=== 开始上传图片 ===')
    console.log('文件名:', file.name)
    console.log('文件大小:', file.size)
    
    const uploadRes = await uploadImage(file)
    
    console.log('=== 上传响应 ===')
    console.log('完整响应:', uploadRes)
    console.log('响应code:', uploadRes.code)
    console.log('响应data:', uploadRes.data)
    console.log('响应message:', uploadRes.message)
    
    if (uploadRes.code === 200) {
      // 获取上传后的图片URL
      let imageUrl = uploadRes.data
      
      console.log('原始图片URL:', imageUrl)
      console.log('URL类型:', typeof imageUrl)
      
      if (!imageUrl) {
        loading.close()
        ElMessage.error('上传失败：未获取到图片URL')
        console.error('错误：uploadRes.data 为空')
        return
      }
      
      // 如果是相对路径，拼接后端地址
      if (imageUrl.startsWith('/uploads')) {
        imageUrl = `http://localhost:8080${imageUrl}`
        console.log('拼接后的URL:', imageUrl)
      }
      
      console.log('最终图片URL:', imageUrl)
      
      // 更新用户头像到数据库
      console.log('=== 更新用户头像 ===')
      const updateRes = await updateUserInfo({
        avatar: imageUrl
      })
      
      console.log('更新响应:', updateRes)
      
      loading.close()
      
      if (updateRes.code === 200) {
        // 立即更新显示
        const newAvatar = imageUrl + '?t=' + Date.now()
        userInfo.value = { ...userInfo.value, avatar: newAvatar }
        
        // ✅ 同时更新全局store，让右上角头像也更新
        userStore.userInfo.avatar = newAvatar
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
        
        ElMessage.success('头像更新成功！')
        console.log('✅ 头像已更新为:', newAvatar)
        console.log('✅ 全局store已更新')
        
        // 1秒后重新加载用户信息
        setTimeout(async () => {
          await loadUserInfo()
        }, 1000)
      } else {
        ElMessage.error(updateRes.message || '头像更新失败')
      }
    } else {
      loading.close()
      ElMessage.error(uploadRes.message || '图片上传失败')
    }
  } catch (error) {
    console.error('❌ 上传头像失败:', error)
    console.error('错误详情:', error.response || error.message)
    ElMessage.error('上传失败：' + (error.message || '请重试'))
  } finally {
    // 清空input，允许重新选择同一文件
    event.target.value = ''
  }
}

// 获取角色类型
const getRoleType = (role) => {
  const typeMap = {
    'admin': 'danger',
    'coach': 'warning',
    'staff': 'info',
    'user': 'success'
  }
  return typeMap[role] || 'info'
}

// 获取角色文本
const getRoleText = (role) => {
  const textMap = {
    'admin': '管理员',
    'coach': '教练',
    'staff': '工作人员',
    'user': '普通用户'
  }
  return textMap[role] || role
}

// 隐藏手机号
const hidePhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 隐藏邮箱
const hideEmail = (email) => {
  if (!email) return ''
  return email.replace(/(.{2}).*(@.*)/, '$1***$2')
}

// 更新个人信息
const updateProfile = async () => {
  try {
    // 验证表单
    await profileFormRef.value.validate()
    
    updating.value = true
    
    console.log('=== 提交更新 ===')
    console.log('表单数据:', profileForm)
    
    const response = await updateUserInfo(profileForm)
    
    console.log('更新响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('保存成功！')
      loadUserInfo() // 重新加载
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('❌ 更新失败:', error)
      
      // 如果是表单验证错误，提示用户
      if (typeof error === 'object' && !error.message) {
        ElMessage.error('请检查表单填写是否正确（红色标注的字段）')
      } else {
        ElMessage.error(error.message || '更新失败，请重试')
      }
    }
  } finally {
    updating.value = false
  }
}

// 重置个人信息表单
const resetProfileForm = () => {
  Object.assign(profileForm, {
    realName: userInfo.value.realName,
    phone: userInfo.value.phone,
    email: userInfo.value.email,
    gender: userInfo.value.gender,
    birthday: userInfo.value.birthday,
    idCard: userInfo.value.idCard
  })
}

// 修改密码
const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    changingPassword.value = true
    
    const response = await changeUserPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.code === 200) {
      ElMessage.success('密码修改成功！请重新登录')
      resetPasswordForm()
      setTimeout(() => {
        userStore.logout()
        router.push('/login')
      }, 1500)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '修改密码失败')
    }
  } finally {
    changingPassword.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordFormRef.value?.resetFields()
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

// 组件挂载
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #fafafa;
}

/* Banner - 简约灰色系 */
.banner {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 60px 20px;
  color: #262626;
  position: relative;
  border-bottom: 1px solid #e0e0e0;
}

.banner-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 32px;
  position: relative;
  z-index: 1;
}

.avatar-section {
  position: relative;
}

.avatar-section :deep(.el-avatar) {
  border: 3px solid #d9d9d9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  background: #fafafa;
}

.avatar-section:hover :deep(.el-avatar) {
  transform: scale(1.05);
  border-color: #bfbfbf;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.upload-btn {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(38, 38, 38, 0.85);
  color: white;
  border: none;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.upload-btn:hover {
  background: rgba(0, 0, 0, 0.95);
  transform: translateX(-50%) translateY(-1px);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
}

.user-info-section {
  flex: 1;
}

.user-name {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #262626;
  letter-spacing: 0.3px;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.join-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #595959;
  font-size: 13px;
  opacity: 0.85;
}

/* 统计卡片 - 简约灰色系 */
.stats-container {
  max-width: 1200px;
  margin: -40px auto 32px;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  position: relative;
  z-index: 10;
}

.stat-card {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border-color: #d9d9d9;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.stat-card:hover .stat-icon {
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
  line-height: 1.2;
  color: #262626;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
  font-weight: 500;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.profile-tabs {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.profile-form,
.password-form {
  max-width: 600px;
  margin: 20px 0;
}

/* 账号安全 */
.security-items {
  max-width: 800px;
  margin: 20px 0;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.security-item:last-child {
  border-bottom: none;
}

.security-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.security-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.security-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.security-desc {
  font-size: 14px;
  color: #909399;
}

/* 自定义对话框样式 */
:deep(.avatar-upload-dialog .el-message-box__message) {
  white-space: pre-line;
  line-height: 1.8;
  font-size: 14px;
  color: #606266;
}

:deep(.avatar-upload-dialog .el-message-box__btns button) {
  min-width: 120px;
  font-weight: 500;
}

:deep(.avatar-url-dialog .el-message-box__message) {
  white-space: pre-line;
  line-height: 1.8;
  font-size: 13px;
  color: #606266;
  max-height: 300px;
  overflow-y: auto;
}

:deep(.avatar-url-dialog .el-input__inner) {
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .banner-content {
    flex-direction: column;
    text-align: center;
  }
  
  .user-name {
    font-size: 24px;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .stats-container {
    grid-template-columns: 1fr;
    margin-top: -20px;
  }
  
  .profile-form,
  .password-form {
    max-width: 100%;
  }
}
</style>

