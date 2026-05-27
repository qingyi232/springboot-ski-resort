<template>
  <div class="course-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增课程</el-button>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column label="课程图片" width="100">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 60px; height: 45px; border-radius: 4px" fit="cover" :preview-src-list="[row.imageUrl]" preview-teleported />
            <span v-else style="color: #999; font-size: 12px">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="课程类型" width="120" />
        <el-table-column prop="coachName" label="教练" width="100" />
        <el-table-column prop="coursePrice" label="价格" width="100">
          <template #default="{ row }">¥{{ row.coursePrice }}</template>
        </el-table-column>
        <el-table-column prop="maxStudents" label="最大人数" width="90" />
        <el-table-column prop="currentStudents" label="当前人数" width="90" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">待开课</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">进行中</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">已结束</el-tag>
            <el-tag v-else type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程编号" prop="courseCode">
              <el-input v-model="formData.courseCode" placeholder="请输入课程编号" :disabled="!!formData.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="formData.courseType" placeholder="请选择课程类型" style="width: 100%">
                <el-option label="单板" value="单板" />
                <el-option label="双板" value="双板" />
                <el-option label="自由式" value="自由式" />
                <el-option label="竞技" value="竞技" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="formData.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="初级" value="初级" />
                <el-option label="中级" value="中级" />
                <el-option label="高级" value="高级" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教练ID" prop="coachId">
              <el-input-number v-model="formData.coachId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教练姓名" prop="coachName">
              <el-input v-model="formData.coachName" placeholder="请输入教练姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程价格" prop="coursePrice">
              <el-input-number v-model="formData.coursePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大人数" prop="maxStudents">
              <el-input-number v-model="formData.maxStudents" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程日期" prop="courseDate">
              <el-date-picker v-model="formData.courseDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程时长(小时)" prop="courseDuration">
              <el-input-number v-model="formData.courseDuration" :min="1" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="课程图片" prop="imageUrl">
          <ImageUpload v-model="formData.imageUrl" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">待开课</el-radio>
            <el-radio :label="1">进行中</el-radio>
            <el-radio :label="2">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseList, createCourse, updateCourse, deleteCourse, getCoursesByCoachId } from '@/api/course'
import { useUserStore } from '@/stores/user'
import ImageUpload from '@/components/ImageUpload.vue'

const userStore = useUserStore()
const isCoach = computed(() => userStore.userInfo?.role === 'coach')

const loading = ref(false)
const tableData = ref([])
const allCourses = ref([]) // 教练端存储所有课程，用于前端分页
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const formRef = ref(null)
const formData = ref({
  id: null,
  courseCode: '',
  courseName: '',
  courseType: '',
  coachId: null,
  coachName: '',
  maxStudents: 10,
  coursePrice: 0,
  courseDuration: 2,
  courseDate: '',
  difficulty: '',
  description: '',
  imageUrl: '',  // 课程图片URL
  status: 0
})

const formRules = {
  courseCode: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  coachId: [{ required: true, message: '请输入教练ID', trigger: 'blur' }],
  coachName: [{ required: true, message: '请输入教练姓名', trigger: 'blur' }],
  coursePrice: [{ required: true, message: '请输入课程价格', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    let res
    if (isCoach.value) {
      // 教练只能看到自己的课程
      const userId = userStore.userInfo.id
      console.log('=== 教练加载课程列表 ===')
      console.log('用户ID:', userId)
      
      // 如果 allCourses 为空，重新加载
      if (allCourses.value.length === 0) {
        res = await getCoursesByCoachId(userId)
        console.log('课程列表响应:', res)
        if (res.code === 200) {
          allCourses.value = res.data || []
        }
      }
      
      // 前端分页
      pagination.total = allCourses.value.length
      const start = (pagination.pageNum - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      tableData.value = allCourses.value.slice(start, end)
      console.log('当前页数据:', tableData.value)
    } else {
      // 管理员可以看到所有课程（后端分页）
      console.log('=== 管理员加载所有课程 ===')
      res = await getCourseList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
      if (res.code === 200) {
        tableData.value = res.data.list
        pagination.total = res.data.total
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增课程'
  formData.value = {
    id: null,
    courseCode: '',
    courseName: '',
    courseType: '',
    coachId: null,
    coachName: '',
    maxStudents: 10,
    coursePrice: 0,
    courseDuration: 2,
    courseDate: '',
    difficulty: '',
    description: '',
    imageUrl: '',
    status: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑课程'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      let res
      if (formData.value.id) {
        res = await updateCourse(formData.value.id, formData.value)
      } else {
        res = await createCourse(formData.value)
      }
      
      if (res.code === 200) {
        ElMessage.success(formData.value.id ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        // 清空教练端缓存，重新加载
        if (isCoach.value) {
          allCourses.value = []
        }
        loadData()
      }
    } catch (error) {
      // 拦截器已处理错误提示
    } finally {
      loading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除课程"${row.courseName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    loading.value = true
    try {
      const res = await deleteCourse(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 清空教练端缓存，重新加载
        if (isCoach.value) {
          allCourses.value = []
        }
        loadData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
