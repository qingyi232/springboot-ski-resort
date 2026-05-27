<template>
  <div class="coach-booking-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">教练预约</h1>
      <p class="page-subtitle">选择专业教练，享受一对一指导</p>
    </div>

    <!-- 教练列表 -->
    <div class="coach-container" v-loading="loading">
      <el-row :gutter="24">
        <el-col 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6" 
          v-for="coach in coachList" 
          :key="coach.id"
        >
          <div class="coach-card">
            <!-- 教练头像和徽章 -->
            <div class="coach-avatar-section">
              <el-avatar :size="80" :src="coach.avatar || defaultAvatar" />
              <div class="coach-badge" :class="getLevelClass(coach.coachLevel)">
                {{ coach.coachLevel }}
              </div>
            </div>

            <!-- 教练信息 -->
            <div class="coach-info">
              <h3 class="coach-name">{{ coach.coachName }}</h3>
              <div class="coach-rating">
                <el-rate 
                  v-model="coach.rating" 
                  disabled 
                  show-score 
                  text-color="#ff9900"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                />
              </div>

              <div class="coach-stats">
                <div class="stat-item">
                  <el-icon><Trophy /></el-icon>
                  <span>{{ coach.experienceYears }}年经验</span>
                </div>
                <div class="stat-item">
                  <el-icon><User /></el-icon>
                  <span>{{ coach.totalStudents }}位学员</span>
                </div>
              </div>

              <div class="coach-specialty">
                <el-tag 
                  v-for="(item, index) in getSpecialtyList(coach.specialty)" 
                  :key="index"
                  size="small"
                  type="info"
                >
                  {{ item }}
                </el-tag>
              </div>

              <div class="coach-price">
                <span class="price-label">课时费</span>
                <span class="price-value">¥{{ coach.hourlyRate }}</span>
                <span class="price-unit">/小时</span>
              </div>

              <el-button 
                type="primary" 
                class="book-btn"
                @click="handleBook(coach)"
                :disabled="coach.status !== 1"
              >
                立即预约
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 预约对话框 -->
    <el-dialog 
      v-model="bookingDialogVisible" 
      title="预约教练"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="教练">
          <el-input v-model="selectedCoach.coachName" disabled>
            <template #prepend>
              <el-avatar :size="32" :src="selectedCoach.avatar || defaultAvatar" />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="预约日期" prop="bookingDate">
          <el-date-picker
            v-model="bookingForm.bookingDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            style="width: 100%"
            @change="loadAvailableSlots"
          />
        </el-form-item>

        <el-form-item label="时间段" prop="timeSlot">
          <div class="time-slots">
            <div 
              v-for="slot in availableSlots" 
              :key="slot.value"
              class="time-slot"
              :class="{ 'selected': bookingForm.timeSlot === slot.value, 'disabled': slot.disabled }"
              @click="selectTimeSlot(slot)"
            >
              {{ slot.label }}
            </div>
          </div>
        </el-form-item>

        <el-form-item label="预约时长" prop="hours">
          <el-input-number 
            v-model="bookingForm.hours" 
            :min="1" 
            :max="8"
            @change="calculateAmount"
          />
          <span style="margin-left: 10px">小时</span>
        </el-form-item>

        <el-form-item label="课程类型" prop="courseType">
          <el-select v-model="bookingForm.courseType" placeholder="请选择课程类型">
            <el-option label="单板入门" value="单板入门" />
            <el-option label="双板入门" value="双板入门" />
            <el-option label="单板进阶" value="单板进阶" />
            <el-option label="双板进阶" value="双板进阶" />
            <el-option label="自由式" value="自由式" />
            <el-option label="竞技训练" value="竞技训练" />
          </el-select>
        </el-form-item>

        <el-form-item label="预约金额">
          <div class="amount-detail">
            <div class="amount-item">
              <span>课时费：</span>
              <span>¥{{ selectedCoach.hourlyRate }} × {{ bookingForm.hours }}小时</span>
            </div>
            <div class="amount-total">
              <span>总计：</span>
              <span class="total-price">¥{{ totalAmount }}</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input 
            v-model="bookingForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入您的需求或特殊要求"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBooking" :loading="submitting">
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Trophy, User } from '@element-plus/icons-vue'
import { getCoachList } from '@/api/coach'
import { createCoachBooking } from '@/api/coach-booking'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 数据
const loading = ref(false)
const submitting = ref(false)
const coachList = ref([])
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 预约对话框
const bookingDialogVisible = ref(false)
const bookingFormRef = ref(null)
const selectedCoach = ref({})
const availableSlots = ref([])

