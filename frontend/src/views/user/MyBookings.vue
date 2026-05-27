<template>
  <div class="my-bookings">
    <div class="page-header">
      <h1>我的预约</h1>
      <p>查看和管理您的所有预约</p>
    </div>

    <!-- 预约类型标签页 -->
    <el-tabs v-model="bookingType" @tab-change="handleTypeChange" class="booking-tabs">
      <!-- 教练预约 -->
      <el-tab-pane label="教练预约" name="coach">
        <div class="booking-list" v-loading="loading">
          <div v-for="booking in coachBookings" :key="booking.id" class="booking-card">
            <div class="booking-header">
              <div class="coach-info">
                <el-avatar :size="60" :src="booking.coachAvatar" />
                <div class="coach-details">
                  <h3>{{ booking.coachName }}</h3>
                  <div class="coach-meta">
                    <el-tag size="small">{{ booking.coachLevel }}</el-tag>
                    <el-rate
                      v-model="booking.coachRating"
                      disabled
                      show-score
                      text-color="#ff9900"
                      size="small"
                    />
                  </div>
                </div>
              </div>
              <el-tag :type="getStatusType(booking.status)" size="large">
                {{ getBookingStatusText(booking.status) }}
              </el-tag>
            </div>

            <div class="booking-content">
              <div class="info-grid">
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <div>
                    <label>预约日期</label>
                    <span>{{ formatDate(booking.bookingDate) }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <div>
                    <label>上课时间</label>
                    <span>{{ booking.startTime }} - {{ booking.endTime }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Timer /></el-icon>
                  <div>
                    <label>课程时长</label>
                    <span>{{ booking.duration }} 小时</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Reading /></el-icon>
                  <div>
                    <label>课程类型</label>
                    <span>{{ booking.courseType }}</span>
                  </div>
                </div>
              </div>

              <div class="booking-footer">
                <div class="price-info">
                  <span class="price-label">课时费：</span>
                  <span class="price-value">¥{{ booking.totalAmount }}</span>
                </div>
                <div class="booking-actions">
                  <el-button size="small" @click="viewBookingDetail(booking)">
                    查看详情
                  </el-button>
                  <el-button
                    v-if="booking.status === 'confirmed'"
                    type="warning"
                    size="small"
                    @click="cancelBooking(booking)"
                  >
                    取消预约
                  </el-button>
                  <el-button
                    v-if="booking.status === 'completed'"
                    type="success"
                    size="small"
                    @click="rateCoach(booking)"
                  >
                    评价教练
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 场地预订 -->
      <el-tab-pane label="场地预订" name="venue">
        <div class="booking-list" v-loading="loading">
          <div v-for="booking in venueBookings" :key="booking.id" class="booking-card venue-card">
            <div class="venue-header">
              <div class="venue-image">
                <img :src="booking.venueImage || booking.imageUrl || 'https://picsum.photos/seed/venue-placeholder/800/400'" :alt="booking.venueName" />
                <div class="venue-badge">
                  <el-tag :type="getVenueDifficultyType(booking.venueDifficulty)">
                    {{ booking.venueDifficulty }}
                  </el-tag>
                </div>
              </div>
              <div class="venue-info">
                <h3>{{ booking.venueName }}</h3>
                <p>{{ booking.venueDescription }}</p>
                <el-tag :type="getStatusType(booking.status)" size="large">
                  {{ getBookingStatusText(booking.status) }}
                </el-tag>
              </div>
            </div>

            <div class="booking-content">
              <div class="info-grid">
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <div>
                    <label>预订日期</label>
                    <span>{{ formatDate(booking.bookingDate) }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <div>
                    <label>使用时间</label>
                    <span>{{ booking.startTime }} - {{ booking.endTime }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <div>
                    <label>预订人数</label>
                    <span>{{ booking.peopleCount }} 人</span>
                  </div>
                </div>
                <div class="info-item">
                  <el-icon><LocationInformation /></el-icon>
                  <div>
                    <label>场地容量</label>
                    <span>{{ booking.venueCapacity }} 人</span>
                  </div>
                </div>
              </div>

              <div class="booking-footer">
                <div class="price-info">
                  <span class="price-label">场地费：</span>
                  <span class="price-value">¥{{ booking.totalAmount }}</span>
                </div>
                <div class="booking-actions">
                  <el-button size="small" @click="viewBookingDetail(booking)">
                    查看详情
                  </el-button>
                  <el-button
                    v-if="booking.status === 'confirmed'"
                    type="warning"
                    size="small"
                    @click="cancelBooking(booking)"
                  >
                    取消预订
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 空状态 -->
    <el-empty v-if="!loading && getCurrentBookings().length === 0" description="暂无预约记录" />

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        :current-page="pagination.page"
        :page-size="pagination.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 评价对话框 -->
    <el-dialog v-model="rateDialogVisible" title="评价教练" width="500px">
      <el-form :model="rateForm" label-width="80px">
        <el-form-item label="教练">
          <el-input v-model="selectedBooking.coachName" disabled />
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="rateForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="rateForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请分享您的上课体验"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar, Clock, Timer, Reading, User, LocationInformation
} from '@element-plus/icons-vue'
import { getBookingsByUserId, cancelCoachBooking, rateBooking } from '@/api/coach-booking'
import { getVenueBookings, getVenueBookingsByUserId, cancelVenueBooking } from '@/api/venue-booking'
import { formatDate } from '@/utils/performance'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 预约类型
const bookingType = ref('coach')

// 预约列表
const coachBookings = ref([])
const venueBookings = ref([])
const loading = ref(false)
const pagination = ref({
  page: 1,
  pageSize: 10
})
const total = ref(0)

// 评价对话框
const rateDialogVisible = ref(false)
const selectedBooking = ref({})
const rateForm = reactive({
  rating: 5,
  comment: ''
})

// 获取当前预约列表
const getCurrentBookings = () => {
  return bookingType.value === 'coach' ? coachBookings.value : venueBookings.value
}

// 加载教练预约
const loadCoachBookings = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo?.id
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    const response = await getBookingsByUserId(userId)
    if (response.code === 200) {
      coachBookings.value = response.data || []
      total.value = coachBookings.value.length
    }
  } catch (error) {
    console.error('加载教练预约失败:', error)
    ElMessage.error('加载预约失败')
  } finally {
    loading.value = false
  }
}

// 加载场地预订
const loadVenueBookings = async () => {
  try {
    loading.value = true
    const userId = userStore.userInfo?.id
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }
    
    const response = await getVenueBookingsByUserId(userId)
    if (response.code === 200) {
      venueBookings.value = response.data || []
      total.value = venueBookings.value.length
    }
  } catch (error) {
    console.error('加载场地预订失败:', error)
    ElMessage.error('加载预订失败')
  } finally {
    loading.value = false
  }
}

