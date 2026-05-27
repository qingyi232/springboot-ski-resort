<template>
  <div class="rental-order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>租赁订单管理</span>
          <!-- 管理员不应该新增订单，订单应由用户购买/租赁时自动生成 -->
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="150" />
        <el-table-column prop="equipmentName" label="雪具名称" width="150" />
        <el-table-column prop="rentalStartTime" label="租赁开始时间" width="160" />
        <el-table-column prop="rentalEndTime" label="租赁结束时间" width="160" />
        <el-table-column prop="rentalHours" label="租赁时长(小时)" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.orderStatus === 0" type="info">待支付</el-tag>
            <el-tag v-else-if="row.orderStatus === 1" type="warning">使用中</el-tag>
            <el-tag v-else-if="row.orderStatus === 2" type="success">已归还</el-tag>
            <el-tag v-else type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.paymentStatus === 1" type="success">已支付</el-tag>
            <el-tag v-else type="info">未支付</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="handleView(row)">详情</el-button>
            <!-- 只有普通用户可以支付，管理员不能支付 -->
            <el-button 
              v-if="row.orderStatus === 0 && userRole !== 'admin'" 
              type="success" 
              size="small" 
              @click="handlePay(row)"
            >支付</el-button>
            <el-button v-if="row.orderStatus === 1" type="warning" size="small" @click="handleReturn(row)">归还</el-button>
            <el-button v-if="row.orderStatus === 0 || row.orderStatus === 1" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
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

    <!-- 新增订单对话框 - 已移除：管理员不应该新增订单，订单应由用户下单时自动生成 -->

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="雪具名称">{{ currentOrder.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="雪具ID">{{ currentOrder.equipmentId }}</el-descriptions-item>
        <el-descriptions-item label="租赁开始时间">{{ currentOrder.rentalStartTime }}</el-descriptions-item>
        <el-descriptions-item label="租赁结束时间">{{ currentOrder.rentalEndTime }}</el-descriptions-item>
        <el-descriptions-item label="实际归还时间">{{ currentOrder.actualReturnTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="租赁时长">{{ currentOrder.rentalHours }}小时</el-descriptions-item>
        <el-descriptions-item label="租赁价格">¥{{ currentOrder.rentalPrice }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="押金">¥{{ currentOrder.deposit }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag v-if="currentOrder.orderStatus === 0" type="info">待支付</el-tag>
          <el-tag v-else-if="currentOrder.orderStatus === 1" type="warning">使用中</el-tag>
          <el-tag v-else-if="currentOrder.orderStatus === 2" type="success">已归还</el-tag>
          <el-tag v-else type="danger">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag v-if="currentOrder.paymentStatus === 1" type="success">已支付</el-tag>
          <el-tag v-else type="info">未支付</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.paymentTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ currentOrder.paymentMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 支付对话框 -->
    <el-dialog v-model="payVisible" title="订单支付" width="400px" :close-on-click-modal="false">
      <el-form :model="payForm" ref="payFormRef" label-width="100px">
        <el-form-item label="支付方式" prop="paymentMethod" :rules="[{ required: true, message: '请选择支付方式', trigger: 'change' }]">
          <el-select v-model="payForm.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
            <el-option label="微信支付" value="微信支付" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="现金" value="现金" />
            <el-option label="银行卡" value="银行卡" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmPay" :loading="loading">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRentalOrders, payRentalOrder, returnRentalOrder, cancelRentalOrder } from '@/api/rental'
// 已移除 createRentalOrder - 管理员不应该新增订单
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userRole = computed(() => userStore.userInfo?.role)

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 已移除新增订单相关变量和函数 - 管理员不应该新增订单
// const dialogVisible = ref(false)
// const formRef = ref(null)
// const formData = ref({...})
// const formRules = {...}

const detailVisible = ref(false)
const currentOrder = ref(null)

const payVisible = ref(false)
const payFormRef = ref(null)
const payForm = ref({
  orderId: null,
  paymentMethod: ''
})

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRentalOrders({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
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

// 已移除新增订单功能 - 管理员不应该新增订单，订单应由用户下单时自动生成
// const handleAdd = () => {...}
// const handleSave = async () => {...}

const handleView = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handlePay = (row) => {
  payForm.value = {
    orderId: row.id,
    paymentMethod: ''
  }
  payVisible.value = true
}

const handleConfirmPay = async () => {
  if (!payFormRef.value) return
  
  await payFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const res = await payRentalOrder(payForm.value.orderId, payForm.value.paymentMethod)
      
      if (res.code === 200) {
        ElMessage.success('支付成功')
        payVisible.value = false
        loadData()
      } else {
        ElMessage.error(res.msg || '支付失败')
      }
    } catch (error) {
      ElMessage.error('支付失败')
    } finally {
      loading.value = false
    }
  })
}

const handleReturn = (row) => {
  ElMessageBox.confirm(`确认归还雪具"${row.equipmentName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    loading.value = true
    try {
      const res = await returnRentalOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('归还成功')
        loadData()
      } else {
        ElMessage.error(res.msg || '归还失败')
      }
    } catch (error) {
      ElMessage.error('归还失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm(`确认取消订单"${row.orderNo}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    loading.value = true
    try {
      const res = await cancelRentalOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('取消成功')
        loadData()
      } else {
        ElMessage.error(res.msg || '取消失败')
      }
    } catch (error) {
      ElMessage.error('取消失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
