<template>
  <div class="course-management">
    <div class="page-header">
      <h1>课程管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加课程
      </el-button>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="课程名称" clearable @input="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="入门" value="easy" />
            <el-option label="进阶" value="medium" />
            <el-option label="高级" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="报名中" value="enrolling" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="courses" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="name" label="课程名称" min-width="180" />
        <el-table-column prop="coachName" label="教练" width="100" />
        <el-table-column label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">{{ getDifficultyText(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="{ row }">{{ row.duration }} 分钟</template>
        </el-table-column>
        <el-table-column label="课节" width="120">
          <template #default="{ row }">
            <span v-if="row.totalLessons">{{ row.totalLessons }}节/每节{{ row.perLessonHours || '-' }}h</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="迟到规则" width="140">
          <template #default="{ row }">
            <el-tag :type="row.latePolicy === 0 ? 'danger' : row.latePolicy === 1 ? 'success' : 'warning'" size="small">
              {{ row.latePolicy === 0 ? '课时作废' : row.latePolicy === 1 ? '可延期补课' : '迟到15分钟内可上' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="人数" width="120">
          <template #default="{ row }">
            {{ row.currentStudents }}/{{ row.maxStudents }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120">
          <template #default="{ row }">{{ formatDate(row.startDate) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="showEditDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
            <el-button type="danger" text @click="deleteCourse(row)"><el-icon><Delete /></el-icon> 删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadCourses"
          @current-change="loadCourses"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="resetForm">
      <el-form :model="courseForm" :rules="courseRules" ref="courseFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教练" prop="coachId">
              <el-select v-model="courseForm.coachId" placeholder="请选择教练" filterable style="width: 100%;">
                <el-option
                  v-for="coach in coaches"
                  :key="coach.id"
                  :label="coach.name"
                  :value="coach.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="courseForm.difficulty" placeholder="请选择难度" style="width: 100%;">
                <el-option label="入门" value="easy" />
                <el-option label="进阶" value="medium" />
                <el-option label="高级" value="hard" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-input v-model="courseForm.type" placeholder="如：单板初级班" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="时长" prop="duration">
              <el-input-number v-model="courseForm.duration" :min="30" :step="30" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="courseForm.price" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大人数" prop="maxStudents">
              <el-input-number v-model="courseForm.maxStudents" :min="1" :max="50" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="总节数">
              <el-input-number v-model="courseForm.totalLessons" :min="1" :max="100" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="每节时长">
              <el-input-number v-model="courseForm.perLessonHours" :min="0.5" :step="0.5" :precision="1" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">小时</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="迟到规则">
              <el-select v-model="courseForm.latePolicy" placeholder="请选择" style="width: 100%;">
                <el-option label="课时作废不补" :value="0" />
                <el-option label="可延期补课" :value="1" />
                <el-option label="迟到15分钟内可上课" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="courseForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="courseForm.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="报名中" value="enrolling" />
                <el-option label="进行中" value="ongoing" />
                <el-option label="已结束" value="completed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="课程图片">
          <div class="image-upload-wrapper">
            <el-radio-group v-model="uploadMethod" size="small" style="margin-bottom: 12px;">
              <el-radio-button label="file">本地上传</el-radio-button>
              <el-radio-button label="url">图片URL</el-radio-button>
            </el-radio-group>

            <div v-if="uploadMethod === 'file'">
              <el-upload
                class="course-image-uploader"
                action="/api/v1/upload/image"
                :show-file-list="false"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                :headers="uploadHeaders"
                accept="image/*"
              >
                <div v-if="courseForm.imageUrl" class="upload-preview-box">
                  <img :src="getImageDisplayUrl(courseForm.imageUrl)" alt="课程图片" />
                  <div class="upload-overlay">
                    <el-icon><Upload /></el-icon>
                    <span>点击重新上传</span>
                  </div>
                </div>
                <div v-else class="upload-placeholder-box">
                  <el-icon class="upload-icon-large"><Plus /></el-icon>
                  <div class="upload-text">点击上传图片</div>
                  <div class="upload-tip">支持 JPG、PNG、GIF，≤5MB</div>
                </div>
              </el-upload>
              <el-button v-if="courseForm.imageUrl" size="small" type="danger" @click="clearImage" style="margin-top: 8px;">
                <el-icon><Delete /></el-icon> 清除图片
              </el-button>
            </div>

            <div v-else>
              <el-input v-model="courseForm.imageUrl" placeholder="请输入图片URL（如：https://example.com/ski.jpg）" clearable />
              <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                提示：输入滑雪相关图片的URL地址，推荐尺寸 800×600
              </div>
            </div>

            <div v-if="courseForm.imageUrl" class="image-preview-area">
              <el-image :src="getImageDisplayUrl(courseForm.imageUrl)" style="width: 200px; height: 150px; border-radius: 6px;" fit="cover">
                <template #error>
                  <div style="width: 200px; height: 150px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; color: #909399; font-size: 12px; border-radius: 6px;">
                    图片加载失败
                  </div>
                </template>
              </el-image>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="课程描述">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>

        <el-form-item label="课程大纲">
          <el-input v-model="courseForm.syllabus" type="textarea" :rows="4" placeholder="请输入课程大纲" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Upload } from '@element-plus/icons-vue'
import { getCourses, createCourse, updateCourse, deleteCourse as deleteCourseApi } from '@/api/course'
import { getCoaches } from '@/api/coach'
import { debounce, formatDate } from '@/utils/performance'
import { useUserStore } from '@/stores/user'

const searchForm = reactive({ keyword: '', difficulty: '', status: '' })
const courses = ref([])
const coaches = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑课程' : '添加课程')
const submitting = ref(false)
const courseFormRef = ref(null)

const uploadMethod = ref('file')
const userStore = useUserStore()
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token || ''}`
}))

const courseForm = reactive({
  id: null, name: '', coachId: null, difficulty: '', type: '',
  duration: 60, price: 0, maxStudents: 10, currentStudents: 0,
  totalLessons: 1, perLessonHours: 2.0, latePolicy: 2,
  startDate: null, status: 'enrolling', description: '', syllabus: '',
  imageUrl: ''
})

const courseRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  type: [{ required: true, message: '请输入类型', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadCourses = async () => {
  try {
    loading.value = true
    const response = await getCourses({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      courses.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const loadCoaches = async () => {
  const response = await getCoaches({ page: 1, pageSize: 100 })
  if (response.code === 200) coaches.value = response.data.records || []
}

const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadCourses()
}, 300)

const resetSearch = () => {
  Object.assign(searchForm, { keyword: '', difficulty: '', status: '' })
  pagination.value.page = 1
  loadCourses()
}

const getDifficultyType = (difficulty) => ({
  'easy': 'success', 'medium': 'warning', 'hard': 'danger'
}[difficulty] || 'info')

const getDifficultyText = (difficulty) => ({
  'easy': '入门', 'medium': '进阶', 'hard': '高级'
}[difficulty] || difficulty)

const getStatusType = (status) => ({
  'enrolling': 'success', 'ongoing': 'warning', 'completed': 'info'
}[status] || 'info')

const getStatusText = (status) => ({
  'enrolling': '报名中', 'ongoing': '进行中', 'completed': '已结束'
}[status] || status)

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(courseForm, row)
  dialogVisible.value = true
}

const resetForm = () => {
  courseFormRef.value?.resetFields()
  Object.assign(courseForm, {
    id: null, name: '', coachId: null, difficulty: '', type: '',
    duration: 60, price: 0, maxStudents: 10, currentStudents: 0,
    totalLessons: 1, perLessonHours: 2.0, latePolicy: 2,
    startDate: null, status: 'enrolling', description: '', syllabus: '',
    imageUrl: ''
  })
}

const submitForm = async () => {
  try {
    await courseFormRef.value.validate()
    submitting.value = true
    const apiFunc = isEdit.value ? updateCourse : createCourse
    const params = isEdit.value ? [courseForm.id, courseForm] : [courseForm]
    const response = await apiFunc(...params)
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadCourses()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const getImageDisplayUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return url
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200 && response.data) {
    courseForm.imageUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败，请重试')
}

const clearImage = () => {
  courseForm.imageUrl = ''
  ElMessage.success('已清除图片')
}

const deleteCourse = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.name}"吗？`, '删除课程', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await deleteCourseApi(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadCourses()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadCourses()
  loadCoaches()
})
</script>

<style scoped>
.course-management { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 280px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }

.image-upload-wrapper { width: 100%; }
.image-preview-area { margin-top: 12px; }

.course-image-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}
.course-image-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.upload-placeholder-box {
  width: 240px;
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}
.upload-icon-large { font-size: 40px; color: #8c939d; margin-bottom: 12px; }
.upload-text { font-size: 14px; color: #606266; margin-bottom: 6px; }
.upload-tip { font-size: 12px; color: #909399; }

.upload-preview-box {
  position: relative;
  width: 240px;
  height: 180px;
}
.upload-preview-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
  font-size: 14px;
  gap: 6px;
}
.upload-overlay .el-icon { font-size: 28px; }
.upload-preview-box:hover .upload-overlay { opacity: 1; }
</style>







