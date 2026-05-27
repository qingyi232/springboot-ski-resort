<template>
  <div class="login-container">
    <!-- 左侧装饰区域 -->
    <div class="login-banner">
      <div class="banner-overlay">
        <div class="banner-content">
          <div class="logo-section">
            <div class="logo-circle">
              <el-icon :size="60"><IceCreamSquare /></el-icon>
            </div>
            <h1 class="system-title">飞跃滑雪场管理系统</h1>
            <p class="system-subtitle">Feiyue Ski Resort Management System</p>
          </div>
          <div class="features">
            <div class="feature-item" v-for="(feature, index) in features" :key="index">
              <el-icon class="feature-icon"><component :is="feature.icon" /></el-icon>
              <span>{{ feature.text }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-form-section">
      <div class="form-container">
        <div class="form-header">
          <h2 class="form-title">{{ activeTab === 'login' ? '欢迎回来' : '创建账户' }}</h2>
          <p class="form-subtitle">{{ activeTab === 'login' ? '登录您的账户以继续' : '填写信息完成注册' }}</p>
        </div>

        <el-tabs v-model="activeTab" class="login-tabs" stretch>
          <el-tab-pane label="登录" name="login">
            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              @submit.prevent="handleLogin"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  @keyup.enter="handleLogin"
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  @click="handleLogin"
                  class="submit-btn"
                >
                  <span v-if="!loading">立即登录</span>
                  <span v-else>登录中...</span>
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="注册" name="register">
            <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              class="login-form"
              @submit.prevent="handleRegister"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请确认密码"
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="phone">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  size="large"
                  clearable
                  @keyup.enter="handleRegister"
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  @click="handleRegister"
                  class="submit-btn"
                >
                  <span v-if="!loading">立即注册</span>
                  <span v-else>注册中...</span>
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, TrophyBase, Notebook, Location } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const features = [
  { icon: 'TrophyBase', text: '专业滑雪场管理' },
  { icon: 'Notebook', text: '一站式课程预约' },
  { icon: 'Location', text: '智能场地调度' }
]

