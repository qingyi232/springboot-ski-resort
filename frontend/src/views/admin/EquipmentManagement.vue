<template>
  <div class="equipment-management">
    <div class="page-header">
      <h1>雪具管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加雪具
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="雪具名称/品牌"
            clearable
            @input="handleSearch"
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable @change="handleSearch" style="width: 150px;">
            <el-option label="单板" value="单板" />
            <el-option label="双板" value="双板" />
            <el-option label="雪杖" value="雪杖" />
            <el-option label="头盔" value="头盔" />
            <el-option label="护具" value="护具" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="可租赁" value="available" />
            <el-option label="已租出" value="rented" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 雪具表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="equipments" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="name" label="雪具名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column label="租金" width="180">
          <template #default="{ row }">
            <div>日租：¥{{ row.dailyRent }}</div>
            <div style="font-size: 12px; color: #909399;">时租：¥{{ row.hourlyRent }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="deposit" label="押金" width="100">
          <template #default="{ row }">
            ¥{{ row.deposit }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" text @click="deleteEquipment(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadEquipments"
          @current-change="loadEquipments"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @close="resetForm">
      <el-form :model="equipmentForm" :rules="equipmentRules" ref="equipmentFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="雪具名称" prop="name">
              <el-input v-model="equipmentForm.name" placeholder="请输入雪具名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-select v-model="equipmentForm.type" placeholder="请选择类型" style="width: 100%;">
                <el-option label="单板" value="单板" />
                <el-option label="双板" value="双板" />
                <el-option label="雪杖" value="雪杖" />
                <el-option label="头盔" value="头盔" />
                <el-option label="护具" value="护具" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="equipmentForm.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格">
              <el-input v-model="equipmentForm.specification" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="时租金" prop="hourlyRent">
              <el-input-number v-model="equipmentForm.hourlyRent" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日租金" prop="dailyRent">
              <el-input-number v-model="equipmentForm.dailyRent" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="押金" prop="deposit">
              <el-input-number v-model="equipmentForm.deposit" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number v-model="equipmentForm.stock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="equipmentForm.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="可租赁" value="available" />
                <el-option label="维护中" value="maintenance" />
                <el-option label="已报废" value="scrapped" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="描述">
          <el-input v-model="equipmentForm.description" type="textarea" :rows="3" placeholder="请输入雪具描述" />
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
import { getEquipments, createEquipment, updateEquipment, deleteEquipment as deleteEquipmentApi } from '@/api/equipment'
import { debounce, formatDateTime } from '@/utils/performance'

const searchForm = reactive({ keyword: '', type: '', status: '' })
const equipments = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑雪具' : '添加雪具')
const submitting = ref(false)
const equipmentFormRef = ref(null)

const equipmentForm = reactive({
  id: null, name: '', type: '', brand: '', specification: '',
  hourlyRent: 0, dailyRent: 0, deposit: 0, stock: 0,
  status: 'available', description: ''
})

const equipmentRules = {
  name: [{ required: true, message: '请输入雪具名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  dailyRent: [{ required: true, message: '请输入日租金', trigger: 'blur' }],
  deposit: [{ required: true, message: '请输入押金', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadEquipments = async () => {
  try {
    loading.value = true
    const response = await getEquipments({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      equipments.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载雪具列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadEquipments()
}, 300)

const resetSearch = () => {
  Object.assign(searchForm, { keyword: '', type: '', status: '' })
  pagination.value.page = 1
  loadEquipments()
}

const getStatusType = (status) => ({
  'available': 'success', 'rented': 'warning',
  'maintenance': 'info', 'scrapped': 'danger'
}[status] || 'info')

const getStatusText = (status) => ({
  'available': '可租赁', 'rented': '已租出',
  'maintenance': '维护中', 'scrapped': '已报废'
}[status] || status)

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(equipmentForm, row)
  dialogVisible.value = true
}

const resetForm = () => {
  equipmentFormRef.value?.resetFields()
  Object.assign(equipmentForm, {
    id: null, name: '', type: '', brand: '', specification: '',
    hourlyRent: 0, dailyRent: 0, deposit: 0, stock: 0,
    status: 'available', description: ''
  })
}

const submitForm = async () => {
  try {
    await equipmentFormRef.value.validate()
    submitting.value = true
    const apiFunc = isEdit.value ? updateEquipment : createEquipment
    const params = isEdit.value ? [equipmentForm.id, equipmentForm] : [equipmentForm]
    const response = await apiFunc(...params)
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadEquipments()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const deleteEquipment = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除雪具"${row.name}"吗？`, '删除雪具', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await deleteEquipmentApi(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadEquipments()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => loadEquipments())
</script>

<style scoped>
.equipment-management { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 280px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>







