<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in statsCards" :key="card.title">
        <el-card class="stats-card" shadow="hover" @click="card.link && router.push(card.link)" :style="card.link ? 'cursor:pointer' : ''">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-title">{{ card.title }}</div>
              <div class="stats-value">{{ card.value }}</div>
            </div>
            <div class="stats-icon" :style="{ background: card.color }">
              <el-icon :size="32"><component :is="card.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdmin || isCoach">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>{{ isAdmin ? '租赁趋势' : '预约趋势' }}</span>
          </template>
          <div ref="rentalChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>课程报名情况</span>
          </template>
          <div ref="courseChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 管理员端：显示热门雪具、热门课程、教练排行 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdmin">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>热门雪具</span>
          </template>
          <el-table :data="hotEquipment" style="width: 100%">
            <el-table-column prop="equipmentName" label="雪具名称" />
            <el-table-column prop="rentalCount" label="租赁次数" width="100" />
          </el-table>
          <el-empty v-if="hotEquipment.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>热门课程</span>
          </template>
          <el-table :data="hotCourses" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="currentStudents" label="学员数" width="80" />
          </el-table>
          <el-empty v-if="hotCourses.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>教练排行</span>
          </template>
          <el-table :data="topCoaches" style="width: 100%">
            <el-table-column prop="coachName" label="教练" />
            <el-table-column prop="rating" label="评分" width="80">
              <template #default="{ row }">
                <el-rate v-model="row.rating" disabled size="small" />
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="topCoaches.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getAdminStatistics, getCoachStatistics } from '@/api/statistics'

const userStore = useUserStore()
const router = useRouter()

// 判断用户角色
const isAdmin = computed(() => userStore.userInfo?.role === 'admin')
const isCoach = computed(() => userStore.userInfo?.role === 'coach')
const isStaff = computed(() => userStore.userInfo?.role === 'staff')

// 统计卡片数据
const statsCards = ref([])

// 热门雪具
const hotEquipment = ref([])

// 热门课程
const hotCourses = ref([])

// 教练排行
const topCoaches = ref([])

// 图表引用
const rentalChartRef = ref(null)
const courseChartRef = ref(null)

// 图表实例
let rentalChart = null
let courseChart = null

onMounted(async () => {
  await loadStatistics()
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    if (isAdmin.value) {
      // 管理员：加载管理员统计数据
      const response = await getAdminStatistics()
      console.log('=== 管理员统计数据 ===')
      console.log('响应:', response)
      
      if (response.code === 200 && response.data) {
        const data = response.data
        
        // 设置统计卡片
        statsCards.value = [
          { title: '总用户数', value: data.totalUsers || 0, icon: 'UserFilled', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
          { title: '在租雪具', value: data.activeRentals || 0, icon: 'Goods', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
          { title: '进行中预约', value: data.ongoingCourses || 0, icon: 'Reading', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
          { title: '今日收入', value: data.todayIncome || '¥0.00', icon: 'Coin', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
        ]
        
        // 设置热门雪具
        hotEquipment.value = data.hotEquipment || []
        
        // 设置热门课程
        hotCourses.value = data.hotCourses || []
        
        // 设置教练排行
        topCoaches.value = data.topCoaches || []
        
        // 初始化租赁趋势图表
        if (data.rentalTrend && data.rentalTrend.length > 0) {
          initRentalChart(data.rentalTrend)
        }
        
        // 初始化课程分布图表
        if (data.courseDistribution && data.courseDistribution.length > 0) {
          initCourseChart(data.courseDistribution)
        }
      }
    } else if (isStaff.value) {
      // 工作人员：显示基础概览
      statsCards.value = [
        { title: '快捷入口', value: '租赁办理', icon: 'Tickets', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', link: '/admin/staff/rental' },
        { title: '快捷入口', value: '订单管理', icon: 'Document', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', link: '/admin/staff/orders' },
        { title: '快捷入口', value: '库存管理', icon: 'Goods', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', link: '/admin/staff/inventory' },
        { title: '快捷入口', value: '场地查看', icon: 'MapLocation', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', link: '/admin/staff/venues' }
      ]
    } else if (isCoach.value) {
      // 教练：加载教练统计数据
      const coachId = userStore.userInfo.id
      const response = await getCoachStatistics(coachId)
      console.log('=== 教练统计数据 ===')
      console.log('响应:', response)
      
      if (response.code === 200 && response.data) {
        const data = response.data
        
        // 设置统计卡片（教练版本）
        statsCards.value = [
          { title: '学员总数', value: data.totalStudents || 0, icon: 'UserFilled', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100())' },
          { title: '待确认预约', value: data.pendingBookings || 0, icon: 'Clock', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
          { title: '进行中预约', value: data.ongoingCourses || 0, icon: 'Reading', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
          { title: '已完成预约', value: data.completedBookings || 0, icon: 'CircleCheck', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
        ]
        
        // 初始化预约趋势图表
        if (data.bookingTrend && data.bookingTrend.length > 0) {
          initBookingTrendChart(data.bookingTrend)
        }
        
        // 初始化课程分布图表
        if (data.courseDistribution && data.courseDistribution.length > 0) {
          initCourseChart(data.courseDistribution)
        }
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 初始化租赁趋势图表（管理员）
const initRentalChart = (trendData) => {
  if (!rentalChartRef.value) return
  
  if (rentalChart) {
    rentalChart.dispose()
  }
  
  rentalChart = echarts.init(rentalChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: trendData.map(item => item.date)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '租赁数量',
      type: 'line',
      smooth: true,
      data: trendData.map(item => item.count),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0)' }
        ])
      }
    }]
  }
  rentalChart.setOption(option)
}

// 初始化预约趋势图表（教练）
const initBookingTrendChart = (trendData) => {
  if (!rentalChartRef.value) return
  
  if (rentalChart) {
    rentalChart.dispose()
  }
  
  rentalChart = echarts.init(rentalChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: trendData.map(item => item.date)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '预约数量',
      type: 'line',
      smooth: true,
      data: trendData.map(item => item.count),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0)' }
        ])
      }
    }]
  }
  rentalChart.setOption(option)
}

// 初始化课程分布图表
const initCourseChart = (distributionData) => {
  if (!courseChartRef.value) return
  
  if (courseChart) {
    courseChart.dispose()
  }
  
  courseChart = echarts.init(courseChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '课程类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      data: distributionData
    }]
  }
  courseChart.setOption(option)
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stats-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
</style>




