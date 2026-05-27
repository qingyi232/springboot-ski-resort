<template>
  <div class="data-visualization-page">
    <!-- 头部标题 -->
    <div class="page-header">
      <h1 class="header-title">📊 飞跃滑雪场数据可视化大屏</h1>
      <div class="header-subtitle">
        <span>实时数据监控</span>
        <span class="update-time">最后更新: {{ updateTime }}</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="50"><Loading /></el-icon>
      <p>数据加载中...</p>
    </div>

    <!-- 主体内容 -->
    <div v-else class="dashboard-content">
      <!-- 第一行：总体概览卡片 -->
      <el-row :gutter="20" class="overview-row">
        <el-col :span="6">
          <div class="stat-card card-blue">
            <div class="stat-icon">👥</div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.overview?.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
            <div class="stat-badge">+{{ dashboardData.overview?.activeUsers || 0 }} 活跃</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card card-green">
            <div class="stat-icon">💰</div>
            <div class="stat-content">
              <div class="stat-value">¥{{ formatNumber(dashboardData.overview?.totalRevenue || 0) }}</div>
              <div class="stat-label">总收入</div>
            </div>
            <div class="stat-badge">累计收入</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card card-orange">
            <div class="stat-icon">📚</div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.overview?.totalCourses || 0 }}</div>
              <div class="stat-label">课程数量</div>
            </div>
            <div class="stat-badge">在线课程</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card card-purple">
            <div class="stat-icon">🛒</div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.overview?.todayOrders || 0 }}</div>
              <div class="stat-label">今日订单</div>
            </div>
            <div class="stat-badge">实时统计</div>
          </div>
        </el-col>
      </el-row>

      <!-- 第二行：收入趋势 + 课程统计 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="16">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-icon">📈</span>
                <span>收入趋势（最近7天）</span>
              </div>
            </template>
            <div data-chart="revenueTrend" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-icon">🎯</span>
                <span>课程难度分布</span>
              </div>
            </template>
            <div data-chart="courseDifficulty" class="chart-container-small"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 第三行：装备销售排行 + 用户增长 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-icon">🏆</span>
                <span>装备销售排行TOP10</span>
              </div>
            </template>
            <div data-chart="productRanking" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-icon">📊</span>
                <span>用户增长趋势（最近30天）</span>
              </div>
            </template>
            <div data-chart="userGrowth" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 第四行：预约统计 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-icon">🏂</span>
                <span>教练预约统计</span>
              </div>
            </template>
            <div data-chart="coachBooking" class="chart-container-small"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="header-icon">🏔️</span>
                <span>场地预订统计</span>
              </div>
            </template>
            <div data-chart="venueBooking" class="chart-container-small"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardData } from '@/api/visualization'

const loading = ref(true)
const updateTime = ref('')
const dashboardData = reactive({
  overview: {},
  revenueTrend: {},
  courseStats: {},
  productRanking: [],
  userGrowth: {},
  venueBooking: {},
  coachBooking: {}
})

// 图表实例
let revenueTrendChart = null
let courseDifficultyChart = null
let productRankingChart = null
let userGrowthChart = null
let coachBookingChart = null
let venueBookingChart = null

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const response = await getDashboardData()
    
    if (response.code === 200) {
      console.log('获取到API数据:', response.data)
      Object.assign(dashboardData, response.data)
      updateTime.value = new Date().toLocaleString()
      
      // 立即隐藏加载状态，让DOM渲染出来
      loading.value = false
      
      // 等待 DOM 完全渲染
      await nextTick()
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 500))
      
      console.log('DOM已准备就绪，开始初始化图表')
      initAllCharts()
      
      ElMessage.success('数据加载成功')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败: ' + (error.message || '未知错误'))
    loading.value = false
  }
}

// 初始化所有图表
const initAllCharts = () => {
  initRevenueTrendChart()
  initCourseDifficultyChart()
  initProductRankingChart()
  initUserGrowthChart()
  initCoachBookingChart()
  initVenueBookingChart()
}

