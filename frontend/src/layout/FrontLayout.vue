<template>
  <div class="front-layout">
    <!-- 顶部导航栏 - 专业简洁版 -->
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <div class="logo-icon">⛷️</div>
          <div class="logo-content">
            <span class="logo-title">飞跃滑雪场</span>
            <span class="logo-subtitle">SKI RESORT</span>
          </div>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/home" class="nav-link">
            <span>首页</span>
          </router-link>
          <router-link to="/front-courses" class="nav-link">
            <span>滑雪课程</span>
          </router-link>
          <router-link to="/product-mall" class="nav-link">
            <span>装备商城</span>
          </router-link>
          <router-link to="/equipment-rental" class="nav-link">
            <span>雪具租赁</span>
          </router-link>
          <router-link to="/venues" class="nav-link">
            <span>场地预约</span>
          </router-link>
          <router-link to="/ai-recommendation" class="nav-link">
            <span>🤖 AI推荐</span>
          </router-link>
          <router-link to="/repair-submit" class="nav-link">
            <span>雪具报修</span>
          </router-link>
          <router-link to="/my-orders" class="nav-link">
            <span>我的订单</span>
          </router-link>
        </nav>

        <div class="header-actions">
          <button class="book-now-btn" @click="router.push('/front-courses')">
            <span class="btn-text">立即预约</span>
            <span class="btn-icon">→</span>
          </button>
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <div class="user-avatar">
              <el-avatar :size="38" :src="userStore.userInfo.avatar" :key="userStore.userInfo.avatar" :style="{ backgroundColor: '#1890ff' }">
                {{ userStore.userInfo.realName?.charAt(0) || 'U' }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled style="font-weight: 600; color: #333;">
                  {{ userStore.userInfo.realName || userStore.userInfo.username }}
                </el-dropdown-item>
                <el-dropdown-item divided command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="bookings">我的预约</el-dropdown-item>
                <el-dropdown-item divided command="logout" style="color: #ff4d4f;">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- AI智能客服 -->
    <AIChatbot />

    <!-- 底部信息 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>关于我们</h3>
            <p>飞跃滑雪场致力于提供最优质的滑雪体验</p>
          </div>
          <div class="footer-section">
            <h3>联系方式</h3>
            <p>电话：400-123-4567</p>
            <p>邮箱：info@skiresort.com</p>
          </div>
          <div class="footer-section">
            <h3>营业时间</h3>
            <p>周一至周五：9:00-21:00</p>
            <p>周末及节假日：8:00-22:00</p>
          </div>
        </div>
        <div class="footer-copyright">
          <p>© 2025 飞跃滑雪场管理系统. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import AIChatbot from '@/components/AIChatbot.vue'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/my-orders')
      break
    case 'bookings':
      router.push('/my-bookings')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.logout()
        router.push('/login')
        ElMessage.success('退出成功')
      } catch (error) {
        // 取消退出
      }
      break
  }
}
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

/* 顶部导航栏 - 专业简洁版 */
.header {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid #e8e8e8;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 50px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo:hover {
  transform: translateX(4px);
}

.logo-icon {
  font-size: 38px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.logo-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 0.5px;
  line-height: 1;
}

.logo-subtitle {
  font-size: 11px;
  font-weight: 600;
  color: #999;
  letter-spacing: 2px;
}

.nav-menu {
  display: flex;
  gap: 8px;
  flex: 1;
  justify-content: center;
  margin: 0 60px;
}

.nav-link {
  position: relative;
  color: #333;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 60%;
  height: 2px;
  background: #1890ff;
  transition: transform 0.3s ease;
}

.nav-link:hover {
  color: #1890ff;
  background: #f0f7ff;
}

.nav-link:hover::after {
  transform: translateX(-50%) scaleX(1);
}

.nav-link.router-link-active {
  color: #1890ff;
  background: #e6f4ff;
  font-weight: 600;
}

.nav-link.router-link-active::after {
  transform: translateX(-50%) scaleX(1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.book-now-btn {
  position: relative;
  padding: 12px 32px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 8px rgba(30, 41, 59, 0.15);
}

.book-now-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.book-now-btn:hover::before {
  left: 100%;
}

.book-now-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(30, 41, 59, 0.25);
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.book-now-btn:active {
  transform: translateY(0);
}

.btn-text {
  position: relative;
  z-index: 1;
}

.btn-icon {
  position: relative;
  z-index: 1;
  font-size: 16px;
  transition: transform 0.3s ease;
}

.book-now-btn:hover .btn-icon {
  transform: translateX(4px);
}

.user-dropdown {
  cursor: pointer;
}

.user-avatar {
  display: flex;
  align-items: center;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  background: #f0f0f0;
  transform: scale(1.05);
}

/* 主体内容 */
.main-content {
  flex: 1;
  width: 100%;
  margin: 0 auto;
  padding: 0;
  background: #ffffff;
}

/* 底部信息 */
.footer {
  background: #001529;
  color: #ecf0f1;
  margin-top: auto;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #fff;
}

.footer-section p {
  font-size: 14px;
  line-height: 2;
  color: #bdc3c7;
}

.footer-copyright {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 14px;
  color: #95a5a6;
}
</style>

