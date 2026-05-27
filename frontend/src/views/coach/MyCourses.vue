<template>
  <div class="my-courses-page">
    <div class="page-header">
      <h1>我的课程</h1>
      <p class="subtitle">管理我教授的所有课程</p>
    </div>

    <!-- 课程统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card total">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ courses.length }}</div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card ongoing">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.ongoing }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card students">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStudents }}</div>
              <div class="stat-label">学员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card hot">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.hotCourses }}</div>
              <div class="stat-label">热门课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选和搜索 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" class="search-form">
        <el-form-item label="课程类型">
          <el-select v-model="typeFilter" placeholder="全部类型" clearable style="width: 150px;">
            <el-option label="初级入门" value="初级入门" />
            <el-option label="中级进阶" value="中级进阶" />
            <el-option label="高级提升" value="高级提升" />
            <el-option label="竞技训练" value="竞技训练" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 150px;">
            <el-option label="未开始" :value="0" />
            <el-option label="报名中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已结束" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCourses">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetFilter">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 课程列表 -->
    <el-row :gutter="20" v-loading="loading">
      <el-col 
        :xs="24" 
        :sm="12" 
        :md="8" 
        :lg="6"
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-col"
      >
        <el-card class="course-card" shadow="hover">
          <!-- 课程图片 -->
          <div class="course-image">
            <img 
              :src="course.imageUrl || 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400'" 
              :alt="course.courseName"
            />
            <div class="course-badge" v-if="course.isHot === 1">
              <el-icon><Promotion /></el-icon> 热门
            </div>
            <div class="course-status" :class="`status-${course.status}`">
              {{ getStatusText(course.status) }}
            </div>
          </div>

          <!-- 课程信息 -->
          <div class="course-info">
            <h3 class="course-name">{{ course.courseName }}</h3>
            <div class="course-meta">
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>{{ course.courseDuration }}小时</span>
              </div>
              <div class="meta-item">
                <el-icon><Location /></el-icon>
                <span>{{ course.venueName }}</span>
              </div>
            </div>
            <div class="course-meta">
              <div class="meta-item">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatDate(course.courseDate) }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Timer /></el-icon>
                <span>{{ course.startTime }} - {{ course.endTime }}</span>
              </div>
            </div>
            <div class="course-students">
              <el-progress 
                :percentage="getStudentPercentage(course)" 
                :color="getProgressColor(course)"
              >
                <span class="progress-text">
                  {{ course.currentStudents || 0 }} / {{ course.maxStudents }} 人
                </span>
              </el-progress>
            </div>
            <div class="course-price">
              <span class="price-label">课程费用：</span>
              <span class="price-value">¥{{ course.coursePrice }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="course-actions">
            <el-button type="primary" size="small" @click="viewDetail(course)">
              <el-icon><View /></el-icon> 查看详情
            </el-button>
            <el-button type="success" size="small" @click="viewStudents(course)">
              <el-icon><User /></el-icon> 学员列表
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(course)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(course)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-if="!loading && filteredCourses.length === 0" description="暂无课程数据" />

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="课程详情"
      width="700px"
    >
      <div v-if="currentCourse" class="course-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程编号">{{ currentCourse.courseCode }}</el-descriptions-item>
          <el-descriptions-item label="课程名称">{{ currentCourse.courseName }}</el-descriptions-item>
          <el-descriptions-item label="课程类型">{{ currentCourse.courseType }}</el-descriptions-item>
          <el-descriptions-item label="难度等级">{{ currentCourse.difficulty }}</el-descriptions-item>
          <el-descriptions-item label="授课教练">{{ currentCourse.coachName }}</el-descriptions-item>
          <el-descriptions-item label="授课场地">{{ currentCourse.venueName }}</el-descriptions-item>
          <el-descriptions-item label="课程时长">{{ currentCourse.courseDuration }} 小时</el-descriptions-item>
          <el-descriptions-item label="课程费用">¥{{ currentCourse.coursePrice }}</el-descriptions-item>
          <el-descriptions-item label="开课日期">{{ formatDate(currentCourse.courseDate) }}</el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ currentCourse.startTime }} - {{ currentCourse.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="最大学员数">{{ currentCourse.maxStudents }} 人</el-descriptions-item>
          <el-descriptions-item label="当前学员数">{{ currentCourse.currentStudents || 0 }} 人</el-descriptions-item>
          <el-descriptions-item label="课程状态">
            <el-tag :type="getStatusType(currentCourse.status)">
              {{ getStatusText(currentCourse.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否热门">
            <el-tag :type="currentCourse.isHot === 1 ? 'danger' : 'info'">
              {{ currentCourse.isHot === 1 ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">
            {{ currentCourse.description || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 学员列表对话框 -->
    <el-dialog
      v-model="studentsDialogVisible"
      title="学员列表"
      width="800px"
    >
      <div v-if="currentCourse">
        <div class="students-header">
          <h3>{{ currentCourse.courseName }}</h3>
          <p>当前报名人数：{{ currentStudents.length }} / {{ currentCourse.maxStudents }}</p>
        </div>
        <el-table :data="currentStudents" v-loading="studentsLoading" stripe>
          <el-table-column prop="userName" label="学员姓名" width="120" />
          <el-table-column prop="userPhone" label="联系电话" width="140" />
          <el-table-column prop="bookingDate" label="预约日期" width="120">
            <template #default="{ row }">
              {{ formatDate(row.bookingDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="上课时间" width="180">
            <template #default="{ row }">
              {{ row.startTime }} - {{ row.endTime }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getBookingStatusType(row.status)">
                {{ getBookingStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="140">
            <template #default="{ row }">
              <el-rate v-if="row.rating" v-model="row.rating" disabled size="small" />
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="studentsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑课程对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑课程"
      width="750px"
      :close-on-click-modal="false"
    >
      <el-form ref="editFormRef" :model="editFormData" :rules="editFormRules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程编号" prop="courseCode">
              <el-input v-model="editFormData.courseCode" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="editFormData.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="editFormData.courseType" placeholder="请选择课程类型" style="width: 100%">
                <el-option label="初级入门" value="初级入门" />
                <el-option label="中级进阶" value="中级进阶" />
                <el-option label="高级提升" value="高级提升" />
                <el-option label="竞技训练" value="竞技训练" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="editFormData.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="初级" value="初级" />
                <el-option label="中级" value="中级" />
                <el-option label="高级" value="高级" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程价格" prop="coursePrice">
              <el-input-number v-model="editFormData.coursePrice" :min="0" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程时长" prop="courseDuration">
              <el-input-number v-model="editFormData.courseDuration" :min="1" :max="24" style="width: 100%" />
              <span style="margin-left: 8px;">小时</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大人数" prop="maxStudents">
              <el-input-number v-model="editFormData.maxStudents" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程状态" prop="status">
              <el-select v-model="editFormData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="未开始" :value="0" />
                <el-option label="报名中" :value="1" />
                <el-option label="进行中" :value="2" />
                <el-option label="已结束" :value="3" />
                <el-option label="已取消" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开课日期" prop="courseDate">
              <el-date-picker
                v-model="editFormData.courseDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课时间" prop="startTime">
              <el-time-picker
                v-model="editFormData.startTime"
                placeholder="开始时间"
                style="width: 48%"
                value-format="HH:mm:ss"
              />
              <span style="margin: 0 4px;">-</span>
              <el-time-picker
                v-model="editFormData.endTime"
                placeholder="结束时间"
                style="width: 48%"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
         <el-form-item label="课程描述" prop="description">
           <el-input
             v-model="editFormData.description"
             type="textarea"
             :rows="3"
             placeholder="请输入课程描述"
           />
         </el-form-item>
         <el-form-item label="课程图片">
           <el-tabs v-model="imageUploadType" style="width: 100%">
             <el-tab-pane label="上传图片" name="upload">
               <ImageUpload v-model="editFormData.imageUrl" />
               <div style="color: #909399; font-size: 12px; margin-top: 8px;">
                 建议上传尺寸：800x600，支持 jpg、png 格式，大小不超过 2MB
               </div>
             </el-tab-pane>
             <el-tab-pane label="图片链接" name="url">
               <el-input
                 v-model="editFormData.imageUrl"
                 placeholder="请输入或粘贴图片URL，支持 https:// 开头的图片链接"
                 clearable
               >
                 <template #prepend>
                   <el-icon><Link /></el-icon>
                 </template>
               </el-input>
               <div style="color: #909399; font-size: 12px; margin-top: 8px; margin-bottom: 12px;">
                 💡 提示：从 
                 <a href="https://unsplash.com/s/photos/skiing" target="_blank" style="color: #409EFF;">
                   Unsplash 滑雪图库
                 </a>
                 复制图片链接，或点击下方快捷按钮
               </div>
               <!-- 快捷选择图片 -->
               <div style="margin-bottom: 12px;">
                 <div style="color: #606266; font-size: 13px; margin-bottom: 8px; font-weight: 500;">🎨 快速选择推荐图片：</div>
                 <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px;">
                   <el-button 
                     size="small" 
                     @click="editFormData.imageUrl = 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=800&auto=format&fit=crop&q=80'"
                   >
                     🏂 单板滑雪
                   </el-button>
                   <el-button 
                     size="small" 
                     @click="editFormData.imageUrl = 'https://images.unsplash.com/photo-1605540436563-5bca919ae766?w=800&auto=format&fit=crop&q=80'"
                   >
                     🎿 双板滑雪
                   </el-button>
                   <el-button 
                     size="small" 
                     @click="editFormData.imageUrl = 'https://images.unsplash.com/photo-1551524559-8af4e6624178?w=800&auto=format&fit=crop&q=80'"
                   >
                     ⛰️ 高山滑雪
                   </el-button>
                   <el-button 
                     size="small" 
                     @click="editFormData.imageUrl = 'https://images.unsplash.com/photo-1609006398973-1c6e1e3e4316?w=800&auto=format&fit=crop&q=80'"
                   >
                     🤸 自由式
                   </el-button>
                   <el-button 
                     size="small" 
                     @click="editFormData.imageUrl = 'https://images.unsplash.com/photo-1486693128850-a77436e7ba3c?w=800&auto=format&fit=crop&q=80'"
                   >
                     👶 儿童滑雪
                   </el-button>
                   <el-button 
                     size="small" 
                     @click="editFormData.imageUrl = 'https://images.unsplash.com/photo-1551524164-687a55dd1126?w=800&auto=format&fit=crop&q=80'"
                   >
                     🏆 竞技训练
                   </el-button>
                 </div>
               </div>
               <!-- 图片预览 -->
               <div v-if="editFormData.imageUrl && editFormData.imageUrl.trim()" style="margin-top: 16px;">
                 <div style="color: #606266; font-size: 14px; margin-bottom: 8px;">图片预览：</div>
                 <el-image
                   :src="editFormData.imageUrl"
                   fit="cover"
                   style="width: 200px; height: 150px; border-radius: 8px; border: 1px solid #dcdfe6;"
                   :preview-src-list="[editFormData.imageUrl]"
                   hide-on-click-modal
                 >
                   <template #placeholder>
                     <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: #f5f7fa; color: #909399;">
                       <el-icon class="is-loading"><Loading /></el-icon>
                       <span style="margin-left: 4px;">加载中...</span>
                     </div>
                   </template>
                   <template #error>
                     <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; background: #fef0f0; color: #f56c6c; padding: 8px; text-align: center;">
                       <el-icon size="24"><Picture /></el-icon>
                       <span style="margin-top: 8px; font-size: 12px;">图片加载失败</span>
                       <span style="font-size: 11px; color: #909399;">请检查URL</span>
                     </div>
                   </template>
                 </el-image>
               </div>
             </el-tab-pane>
           </el-tabs>
         </el-form-item>
       </el-form>
       <template #footer>
         <el-button @click="editDialogVisible = false">取消</el-button>
         <el-button type="primary" @click="handleSaveEdit" :loading="loading">保存</el-button>
       </template>
     </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Reading, VideoPlay, User, TrendCharts, Search, Refresh,
  Clock, Location, Calendar, Timer, View, Promotion, Edit, Delete,
  Link, Picture, Loading
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCoursesByCoachId, updateCourse, deleteCourse } from '@/api/course'
import { getBookingsByCoachId } from '@/api/coach-booking'
import ImageUpload from '@/components/ImageUpload.vue'

const userStore = useUserStore()

// 数据
const loading = ref(false)
const courses = ref([])
const typeFilter = ref(null)
const statusFilter = ref(null)
const detailDialogVisible = ref(false)
const studentsDialogVisible = ref(false)
const editDialogVisible = ref(false)
const imageUploadType = ref('url') // 图片上传方式：upload=上传文件，url=URL链接
const currentCourse = ref(null)
const currentStudents = ref([])
const studentsLoading = ref(false)

// 编辑表单
const editFormRef = ref(null)
const editFormData = ref({
  id: null,
  courseCode: '',
  courseName: '',
  courseType: '',
  difficulty: '',
  coursePrice: 0,
  courseDuration: 2,
  maxStudents: 10,
  status: 1,
  courseDate: '',
  startTime: '',
  endTime: '',
  description: '',
  imageUrl: '' // 添加图片URL字段
})

const editFormRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  coursePrice: [{ required: true, message: '请输入课程价格', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
}

// 统计数据
const stats = computed(() => {
  return {
    ongoing: courses.value.filter(c => c.status === 2).length,
    totalStudents: courses.value.reduce((sum, c) => sum + (c.currentStudents || 0), 0),
    hotCourses: courses.value.filter(c => c.isHot === 1).length
  }
})

// 过滤后的课程列表
const filteredCourses = computed(() => {
  let result = [...courses.value]
  
  // 按类型筛选
  if (typeFilter.value) {
    result = result.filter(c => c.courseType === typeFilter.value)
  }
  
  // 按状态筛选
  if (statusFilter.value !== null) {
    result = result.filter(c => c.status === statusFilter.value)
  }
  
  // 按创建时间倒序排序
  result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  
  return result
})

// 加载课程列表
const loadCourses = async () => {
  try {
    loading.value = true
    const coachId = userStore.userInfo.id
    console.log('=== 加载教练课程列表 ===')
    console.log('教练ID:', coachId)
    
    const response = await getCoursesByCoachId(coachId)
    console.log('课程列表响应:', response)
    
    courses.value = response.data || []
    console.log('课程数量:', courses.value.length)
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  typeFilter.value = null
  statusFilter.value = null
}

// 查看详情
const viewDetail = (course) => {
  currentCourse.value = course
  detailDialogVisible.value = true
}

// 查看学员列表
const viewStudents = async (course) => {
  currentCourse.value = course
  studentsDialogVisible.value = true
  studentsLoading.value = true
  
  try {
    // 获取该课程的所有预约记录
    const coachId = userStore.userInfo.id
    const response = await getBookingsByCoachId(coachId)
    
    // 筛选出该课程的预约
    currentStudents.value = (response.data || []).filter(
      booking => booking.courseId === course.id
    )
  } catch (error) {
    console.error('加载学员列表失败:', error)
    ElMessage.error('加载学员列表失败')
  } finally {
    studentsLoading.value = false
  }
}

// 获取学员百分比
const getStudentPercentage = (course) => {
  if (!course.maxStudents) return 0
  return Math.round(((course.currentStudents || 0) / course.maxStudents) * 100)
}

// 获取进度条颜色
const getProgressColor = (course) => {
  const percentage = getStudentPercentage(course)
  if (percentage >= 90) return '#f56c6c'
  if (percentage >= 70) return '#e6a23c'
  return '#67c23a'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '未开始',
    1: '报名中',
    2: '进行中',
    3: '已结束',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'success',
    2: 'primary',
    3: 'info',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取预约状态文本
const getBookingStatusText = (status) => {
  const statusMap = {
    0: '待确认',
    1: '已确认',
    2: '进行中',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取预约状态类型
const getBookingStatusType = (status) => {
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

// 编辑课程
const handleEdit = (course) => {
  console.log('=== 编辑课程 ===')
  console.log('课程信息:', course)
  
  // 填充编辑表单数据
  editFormData.value = {
    id: course.id,
    courseCode: course.courseCode,
    courseName: course.courseName,
    courseType: course.courseType,
    difficulty: course.difficulty,
    coursePrice: course.coursePrice,
    courseDuration: course.courseDuration,
    maxStudents: course.maxStudents,
    status: course.status,
    courseDate: course.courseDate,
    startTime: course.startTime,
    endTime: course.endTime,
    description: course.description || '',
    // 保留其他必要字段
    coachId: course.coachId,
    coachName: course.coachName,
    venueId: course.venueId,
    venueName: course.venueName,
    currentStudents: course.currentStudents,
    isHot: course.isHot,
    imageUrl: course.imageUrl || '' // 确保不是 undefined 或 null
  }
  
  // 重置图片上传类型为URL方式
  imageUploadType.value = 'url'
  
  editDialogVisible.value = true
}

// 保存编辑
const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      console.log('=== 保存课程编辑 ===')
      console.log('提交数据:', editFormData.value)
      
      const res = await updateCourse(editFormData.value.id, editFormData.value)
      
      if (res.code === 200) {
        ElMessage.success('编辑成功')
        editDialogVisible.value = false
        // 重新加载课程列表
        loadCourses()
      } else {
        ElMessage.error(res.msg || '编辑失败')
      }
    } catch (error) {
      console.error('编辑课程失败:', error)
      ElMessage.error('编辑课程失败')
    } finally {
      loading.value = false
    }
  })
}

// 删除课程
const handleDelete = (course) => {
  ElMessageBox.confirm(
    `确定要删除课程"${course.courseName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true
    try {
      const res = await deleteCourse(course.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 重新加载课程列表
        loadCourses()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除课程失败:', error)
      ElMessage.error('删除课程失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 初始化
onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.my-courses-page {
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

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-card.ongoing .stat-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.stat-card.students .stat-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: #fff;
}

.stat-card.hot .stat-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
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

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.course-col {
  margin-bottom: 20px;
}

.course-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-image {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.course-card:hover .course-image img {
  transform: scale(1.1);
}

.course-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.course-status.status-0 {
  background: #909399;
}

.course-status.status-1 {
  background: #67c23a;
}

.course-status.status-2 {
  background: #409eff;
}

.course-status.status-3 {
  background: #909399;
}

.course-status.status-4 {
  background: #f56c6c;
}

.course-info {
  flex: 1;
  padding: 16px;
}

.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.meta-item .el-icon {
  color: #909399;
}

.course-students {
  margin: 12px 0;
}

.progress-text {
  font-size: 12px;
  color: #606266;
}

.course-price {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.price-label {
  font-size: 13px;
  color: #909399;
}

.price-value {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
  margin-left: 8px;
}

.course-actions {
  padding: 0 16px 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.course-actions .el-button {
  width: 100%;
  margin: 0;
}

.students-header {
  margin-bottom: 20px;
}

.students-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.students-header p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}
</style>

