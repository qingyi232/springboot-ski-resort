<template>
  <div class="course-enrollment">
    <!-- 顶部Banner -->
    <div class="banner">
      <div class="banner-content">
        <h1 class="gradient-text">课程报名</h1>
        <p class="subtitle">专业教学 · 系统培训 · 快速提升</p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-radio-group v-model="filters.difficulty" @change="handleFilterChange">
          <el-radio-button label="">全部难度</el-radio-button>
          <el-radio-button label="easy">入门</el-radio-button>
          <el-radio-button label="medium">进阶</el-radio-button>
          <el-radio-button label="hard">高级</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="filters.status" @change="handleFilterChange" style="margin-left: 16px;">
          <el-radio-button label="">全部状态</el-radio-button>
          <el-radio-button label="enrolling">报名中</el-radio-button>
          <el-radio-button label="ongoing">进行中</el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-right">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索课程名称"
          prefix-icon="Search"
          clearable
          @input="handleSearch"
          style="width: 300px;"
        />
      </div>
    </div>

    <!-- 课程列表 -->
    <div class="course-list" v-loading="loading">
      <div
        v-for="course in courses"
        :key="course.id"
        class="course-card"
      >
        <div class="course-header">
          <div class="course-type">
            <el-tag :type="getDifficultyType(course.difficulty)" size="large">
              {{ getDifficultyText(course.difficulty) }}
            </el-tag>
            <el-tag type="info" size="large" style="margin-left: 8px;">
              {{ course.type }}
            </el-tag>
          </div>
          <div class="course-status">
            <el-tag :type="getStatusType(course.status)">
              {{ getStatusText(course.status) }}
            </el-tag>
          </div>
        </div>

        <h3 class="course-name">{{ course.name }}</h3>
        <p class="course-desc">{{ course.description }}</p>

        <div class="course-info-grid">
          <div class="info-item">
            <el-icon><User /></el-icon>
            <span>教练：{{ course.coachName }}</span>
          </div>
          <div class="info-item">
            <el-icon><Clock /></el-icon>
            <span>时长：{{ course.duration }}分钟</span>
          </div>
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span>开始：{{ formatDate(course.startDate) }}</span>
          </div>
          <div class="info-item">
            <el-icon><UserFilled /></el-icon>
            <span>人数：{{ course.currentStudents }}/{{ course.maxStudents }}</span>
          </div>
          <div class="info-item" v-if="course.totalLessons">
            <el-icon><Document /></el-icon>
            <span>课节：共{{ course.totalLessons }}节，每节{{ course.perLessonHours || '-' }}小时</span>
          </div>
          <div class="info-item" v-if="course.latePolicy !== null && course.latePolicy !== undefined">
            <el-icon><Clock /></el-icon>
            <span>迟到规则：{{ getLatePolicyText(course.latePolicy) }}</span>
          </div>
        </div>

        <!-- 课程大纲 -->
        <div class="course-syllabus" v-if="course.syllabus">
          <div class="syllabus-title">
            <el-icon><Document /></el-icon>
            <span>课程大纲</span>
          </div>
          <div class="syllabus-content">{{ course.syllabus }}</div>
        </div>

        <!-- 进度条 -->
        <div class="enrollment-progress">
          <div class="progress-text">
            <span>报名进度</span>
            <span>{{ ((course.currentStudents / course.maxStudents) * 100).toFixed(0) }}%</span>
          </div>
          <el-progress
            :percentage="(course.currentStudents / course.maxStudents) * 100"
            :status="course.currentStudents >= course.maxStudents ? 'exception' : ''"
            :show-text="false"
          />
        </div>

        <div class="course-footer">
          <div class="course-price">
            <span class="price">¥{{ course.price }}</span>
            <span class="per-class">/ {{ (course.duration / 60).toFixed(1) }}课时</span>
          </div>
          <el-button
            type="primary"
            size="large"
            @click="enrollCourse(course)"
            :disabled="course.currentStudents >= course.maxStudents || course.status !== 'enrolling'"
          >
            {{ course.currentStudents >= course.maxStudents ? '名额已满' : '立即报名' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && courses.length === 0" description="暂无课程" />

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[6, 12, 18, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 报名对话框 -->
    <el-dialog
      v-model="enrollDialogVisible"
      title="课程报名"
      width="600px"
      @close="resetEnrollForm"
    >
      <el-form :model="enrollForm" :rules="enrollRules" ref="enrollFormRef" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="selectedCourse.name" disabled />
        </el-form-item>
        <el-form-item label="课程价格">
          <el-input :value="`¥${selectedCourse.price}`" disabled />
        </el-form-item>
        <el-form-item label="学员姓名" prop="studentName">
          <el-input v-model="enrollForm.studentName" placeholder="请输入学员真实姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="enrollForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="enrollForm.idCard" placeholder="请输入身份证号（用于保险）" />
        </el-form-item>
        <el-form-item label="滑雪经验" prop="experience">
          <el-select v-model="enrollForm.experience" placeholder="请选择滑雪经验" style="width: 100%;">
            <el-option label="零基础" value="beginner" />
            <el-option label="有一定基础" value="intermediate" />
            <el-option label="较为熟练" value="advanced" />
            <el-option label="专业级别" value="professional" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="enrollForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入特殊需求或备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="enrollDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEnrollment" :loading="submitting">
          确认报名
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Clock, Calendar, UserFilled, Document } from '@element-plus/icons-vue'
import { getCourses } from '@/api/course'
import { createCourseEnrollment } from '@/api/course-enrollment'
import { debounce, formatDate } from '@/utils/performance'

// 筛选条件
const filters = ref({
  difficulty: '',
  status: '',
  keyword: ''
})

// 课程列表
const courses = ref([])
const loading = ref(false)
const pagination = ref({
  page: 1,
  pageSize: 6
})
const total = ref(0)

// 报名对话框
const enrollDialogVisible = ref(false)
const selectedCourse = ref({})
const submitting = ref(false)
const enrollFormRef = ref(null)

const enrollForm = reactive({
  studentName: '',
  phone: '',
  idCard: '',
  experience: '',
  remark: ''
})

const enrollRules = {
  studentName: [
    { required: true, message: '请输入学员姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^\d{17}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  experience: [
    { required: true, message: '请选择滑雪经验', trigger: 'change' }
  ]
}

// 加载课程列表
const loadCourses = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      difficulty: filters.value.difficulty,
      status: filters.value.status,
      keyword: filters.value.keyword
    }
    
    const response = await getCourses(params)
    if (response.code === 200) {
      courses.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  pagination.value.page = 1
  loadCourses()
}

// 搜索（防抖）
const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadCourses()
}, 300)

