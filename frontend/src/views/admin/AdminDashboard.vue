<template>
  <div class="admin-dashboard">
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.id">
        <div class="stat-card" :style="{ borderLeftColor: stat.color }">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="32"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-change" :class="stat.change >= 0 ? 'positive' : 'negative'">
              <el-icon>
                <component :is="stat.change >= 0 ? 'Top' : 'Bottom'" />
              </el-icon>
              <span>{{ Math.abs(stat.change) }}%</span>
              <span class="stat-period">{{ stat.period }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 收入趋势图 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">收入趋势</span>
              <el-radio-group v-model="revenueTimeRange" size="small" @change="loadRevenueChart">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
                <el-radio-button label="year">近一年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="revenueChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 订单统计图 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">订单统计</span>
          </template>
          <div ref="orderChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 热门雪具排行 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">热门雪具排行</span>
          </template>
          <div ref="equipmentChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 教练评分排行 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">教练评分排行</span>
          </template>
          <div ref="coachChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单和活动 -->
    <el-row :gutter="20" class="activity-row">
      <!-- 最近订单 -->
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="activity-card">
          <template #header>
            <span class="card-title">最近订单</span>
          </template>
          <el-table :data="recentOrders" stripe style="width: 100%" max-height="400">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="customerName" label="客户" width="120" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getOrderTypeTag(row.type)">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span class="amount">¥{{ row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 系统动态 -->
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="activity-card">
          <template #header>
            <span class="card-title">系统动态</span>
          </template>
          <el-timeline>
            <el-timeline-item 
              v-for="activity in systemActivities" 
              :key="activity.id"
              :timestamp="activity.time"
              :color="activity.color"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  User,
  Box,
  ShoppingCart,
  TrophyBase,
  Top,
  Bottom
} from '@element-plus/icons-vue'
import { getDashboardOverview } from '@/api/dashboard'

// 统计数据
const stats = ref([
  {
    id: 1,
    label: '今日访问',
    value: '1,234',
    change: 12.5,
    period: '较昨日',
    color: '#667eea',
    icon: 'User'
  },
  {
    id: 2,
    label: '租赁订单',
    value: '89',
    change: 8.3,
    period: '较昨日',
    color: '#f093fb',
    icon: 'Box'
  },
  {
    id: 3,
    label: '今日收入',
    value: '¥15,680',
    change: -3.2,
    period: '较昨日',
    color: '#4facfe',
    icon: 'ShoppingCart'
  },
  {
    id: 4,
    label: '活跃用户',
    value: '458',
    change: 15.7,
    period: '较上周',
    color: '#43e97b',
    icon: 'TrophyBase'
  }
])

// 图表相关
const revenueChartRef = ref(null)
const orderChartRef = ref(null)
const equipmentChartRef = ref(null)
const coachChartRef = ref(null)
const revenueTimeRange = ref('week')

// 最近订单
const recentOrders = ref([
  {
    orderNo: 'RO202510240001',
    customerName: '张三',
    type: '雪具租赁',
    amount: '120.00',
    status: '进行中',
    createTime: '2025-10-24 14:30'
  },
  {
    orderNo: 'SO202510240002',
    customerName: '李四',
    type: '装备销售',
    amount: '1,599.00',
    status: '已完成',
    createTime: '2025-10-24 13:15'
  },
  {
    orderNo: 'CB202510240003',
    customerName: '王五',
    type: '教练预约',
    amount: '400.00',
    status: '待确认',
    createTime: '2025-10-24 12:00'
  }
])

// 系统动态
const systemActivities = ref([
  {
    id: 1,
    content: '用户"张三"完成了雪具租赁',
    time: '2025-10-24 14:35',
    color: '#409eff'
  },
  {
    id: 2,
    content: '新增销售订单 SO202510240002',
    time: '2025-10-24 13:15',
    color: '#67c23a'
  },
  {
    id: 3,
    content: '教练"王教练"确认了预约',
    time: '2025-10-24 12:05',
    color: '#e6a23c'
  },
  {
    id: 4,
    content: '系统完成每日数据备份',
    time: '2025-10-24 10:00',
    color: '#909399'
  }
])

