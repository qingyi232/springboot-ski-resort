<template>
  <div class="staff-venues">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>场地查看</span>
        </div>
      </template>

      <!-- 数据表格（只读） -->
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="venueCode" label="场地编号" width="120" />
        <el-table-column prop="venueName" label="场地名称" width="150" />
        <el-table-column prop="venueType" label="类型" width="100" />
        <el-table-column prop="difficultyLevel" label="难度" width="100" />
        <el-table-column prop="rentalPrice" label="价格" width="120">
          <template #default="{ row }">
            <span v-if="row.rentalPrice > 0">¥{{ row.rentalPrice }}/小时</span>
            <span v-else style="color: #67C23A">免费（含门票）</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxCapacity" label="最大容量" width="100" />
        <el-table-column prop="currentCapacity" label="当前人数" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.currentCapacity >= row.maxCapacity ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
              {{ row.currentCapacity || 0 }}
            </span>
            <span> / {{ row.maxCapacity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="large">开放</el-tag>
            <el-tag v-else type="danger" size="large">关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->
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

    <!-- 场地概览统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="场地总数" :value="stats.total" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="开放中" :value="stats.open">
            <template #suffix>
              <span style="font-size: 14px; color: #67C23A">个</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="已关闭" :value="stats.closed">
            <template #suffix>
              <span style="font-size: 14px; color: #F56C6C">个</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="总容量" :value="stats.totalCapacity">
            <template #suffix>
              <span style="font-size: 14px">人</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getVenueList } from '@/api/venue'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 20, total: 0 })

const stats = computed(() => {
  const list = tableData.value
  return {
    total: pagination.total,
    open: list.filter(v => v.status === 1).length,
    closed: list.filter(v => v.status !== 1).length,
    totalCapacity: list.reduce((sum, v) => sum + (v.maxCapacity || 0), 0)
  }
})

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
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
