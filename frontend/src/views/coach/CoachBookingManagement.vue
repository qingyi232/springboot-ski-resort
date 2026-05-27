<template>
  <div class="coach-booking-management">
    <div class="page-header">
      <h1>学员预约</h1>
      <p class="subtitle">查看和管理学员的课程预约</p>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 150px;">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateFilter"
            type="date"
            placeholder="选择日期"
            style="width: 180px;"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadBookings">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetFilter">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预约统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card pending">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card confirmed">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.confirmed }}</div>
              <div class="stat-label">已确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card ongoing">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.ongoing }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card completed">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预约列表 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="filteredBookings" v-loading="loading" stripe>
        <el-table-column prop="bookingNo" label="预约编号" width="180" />
        <el-table-column prop="userName" label="学员姓名" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="bookingDate" label="预约日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.bookingDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="hours" label="课时" width="80">
          <template #default="{ row }">
            {{ row.hours }} 小时
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <!-- 待确认：可以确认或拒绝 -->
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleConfirm(row)">
                <el-icon><Check /></el-icon> 确认
              </el-button>
              <el-button type="danger" size="small" @click="handleReject(row)">
                <el-icon><Close /></el-icon> 拒绝
              </el-button>
            </template>
            <!-- 已确认：可以标记为进行中 -->
            <template v-else-if="row.status === 1">
              <el-button type="primary" size="small" @click="handleStart(row)">
                <el-icon><VideoPlay /></el-icon> 开始上课
              </el-button>
            </template>
            <!-- 进行中：可以标记为已完成 -->
            <template v-else-if="row.status === 2">
              <el-button type="success" size="small" @click="handleComplete(row)">
                <el-icon><CircleCheck /></el-icon> 完成
              </el-button>
            </template>
            <!-- 所有状态都可以查看详情 -->
            <el-button type="info" size="small" @click="handleViewDetail(row)">
              <el-icon><View /></el-icon> 详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预约详情"
      width="600px"
    >
      <div v-if="currentBooking" class="booking-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预约编号">{{ currentBooking.bookingNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentBooking.status)">
              {{ getStatusText(currentBooking.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学员姓名">{{ currentBooking.userName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentBooking.userPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="课程名称" :span="2">{{ currentBooking.courseName }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ formatDate(currentBooking.bookingDate) }}</el-descriptions-item>
          <el-descriptions-item label="课时">{{ currentBooking.hours }} 小时</el-descriptions-item>
          <el-descriptions-item label="上课时间" :span="2">
            {{ currentBooking.startTime }} - {{ currentBooking.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="课程费用">¥{{ currentBooking.coursePrice }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentBooking.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="currentBooking.paymentStatus === 1 ? 'success' : 'warning'">
              {{ currentBooking.paymentStatus === 1 ? '已支付' : '未支付' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付时间">
            {{ currentBooking.paymentTime ? formatDateTime(currentBooking.paymentTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDateTime(currentBooking.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentBooking.rating" label="评分">
            <el-rate v-model="currentBooking.rating" disabled />
          </el-descriptions-item>
          <el-descriptions-item v-if="currentBooking.comment" label="评价" :span="2">
            {{ currentBooking.comment }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentBooking.remark" label="备注" :span="2">
            {{ currentBooking.remark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Clock, Check, Loading, SuccessFilled,
  Close, VideoPlay, CircleCheck, View
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getBookingsByCoachUserId,
  confirmBooking,
  cancelBooking,
  completeBooking,
  updateBooking
} from '@/api/coach-booking'

const userStore = useUserStore()

// 数据
const loading = ref(false)
const bookings = ref([])
const statusFilter = ref(null)
const dateFilter = ref(null)
const detailDialogVisible = ref(false)
const currentBooking = ref(null)

// 统计数据
const stats = computed(() => {
  return {
    pending: bookings.value.filter(b => b.status === 0).length,
    confirmed: bookings.value.filter(b => b.status === 1).length,
    ongoing: bookings.value.filter(b => b.status === 2).length,
    completed: bookings.value.filter(b => b.status === 3).length
  }
})

// 过滤后的预约列表
const filteredBookings = computed(() => {
  let result = [...bookings.value]
  
  // 按状态筛选
  if (statusFilter.value !== null) {
    result = result.filter(b => b.status === statusFilter.value)
  }
  
  // 按日期筛选
  if (dateFilter.value) {
    const filterDate = formatDate(dateFilter.value)
    result = result.filter(b => formatDate(b.bookingDate) === filterDate)
  }
  
  // 按创建时间倒序排序
  result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  
  return result
})

// 加载预约列表
const loadBookings = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo.id
    console.log('=== 教练端加载预约列表 ===')
    console.log('用户ID:', userId)
    
    // 使用新API，自动将用户ID转换为教练ID
    const response = await getBookingsByCoachUserId(userId)
    console.log('预约列表响应:', response)
    
    bookings.value = response.data || []
    console.log('预约数量:', bookings.value.length)
    console.log('统计数据 - 待确认:', bookings.value.filter(b => b.status === 0).length)
    console.log('统计数据 - 已确认:', bookings.value.filter(b => b.status === 1).length)
    console.log('统计数据 - 进行中:', bookings.value.filter(b => b.status === 2).length)
    console.log('统计数据 - 已完成:', bookings.value.filter(b => b.status === 3).length)
  } catch (error) {
    console.error('加载预约列表失败:', error)
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  statusFilter.value = null
  dateFilter.value = null
  loadBookings()
}

// 确认预约
const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确定要确认这个预约吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await confirmBooking(row.id)
    ElMessage.success('已确认预约')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认预约失败:', error)
      ElMessage.error('确认预约失败')
    }
  }
}

// 拒绝预约
const handleReject = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    
    await cancelBooking(row.id, value)
    ElMessage.success('已拒绝预约')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝预约失败:', error)
      ElMessage.error('拒绝预约失败')
    }
  }
}

// 开始上课（标记为进行中）
const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm('确定要开始这节课吗？', '开始上课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'primary'
    })
    
    // 调用更新接口，将状态改为2（进行中）
    await updateBooking(row.id, { status: 2 })
    ElMessage.success('课程已开始')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开始课程失败:', error)
      ElMessage.error('开始课程失败')
    }
  }
}

// 完成课程
const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要标记这节课为已完成吗？', '完成课程', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await completeBooking(row.id)
    ElMessage.success('课程已完成')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成课程失败:', error)
      ElMessage.error('完成课程失败')
    }
  }
}

// 查看详情
const handleViewDetail = (row) => {
  currentBooking.value = row
  detailDialogVisible.value = true
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'primary',
    3: 'info',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  const d = new Date(datetime)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  loadBookings()
})
</script>

<style scoped>
.coach-booking-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 16px;
}

.stat-card.pending .stat-icon {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #fff;
}

.stat-card.confirmed .stat-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.stat-card.ongoing .stat-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.stat-card.completed .stat-icon {
  background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.table-card {
  margin-top: 20px;
}

.amount {
  font-weight: 600;
  color: #f56c6c;
}

.booking-detail {
  margin-top: 20px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}
</style>

