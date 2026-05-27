<template>
  <div class="order-management">
    <div class="page-header">
      <h1>销售订单管理</h1>
    </div>

    <!-- 移除标签页，直接显示销售订单内容 -->
    <!-- 租赁订单已有单独页面（租赁订单菜单），无需在此重复 -->
    
    <el-card class="search-card" shadow="never">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="订单号">
              <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 200px;" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 150px;">
                <el-option label="待支付" value="pending" />
                <el-option label="待发货" value="paid" />
                <el-option label="已发货" value="shipped" />
                <el-option label="已完成" value="completed" />
                <el-option label="已取消" value="cancelled" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
              <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <el-table :data="salesOrders" v-loading="loading" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="180" />
            <el-table-column prop="userName" label="用户" width="100" />
            <el-table-column prop="itemCount" label="商品数" width="80" />
            <el-table-column label="订单金额" width="120">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="receiverName" label="收货人" width="100" />
            <el-table-column prop="receiverPhone" label="联系电话" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getSalesStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="180">
              <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" text @click="viewDetail(row)">查看</el-button>
                <el-button
                  v-if="row.status === 'paid'"
                  type="success"
                  text
                  @click="shipOrder(row)"
                >发货</el-button>
                <el-button
                  v-if="row.status === 'pending'"
                  type="danger"
                  text
                  @click="cancelOrder(row)"
                >取消</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="loadSalesOrders"
            />
          </div>
        </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress || '-' }}</el-descriptions-item>
        
        <el-descriptions-item label="商品数量">{{ currentOrder.itemCount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ currentOrder.discountAmount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ currentOrder.actualAmount || currentOrder.totalAmount }}</el-descriptions-item>
        
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getSalesStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="物流状态">
          <el-tag v-if="currentOrder.shippingStatus === 0" type="info">未发货</el-tag>
          <el-tag v-else-if="currentOrder.shippingStatus === 1" type="warning">已发货</el-tag>
          <el-tag v-else-if="currentOrder.shippingStatus === 2" type="success">已签收</el-tag>
          <el-tag v-else type="info">-</el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="支付方式">{{ currentOrder.paymentMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.paymentTime ? formatDateTime(currentOrder.paymentTime) : '-' }}</el-descriptions-item>
        
        <el-descriptions-item label="快递公司">{{ currentOrder.expressCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="快递单号">{{ currentOrder.expressNo || '-' }}</el-descriptions-item>
        
        <el-descriptions-item label="下单时间">{{ formatDateTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(currentOrder.updateTime) }}</el-descriptions-item>
        
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
// 只导入销售订单相关API，租赁订单已有单独页面
import { getSalesOrders, cancelSalesOrder, shipSalesOrder } from '@/api/sales-order'
import { formatDateTime } from '@/utils/performance'

// 移除 orderType 和 rentalOrders，只保留销售订单
const searchForm = reactive({ orderNo: '', status: '' })
const salesOrders = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

// 订单详情对话框
const detailVisible = ref(false)
const currentOrder = ref(null)

// 只保留销售订单加载函数
const loadSalesOrders = async () => {
  try {
    loading.value = true
    const response = await getSalesOrders({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      salesOrders.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载销售订单失败')
  } finally {
    loading.value = false
  }
}

// 简化搜索和重置逻辑，直接调用销售订单加载
const handleSearch = () => {
  pagination.value.page = 1
  loadSalesOrders()
}

const resetSearch = () => {
  Object.assign(searchForm, { orderNo: '', status: '' })
  pagination.value.page = 1
  loadSalesOrders()
}

// 只保留销售订单相关的状态处理函数
const getStatusType = (status) => ({
  'pending': 'warning', 'paid': 'primary', 'shipped': 'primary',
  'completed': 'success', 'cancelled': 'info'
}[status] || 'info')

const getSalesStatusText = (status) => ({
  'pending': '待支付', 'paid': '待发货', 'shipped': '已发货',
  'completed': '已完成', 'cancelled': '已取消'
}[status] || status)

const viewDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

// 发货功能
const shipOrder = async (row) => {
  try {
    await ElMessageBox.confirm('确认发货该订单？', '发货确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await shipSalesOrder(row.id)
    if (response.code === 200) {
      ElMessage.success('发货成功')
      loadSalesOrders()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

// 取消订单功能（只处理销售订单）
const cancelOrder = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '取消订单', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await cancelSalesOrder(row.id)
    if (response.code === 200) {
      ElMessage.success('订单已取消')
      loadSalesOrders()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

// 页面加载时只加载销售订单
onMounted(() => loadSalesOrders())
</script>

<style scoped>
.order-management { padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 350px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>