// 1. 收入趋势图
const initRevenueTrendChart = () => {
  const el = document.querySelector('[data-chart="revenueTrend"]')
  if (!el) {
    console.error('[data-chart="revenueTrend"] DOM未找到')
    return
  }
  
  if (revenueTrendChart) {
    revenueTrendChart.dispose()
  }
  
  revenueTrendChart = echarts.init(el)
  
  const revenueTrend = dashboardData.revenueTrend || {}
  console.log('绘制收入趋势图，数据:', revenueTrend)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: revenueTrend.dates || [],
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#666',
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        data: revenueTrend.revenues || [],
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(52, 211, 153, 0.8)' },
              { offset: 1, color: 'rgba(52, 211, 153, 0.1)' }
            ]
          }
        },
        itemStyle: {
          color: '#34d399'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }
  
  revenueTrendChart.setOption(option)
}

// 2. 课程难度分布饼图
const initCourseDifficultyChart = () => {
  const el = document.querySelector('[data-chart="courseDifficulty"]')
  if (!el) {
    console.error('[data-chart="courseDifficulty"] DOM未找到')
    return
  }
  
  if (courseDifficultyChart) {
    courseDifficultyChart.dispose()
  }
  
  courseDifficultyChart = echarts.init(el)
  
  const courseStats = dashboardData.courseStats || {}
  const difficultyCount = courseStats.difficultyCount || {}
  const data = Object.entries(difficultyCount).map(([name, value]) => ({ name, value }))
  
  console.log('绘制课程难度分布图，数据:', data)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '课程难度',
        type: 'pie',
        radius: '60%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          formatter: '{b}\n{d}%'
        },
        color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6']
      }
    ]
  }
  
  courseDifficultyChart.setOption(option)
}

// 3. 装备销售排行柱状图
const initProductRankingChart = () => {
  const el = document.querySelector('[data-chart="productRanking"]')
  if (!el) {
    console.error('[data-chart="productRanking"] DOM未找到')
    return
  }
  
  if (productRankingChart) {
    productRankingChart.dispose()
  }
  
  productRankingChart = echarts.init(el)
  
  const products = dashboardData.productRanking || []
  const names = products.map(p => p.name)
  const sold = products.map(p => p.sold)
  
  console.log('绘制装备销售排行图，数据:', products)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'category',
      data: names,
      axisLabel: {
        color: '#666'
      }
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: sold,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#f59e0b' },
              { offset: 1, color: '#ef4444' }
            ]
          },
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c} 件'
        }
      }
    ]
  }
  
  productRankingChart.setOption(option)
}

// 4. 用户增长趋势
const initUserGrowthChart = () => {
  const el = document.querySelector('[data-chart="userGrowth"]')
  if (!el) {
    console.error('[data-chart="userGrowth"] DOM未找到')
    return
  }
  
  if (userGrowthChart) {
    userGrowthChart.dispose()
  }
  
  userGrowthChart = echarts.init(el)
  
  const userGrowth = dashboardData.userGrowth || {}
  console.log('绘制用户增长趋势图，数据:', userGrowth)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: userGrowth.dates || [],
      axisLabel: {
        color: '#666',
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#666'
      }
    },
    series: [
      {
        name: '累计用户',
        type: 'line',
        smooth: true,
        data: userGrowth.counts || [],
        itemStyle: {
          color: '#3b82f6'
        },
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(59, 130, 246, 0.8)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.1)' }
            ]
          }
        }
      }
    ]
  }
  
  userGrowthChart.setOption(option)
}

// 5. 教练预约统计
const initCoachBookingChart = () => {
  const el = document.querySelector('[data-chart="coachBooking"]')
  if (!el) {
    console.error('[data-chart="coachBooking"] DOM未找到')
    return
  }
  
  if (coachBookingChart) {
    coachBookingChart.dispose()
  }
  
  coachBookingChart = echarts.init(el)
  
  const coachBooking = dashboardData.coachBooking || {}
  const statusCount = coachBooking.statusCount || {}
  const data = Object.entries(statusCount).map(([name, value]) => ({ name, value }))
  
  console.log('绘制教练预约统计图，数据:', data)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        name: '预约状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}'
        },
        data: data,
        color: ['#facc15', '#3b82f6', '#22c55e', '#10b981', '#ef4444']
      }
    ]
  }
  
  coachBookingChart.setOption(option)
}