// 分页大小变化
const handleSizeChange = () => {
  pagination.value.page = 1
  loadCourses()
}

// 页码变化
const handlePageChange = () => {
  loadCourses()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取难度类型
const getDifficultyType = (difficulty) => {
  const typeMap = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  }
  return typeMap[difficulty] || 'info'
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const textMap = {
    'easy': '入门',
    'medium': '进阶',
    'hard': '高级'
  }
  return textMap[difficulty] || difficulty
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'enrolling': 'success',
    'ongoing': 'warning',
    'completed': 'info'
  }
  return typeMap[status] || 'info'
}

// 获取迟到规则文本
const getLatePolicyText = (policy) => {
  const map = { 0: '迟到课时作废', 1: '可延期补课', 2: '迟到15分钟内可上课' }
  return map[policy] || '可延期补课'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'enrolling': '报名中',
    'ongoing': '进行中',
    'completed': '已结束'
  }
  return textMap[status] || status
}

// 报名课程
const enrollCourse = (course) => {
  selectedCourse.value = course
  enrollDialogVisible.value = true
}

// 重置报名表单
const resetEnrollForm = () => {
  enrollFormRef.value?.resetFields()
  Object.assign(enrollForm, {
    studentName: '',
    phone: '',
    idCard: '',
    experience: '',
    remark: ''
  })
}

// 提交报名
const submitEnrollment = async () => {
  try {
    await enrollFormRef.value.validate()
    
    await ElMessageBox.confirm(
      `确认报名课程《${selectedCourse.value.name}》？报名费用为¥${selectedCourse.value.price}`,
      '确认报名',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    submitting.value = true
    
    const data = {
      courseId: selectedCourse.value.id,
      ...enrollForm
    }
    
    const response = await createCourseEnrollment(data)
    
    if (response.code === 200) {
      ElMessage.success('报名成功！')
      enrollDialogVisible.value = false
      loadCourses() // 刷新列表
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('报名失败:', error)
      ElMessage.error(error.message || '报名失败')
    }
  } finally {
    submitting.value = false
  }
}

// 组件挂载
onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.course-enrollment {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding-bottom: 60px;
}

/* Banner */
.banner {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  padding: 80px 20px;
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.gradient-text {
  font-size: 48px;
  font-weight: bold;
  margin: 0 0 16px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  font-size: 20px;
  opacity: 0.95;
  margin: 0;
}

/* 筛选栏 */
.filter-bar {
  max-width: 1200px;
  margin: 0 auto 32px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-left {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

/* 课程列表 */
.course-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
  min-height: 400px;
}

.course-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.course-type {
  display: flex;
  gap: 8px;
}

.course-name {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.course-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 20px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 44px;
}

.course-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.info-item .el-icon {
  color: #409eff;
}

/* 课程大纲 */
.course-syllabus {
  margin-bottom: 20px;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 12px;
  border-left: 4px solid #409eff;
}

.syllabus-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.syllabus-title .el-icon {
  color: #409eff;
}

.syllabus-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 报名进度 */
.enrollment-progress {
  margin-bottom: 20px;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.course-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.per-class {
  font-size: 14px;
  color: #909399;
}

/* 分页 */
.pagination {
  max-width: 1200px;
  margin: 40px auto 0;
  padding: 0 20px;
  display: flex;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .gradient-text {
    font-size: 32px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-left,
  .filter-right {
    width: 100%;
  }
  
  .filter-right .el-input {
    width: 100% !important;
  }
  
  .course-list {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .course-info-grid {
    grid-template-columns: 1fr;
  }
}
</style>







