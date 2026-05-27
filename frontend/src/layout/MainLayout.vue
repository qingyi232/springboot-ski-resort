<template>
  <el-container class="main-layout">
    <el-aside width="200px">
      <div class="logo">
        <el-icon><IceCreamSquare /></el-icon>
        <span>飞跃滑雪场</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/data-visualization" v-if="isAdmin">
          <el-icon><DataLine /></el-icon>
          <span>数据可视化</span>
        </el-menu-item>
        
        <!-- 管理员专属菜单 -->
        <el-menu-item index="/admin/equipment" v-if="isAdmin">
          <el-icon><Goods /></el-icon>
          <span>雪具管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/coach" v-if="isAdmin">
          <el-icon><User /></el-icon>
          <span>教练管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/product" v-if="isAdmin">
          <el-icon><ShoppingCart /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        
        <!-- 管理员专属：课程管理 -->
        <el-menu-item index="/admin/course" v-if="isAdmin">
          <el-icon><Reading /></el-icon>
          <span>课程管理</span>
        </el-menu-item>
        
        <!-- 教练端专属菜单 -->
        <el-menu-item index="/admin/coach/my-courses" v-if="isCoach">
          <el-icon><Reading /></el-icon>
          <span>我的课程</span>
        </el-menu-item>
        <el-menu-item index="/admin/coach/bookings" v-if="isCoach">
          <el-icon><Calendar /></el-icon>
          <span>学员预约</span>
        </el-menu-item>
        
        <!-- 管理员专属菜单 -->
        <el-menu-item index="/admin/venue" v-if="isAdmin">
          <el-icon><MapLocation /></el-icon>
          <span>场地管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/bookings" v-if="isAdmin">
          <el-icon><Calendar /></el-icon>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/rental-order" v-if="isAdmin">
          <el-icon><Document /></el-icon>
          <span>租赁订单</span>
        </el-menu-item>
        <el-menu-item index="/admin/sales-order" v-if="isAdmin">
          <el-icon><ShoppingCart /></el-icon>
          <span>销售订单</span>
        </el-menu-item>
        <el-menu-item index="/admin/user" v-if="isAdmin">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <!-- 工作人员专属菜单 -->
        <el-menu-item index="/admin/staff/rental" v-if="isStaff">
          <el-icon><Tickets /></el-icon>
          <span>租赁办理</span>
        </el-menu-item>
        <el-menu-item index="/admin/staff/orders" v-if="isStaff">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/staff/inventory" v-if="isStaff">
          <el-icon><Box /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/staff/venues" v-if="isStaff">
          <el-icon><MapLocation /></el-icon>
          <span>场地查看</span>
        </el-menu-item>
        <el-menu-item index="/admin/staff/repair" v-if="isStaff">
          <el-icon><SetUp /></el-icon>
          <span>维修管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo.avatar" :key="userStore.userInfo.avatar">
                {{ userStore.userInfo.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataLine, Tickets, Box, SetUp } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  const meta = route.meta
  return meta.title || '未知页面'
})

// 角色权限判断
const isAdmin = computed(() => userStore.userInfo?.role === 'admin')
const isCoach = computed(() => userStore.userInfo?.role === 'coach')
const isStaff = computed(() => userStore.userInfo?.role === 'staff')

const handleCommand = async (command) => {
  if (command === 'logout') {
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
  } else if (command === 'profile') {
    // 跳转到个人中心
    router.push('/admin/profile')
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  gap: 8px;
}

.logo .el-icon {
  font-size: 24px;
}

.el-menu {
  border-right: none;
}

.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  font-size: 14px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>



