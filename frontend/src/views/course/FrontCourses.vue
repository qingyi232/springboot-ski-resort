<template>
  <div class="front-courses-page">
    <!-- 课程列表 -->
    <div class="page-container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-row">
          <el-radio-group v-model="filterType" @change="handleFilterChange" size="large">
            <el-radio-button label="all">全部课程</el-radio-button>
            <el-radio-button label="初级入门">初级入门</el-radio-button>
            <el-radio-button label="中级进阶">中级进阶</el-radio-button>
            <el-radio-button label="高级提升">高级提升</el-radio-button>
          </el-radio-group>

          <el-select v-model="sortBy" placeholder="排序" size="large" style="width: 180px" @change="handleSort">
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="人气最高" value="popular" />
          </el-select>
        </div>
        <div class="filter-row coach-filter">
          <span class="filter-label">按教练筛选：</span>
          <el-radio-group v-model="filterCoachId" @change="handleCoachFilter" size="default">
            <el-radio-button :label="0">全部教练</el-radio-button>
            <el-radio-button v-for="c in coachList" :key="c.id" :label="c.id">
              {{ c.coachName }}（{{ c.coachLevel }}）
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 课程网格 -->
      <div class="courses-grid" v-loading="loading">
        <div v-for="course in courseList" :key="course.id" class="course-card">
          <div class="course-image" @click="viewDetail(course)">
            <img :src="course.imageUrl || 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=600&q=80'" :alt="course.courseName" />
            <div class="course-badge" :class="getDifficultyClass(course.difficulty)">
              {{ getDifficultyIcon(course.difficulty) }} {{ course.difficulty || '中级' }}
            </div>
          </div>
          <div class="course-info">
            <h3 class="course-name">{{ course.courseName }}</h3>
            <div class="course-meta">
              <div class="meta-item">
                <span class="meta-icon">👨‍🏫</span>
                <span>{{ course.coachName }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">⏱</span>
                <span>{{ course.courseDuration }}h</span>
              </div>
              <div class="meta-item" v-if="course.totalLessons">
                <span class="meta-icon">📚</span>
                <span>共{{ course.totalLessons }}节</span>
              </div>
            </div>
            <div class="course-extra" v-if="course.totalLessons || course.latePolicy !== null">
              <span class="extra-tag" v-if="course.perLessonHours">每节{{ course.perLessonHours }}h</span>
              <span class="extra-tag late-tag" :class="'late-' + course.latePolicy">{{ getLatePolicyText(course.latePolicy) }}</span>
            </div>
            <div class="course-footer">
              <div class="price-wrapper">
                <span class="price-label">课程价格</span>
                <span class="price-value">¥{{ course.coursePrice }}</span>
              </div>
              <button class="book-btn" @click.stop="bookCourse(course)">
                <span>立即预约</span>
                <span class="arrow-icon">→</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && courseList.length === 0" description="暂无课程" />
    </div>

    <!-- 预约对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="课程预约" width="600px">
      <el-form :model="bookingForm" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="selectedCourse.courseName" disabled />
        </el-form-item>
        <el-form-item label="教练">
          <el-input v-model="selectedCourse.coachName" disabled />
        </el-form-item>
        <el-form-item label="课程价格">
          <el-input v-model="selectedCourse.coursePrice" disabled>
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="预约类型">
          <el-radio-group v-model="bookingForm.bookingForOther" @change="handleBookingTypeChange">
            <el-radio :label="0">给自己预约</el-radio>
            <el-radio :label="1">帮他人预约</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="bookingForm.bookingForOther === 1">
          <el-form-item label="上课人姓名">
            <el-input v-model="bookingForm.studentName" placeholder="请输入实际上课人姓名" />
          </el-form-item>
          <el-form-item label="上课人电话">
            <el-input v-model="bookingForm.studentPhone" placeholder="请输入实际上课人手机号" />
          </el-form-item>
        </template>
        <el-form-item label="预约人数">
          <el-input-number v-model="bookingForm.personCount" :min="1" :max="5" @change="handlePersonCountChange" />
          <span style="margin-left: 12px; color: #909399; font-size: 13px;">
            实付：¥{{ computedTotalAmount }}
            <span v-if="bookingForm.personCount > 1" style="color: #52c41a;">（多人无额外优惠）</span>
          </span>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="bookingForm.bookingDate"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
            :disabled-date="disabledDate"
            @change="handleDateChange"
          />
        </el-form-item>
        <!-- 教练日程可视化 -->
        <el-form-item label="教练日程" v-if="coachSchedule">
          <div class="schedule-panel">
            <div class="schedule-summary">
              <span class="schedule-coach">{{ coachSchedule.coachName }} · {{ bookingForm.bookingDate ? formatDate(bookingForm.bookingDate) : '' }}</span>
              <span class="schedule-hours">
                剩余可约：<strong :class="coachSchedule.remainingHours > 2 ? 'green' : coachSchedule.remainingHours > 0 ? 'orange' : 'red'">{{ coachSchedule.remainingHours }}h</strong>
                / {{ coachSchedule.maxDailyHours }}h
              </span>
            </div>
            <div class="schedule-bar">
              <div class="time-axis">
                <span v-for="h in timeLabels" :key="h" class="time-label">{{ h }}</span>
              </div>
              <div class="bar-track">
                <div v-for="(slot, idx) in coachSchedule.bookedSlots" :key="idx"
                  class="bar-block booked"
                  :style="getSlotStyle(slot)"
                  :title="`${slot.startTime}-${slot.endTime} ${slot.courseName}`">
                </div>
              </div>
            </div>
            <div class="schedule-legend">
              <span class="legend-item"><span class="legend-dot booked-dot"></span>已预约</span>
              <span class="legend-item"><span class="legend-dot free-dot"></span>可预约</span>
            </div>
            <div class="booked-list" v-if="coachSchedule.bookedSlots && coachSchedule.bookedSlots.length > 0">
              <div class="booked-item" v-for="(slot, idx) in coachSchedule.bookedSlots" :key="idx">
                <el-tag type="danger" size="small">{{ slot.startTime?.substring(0,5) }}-{{ slot.endTime?.substring(0,5) }}</el-tag>
                <span>{{ slot.courseName }} ({{ slot.hours }}h)</span>
              </div>
            </div>
            <div v-else style="color: #52c41a; font-size: 13px;">当天暂无预约，全天可约</div>
          </div>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="bookingForm.contactPhone" placeholder="请输入您的联系电话" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="bookingForm.remark"
            type="textarea"
            :rows="3"
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
import { ref, onMounted, reactive, computed } from 'vue'
import request from '@/utils/request'
import { getCourses } from '@/api/course'
import { createBooking } from '@/api/booking'
import { getActiveCoaches, getCoachSchedule } from '@/api/coach'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const booking = ref(false)
const courseList = ref([])
const allCourses = ref([])
const coachList = ref([])
const filterType = ref('all')
const filterCoachId = ref(0)
const sortBy = ref('popular')
const bookingDialogVisible = ref(false)
const selectedCourse = ref({})
const coachSchedule = ref(null)
const timeLabels = ['08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20']
const bookingForm = reactive({
  bookingDate: '',
  contactPhone: '',
  remark: '',
  bookingForOther: 0,
  studentName: '',
  studentPhone: '',
  personCount: 1
})

const computedTotalAmount = computed(() => {
  const price = selectedCourse.value?.coursePrice || 0
  return (price * bookingForm.personCount).toFixed(2)
})

const handleBookingTypeChange = (val) => {
  if (val === 0) {
    bookingForm.studentName = ''
    bookingForm.studentPhone = ''
  }
}

const handlePersonCountChange = () => {
  // 价格按人数计算，暂无额外优惠
}

onMounted(() => {
  loadCourses()
  loadCoaches()
})

const loadCoaches = async () => {
  try {
    const res = await getActiveCoaches()
    if (res.code === 200) coachList.value = res.data || []
  } catch (e) { console.error(e) }
}

const loadCourses = async () => {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 100 }
    if (filterType.value !== 'all') params.courseType = filterType.value
    
    const url = filterType.value !== 'all' ? '/api/v1/courses/page/condition' : '/api/v1/courses/page'
    const res = await request({ url, method: 'get', params })
    if (res.code === 200) {
      allCourses.value = res.data.list || []
      applyCoachFilter()
      sortCourses()
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

const applyCoachFilter = () => {
  if (filterCoachId.value === 0) {
    courseList.value = [...allCourses.value]
  } else {
    courseList.value = allCourses.value.filter(c => c.coachId === filterCoachId.value)
  }
}

const handleFilterChange = () => loadCourses()
const handleCoachFilter = () => { applyCoachFilter(); sortCourses() }
const handleSort = () => sortCourses()

const sortCourses = () => {
  if (sortBy.value === 'price_asc') {
    courseList.value.sort((a, b) => a.coursePrice - b.coursePrice)
  } else if (sortBy.value === 'price_desc') {
    courseList.value.sort((a, b) => b.coursePrice - a.coursePrice)
  } else {
    courseList.value.sort((a, b) => b.currentStudents - a.currentStudents)
  }
}

const getDifficultyClass = (level) => {
  const map = { '初级': 'easy', '中级': 'medium', '高级': 'hard' }
  return map[level] || 'medium'
}

const getDifficultyIcon = (level) => {
  const map = { '初级': '🟢', '中级': '🔵', '高级': '🔴' }
  return map[level] || '🔵'
}

const getLatePolicyText = (policy) => {
  const map = { 0: '迟到课时作废', 1: '可延期补课', 2: '迟到15分钟内可上课' }
  return map[policy] || '可延期补课'
}

const viewDetail = (course) => {
  const lessonsInfo = course.totalLessons ? `\n课程节数：共${course.totalLessons}节，每节${course.perLessonHours || '-'}小时` : ''
  const lateInfo = course.latePolicy !== null && course.latePolicy !== undefined ? `\n迟到规则：${getLatePolicyText(course.latePolicy)}` : ''
  ElMessage({
    message: `课程名称：${course.courseName}\n教练：${course.coachName}\n价格：¥${course.coursePrice}\n时长：${course.courseDuration}小时${lessonsInfo}${lateInfo}\n难度：${course.difficulty}`,
    type: 'info',
    duration: 5000,
    showClose: true
  })
}

const formatDate = (d) => {
  const dt = new Date(d)
  return `${dt.getFullYear()}-${String(dt.getMonth() + 1).padStart(2, '0')}-${String(dt.getDate()).padStart(2, '0')}`
}

const handleDateChange = async (val) => {
  if (!val || !selectedCourse.value?.coachId) { coachSchedule.value = null; return }
  try {
    const dateStr = formatDate(val)
    const res = await getCoachSchedule(selectedCourse.value.coachId, dateStr)
    if (res.code === 200) coachSchedule.value = res.data
  } catch (e) { console.error(e) }
}

const getSlotStyle = (slot) => {
  const parseMinutes = (t) => {
    if (!t) return 0
    const parts = t.split(':')
    return parseInt(parts[0]) * 60 + parseInt(parts[1])
  }
  const dayStart = 8 * 60
  const daySpan = 12 * 60
  const start = parseMinutes(slot.startTime) - dayStart
  const end = parseMinutes(slot.endTime) - dayStart
  const left = Math.max(0, (start / daySpan) * 100)
  const width = Math.max(2, ((end - start) / daySpan) * 100)
  return { left: left + '%', width: width + '%' }
}

// 预约课程
const bookCourse = (course) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  selectedCourse.value = course
  bookingForm.bookingDate = ''
  bookingForm.contactPhone = userStore.userInfo?.phone || ''
  bookingForm.remark = ''
  bookingForm.bookingForOther = 0
  bookingForm.studentName = ''
  bookingForm.studentPhone = ''
  bookingForm.personCount = 1
  coachSchedule.value = null
  bookingDialogVisible.value = true
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 确认预约
const confirmBooking = async () => {
  if (!bookingForm.bookingDate) {
    ElMessage.warning('请选择预约时间')
    return
  }
  
  if (!bookingForm.contactPhone) {
    ElMessage.warning('请输入联系方式')
    return
  }
  
  try {
    booking.value = true
    
    // 处理日期时间格式
    const bookingDateTime = new Date(bookingForm.bookingDate)
    const bookingDateOnly = bookingDateTime.toISOString().split('T')[0] // yyyy-MM-dd
    const bookingTimeOnly = bookingDateTime.toTimeString().split(' ')[0] // HH:mm:ss
    
    // 计算结束时间（假设课程时长）
    const courseDuration = selectedCourse.value.courseDuration || 2
    const endDateTime = new Date(bookingDateTime.getTime() + courseDuration * 60 * 60 * 1000)
    const endTimeOnly = endDateTime.toTimeString().split(' ')[0]
    
    const bookingData = {
      userId: userStore.userInfo.id,
      userName: userStore.userInfo.realName || userStore.userInfo.username,
      bookingForOther: bookingForm.bookingForOther,
      studentName: bookingForm.bookingForOther === 1 ? bookingForm.studentName : (userStore.userInfo.realName || userStore.userInfo.username),
      studentPhone: bookingForm.bookingForOther === 1 ? bookingForm.studentPhone : bookingForm.contactPhone,
      personCount: bookingForm.personCount,
      coachId: selectedCourse.value.coachId,
      coachName: selectedCourse.value.coachName,
      courseId: selectedCourse.value.id,
      courseName: selectedCourse.value.courseName,
      bookingDate: bookingDateOnly,
      startTime: bookingTimeOnly,
      endTime: endTimeOnly,
      hours: courseDuration,
      totalAmount: parseFloat(computedTotalAmount.value),
      status: 0,
      paymentStatus: 0,
      remark: `联系电话：${bookingForm.contactPhone}${bookingForm.remark ? '，备注：' + bookingForm.remark : ''}`
    }
    
    const response = await createBooking(bookingData)
    
    if (response.code === 200) {
      ElMessage.success('预约成功！请等待确认')
      bookingDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '预约失败')
    }
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error(error.message || '预约失败')
  } finally {
    booking.value = false
  }
}
</script>

<style scoped>
.front-courses-page {
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
  flex-direction: column;
  gap: 16px;
  margin-bottom: 40px;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coach-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
  font-weight: 500;
}

.coach-filter :deep(.el-radio-group) {
  flex-wrap: nowrap;
}

.coach-filter :deep(.el-radio-button__inner) {
  padding: 6px 14px;
  font-size: 13px;
}

/* 课程网格 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.course-card {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-8px);
  border-color: #1890ff;
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.12);
}

.course-image {
  position: relative;
  height: 220px;
  background: #f0f2f5;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-badge.easy { background: #52c41a; }
.course-badge.medium { background: #1890ff; }
.course-badge.hard { background: #ff4d4f; }

.course-info {
  padding: 24px;
}

.course-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #8c8c8c;
}

.meta-icon {
  font-size: 16px;
}

.course-extra {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.extra-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  background: #f0f5ff;
  color: #1890ff;
  border: 1px solid #d6e4ff;
}

.late-tag.late-0 {
  background: #fff1f0;
  color: #ff4d4f;
  border-color: #ffa39e;
}

.late-tag.late-1 {
  background: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}

.late-tag.late-2 {
  background: #fffbe6;
  color: #faad14;
  border-color: #ffe58f;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.price-wrapper {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.price-label {
  font-size: 12px;
  color: #8c8c8c;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #ff4d4f;
}

.book-btn {
  padding: 8px 20px;
  background: #1890ff;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.book-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.arrow-icon {
  transition: transform 0.3s ease;
}

.book-btn:hover .arrow-icon {
  transform: translateX(4px);
}

/* 教练日程面板 */
.schedule-panel {
  width: 100%;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
}

.schedule-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.schedule-coach {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.schedule-hours {
  font-size: 13px;
  color: #606266;
}

.schedule-hours strong.green { color: #52c41a; }
.schedule-hours strong.orange { color: #faad14; }
.schedule-hours strong.red { color: #ff4d4f; }

.schedule-bar {
  margin-bottom: 10px;
}

.time-axis {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.time-label {
  font-size: 11px;
  color: #909399;
  width: calc(100% / 13);
  text-align: center;
}

.bar-track {
  position: relative;
  height: 24px;
  background: linear-gradient(90deg, #f0faf0 0%, #f0faf0 100%);
  border-radius: 4px;
  border: 1px solid #e0e0e0;
}

.bar-block.booked {
  position: absolute;
  top: 2px;
  height: 20px;
  background: #ff4d4f;
  border-radius: 3px;
  opacity: 0.8;
  cursor: pointer;
  transition: opacity 0.2s;
}

.bar-block.booked:hover {
  opacity: 1;
}

.schedule-legend {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.booked-dot { background: #ff4d4f; }
.free-dot { background: #e8f5e9; border: 1px solid #c8e6c9; }

.booked-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.booked-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}
</style>
