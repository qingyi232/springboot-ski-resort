<template>
  <div class="booking-management">
    <div class="page-header">
      <h1>预约管理</h1>
    </div>

    <el-tabs v-model="bookingType" @tab-change="handleTypeChange">
      <el-tab-pane label="教练预约" name="coach">
        <el-card class="search-card" shadow="never">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="教练">
              <el-select v-model="searchForm.coachId" placeholder="全部" clearable filterable style="width: 150px;">
                <el-option
                  v-for="coach in coaches"
                  :key="coach.id"
                  :label="coach.name"
                  :value="coach.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
                <el-option label="待确认" value="pending" />
                <el-option label="已确认" value="confirmed" />
                <el-option label="已完成" value="completed" />
                <el-option label="已取消" value="cancelled" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker
                v-model="searchForm.date"
                type="date"
                placeholder="选择日期"
                style="width: 180px;"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
              <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <el-table :data="coachBookings" v-loading="loading" stripe>
            <el-table-column prop="userName" label="用户" width="100" />
            <el-table-column prop="coachName" label="教练" width="100" />
            <el-table-column prop="bookingDate" label="预约日期" width="120">
              <template #default="{ row }">{{ formatDate(row.bookingDate) }}</template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="100" />
            <el-table-column prop="duration" label="时长" width="80">
              <template #default="{ row }">{{ row.duration }} 小时</template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="courseType" label="课程类型" width="100" />
            <el-table-column label="费用" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约时间" width="180">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <div style="display: flex; gap: 4px; flex-wrap: wrap; align-items: center;">
                  <el-button type="primary" size="small" @click="viewDetail(row)">查看</el-button>
                  <el-button
                    v-if="row.status === 0 || row.status === 'pending'"
                    type="success"
                    size="small"
                    @click="confirmBooking(row)"
                  >确认</el-button>
                  <el-button
                    v-if="row.status === 1 || row.status === 'confirmed'"
                    type="warning"
                    size="small"
                    @click="completeBooking(row)"
                  >完成</el-button>
                  <el-button
                    v-if="[0, 1, 'pending', 'confirmed'].includes(row.status)"
                    type="danger"
                    size="small"
                    @click="cancelBooking(row)"
                  >取消</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadCoachBookings"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="场地预订" name="venue">
        <el-card class="search-card" shadow="never">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="场地">
              <el-select v-model="searchForm.venueId" placeholder="全部" clearable filterable style="width: 150px;">
                <el-option
                  v-for="venue in venues"
                  :key="venue.id"
                  :label="venue.name"
                  :value="venue.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
                <el-option label="待确认" value="pending" />
                <el-option label="已确认" value="confirmed" />
                <el-option label="已完成" value="completed" />
                <el-option label="已取消" value="cancelled" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker
                v-model="searchForm.date"
                type="date"
                placeholder="选择日期"
                style="width: 180px;"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
              <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <el-table :data="venueBookings" v-loading="loading" stripe>
            <el-table-column prop="userName" label="用户" width="100" />
            <el-table-column prop="venueName" label="场地" min-width="120" />
            <el-table-column prop="bookingDate" label="预订日期" width="120">
              <template #default="{ row }">{{ formatDate(row.bookingDate) }}</template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="100" />
            <el-table-column prop="endTime" label="结束时间" width="100" />
            <el-table-column prop="peopleCount" label="人数" width="80" />
            <el-table-column label="费用" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预订时间" width="180">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <div style="display: flex; gap: 4px; flex-wrap: wrap; align-items: center;">
                  <el-button type="primary" size="small" @click="viewDetail(row)">查看</el-button>
                  <el-button
                    v-if="row.status === 0 || row.status === 'pending'"
                    type="success"
                    size="small"
                    @click="confirmBooking(row)"
                  >确认</el-button>
                  <el-button
                    v-if="row.status === 1 || row.status === 'confirmed'"
                    type="warning"
                    size="small"
                    @click="completeBooking(row)"
                  >完成</el-button>
                  <el-button
                    v-if="[0, 1, 'pending', 'confirmed'].includes(row.status)"
                    type="danger"
                    size="small"
                    @click="cancelBooking(row)"
                  >取消</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadVenueBookings"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 教练预约详情对话框 -->
    <el-dialog v-model="coachDetailVisible" title="教练预约详情" width="700px">
      <el-descriptions :column="2" border v-if="currentBooking">
        <el-descriptions-item label="预约编号" :span="2">{{ currentBooking.bookingNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentBooking.userName }}</el-descriptions-item>
        <el-descriptions-item label="教练">{{ currentBooking.coachName }}</el-descriptions-item>
        
        <el-descriptions-item label="课程名称" :span="2">{{ currentBooking.courseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ currentBooking.courseType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程难度">{{ currentBooking.difficultyLevel || '-' }}</el-descriptions-item>
        
        <el-descriptions-item label="预约日期">{{ formatDate(currentBooking.bookingDate) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentBooking.startTime }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ currentBooking.duration }} 小时</el-descriptions-item>
        <el-descriptions-item label="人数">{{ currentBooking.peopleCount || 1 }} 人</el-descriptions-item>
        
        <el-descriptions-item label="单价">¥{{ currentBooking.hourlyRate || 0 }}</el-descriptions-item>
        <el-descriptions-item label="总费用">¥{{ currentBooking.totalAmount }}</el-descriptions-item>
        
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentBooking.status)">
            {{ getStatusText(currentBooking.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag v-if="currentBooking.paymentStatus === 1" type="success">已支付</el-tag>
          <el-tag v-else type="warning">未支付</el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="预约时间">{{ formatDateTime(currentBooking.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(currentBooking.updateTime) }}</el-descriptions-item>
        
        <el-descriptions-item label="备注" :span="2">{{ currentBooking.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="coachDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 场地预订详情对话框 -->
    <el-dialog v-model="venueDetailVisible" title="场地预订详情" width="700px">
      <el-descriptions :column="2" border v-if="currentBooking">
        <el-descriptions-item label="预订编号" :span="2">{{ currentBooking.bookingNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentBooking.userName }}</el-descriptions-item>
        <el-descriptions-item label="场地">{{ currentBooking.venueName }}</el-descriptions-item>
        
        <el-descriptions-item label="场地类型">{{ currentBooking.venueType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="难度级别">{{ currentBooking.venueDifficulty || '-' }}</el-descriptions-item>
        
        <el-descriptions-item label="预订日期">{{ formatDate(currentBooking.bookingDate) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentBooking.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentBooking.endTime }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ currentBooking.hours }} 小时</el-descriptions-item>
        
        <el-descriptions-item label="人数">{{ currentBooking.peopleCount || '-' }} 人</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ currentBooking.rentalPrice || 0 }}/小时</el-descriptions-item>
        <el-descriptions-item label="总费用">¥{{ currentBooking.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="押金">¥{{ currentBooking.deposit || 0 }}</el-descriptions-item>
        
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentBooking.status)">
            {{ getStatusText(currentBooking.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag v-if="currentBooking.paymentStatus === 1" type="success">已支付</el-tag>
          <el-tag v-else type="warning">未支付</el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="预订时间">{{ formatDateTime(currentBooking.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(currentBooking.updateTime) }}</el-descriptions-item>
        
        <el-descriptions-item label="备注" :span="2">{{ currentBooking.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="venueDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getCoachBookings, cancelCoachBooking, confirmBooking as confirmCoachBookingApi, completeBooking as completeCoachBookingApi } from '@/api/coach-booking'
import { getVenueBookings, cancelVenueBooking, confirmVenueBooking, completeVenueBooking } from '@/api/venue-booking'
import { getCoaches } from '@/api/coach'
import { getVenues } from '@/api/venue'
import { formatDate, formatDateTime } from '@/utils/performance'

const bookingType = ref('coach')
const searchForm = reactive({ coachId: null, venueId: null, status: '', date: null })
const coachBookings = ref([])
const venueBookings = ref([])
const coaches = ref([])
const venues = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

// 详情对话框
const coachDetailVisible = ref(false)
const venueDetailVisible = ref(false)
const currentBooking = ref(null)

const loadCoachBookings = async () => {
  try {
    loading.value = true
    const response = await getCoachBookings({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      coachBookings.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载教练预约失败')
  } finally {
    loading.value = false
  }
}

const loadVenueBookings = async () => {
  try {
    loading.value = true
    const response = await getVenueBookings({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      venueBookings.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载场地预订失败')
  } finally {
    loading.value = false
  }
}

const loadCoaches = async () => {
  const response = await getCoaches({ page: 1, pageSize: 100 })
  if (response.code === 200) coaches.value = response.data.records || []
}

const loadVenues = async () => {
  const response = await getVenues({ page: 1, pageSize: 100 })
  if (response.code === 200) venues.value = response.data.records || []
}

const handleTypeChange = () => {
  pagination.value.page = 1
  bookingType.value === 'coach' ? loadCoachBookings() : loadVenueBookings()
}

const handleSearch = () => {
  pagination.value.page = 1
  bookingType.value === 'coach' ? loadCoachBookings() : loadVenueBookings()
}

const resetSearch = () => {
  Object.assign(searchForm, { coachId: null, venueId: null, status: '', date: null })
  pagination.value.page = 1
  bookingType.value === 'coach' ? loadCoachBookings() : loadVenueBookings()
}

const getStatusType = (status) => {
  // 支持数字和字符串两种格式
  const statusMap = {
    0: 'warning', 'pending': 'warning',
    1: 'primary', 'confirmed': 'primary',
    2: 'primary', 'ongoing': 'primary',
    3: 'success', 'completed': 'success',
    4: 'info', 'cancelled': 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  // 支持数字和字符串两种格式
  const statusMap = {
    0: '待确认', 'pending': '待确认',
    1: '已确认', 'confirmed': '已确认',
    2: '进行中', 'ongoing': '进行中',
    3: '已完成', 'completed': '已完成',
    4: '已取消', 'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const viewDetail = (row) => {
  currentBooking.value = row
  if (bookingType.value === 'coach') {
    coachDetailVisible.value = true
  } else {
    venueDetailVisible.value = true
  }
}

const confirmBooking = async (row) => {
  try {
    await ElMessageBox.confirm('确认该预约？', '确认预约', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const apiFunc = bookingType.value === 'coach' ? confirmCoachBookingApi : confirmVenueBooking
    const response = await apiFunc(row.id)
    if (response.code === 200) {
      ElMessage.success('确认成功')
      bookingType.value === 'coach' ? loadCoachBookings() : loadVenueBookings()
    }
  } catch (error) {
    if (error !== 'cancel') {}
  }
}

const completeBooking = async (row) => {
  try {
    await ElMessageBox.confirm('确认完成该预约？', '完成预约', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const apiFunc = bookingType.value === 'coach' ? completeCoachBookingApi : completeVenueBooking
    const response = await apiFunc(row.id)
    if (response.code === 200) {
      ElMessage.success('已完成')
      bookingType.value === 'coach' ? loadCoachBookings() : loadVenueBookings()
    }
  } catch (error) {
    if (error !== 'cancel') {}
  }
}

const cancelBooking = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？', '取消预约', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const cancelFunc = bookingType.value === 'coach' ? cancelCoachBooking : cancelVenueBooking
    const response = await cancelFunc(row.id)
    if (response.code === 200) {
      ElMessage.success('预约已取消')
      bookingType.value === 'coach' ? loadCoachBookings() : loadVenueBookings()
    }
  } catch (error) {
    if (error !== 'cancel') {}
  }
}

onMounted(() => {
  loadCoachBookings()
  loadCoaches()
  loadVenues()
})
</script>

<style scoped>
.booking-management { padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 350px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>


