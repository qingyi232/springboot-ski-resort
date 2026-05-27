<template>
  <div class="venue-management">
    <div class="page-header">
      <h1>场地管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加场地
      </el-button>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="场地名称" clearable @input="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="开放" value="open" />
            <el-option label="维护" value="maintenance" />
            <el-option label="关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="venues" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="name" label="场地名称" min-width="150" />
        <el-table-column prop="location" label="位置" min-width="120" />
        <el-table-column label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">{{ row.difficulty }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="slope" label="坡度" width="100">
          <template #default="{ row }">{{ row.slope }}°</template>
        </el-table-column>
        <el-table-column prop="length" label="长度" width="100">
          <template #default="{ row }">{{ row.length }}米</template>
        </el-table-column>
        <el-table-column prop="width" label="宽度" width="100">
          <template #default="{ row }">{{ row.width }}米</template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量" width="80">
          <template #default="{ row }">{{ row.capacity }}人</template>
        </el-table-column>
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div>¥{{ row.hourlyPrice }}/小时</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="showEditDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
            <el-button type="danger" text @click="deleteVenue(row)"><el-icon><Delete /></el-icon> 删除</el-button>
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
          @size-change="loadVenues"
          @current-change="loadVenues"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="resetForm">
      <el-form :model="venueForm" :rules="venueRules" ref="venueFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="场地名称" prop="name">
              <el-input v-model="venueForm.name" placeholder="请输入场地名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="venueForm.location" placeholder="如：东区A1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficulty">
              <el-select v-model="venueForm.difficulty" placeholder="请选择难度" style="width: 100%;">
                <el-option label="初级" value="初级" />
                <el-option label="中级" value="中级" />
                <el-option label="高级" value="高级" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="场地类型" prop="type">
              <el-input v-model="venueForm.type" placeholder="如：单板道、双板道" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="坡度" prop="slope">
              <el-input-number v-model="venueForm.slope" :min="0" :max="90" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">度</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="长度" prop="length">
              <el-input-number v-model="venueForm.length" :min="0" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">米</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="宽度" prop="width">
              <el-input-number v-model="venueForm.width" :min="0" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">米</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="容量" prop="capacity">
              <el-input-number v-model="venueForm.capacity" :min="1" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">人</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时价" prop="hourlyPrice">
              <el-input-number v-model="venueForm.hourlyPrice" :min="0" :precision="2" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">元/小时</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日价">
              <el-input-number v-model="venueForm.dailyPrice" :min="0" :precision="2" style="width: 100%;" />
              <span style="margin-left: 8px; color: #909399;">元/天</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开放时间" prop="openTime">
              <el-time-picker
                v-model="venueForm.openTime"
                placeholder="选择开放时间"
                format="HH:mm"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关闭时间" prop="closeTime">
              <el-time-picker
                v-model="venueForm.closeTime"
                placeholder="选择关闭时间"
                format="HH:mm"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="场地状态" prop="status">
          <el-radio-group v-model="venueForm.status">
            <el-radio value="open">开放</el-radio>
            <el-radio value="maintenance">维护中</el-radio>
            <el-radio value="closed">关闭</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="场地图片">
          <el-input v-model="venueForm.imageUrl" placeholder="请输入图片URL" />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            提示：可以输入图片URL地址
          </div>
        </el-form-item>

        <el-form-item label="设施标签">
          <el-input v-model="venueForm.facilities" placeholder="多个设施用逗号分隔，如：缆车,休息区,餐厅" />
        </el-form-item>

        <el-form-item label="场地描述">
          <el-input v-model="venueForm.description" type="textarea" :rows="4" placeholder="请输入场地详细描述" />
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
import { getVenues, createVenue, updateVenue, deleteVenue as deleteVenueApi } from '@/api/venue'
import { debounce } from '@/utils/performance'

const searchForm = reactive({ keyword: '', difficulty: '', status: '' })
const venues = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑场地' : '添加场地')
const submitting = ref(false)
const venueFormRef = ref(null)

const venueForm = reactive({
  id: null, name: '', location: '', difficulty: '', type: '',
  slope: 0, length: 0, width: 0, capacity: 50,
  hourlyPrice: 0, dailyPrice: 0, openTime: null, closeTime: null,
  status: 'open', imageUrl: '', facilities: '', description: ''
})

const venueRules = {
  name: [{ required: true, message: '请输入场地名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  type: [{ required: true, message: '请输入类型', trigger: 'blur' }],
  slope: [{ required: true, message: '请输入坡度', trigger: 'blur' }],
  length: [{ required: true, message: '请输入长度', trigger: 'blur' }],
  width: [{ required: true, message: '请输入宽度', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }],
  hourlyPrice: [{ required: true, message: '请输入时价', trigger: 'blur' }],
  openTime: [{ required: true, message: '请选择开放时间', trigger: 'change' }],
  closeTime: [{ required: true, message: '请选择关闭时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadVenues = async () => {
  try {
    loading.value = true
    const response = await getVenues({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      venues.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载场地列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadVenues()
}, 300)

const resetSearch = () => {
  Object.assign(searchForm, { keyword: '', difficulty: '', status: '' })
  pagination.value.page = 1
  loadVenues()
}

const getDifficultyType = (difficulty) => ({
  '初级': 'success', '中级': 'warning', '高级': 'danger'
}[difficulty] || 'info')

const getStatusType = (status) => ({
  'open': 'success', 'maintenance': 'warning', 'closed': 'info'
}[status] || 'info')

const getStatusText = (status) => ({
  'open': '开放', 'maintenance': '维护中', 'closed': '关闭'
}[status] || status)

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(venueForm, {
    ...row,
    facilities: Array.isArray(row.facilities) ? row.facilities.join(',') : row.facilities || ''
  })
  dialogVisible.value = true
}

const resetForm = () => {
  venueFormRef.value?.resetFields()
  Object.assign(venueForm, {
    id: null, name: '', location: '', difficulty: '', type: '',
    slope: 0, length: 0, width: 0, capacity: 50,
    hourlyPrice: 0, dailyPrice: 0, openTime: null, closeTime: null,
    status: 'open', imageUrl: '', facilities: '', description: ''
  })
}

const submitForm = async () => {
  try {
    await venueFormRef.value.validate()
    submitting.value = true
    
    const data = {
      ...venueForm,
      facilities: venueForm.facilities ? venueForm.facilities.split(',').map(f => f.trim()).filter(f => f) : []
    }
    
    const apiFunc = isEdit.value ? updateVenue : createVenue
    const params = isEdit.value ? [venueForm.id, data] : [data]
    const response = await apiFunc(...params)
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadVenues()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const deleteVenue = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除场地"${row.name}"吗？`, '删除场地', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await deleteVenueApi(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadVenues()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => loadVenues())
</script>

<style scoped>
.venue-management { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 280px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>







