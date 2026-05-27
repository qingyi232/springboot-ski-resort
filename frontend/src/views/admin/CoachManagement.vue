<template>
  <div class="coach-management">
    <div class="page-header">
      <h1>教练管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加教练
      </el-button>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="教练姓名/手机号" clearable @input="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="searchForm.level" placeholder="全部" clearable @change="handleSearch" style="width: 150px;">
            <el-option label="初级" value="junior" />
            <el-option label="中级" value="intermediate" />
            <el-option label="高级" value="senior" />
            <el-option label="专业级" value="professional" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="coaches" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">{{ getLevelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="specialty" label="专长" min-width="150" />
        <el-table-column label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="hourlyRate" label="课时费" width="100">
          <template #default="{ row }">¥{{ row.hourlyRate }}</template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学员数" width="80" />
        <el-table-column prop="experienceYears" label="经验(年)" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="showEditDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
            <el-button type="danger" text @click="deleteCoach(row)"><el-icon><Delete /></el-icon> 删除</el-button>
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
          @size-change="loadCoaches"
          @current-change="loadCoaches"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @close="resetForm">
      <el-form :model="coachForm" :rules="coachRules" ref="coachFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="coachForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="coachForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="coachForm.level" placeholder="请选择等级" style="width: 100%;">
                <el-option label="初级" value="junior" />
                <el-option label="中级" value="intermediate" />
                <el-option label="高级" value="senior" />
                <el-option label="专业级" value="professional" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课时费" prop="hourlyRate">
              <el-input-number v-model="coachForm.hourlyRate" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="从业年限">
              <el-input-number v-model="coachForm.experienceYears" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="coachForm.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="在岗" value="active" />
                <el-option label="休假" value="on_leave" />
                <el-option label="离职" value="resigned" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="专长">
          <el-input v-model="coachForm.specialty" placeholder="请输入教练专长" />
        </el-form-item>
        
        <el-form-item label="证书">
          <el-input v-model="coachForm.certificate" placeholder="请输入证书信息" />
        </el-form-item>

        <el-form-item label="个人介绍">
          <el-input v-model="coachForm.introduction" type="textarea" :rows="3" placeholder="请输入个人介绍" />
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
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getCoaches, createCoach, updateCoach, deleteCoach as deleteCoachApi } from '@/api/coach'
import { debounce } from '@/utils/performance'

const searchForm = reactive({ keyword: '', level: '' })
const coaches = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑教练' : '添加教练')
const submitting = ref(false)
const coachFormRef = ref(null)

const coachForm = reactive({
  id: null, name: '', phone: '', level: '', hourlyRate: 0,
  specialty: '', certificate: '', experienceYears: 0,
  status: 'active', introduction: ''
})

const coachRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  level: [{ required: true, message: '请选择等级', trigger: 'change' }],
  hourlyRate: [{ required: true, message: '请输入课时费', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadCoaches = async () => {
  try {
    loading.value = true
    const response = await getCoaches({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      coaches.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载教练列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadCoaches()
}, 300)

const resetSearch = () => {
  Object.assign(searchForm, { keyword: '', level: '' })
  pagination.value.page = 1
  loadCoaches()
}

const getLevelType = (level) => ({
  'junior': 'info', 'intermediate': 'success',
  'senior': 'warning', 'professional': 'danger'
}[level] || 'info')

const getLevelText = (level) => ({
  'junior': '初级', 'intermediate': '中级',
  'senior': '高级', 'professional': '专业级'
}[level] || level)

const getStatusType = (status) => ({
  'active': 'success', 'on_leave': 'warning', 'resigned': 'info'
}[status] || 'info')

const getStatusText = (status) => ({
  'active': '在岗', 'on_leave': '休假', 'resigned': '离职'
}[status] || status)

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(coachForm, row)
  dialogVisible.value = true
}

const resetForm = () => {
  coachFormRef.value?.resetFields()
  Object.assign(coachForm, {
    id: null, name: '', phone: '', level: '', hourlyRate: 0,
    specialty: '', certificate: '', experienceYears: 0,
    status: 'active', introduction: ''
  })
}

const submitForm = async () => {
  try {
    await coachFormRef.value.validate()
    submitting.value = true
    const apiFunc = isEdit.value ? updateCoach : createCoach
    const params = isEdit.value ? [coachForm.id, coachForm] : [coachForm]
    const response = await apiFunc(...params)
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadCoaches()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const deleteCoach = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除教练"${row.name}"吗？`, '删除教练', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await deleteCoachApi(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadCoaches()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => loadCoaches())
</script>

<style scoped>
.coach-management { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 280px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>







