<template>
  <div class="coach-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教练管理</span>
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增教练</el-button>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="coachCode" label="教练编号" width="120" />
        <el-table-column prop="coachName" label="姓名" width="100" />
        <el-table-column prop="coachLevel" label="等级" width="100" />
        <el-table-column prop="specialty" label="专长" />
        <el-table-column prop="experienceYears" label="从业年限" width="100" />
        <el-table-column prop="hourlyRate" label="课时费(元/小时)" width="130" />
        <el-table-column prop="rating" label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="totalStudents" label="累计学员" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">休假</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">在职</el-tag>
            <el-tag v-else type="danger">离职</el-tag>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="教练编号" prop="coachCode">
          <el-input v-model="formData.coachCode" placeholder="请输入教练编号" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="coachName">
          <el-input v-model="formData.coachName" placeholder="请输入教练姓名" />
        </el-form-item>
        <el-form-item label="等级" prop="coachLevel">
          <el-select v-model="formData.coachLevel" placeholder="请选择教练等级" style="width: 100%">
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
            <el-option label="资深" value="资深" />
          </el-select>
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="formData.specialty" placeholder="请输入专长（如：单板、双板）" />
        </el-form-item>
        <el-form-item label="从业年限" prop="experienceYears">
          <el-input-number v-model="formData.experienceYears" :min="0" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课时费" prop="hourlyRate">
          <el-input-number v-model="formData.hourlyRate" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="简介" prop="introduction">
          <el-input v-model="formData.introduction" type="textarea" :rows="3" placeholder="请输入教练简介" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">休假</el-radio>
            <el-radio :label="2">离职</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoachList, addCoach, updateCoach, deleteCoach } from '@/api/coach'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 对话框控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增教练')
const formRef = ref(null)
const formData = ref({
  id: null,
  coachCode: '',
  coachName: '',
  coachLevel: '',
  specialty: '',
  experienceYears: 0,
  hourlyRate: 0,
  phone: '',
  email: '',
  introduction: '',
  status: 1
})

const formRules = {
  coachCode: [{ required: true, message: '请输入教练编号', trigger: 'blur' }],
  coachName: [{ required: true, message: '请输入教练姓名', trigger: 'blur' }],
  coachLevel: [{ required: true, message: '请选择教练等级', trigger: 'change' }],
  hourlyRate: [{ required: true, message: '请输入课时费', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCoachList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 新增教练
const handleAdd = () => {
  dialogTitle.value = '新增教练'
  formData.value = {
    id: null,
    coachCode: '',
    coachName: '',
    coachLevel: '',
    specialty: '',
    experienceYears: 0,
    hourlyRate: 0,
    phone: '',
    email: '',
    introduction: '',
    status: 1
  }
  dialogVisible.value = true
}

// 编辑教练
const handleEdit = (row) => {
  dialogTitle.value = '编辑教练'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 保存教练
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const api = formData.value.id ? updateCoach : addCoach
      const res = await api(formData.value)
      
      if (res.code === 200) {
        ElMessage.success(formData.value.id ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      }
    } catch (error) {
      // 拦截器已处理错误提示
    } finally {
      loading.value = false
    }
  })
}

// 删除教练
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除教练"${row.coachName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    loading.value = true
    try {
      const res = await deleteCoach(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
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



