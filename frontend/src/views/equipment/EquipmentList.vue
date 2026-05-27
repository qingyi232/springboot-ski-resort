<template>
  <div class="equipment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>雪具管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增雪具
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="雪具编号">
          <el-input v-model="searchForm.equipmentCode" placeholder="请输入雪具编号" clearable />
        </el-form-item>
        <el-form-item label="雪具名称">
          <el-input v-model="searchForm.equipmentName" placeholder="请输入雪具名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.equipmentType" placeholder="请选择类型" clearable>
            <el-option label="单板" value="单板" />
            <el-option label="双板" value="双板" />
            <el-option label="雪杖" value="雪杖" />
            <el-option label="头盔" value="头盔" />
            <el-option label="雪镜" value="雪镜" />
            <el-option label="雪鞋" value="雪鞋" />
            <el-option label="护具" value="护具" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="维修中" :value="0" />
            <el-option label="可用" :value="1" />
            <el-option label="已租出" :value="2" />
            <el-option label="已报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="equipmentCode" label="雪具编号" width="120" />
        <el-table-column prop="equipmentName" label="雪具名称" width="150" />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 60px; height: 45px; border-radius: 4px" fit="cover" :preview-src-list="[row.imageUrl]" preview-teleported />
            <span v-else style="color: #999; font-size: 12px">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="equipmentType" label="类型" width="100" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="rentalPrice" label="租赁价格(元/小时)" width="140" />
        <el-table-column prop="stockQuantity" label="库存数量" width="100" />
        <el-table-column prop="availableQuantity" label="可用数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">维修中</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">可用</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">已租出</el-tag>
            <el-tag v-else type="danger">已报废</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="雪具编号" prop="equipmentCode">
          <el-input v-model="form.equipmentCode" placeholder="请输入雪具编号" />
        </el-form-item>
        <el-form-item label="雪具名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" placeholder="请输入雪具名称" />
        </el-form-item>
        <el-form-item label="类型" prop="equipmentType">
          <el-select v-model="form.equipmentType" placeholder="请选择类型">
            <el-option label="单板" value="单板" />
            <el-option label="双板" value="双板" />
            <el-option label="雪杖" value="雪杖" />
            <el-option label="头盔" value="头盔" />
            <el-option label="雪镜" value="雪镜" />
            <el-option label="雪鞋" value="雪鞋" />
            <el-option label="护具" value="护具" />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="尺寸规格" prop="size">
          <el-input v-model="form.size" placeholder="请输入尺寸规格" />
        </el-form-item>
        <el-form-item label="租赁价格" prop="rentalPrice">
          <el-input-number v-model="form.rentalPrice" :min="0" :precision="2" />
          <span style="margin-left: 10px">元/小时</span>
        </el-form-item>
        <el-form-item label="购入价格" prop="purchasePrice">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" />
          <span style="margin-left: 10px">元</span>
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input-number v-model="form.stockQuantity" :min="1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">维修中</el-radio>
            <el-radio :label="1">可用</el-radio>
            <el-radio :label="2">已租出</el-radio>
            <el-radio :label="3">已报废</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="雪具图片">
          <ImageUpload v-model="form.imageUrl" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEquipmentList, addEquipment, updateEquipment, deleteEquipment } from '@/api/equipment'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  equipmentCode: '',
  equipmentName: '',
  equipmentType: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增雪具')
const formRef = ref(null)

const form = reactive({
  id: null,
  equipmentCode: '',
  equipmentName: '',
  equipmentType: '',
  brand: '',
  model: '',
  size: '',
  rentalPrice: 0,
  purchasePrice: 0,
  stockQuantity: 1,
    status: 1,
    imageUrl: '',
    description: ''
})

const rules = {
  equipmentCode: [{ required: true, message: '请输入雪具编号', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入雪具名称', trigger: 'blur' }],
  equipmentType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  rentalPrice: [{ required: true, message: '请输入租赁价格', trigger: 'blur' }],
  stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEquipmentList({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
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

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    equipmentCode: '',
    equipmentName: '',
    equipmentType: '',
    status: null
  })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增雪具'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑雪具'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该雪具吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteEquipment(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = form.id ? updateEquipment : addEquipment
        const res = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        ElMessage.error(form.id ? '更新失败' : '新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    equipmentCode: '',
    equipmentName: '',
    equipmentType: '',
    brand: '',
    model: '',
    size: '',
    rentalPrice: 0,
    purchasePrice: 0,
    stockQuantity: 1,
    status: 1,
    imageUrl: '',
    description: ''
  })
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.equipment-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>