const bookingForm = reactive({
  bookingDate: '',
  timeSlot: '',
  hours: 2,
  courseType: '',
  remark: ''
})

const bookingRules = {
  bookingDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  hours: [{ required: true, message: '请输入预约时长', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }]
}

// 计算总金额
const totalAmount = computed(() => {
  if (!selectedCoach.value.hourlyRate) return '0.00'
  const amount = selectedCoach.value.hourlyRate * bookingForm.hours
  return amount.toFixed(2)
})

// 获取教练等级样式
const getLevelClass = (level) => {
  const levelMap = {
    '初级': 'level-junior',
    '中级': 'level-intermediate',
    '高级': 'level-senior',
    '专业': 'level-professional'
  }
  return levelMap[level] || ''
}

// 获取专长列表
const getSpecialtyList = (specialty) => {
  if (!specialty) return []
  return specialty.split(',').slice(0, 3)
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000
}

// 加载可用时间段
const loadAvailableSlots = () => {
  // 模拟可用时间段
  availableSlots.value = [
    { label: '09:00-10:00', value: '09:00', disabled: false },
    { label: '10:00-11:00', value: '10:00', disabled: false },
    { label: '11:00-12:00', value: '11:00', disabled: true },
    { label: '13:00-14:00', value: '13:00', disabled: false },
    { label: '14:00-15:00', value: '14:00', disabled: false },
    { label: '15:00-16:00', value: '15:00', disabled: false },
    { label: '16:00-17:00', value: '16:00', disabled: false },
    { label: '17:00-18:00', value: '17:00', disabled: true }
  ]
}

// 选择时间段
const selectTimeSlot = (slot) => {
  if (slot.disabled) return
  bookingForm.timeSlot = slot.value
}

// 加载教练列表
const loadData = async () => {
  loading.value = true
  try {
    const response = await getCoachList()
    if (response.code === 200) {
      coachList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 预约教练
const handleBook = (coach) => {
  selectedCoach.value = coach
  bookingDialogVisible.value = true
  // 清空表单
  Object.assign(bookingForm, {
    bookingDate: '',
    timeSlot: '',
    hours: 2,
    courseType: '',
    remark: ''
  })
}

// 计算金额
const calculateAmount = () => {
  // 自动计算
}

// 确认预约
const confirmBooking = async () => {
  const valid = await bookingFormRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      coachId: selectedCoach.value.id,
      coachName: selectedCoach.value.coachName,
      bookingDate: bookingForm.bookingDate,
      startTime: bookingForm.timeSlot + ':00',
      hours: bookingForm.hours,
      hourlyRate: selectedCoach.value.hourlyRate,
      totalAmount: parseFloat(totalAmount.value),
      remark: bookingForm.remark,
      userId: userStore.userInfo?.id,
      status: 0,
      paymentStatus: 0
    }

    const response = await createCoachBooking(data)
    if (response.code === 200) {
      ElMessage.success('预约成功！')
      bookingDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('预约失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.coach-booking-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
}

/* 教练卡片 */
.coach-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.coach-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.coach-avatar-section {
  position: relative;
  text-align: center;
  margin-bottom: 16px;
}

.coach-badge {
  position: absolute;
  top: 0;
  right: 50%;
  transform: translateX(50px);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.level-junior {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.level-intermediate {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.level-senior {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.level-professional {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.coach-info {
  text-align: center;
}

.coach-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.coach-rating {
  margin-bottom: 16px;
}

.coach-stats {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.coach-specialty {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.coach-price {
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 16px;
}

.price-label {
  font-size: 14px;
  color: #909399;
}

.price-value {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.price-unit {
  font-size: 12px;
  color: #909399;
}

.book-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 12px;
}

/* 时间段选择 */
.time-slots {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.time-slot {
  padding: 12px;
  text-align: center;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.time-slot:hover:not(.disabled) {
  border-color: #409eff;
  background: #ecf5ff;
}

.time-slot.selected {
  border-color: #409eff;
  background: #409eff;
  color: white;
}

.time-slot.disabled {
  background: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
}

/* 金额详情 */
.amount-detail {
  width: 100%;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #606266;
}

.amount-total {
  display: flex;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  font-weight: 600;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
}

/* 响应式 */
@media (max-width: 768px) {
  .time-slots {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>







