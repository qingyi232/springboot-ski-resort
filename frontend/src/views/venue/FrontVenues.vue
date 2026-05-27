<template>
  <div class="front-venues-page">
    <!-- 场地列表 -->
    <div class="page-container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterDifficulty" @change="handleFilterChange" size="large">
          <el-radio-button label="all">全部场地</el-radio-button>
          <el-radio-button label="初级">初级雪道</el-radio-button>
          <el-radio-button label="中级">中级雪道</el-radio-button>
          <el-radio-button label="高级">高级雪道</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 场地网格 -->
      <div class="venues-grid" v-loading="loading">
        <div v-for="venue in venueList" :key="venue.id" class="venue-card" @click="viewDetail(venue)">
          <div class="venue-image">
            <img :src="venue.imageUrl || 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=800&q=80'" :alt="venue.venueName" />
            <div class="difficulty-badge" :class="getDifficultyClass(venue.difficultyLevel)">
              <span class="difficulty-icon">{{ getDifficultyIcon(venue.difficultyLevel) }}</span>
              <span>{{ venue.difficultyLevel }}</span>
            </div>
          </div>
          <div class="venue-info">
            <h3 class="venue-name">{{ venue.venueName }}</h3>
            <div class="venue-type">{{ venue.venueType }}</div>
            
            <div class="venue-stats">
              <div class="stat">
                <span class="stat-label">长度</span>
                <span class="stat-value">{{ venue.length }}m</span>
              </div>
              <div class="stat">
                <span class="stat-label">宽度</span>
                <span class="stat-value">{{ venue.width }}m</span>
              </div>
              <div class="stat">
                <span class="stat-label">容量</span>
                <span class="stat-value">{{ venue.maxCapacity }}人</span>
              </div>
            </div>

            <div class="venue-footer">
              <div class="price-info">
                <span class="price">¥{{ venue.rentalPrice }}</span>
                <span class="unit">/小时</span>
              </div>
              <button class="reserve-btn" @click="handleReserve(venue)">预约场地</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && venueList.length === 0" description="暂无场地" />
    </div>

    <!-- 预约对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="场地预约" width="600px">
      <el-form :model="bookingForm" label-width="100px">
        <el-form-item label="场地名称">
          <el-input v-model="selectedVenue.venueName" disabled />
        </el-form-item>
        <el-form-item label="场地类型">
          <el-input v-model="selectedVenue.venueType" disabled />
        </el-form-item>
        <el-form-item label="难度等级">
          <el-input v-model="selectedVenue.difficultyLevel" disabled />
        </el-form-item>
        <el-form-item label="场地价格">
          <el-input v-model="selectedVenue.rentalPrice" disabled>
            <template #prepend>¥</template>
            <template #append>/小时</template>
          </el-input>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker
            v-model="bookingForm.bookingTime"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="使用时长">
          <el-input-number
            v-model="bookingForm.duration"
            :min="1"
            :max="12"
            @change="calculateTotal"
          />
          <span style="margin-left: 12px;">小时</span>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="bookingForm.contactPhone" placeholder="请输入您的联系电话" />
        </el-form-item>
        <el-form-item label="预约人数">
          <el-input-number
            v-model="bookingForm.peopleCount"
            :min="1"
            :max="selectedVenue.maxCapacity"
          />
          <span style="margin-left: 12px;">人</span>
        </el-form-item>
        <el-form-item label="总价">
          <el-input :value="`¥${bookingForm.totalAmount}`" disabled>
            <template #prepend>💰</template>
          </el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="bookingForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBooking" :loading="booking">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getVenues } from '@/api/venue'
import { createVenueBooking } from '@/api/venue-booking'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const booking = ref(false)
const venueList = ref([])
const filterDifficulty = ref('all')
const bookingDialogVisible = ref(false)
const selectedVenue = ref({})
const bookingForm = reactive({
  bookingTime: '',
  duration: 2,
  contactPhone: '',
  peopleCount: 1,
  totalAmount: 0,
  remark: ''
})

onMounted(() => {
  loadVenues()
})

