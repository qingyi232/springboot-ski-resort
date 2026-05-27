import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  // 前台路由（普通用户）
  {
    path: '/',
    component: () => import('@/layout/FrontLayout.vue'),
    redirect: '/home',
    meta: { requiresAuth: true, roles: ['user'] },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'front-courses',
        name: 'FrontCourses',
        component: () => import('@/views/course/FrontCourses.vue'),
        meta: { title: '滑雪课程' }
      },
      {
        path: 'product-mall',
        name: 'ProductMall',
        component: () => import('@/views/product/ProductMall.vue'),
        meta: { title: '装备商城' }
      },
      {
        path: 'equipment-rental',
        name: 'EquipmentRental',
        component: () => import('@/views/equipment/EquipmentRental.vue'),
        meta: { title: '雪具租赁' }
      },
      {
        path: 'venues',
        name: 'Venues',
        component: () => import('@/views/venue/FrontVenues.vue'),
        meta: { title: '场地预约' }
      },
      {
        path: 'my-orders',
        name: 'MyOrders',
        component: () => import('@/views/user/MyOrders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'my-bookings',
        name: 'MyBookings',
        component: () => import('@/views/user/MyBookings.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: 'ai-recommendation',
        name: 'AIRecommendation',
        component: () => import('@/views/user/AIRecommendation.vue'),
        meta: { title: 'AI智能推荐' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'repair-submit',
        name: 'RepairSubmit',
        component: () => import('@/views/equipment/RepairSubmit.vue'),
        meta: { title: '雪具报修' }
      }
    ]
  },
  // 后台管理路由（管理员和教练）
  {
    path: '/admin',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, roles: ['admin', 'coach', 'staff'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'data-visualization',
        name: 'DataVisualization',
        component: () => import('@/views/admin/DataVisualization.vue'),
        meta: { title: '数据可视化', icon: 'DataLine', roles: ['admin'] }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/EquipmentList.vue'),
        meta: { title: '雪具管理', icon: 'Goods', roles: ['admin'] }
      },
      {
        path: 'coach',
        name: 'Coach',
        component: () => import('@/views/coach/CoachList.vue'),
        meta: { title: '教练管理', icon: 'User', roles: ['admin'] }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/product/ProductList.vue'),
        meta: { title: '商品管理', icon: 'ShoppingCart', roles: ['admin'] }
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('@/views/course/CourseList.vue'),
        meta: { title: '课程管理', icon: 'Reading', roles: ['admin'] }
      },
      // 教练端专属功能
      {
        path: 'coach/my-courses',
        name: 'CoachMyCourses',
        component: () => import('@/views/coach/MyCourses.vue'),
        meta: { title: '我的课程', icon: 'Reading', roles: ['coach'] }
      },
      {
        path: 'coach/bookings',
        name: 'CoachBookings',
        component: () => import('@/views/coach/CoachBookingManagement.vue'),
        meta: { title: '学员预约', icon: 'Calendar', roles: ['coach'] }
      },
      // 工作人员专属功能
      {
        path: 'staff/rental',
        name: 'StaffRental',
        component: () => import('@/views/staff/StaffRental.vue'),
        meta: { title: '租赁办理', icon: 'Tickets', roles: ['staff'] }
      },
      {
        path: 'staff/orders',
        name: 'StaffOrders',
        component: () => import('@/views/staff/StaffOrders.vue'),
        meta: { title: '订单管理', icon: 'Document', roles: ['staff'] }
      },
      {
        path: 'staff/inventory',
        name: 'StaffInventory',
        component: () => import('@/views/staff/StaffInventory.vue'),
        meta: { title: '库存管理', icon: 'Box', roles: ['staff'] }
      },
      {
        path: 'staff/venues',
        name: 'StaffVenues',
        component: () => import('@/views/staff/StaffVenues.vue'),
        meta: { title: '场地查看', icon: 'MapLocation', roles: ['staff'] }
      },
      {
        path: 'staff/repair',
        name: 'StaffRepair',
        component: () => import('@/views/staff/StaffRepair.vue'),
        meta: { title: '维修管理', icon: 'SetUp', roles: ['staff'] }
      },
      // 管理员端专属功能
      {
        path: 'venue',
        name: 'Venue',
        component: () => import('@/views/venue/VenueList.vue'),
        meta: { title: '场地管理', icon: 'MapLocation', roles: ['admin'] }
      },
      {
        path: 'bookings',
        name: 'BookingManagement',
        component: () => import('@/views/admin/BookingManagement.vue'),
        meta: { title: '预约管理', icon: 'Calendar', roles: ['admin'] }
      },
      {
        path: 'rental-order',
        name: 'RentalOrder',
        component: () => import('@/views/order/RentalOrderList.vue'),
        meta: { title: '租赁订单', icon: 'Document', roles: ['admin'] }
      },
      {
        path: 'sales-order',
        name: 'SalesOrder',
        component: () => import('@/views/admin/OrderManagement.vue'),
        meta: { title: '销售订单', icon: 'ShoppingCart', roles: ['admin'] }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', roles: ['admin'] }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', icon: 'User' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const userRole = userStore.userInfo?.role

  // 登录页面直接放行
  if (to.path === '/login') {
    if (token && userRole) {
      // 已登录，根据角色跳转到对应首页
      if (userRole === 'user') {
        next('/home')
      } else {
        next('/admin/dashboard')
      }
    } else {
      next()
    }
    return
  }

  // 需要登录的页面
  if (to.meta.requiresAuth || to.path !== '/login') {
    if (!token) {
      next('/login')
      return
    }

    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      if (!to.meta.roles.includes(userRole)) {
        // 角色不匹配，跳转到对应的首页
        if (userRole === 'user') {
          next('/home')
        } else {
          next('/admin/dashboard')
        }
        return
      }
    }
  }

  next()
})

export default router