// 获取订单类型标签
const getOrderTypeTag = (type) => {
  const tagMap = {
    '雪具租赁': 'primary',
    '装备销售': 'success',
    '教练预约': 'warning',
    '课程报名': 'info'
  }
  return tagMap[type] || ''
}

// 获取状态标签
const getStatusTag = (status) => {
  const tagMap = {
    '进行中': 'warning',
    '已完成': 'success',
    '待确认': 'info',
    '已取消': 'danger'
  }
  return tagMap[status] || ''
}

// 初始化收入趋势图
const initRevenueChart = () => {
  const chart = echarts.init(revenueChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
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
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '租赁收入',
        type: 'line',
        smooth: true,
        data: [8200, 9500, 7800, 10200, 11500, 13800, 15600],
        lineStyle: {
          width: 3,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
            { offset: 1, color: 'rgba(118, 75, 162, 0.1)' }
          ])
        }
      },
      {
        name: '销售收入',
        type: 'line',
        smooth: true,
        data: [5200, 6100, 5800, 7200, 8500, 9200, 10800],
        lineStyle: {
          width: 3,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#f093fb' },
            { offset: 1, color: '#f5576c' }
          ])
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(240, 147, 251, 0.3)' },
            { offset: 1, color: 'rgba(245, 87, 108, 0.1)' }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
  
  // 响应式
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化订单统计图
const initOrderChart = () => {
  const chart = echarts.init(orderChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '订单类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 45, name: '雪具租赁', itemStyle: { color: '#667eea' } },
          { value: 28, name: '装备销售', itemStyle: { color: '#f093fb' } },
          { value: 32, name: '教练预约', itemStyle: { color: '#4facfe' } },
          { value: 18, name: '课程报名', itemStyle: { color: '#43e97b' } }
        ]
      }
    ]
  }
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化热门雪具图
const initEquipmentChart = () => {
  const chart = echarts.init(equipmentChartRef.value)
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
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['Burton单板', 'K2双板', 'Smith雪镜', 'POC头盔', 'BD雪杖']
    },
    series: [
      {
        name: '租赁次数',
        type: 'bar',
        data: [78, 65, 52, 48, 35],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [0, 10, 10, 0]
        }
      }
    ]
  }
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化教练排行图
const initCoachChart = () => {
  const chart = echarts.init(coachChartRef.value)
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
      data: ['王教练', '赵教练', '李教练', '张教练', '刘教练']
    },
    yAxis: {
      type: 'value',
      max: 5
    },
    series: [
      {
        name: '评分',
        type: 'bar',
        data: [4.9, 4.7, 4.6, 4.5, 4.3],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [
            { offset: 0, color: '#f093fb' },
            { offset: 1, color: '#f5576c' }
          ]),
          borderRadius: [10, 10, 0, 0]
        },
        barWidth: '50%'
      }
    ]
  }
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 加载收入图表
const loadRevenueChart = () => {
  // TODO: 根据时间范围加载数据
  initRevenueChart()
}

// 加载概览数据
const loadOverviewData = async () => {
  try {
    const response = await getDashboardOverview()
    if (response.code === 200) {
      // 更新统计数据
      const data = response.data
      // TODO: 更新stats数据
    }
  } catch (error) {
    console.error('加载概览数据失败', error)
  }
}

onMounted(() => {
  loadOverviewData()
  
  nextTick(() => {
    initRevenueChart()
    initOrderChart()
    initEquipmentChart()
    initCoachChart()
  })
})
</script>

<style scoped>
.admin-dashboard {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border-left: 4px solid;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  gap: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.stat-change {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.stat-change.positive {
  color: #67c23a;
}

.stat-change.negative {
  color: #f56c6c;
}

.stat-period {
  color: #909399;
  margin-left: 4px;
}

/* 图表区域 */
.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 12px;
  border: none;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  height: 350px;
}

/* 活动区域 */
.activity-row {
  margin-bottom: 24px;
}

.activity-card {
  border-radius: 12px;
  border: none;
}

.amount {
  font-weight: 600;
  color: #f56c6c;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }
  
  .stat-card {
    margin-bottom: 16px;
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>