// 预约类型变化
const handleTypeChange = () => {
  pagination.value.page = 1
  if (bookingType.value === 'coach') {
    loadCoachBookings()
  } else {
    loadVenueBookings()
  }
}

// 页码变化
const handlePageChange = () => {
  if (bookingType.value === 'coach') {
    loadCoachBookings()
  } else {
    loadVenueBookings()
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'confirmed': 'primary',
    'completed': 'success',
    'cancelled': 'info'
  }
  return typeMap[status] || 'info'
}

// 获取预约状态文本
const getBookingStatusText = (status) => {
  const textMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

// 获取场地难度类型
const getVenueDifficultyType = (difficulty) => {
  const typeMap = {
    '初级': 'success',
    '中级': 'warning',
    '高级': 'danger'
  }
  return typeMap[difficulty] || 'info'
}

// 查看预约详情
const viewBookingDetail = (booking) => {
  ElMessageBox.alert(
    `<div style="text-align: left;">
      <p><strong>预约编号：</strong>${booking.bookingNo || '-'}</p>
      <p><strong>课程名称：</strong>${booking.courseName || '-'}</p>
      <p><strong>课程价格：</strong>¥${booking.coursePrice || 0}</p>
      <p><strong>预约状态：</strong>${getStatusText(booking.status)}</p>
      <p><strong>课程日期：</strong>${booking.courseDate || '-'}</p>
      <p><strong>开始时间：</strong>${booking.startTime || '-'}</p>
      <p><strong>结束时间：</strong>${booking.endTime || '-'}</p>
      <p><strong>教练：</strong>${booking.coachName || '-'}</p>
      <p><strong>场地：</strong>${booking.venueName || '-'}</p>
      <p><strong>创建时间：</strong>${booking.createTime}</p>
      ${booking.paymentTime ? `<p><strong>支付时间：</strong>${booking.paymentTime}</p>` : ''}
    </div>`,
    '预约详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
  )
}

// 取消预约
const cancelBooking = async (booking) => {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？', '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const cancelFunc = bookingType.value === 'coach' ? cancelCoachBooking : cancelVenueBooking
    const response = await cancelFunc(booking.id)
    
    if (response.code === 200) {
      ElMessage.success('预约已取消')
      // 刷新列表
      if (bookingType.value === 'coach') {
        loadCoachBookings()
      } else {
        loadVenueBookings()
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error(error.message || '取消预约失败')
    }
  }
}

// 评价教练
const rateCoach = (booking) => {
  selectedBooking.value = booking
  rateForm.rating = 5
  rateForm.comment = ''
  rateDialogVisible.value = true
}

// 提交评价
const submitRate = async () => {
  if (!rateForm.comment.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    const response = await rateBooking(selectedBooking.value.id, rateForm.rating, rateForm.comment)
    if (response.code === 200) {
      ElMessage.success('评价提交成功！')
      rateDialogVisible.value = false
      loadCoachBookings()
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error(error.message || '提交评价失败')
  }
}

// 组件挂载
onMounted(() => {
  loadCoachBookings()
})
</script>

<style scoped>
.my-bookings {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 32px;
  text-align: center;
}

.page-header h1 {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.page-header p {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.booking-tabs {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 400px;
}

.booking-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 24px;
  background: white;
  transition: all 0.3s ease;
}

.booking-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.coach-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.coach-details h3 {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 8px 0;
}

.coach-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.booking-content {
  margin-bottom: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-item .el-icon {
  font-size: 24px;
  color: #409eff;
}

.info-item div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 12px;
  color: #909399;
}

.info-item span {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.booking-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-label {
  font-size: 14px;
  color: #606266;
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.booking-actions {
  display: flex;
  gap: 8px;
}

/* 场地预订卡片 */
.venue-card .venue-header {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.venue-image {
  position: relative;
  width: 200px;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
}

.venue-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.venue-badge {
  position: absolute;
  top: 12px;
  right: 12px;
}

.venue-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.venue-info h3 {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.venue-info p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  flex: 1;
}

.pagination {
  max-width: 1200px;
  margin: 32px auto 0;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 24px;
  }
  
  .booking-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .booking-footer {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .booking-actions {
    flex-wrap: wrap;
  }
  
  .venue-card .venue-header {
    flex-direction: column;
  }
  
  .venue-image {
    width: 100%;
    height: 200px;
  }
}
</style>