// 6. 场地预订统计
const initVenueBookingChart = () => {
  const el = document.querySelector('[data-chart="venueBooking"]')
  if (!el) {
    console.error('[data-chart="venueBooking"] DOM未找到')
    return
  }
  
  if (venueBookingChart) {
    venueBookingChart.dispose()
  }
  
  venueBookingChart = echarts.init(el)
  
  const venueBooking = dashboardData.venueBooking || {}
  const statusCount = venueBooking.statusCount || {}
  const data = Object.entries(statusCount).map(([name, value]) => ({ name, value }))
  
  console.log('绘制场地预订统计图，数据:', data)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        name: '预订状态',
        type: 'pie',
        radius: ['40%', '70%'],
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}'
        },
        data: data,
        color: ['#facc15', '#3b82f6', '#22c55e', '#10b981', '#ef4444']
      }
    ]
  }
  
  venueBookingChart.setOption(option)
}

// 格式化数字
const formatNumber = (num) => {
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 自动刷新
let refreshTimer = null
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    loadData()
  }, 60000) // 每分钟刷新一次
}

// 响应式调整图表大小
const handleResize = () => {
  revenueTrendChart?.resize()
  courseDifficultyChart?.resize()
  productRankingChart?.resize()
  userGrowthChart?.resize()
  coachBookingChart?.resize()
  venueBookingChart?.resize()
}

// 组件挂载
onMounted(() => {
  loadData()
  startAutoRefresh()
  window.addEventListener('resize', handleResize)
})

// 组件卸载
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例
  revenueTrendChart?.dispose()
  courseDifficultyChart?.dispose()
  productRankingChart?.dispose()
  userGrowthChart?.dispose()
  coachBookingChart?.dispose()
  venueBookingChart?.dispose()
})
</script>

<style scoped>
.data-visualization-page {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  color: #1f2937;
  margin-bottom: 32px;
  padding: 32px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #3b82f6;
}

.header-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #1f2937;
}

.header-subtitle {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  font-size: 14px;
  color: #6b7280;
}

.update-time {
  background: #f3f4f6;
  padding: 4px 12px;
  border-radius: 4px;
  color: #3b82f6;
  font-weight: 500;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 50vh;
  color: #6b7280;
  font-size: 16px;
  gap: 20px;
}

.dashboard-content {
  max-width: 1400px;
  margin: 0 auto;
}

.overview-row {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  border-left: 4px solid #3b82f6;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-card.card-blue {
  border-left-color: #3b82f6;
}

.stat-card.card-green {
  border-left-color: #10b981;
}

.stat-card.card-orange {
  border-left-color: #f59e0b;
}

.stat-card.card-purple {
  border-left-color: #8b5cf6;
}

.stat-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.stat-badge {
  position: absolute;
  top: 8px;
  right: 12px;
  background: #f3f4f6;
  color: #3b82f6;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.chart-row {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

:deep(.chart-card .el-card__header) {
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  padding: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.header-icon {
  font-size: 20px;
}

.chart-container {
  height: 340px;
  width: 100%;
}

.chart-container-small {
  height: 280px;
  width: 100%;
}

@media (max-width: 1200px) {
  .overview-row .el-col {
    margin-bottom: 12px;
  }
  
  .chart-row .el-col {
    margin-bottom: 12px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }
  
  .header-title {
    font-size: 20px;
  }
  
  .header-subtitle {
    flex-direction: column;
    gap: 12px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .stat-icon {
    font-size: 32px;
  }
  
  .chart-container {
    height: 280px;
  }
  
  .chart-container-small {
    height: 240px;
  }
}
</style>





