<template>
  <div class="venue-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>场地管理</span>
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增场地</el-button>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="venueCode" label="场地编号" width="120" />
        <el-table-column prop="venueName" label="场地名称" width="150" />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 60px; height: 45px; border-radius: 4px" fit="cover" :preview-src-list="[row.imageUrl]" preview-teleported />
            <span v-else style="color: #999; font-size: 12px">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="venueType" label="类型" width="100" />
        <el-table-column prop="difficultyLevel" label="难度" width="100" />
        <el-table-column prop="rentalPrice" label="租赁价格" width="100">
          <template #default="{ row }">¥{{ row.rentalPrice }}/小时</template>
        </el-table-column>
        <el-table-column prop="maxCapacity" label="最大容量" width="100" />
        <el-table-column prop="currentCapacity" label="当前人数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">开放</el-tag>
            <el-tag v-else type="info">关闭</el-tag>
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
        <el-form-item label="场地编号" prop="venueCode">
          <el-input v-model="formData.venueCode" placeholder="请输入场地编号" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="场地名称" prop="venueName">
          <el-input v-model="formData.venueName" placeholder="请输入场地名称" />
        </el-form-item>
        <el-form-item label="场地类型" prop="venueType">
          <el-select v-model="formData.venueType" placeholder="请选择场地类型" style="width: 100%">
            <el-option label="初级道" value="初级道" />
            <el-option label="中级道" value="中级道" />
            <el-option label="高级道" value="高级道" />
            <el-option label="练习场" value="练习场" />
            <el-option label="自由式公园" value="自由式公园" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficultyLevel">
          <el-select v-model="formData.difficultyLevel" placeholder="请选择难度等级" style="width: 100%">
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
            <el-option label="专业" value="专业" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="长度(米)" prop="length">
              <el-input-number v-model="formData.length" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="宽度(米)" prop="width">
              <el-input-number v-model="formData.width" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="租赁价格" prop="rentalPrice">
          <el-input-number v-model="formData.rentalPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="formData.maxCapacity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="场地图片">
          <ImageUpload v-model="formData.imageUrl" />
        </el-form-item>
        <el-form-item label="场地描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入场地描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">开放</el-radio>
            <el-radio :label="0">关闭</el-radio>
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
import { getVenueList, createVenue, updateVenue, deleteVenue } from '@/api/venue'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增场地')
const formRef = ref(null)
const formData = ref({
  id: null,
  venueCode: '',
  venueName: '',
  venueType: '',
  difficultyLevel: '',
  length: 0,
  width: 0,
  maxCapacity: 50,
  rentalPrice: 0,
    imageUrl: '',
    description: '',
    status: 1
})

const formRules = {
  venueCode: [{ required: true, message: '请输入场地编号', trigger: 'blur' }],
  venueName: [{ required: true, message: '请输入场地名称', trigger: 'blur' }],
  venueType: [{ required: true, message: '请选择场地类型', trigger: 'change' }],
  difficultyLevel: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  rentalPrice: [{ required: true, message: '请输入租赁价格', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getVenueList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
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

const handleAdd = () => {
  dialogTitle.value = '新增场地'
  formData.value = {
    id: null,
    venueCode: '',
    venueName: '',
    venueType: '',
    difficultyLevel: '',
    length: 0,
    width: 0,
    maxCapacity: 50,
    rentalPrice: 0,
    imageUrl: '',
    description: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑场地'
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
        res = await updateVenue(formData.value.id, formData.value)
      } else {
        res = await createVenue(formData.value)
      }
      
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

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除场地"${row.venueName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    loading.value = true
    try {
      const res = await deleteVenue(row.id)
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