const loadVenues = async () => {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 100 }
    const res = await getVenues(params)
    if (res.code === 200) {
      let list = res.data.list || []
      if (filterDifficulty.value !== 'all') {
        list = list.filter(v => v.difficultyLevel === filterDifficulty.value)
      }
      venueList.value = list
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => loadVenues()

const getDifficultyClass = (level) => {
  const map = { '初级': 'easy', '中级': 'medium', '高级': 'hard' }
  return map[level] || 'easy'
}

const getDifficultyIcon = (level) => {
  const map = { '初级': '🟢', '中级': '🔵', '高级': '🔴' }
  return map[level] || '⛷️'
}

const viewDetail = (venue) => {
  handleReserve(venue)
}

// 打开预约对话框
const handleReserve = (venue) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  selectedVenue.value = venue
  bookingForm.bookingTime = new Date()
  bookingForm.duration = 2
  bookingForm.contactPhone = userStore.userInfo?.phone || ''
  bookingForm.peopleCount = 1
  bookingForm.remark = ''
  calculateTotal()
  bookingDialogVisible.value = true
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 计算总金额
const calculateTotal = () => {
  bookingForm.totalAmount = (selectedVenue.value.rentalPrice || 0) * bookingForm.duration
}

// 确认预约
const confirmBooking = async () => {
  if (!bookingForm.bookingTime) {
    ElMessage.warning('请选择预约时间')
    return
  }
  
  if (!bookingForm.contactPhone) {
    ElMessage.warning('请输入联系方式')
    return
  }
  
  if (bookingForm.peopleCount > selectedVenue.value.maxCapacity) {
    ElMessage.warning(`预约人数不能超过场地容量（${selectedVenue.value.maxCapacity}人）`)
    return
  }
  
  try {
    booking.value = true
    
    const bookingTime = new Date(bookingForm.bookingTime)
    const endTimeDate = new Date(bookingTime)
    endTimeDate.setHours(endTimeDate.getHours() + bookingForm.duration)
    
    const pad = (n) => String(n).padStart(2, '0')
    const formatDate = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
    const formatTime = (d) => `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    
    const bookingData = {
      userId: userStore.userInfo.id,
      userName: userStore.userInfo.realName || userStore.userInfo.username,
      venueId: selectedVenue.value.id,
      venueName: selectedVenue.value.venueName,
      bookingDate: formatDate(bookingTime),
      startTime: formatTime(bookingTime),
      endTime: formatTime(endTimeDate),
      hours: bookingForm.duration,
      peopleCount: bookingForm.peopleCount,
      totalAmount: bookingForm.totalAmount,
      status: 0,
      remark: bookingForm.remark
    }
    
    const response = await createVenueBooking(bookingData)
    
    if (response.code === 200) {
      ElMessage.success('场地预约成功！请等待确认')
      bookingDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '预约失败')
    }
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error('预约失败')
  } finally {
    booking.value = false
  }
}
</script>

<style scoped>
.front-venues-page {
  background: #ffffff;
  min-height: calc(100vh - 80px);
  padding: 40px 0;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 50px;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
}

/* 场地网格 */
.venues-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 30px;
}

.venue-card {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.venue-card:hover {
  transform: translateY(-8px);
  border-color: #1890ff;
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.12);
}

.venue-image {
  position: relative;
  height: 240px;
  background: #f0f2f5;
  overflow: hidden;
}

.venue-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.difficulty-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  display: flex;
  align-items: center;
  gap: 6px;
}

.difficulty-badge.easy { background: #52c41a; }
.difficulty-badge.medium { background: #1890ff; }
.difficulty-badge.hard { background: #ff4d4f; }

.difficulty-icon {
  font-size: 16px;
}

.venue-info {
  padding: 24px;
}

.venue-name {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.venue-type {
  font-size: 13px;
  color: #8c8c8c;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.venue-stats {
  display: flex;
  justify-content: space-around;
  padding: 16px 0;
  margin-bottom: 16px;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.venue-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price {
  font-size: 28px;
  font-weight: 700;
  color: #ff4d4f;
}

.unit {
  font-size: 12px;
  color: #8c8c8c;
}

.reserve-btn {
  padding: 10px 24px;
  background: #1890ff;
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reserve-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
}
</style>
