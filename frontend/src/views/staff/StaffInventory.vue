<template>
  <div class="staff-inventory">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="雪具名称">
          <el-input v-model="searchForm.equipmentName" placeholder="请输入雪具名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.equipmentType" placeholder="全部" clearable>
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
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
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
        <el-table-column prop="equipmentType" label="类型" width="100" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="rentalPrice" label="租赁价格(元/小时)" width="140" />
        <el-table-column prop="stockQuantity" label="库存数量" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.stockQuantity <= 2 ? '#F56C6C' : '', fontWeight: row.stockQuantity <= 2 ? 'bold' : '' }">
              {{ row.stockQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="availableQuantity" label="可用数量" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.availableQuantity === 0 ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
              {{ row.availableQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">维修中</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">可用</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">已租出</el-tag>
            <el-tag v-else type="danger">已报废</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditStock(row)">更新库存</el-button>
            <el-button type="warning" size="small" @click="handleEditStatus(row)">改状态</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 更新库存对话框 -->
    <el-dialog v-model="stockDialogVisible" title="更新库存数量" width="400px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="雪具名称">
          <span>{{ editingRow?.equipmentName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ editingRow?.stockQuantity }}</span>
        </el-form-item>
        <el-form-item label="新库存数量">
          <el-input-number v-model="newStockQuantity" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveStock" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改状态对话框 -->
    <el-dialog v-model="statusDialogVisible" title="修改雪具状态" width="400px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="雪具名称">
          <span>{{ editingRow?.equipmentName }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag v-if="editingRow?.status === 0" type="warning">维修中</el-tag>
          <el-tag v-else-if="editingRow?.status === 1" type="success">可用</el-tag>
          <el-tag v-else-if="editingRow?.status === 2" type="info">已租出</el-tag>
          <el-tag v-else type="danger">已报废</el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-radio-group v-model="newStatus">
            <el-radio :label="0">维修中</el-radio>
            <el-radio :label="1">可用</el-radio>
            <el-radio :label="2">已租出</el-radio>
            <el-radio :label="3">已报废</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveStatus" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEquipmentList, updateEquipment } from '@/api/equipment'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ equipmentName: '', equipmentType: '', status: null })

const stockDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const editingRow = ref(null)
const newStockQuantity = ref(0)
const newStatus = ref(1)

onMounted(() => loadData())

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
  searchForm.equipmentName = ''
  searchForm.equipmentType = ''
  searchForm.status = null
  handleSearch()
}

const handleEditStock = (row) => {
  editingRow.value = row
  newStockQuantity.value = row.stockQuantity
  stockDialogVisible.value = true
}

const handleEditStatus = (row) => {
  editingRow.value = row
  newStatus.value = row.status
  statusDialogVisible.value = true
}

const handleSaveStock = async () => {
  submitLoading.value = true
  try {
    const res = await updateEquipment({
      id: editingRow.value.id,
      stockQuantity: newStockQuantity.value
    })
    if (res.code === 200) {
      ElMessage.success('库存更新成功')
      stockDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    submitLoading.value = false
  }
}

const handleSaveStatus = async () => {
  submitLoading.value = true
  try {
    const res = await updateEquipment({
      id: editingRow.value.id,
      status: newStatus.value
    })
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      statusDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
</style>