const validatePasswordConfirm = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePasswordConfirm, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.login(loginForm.username, loginForm.password)
        if (success) {
          ElMessage.success('登录成功')
          // 根据用户角色跳转到不同页面
          const userRole = userStore.userInfo?.role
          if (userRole === 'user') {
            router.push('/home')
          } else if (userRole === 'admin' || userRole === 'coach' || userRole === 'staff') {
            router.push('/admin/dashboard')
          } else {
            router.push('/')
          }
        } else {
          ElMessage.error('用户名或密码错误')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register({
          username: registerForm.username,
          password: registerForm.password,
          phone: registerForm.phone
        })
        if (res.code === 200) {
          ElMessage.success('注册成功，请登录')
          activeTab.value = 'login'
          registerForm.username = ''
          registerForm.password = ''
          registerForm.confirmPassword = ''
          registerForm.phone = ''
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (error) {
        console.error('注册错误:', error)
        ElMessage.error('注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

/* 左侧装饰区域 */
.login-banner {
  flex: 1;
  position: relative;
  background: url('https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=1200&auto=format&fit=crop&q=80');
  background-size: cover;
  background-position: center;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.5) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.banner-content {
  max-width: 500px;
  color: white;
  animation: fadeInUp 0.8s ease-out;
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
}

.logo-circle {
  width: 120px;
  height: 120px;
  margin: 0 auto 30px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  animation: float 3s ease-in-out infinite;
}

.logo-circle .el-icon {
  color: white;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.system-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.5), 0 4px 24px rgba(0, 0, 0, 0.3);
  letter-spacing: 2px;
}

.system-subtitle {
  font-size: 16px;
  opacity: 0.95;
  font-weight: 300;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.features {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 16px;
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(10px);
}

.feature-icon {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.9);
}

/* 右侧表单区域 */
.login-form-section {
  width: 550px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: 
    linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, transparent 60%),
    linear-gradient(225deg, rgba(118, 75, 162, 0.10) 0%, transparent 60%),
    linear-gradient(135deg, #e8ecf4 0%, #f8f9fc 50%, #e3e8f0 100%);
  box-shadow: -10px 0 40px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

/* 装饰元素 - 超级明显版 */
.login-form-section::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, 
    rgba(102, 126, 234, 0.35) 0%, 
    rgba(102, 126, 234, 0.22) 30%, 
    rgba(102, 126, 234, 0.12) 50%,
    transparent 70%
  );
  top: -300px;
  right: -300px;
  border-radius: 50%;
  animation: pulse 6s ease-in-out infinite;
  box-shadow: 0 0 100px rgba(102, 126, 234, 0.3);
}

.login-form-section::after {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, 
    rgba(118, 75, 162, 0.30) 0%, 
    rgba(118, 75, 162, 0.18) 30%, 
    rgba(118, 75, 162, 0.10) 50%,
    transparent 70%
  );
  bottom: -250px;
  left: -250px;
  border-radius: 50%;
  animation: pulse 8s ease-in-out infinite;
  box-shadow: 0 0 80px rgba(118, 75, 162, 0.25);
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.form-container {
  width: 100%;
  max-width: 420px;
  animation: fadeIn 0.6s ease-out;
  position: relative;
  z-index: 1;
}

/* 给表单容器添加装饰线条 - 超级明显版 */
.form-container::before {
  content: '';
  position: absolute;
  width: 220px;
  height: 220px;
  border: 3px solid rgba(102, 126, 234, 0.35);
  border-top-color: rgba(102, 126, 234, 0.6);
  border-right-color: rgba(102, 126, 234, 0.5);
  border-radius: 50%;
  top: -100px;
  right: -100px;
  z-index: -1;
  animation: rotate 12s linear infinite;
  box-shadow: 0 0 30px rgba(102, 126, 234, 0.3);
}

.form-container::after {
  content: '';
  position: absolute;
  width: 160px;
  height: 160px;
  border: 3px solid rgba(118, 75, 162, 0.30);
  border-bottom-color: rgba(118, 75, 162, 0.55);
  border-left-color: rgba(118, 75, 162, 0.45);
  border-radius: 50%;
  bottom: -70px;
  left: -70px;
  z-index: -1;
  animation: rotate 10s linear infinite reverse;
  box-shadow: 0 0 25px rgba(118, 75, 162, 0.25);
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 添加更多装饰点 */
.login-form-section {
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(102, 126, 234, 0.15) 0, transparent 50px),
    radial-gradient(circle at 80% 70%, rgba(118, 75, 162, 0.15) 0, transparent 50px),
    radial-gradient(circle at 50% 50%, rgba(102, 126, 234, 0.08) 0, transparent 80px),
    linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, transparent 60%),
    linear-gradient(225deg, rgba(118, 75, 162, 0.10) 0%, transparent 60%),
    linear-gradient(135deg, #e8ecf4 0%, #f8f9fc 50%, #e3e8f0 100%);
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

/* 标题装饰线 */
.form-header::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: -10px;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.form-header::after {
  content: '';
  position: absolute;
  right: 0;
  bottom: -10px;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #764ba2, #667eea);
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(118, 75, 162, 0.4);
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% auto;
  animation: textShine 3s ease-in-out infinite;
}

@keyframes textShine {
  0%, 100% {
    background-position: 0% center;
  }
  50% {
    background-position: 100% center;
  }
}

.form-subtitle {
  font-size: 15px;
  color: #7f8c8d;
  margin: 0;
}

.login-tabs {
  margin-bottom: 30px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 30px;
  height: 48px;
  line-height: 48px;
  color: #7f8c8d;
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 600;
}

.login-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 12px 16px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea inset, 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-form :deep(.el-input__prefix) {
  font-size: 18px;
  color: #909399;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  margin-top: 12px;
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.4),
    0 0 30px rgba(102, 126, 234, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f91 100%);
  transform: translateY(-2px);
  box-shadow: 
    0 8px 30px rgba(102, 126, 234, 0.5),
    0 0 40px rgba(102, 126, 234, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-banner {
    display: none;
  }
  
  .login-form-section {
    width: 100%;
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .login-form-section {
    padding: 20px;
  }
  
  .form-container {
    max-width: 100%;
  }
  
  .form-title {
    font-size: 26px;
  }
}
</style>
