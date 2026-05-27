<template>
  <div class="my-orders">
    <div class="page-header">
      <h1>我的订单</h1>
      <p>查看和管理您的所有订单</p>
    </div>

    <!-- 订单类型标签页 -->
    <el-tabs v-model="orderType" @tab-change="handleTypeChange" class="order-tabs">
      <el-tab-pane label="租赁订单" name="rental">
        <div class="order-list" v-loading="loading">
          <div v-for="order in rentalOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <div class="order-no">
                <span>订单号：{{ order.orderNo }}</span>
                <el-tag :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
              <div class="order-time">{{ formatDateTime(order.createTime) }}</div>
            </div>

            <div class="order-content">
              <div class="order-info">
                <div class="info-item">
                  <label>租赁雪具：</label>
                  <span>{{ order.equipmentName }}</span>
                </div>
                <div class="info-item">
                  <label>租赁时间：</label>
                  <span>{{ formatDateTime(order.rentDate) }}</span>
                </div>
                <div class="info-item">
                  <label>计划归还：</label>
                  <span>{{ formatDateTime(order.planReturnDate) }}</span>
                </div>
                <div class="info-item" v-if="order.returnDate">
                  <label>实际归还：</label>
                  <span>{{ formatDateTime(order.returnDate) }}</span>
                </div>
                <div class="info-item">
                  <label>租赁时长：</label>
                  <span>{{ order.rentalHours }} 小时</span>
                </div>
              </div>

              <div class="order-price">
                <div class="price-item">
                  <span>租金：</span>
                  <span>¥{{ order.totalAmount }}</span>
                </div>
                <div class="price-item">
                  <span>押金：</span>
                  <span>¥{{ order.deposit }}</span>
                </div>
                <div class="price-total">
                  <span>总计：</span>
                  <span class="total-amount">¥{{ (parseFloat(order.totalAmount) + parseFloat(order.deposit)).toFixed(2) }}</span>
                </div>
              </div>
            </div>

            <div class="order-actions">
              <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
              <el-button
                v-if="order.status === 'pending'"
                type="primary"
                size="small"
                @click="payOrder(order)"
              >
                去支付
              </el-button>
              <el-button
                v-if="order.status === 'ongoing'"
                type="success"
                size="small"
                @click="returnEquipment(order)"
              >
                归还雪具
              </el-button>
              <el-button
                v-if="order.status === 'pending'"
                type="danger"
                size="small"
                @click="cancelOrder(order)"
              >
                取消订单
              </el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="销售订单" name="sales">
        <div class="order-list" v-loading="loading">
          <div v-for="order in salesOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <div class="order-no">
                <span>订单号：{{ order.orderNo }}</span>
                <el-tag :type="getStatusType(order.status)">
                  {{ getSalesStatusText(order.status) }}
                </el-tag>
              </div>
              <div class="order-time">{{ formatDateTime(order.createTime) }}</div>
            </div>

            <div class="order-content">
              <div class="order-info">
                <div class="info-item">
                  <label>商品详情：</label>
                  <span>{{ order.remark }}</span>
                </div>
                <div class="info-item" v-if="order.paymentMethod">
                  <label>支付方式：</label>
                  <span>{{ order.paymentMethod }}</span>
                </div>
                <div class="info-item" v-if="order.paymentTime">
                  <label>支付时间：</label>
                  <span>{{ formatDateTime(order.paymentTime) }}</span>
                </div>
                <div class="info-item" v-if="order.shippingAddress">
                  <label>收货地址：</label>
                  <span>{{ order.shippingAddress }}</span>
                </div>
              </div>

              <div class="order-price">
                <div class="price-item">
                  <span>订单总额：</span>
                  <span>¥{{ order.totalAmount }}</span>
                </div>
                <div class="price-item" v-if="order.discountAmount > 0">
                  <span>优惠金额：</span>
                  <span>-¥{{ order.discountAmount }}</span>
                </div>
                <div class="price-total">
                  <span>实付金额：</span>
                  <span class="total-amount">¥{{ order.actualAmount }}</span>
                </div>
              </div>
            </div>

            <div class="order-actions">
              <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
              <el-button
                v-if="order.status === 0"
                type="primary"
                size="small"
                @click="payOrder(order)"
              >
                去支付
              </el-button>
              <el-button
                v-if="order.status === 2"
                type="success"
                size="small"
                @click="confirmReceiveSales(order)"
              >
                确认收货
              </el-button>
              <el-button
                v-if="order.status === 0"
                type="danger"
                size="small"
                @click="cancelSalesOrder(order)"
              >
                取消订单
              </el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 空状态 -->
    <el-empty v-if="!loading && getCurrentOrders().length === 0" description="暂无订单" />

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        :current-page="pagination.page"
        :page-size="pagination.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, cancelRentalOrder, returnRentalOrder, payOrder as payRentalOrder } from '@/api/rental-order'
import { getSalesOrdersByUserId, cancelSalesOrder as cancelSalesOrderApi, confirmReceiveSalesOrder, paySalesOrder as paySalesOrderApi } from '@/api/sales-order'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/performance'

// 获取用户信息
const userStore = useUserStore()

// 订单类型
const orderType = ref('rental')

// 订单列表
const rentalOrders = ref([])
const salesOrders = ref([])
const loading = ref(false)
const pagination = ref({
  page: 1,
  pageSize: 10
})
const total = ref(0)

// 获取当前订单
const getCurrentOrders = () => {
  return orderType.value === 'rental' ? rentalOrders.value : salesOrders.value
}

// 加载租赁订单
const loadRentalOrders = async () => {
  try {
    loading.value = true
    
    console.log('=== 开始加载租赁订单 ===')
    console.log('userStore.userInfo：', userStore.userInfo)
    
    const userId = userStore.userInfo.userId || userStore.userInfo.id
    console.log('userId：', userId)
    
    // 使用getUserOrders获取当前用户的租赁订单
    const response = await getUserOrders(userId)
    console.log('租赁订单响应：', response)
    
    if (response.code === 200) {
      // 转换状态字段：orderStatus (数字) -> status (字符串)
      const orders = (response.data || []).map(order => {
        const status = getStatusFromCode(order.orderStatus)
        console.log(`订单 ${order.orderNo}: orderStatus=${order.orderStatus}, status=${status}`)
        return {
          ...order,
          status,
          rentDate: order.rentalStartTime,
          planReturnDate: order.rentalEndTime,
          returnDate: order.actualReturnTime
        }
      })
      
      rentalOrders.value = orders
      total.value = orders.length
      console.log('租赁订单数量：', rentalOrders.value.length)
      console.log('完整订单列表：', orders)
    } else {
      console.error('响应码不是 200：', response.code, response.message)
      ElMessage.error(response.message || '加载订单失败')
    }
  } catch (error) {
    console.error('=== 加载租赁订单失败 ===', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 将数字状态码转换为字符串状态
const getStatusFromCode = (code) => {
  const statusMap = {
    0: 'pending',    // 待支付
    1: 'ongoing',    // 使用中
    2: 'returned',   // 已归还
    3: 'cancelled',  // 已取消
    4: 'cancelled'   // 已取消（兼容销售订单的状态码）
  }
  const status = statusMap[code]
  if (!status) {
    console.warn(`未知的订单状态码: ${code}，默认为 pending`)
    return 'pending'
  }
  return status
}

// 加载销售订单
const loadSalesOrders = async () => {
  try {
    loading.value = true
    
    console.log('=== 开始加载销售订单 ===')
    console.log('userStore.userInfo：', userStore.userInfo)
    
    const userId = userStore.userInfo.id  // 修复：字段名是 id，不是 userId
    console.log('userId：', userId)
    
    const response = await getSalesOrdersByUserId(userId)
    console.log('销售订单响应：', response)
    
    if (response.code === 200) {
      salesOrders.value = response.data || []
      total.value = response.data?.length || 0
      console.log('销售订单数量：', salesOrders.value.length)
    } else {
      console.error('响应码不是 200：', response.code, response.message)
      ElMessage.error(response.message || '加载订单失败')
    }
  } catch (error) {
    console.error('=== 加载销售订单失败 ===')
    console.error('错误类型：', typeof error)
    console.error('错误内容：', error)
    console.error('错误消息：', error.message)
    console.error('错误响应：', error.response)
    console.error('错误堆栈：', error.stack)
    ElMessage.error(`加载订单失败：${error.message || '系统错误'}`)
  } finally {
    loading.value = false
  }
}

// 订单类型变化
const handleTypeChange = () => {
  pagination.value.page = 1
  if (orderType.value === 'rental') {
    loadRentalOrders()
  } else {
    loadSalesOrders()
  }
}

// 页码变化
const handlePageChange = () => {
  if (orderType.value === 'rental') {
    loadRentalOrders()
  } else {
    loadSalesOrders()
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'ongoing': 'primary',
    'returned': 'success',
    'cancelled': 'info',
    'shipped': 'primary',
    'completed': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本（租赁）
const getStatusText = (status) => {
  const textMap = {
    'pending': '待支付',
    'ongoing': '进行中',
    'returned': '已归还',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

// 获取状态文本（销售）
const getSalesStatusText = (status) => {
  const textMap = {
    0: '待支付',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return textMap[status] || '未知状态'
}

// 查看订单详情
const viewOrderDetail = (order) => {
  ElMessageBox.alert(
    `<div style="text-align: left;">
      <p><strong>订单编号：</strong>${order.orderNo || order.orderNumber || '-'}</p>
      <p><strong>订单金额：</strong>¥${order.totalAmount || order.totalPrice || 0}</p>
      <p><strong>订单状态：</strong>${getStatusText(order.status)}</p>
      <p><strong>创建时间：</strong>${order.createTime}</p>
      ${order.rentalStartTime ? `<p><strong>租赁开始：</strong>${order.rentalStartTime}</p>` : ''}
      ${order.rentalEndTime ? `<p><strong>租赁结束：</strong>${order.rentalEndTime}</p>` : ''}
      ${order.paymentTime ? `<p><strong>支付时间：</strong>${order.paymentTime}</p>` : ''}
      ${order.paymentMethod ? `<p><strong>支付方式：</strong>${order.paymentMethod}</p>` : ''}
    </div>`,
    '订单详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
  )
}

// 支付订单
const payOrder = async (order) => {
  try {
    console.log('=== 支付订单 ===')
    console.log('订单信息：', order)
    
    const { value: paymentMethod } = await ElMessageBox.prompt('请选择支付方式：微信支付、支付宝、现金', '订单支付', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入支付方式'
    })
    
    console.log('支付方式：', paymentMethod)
    console.log('订单类型：', orderType.value)
    
    const payFunc = orderType.value === 'rental' ? payRentalOrder : paySalesOrder
    const response = await payFunc(order.id, paymentMethod)
    
    console.log('支付响应：', response)
    
    if (response.code === 200) {
      ElMessage.success('支付成功')
      
      // 立即更新本地订单状态
      if (orderType.value === 'rental') {
        const index = rentalOrders.value.findIndex(o => o.id === order.id)
        if (index !== -1) {
          rentalOrders.value[index].status = 'ongoing'
          rentalOrders.value[index].orderStatus = 1
          rentalOrders.value[index].paymentStatus = 1
          console.log('本地状态已更新为 ongoing (使用中)')
        }
        await loadRentalOrders()
      } else {
        await loadSalesOrders()
      }
    } else {
      console.error('支付失败，响应码：', response.code, '消息：', response.message)
      ElMessage.error(response.message || response.msg || '支付失败')
    }
  } catch (error) {
    console.error('=== 支付订单异常 ===', error)
    if (error !== 'cancel') {
      ElMessage.error('支付失败: ' + (error.message || '未知错误'))
    }
  }
}

// 归还雪具
const returnEquipment = async (order) => {
  try {
    console.log('=== 归还雪具 ===')
    console.log('订单信息：', order)
    
    await ElMessageBox.confirm('确认归还雪具吗？', '归还确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await returnRentalOrder(order.id)
    console.log('归还响应：', response)
    
    if (response.code === 200) {
      ElMessage.success('归还成功')
      
      // 立即更新本地订单状态
      const index = rentalOrders.value.findIndex(o => o.id === order.id)
      if (index !== -1) {
        rentalOrders.value[index].status = 'returned'
        rentalOrders.value[index].orderStatus = 2
        console.log('本地状态已更新为 returned (已归还)')
      }
      
      await loadRentalOrders()
    } else {
      console.error('归还失败，响应码：', response.code, '消息：', response.message)
      ElMessage.error(response.message || response.msg || '归还失败')
    }
  } catch (error) {
    console.error('=== 归还雪具异常 ===', error)
    if (error !== 'cancel') {
      ElMessage.error('归还失败: ' + (error.message || '未知错误'))
    }
  }
}

// 确认收货
const confirmReceive = async (order) => {
  try {
    await ElMessageBox.confirm('确认收货吗？', '确认收货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const response = await completeSalesOrder(order.id)
    
    if (response.code === 200) {
      ElMessage.success('确认收货成功')
      loadSalesOrders()
    } else {
      ElMessage.error(response.msg || '确认收货失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    console.log('=== 取消订单 ===')
    console.log('订单信息：', order)
    console.log('订单ID：', order.id)
    console.log('订单状态：', order.status)
    console.log('订单状态码：', order.orderStatus)
    console.log('订单类型：', orderType.value)
    
    await ElMessageBox.confirm('确定要取消此订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const cancelFunc = orderType.value === 'rental' ? cancelRentalOrder : cancelSalesOrderApi
    const response = await cancelFunc(order.id)
    
    console.log('取消响应：', response)
    
    if (response.code === 200) {
      ElMessage.success('订单已取消')
      
      // 立即更新本地订单状态，避免等待接口响应
      if (orderType.value === 'rental') {
        const index = rentalOrders.value.findIndex(o => o.id === order.id)
        if (index !== -1) {
          rentalOrders.value[index].status = 'cancelled'
          rentalOrders.value[index].orderStatus = 3
          console.log('本地状态已更新为 cancelled')
        }
        // 同时刷新列表确保数据一致
        await loadRentalOrders()
      } else {
        await loadSalesOrders()
      }
    } else {
      console.error('取消失败，响应码：', response.code, '消息：', response.message)
      ElMessage.error(response.message || response.msg || '取消订单失败')
    }
  } catch (error) {
    console.error('=== 取消订单异常 ===', error)
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败: ' + (error.message || '未知错误'))
    }
  }
}

// 支付销售订单
const paySalesOrder = async (orderId, paymentMethod) => {
  try {
    const response = await paySalesOrderApi(orderId, paymentMethod)
    if (response.code === 200) {
      ElMessage.success('支付成功')
      loadSalesOrders()
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error(error.message || '支付失败')
  }
}

// 确认收货（销售订单）
const confirmReceiveSales = async (order) => {
  try {
    await ElMessageBox.confirm('确认收货吗？', '确认收货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const response = await confirmReceiveSalesOrder(order.id)
    
    if (response.code === 200) {
      ElMessage.success('确认收货成功')
      loadSalesOrders()
    } else {
      ElMessage.error(response.msg || '确认收货失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 取消销售订单
const cancelSalesOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await cancelSalesOrderApi(order.id)
    
    if (response.code === 200) {
      ElMessage.success('订单已取消')
      loadSalesOrders()
    } else {
      ElMessage.error(response.msg || '取消订单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

// 组件挂载
onMounted(() => {
  loadRentalOrders()
})
</script>

<style scoped>
.my-orders {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 20px;
}

.page-header {
  max-width: 1200px;
  margin: 0 auto 32px;
  text-align: center;
}

.page-header h1 {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.page-header p {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.order-tabs {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 400px;
}

.order-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  background: white;
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.order-no {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #303133;
}

.order-time {
  font-size: 13px;
  color: #909399;
}

.order-content {
  margin-bottom: 16px;
}

.order-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.info-item {
  font-size: 14px;
}

.info-item label {
  color: #909399;
  margin-right: 8px;
}

.info-item span {
  color: #303133;
}

.order-price {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
}

.price-total {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  font-weight: bold;
  padding-top: 8px;
  border-top: 1px solid #e4e7ed;
}

.total-amount {
  color: #f56c6c;
  font-size: 20px;
}

/* 销售订单商品列表 */
.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.order-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-name {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}

.item-specs {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #606266;
}

.item-price {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.order-summary {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}

.summary-item.total {
  font-size: 16px;
  font-weight: bold;
  padding-top: 8px;
  border-top: 1px solid #e4e7ed;
  margin-bottom: 0;
}

.order-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.pagination {
  max-width: 1200px;
  margin: 32px auto 0;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 24px;
  }
  
  .order-info {
    grid-template-columns: 1fr;
  }
  
  .order-actions {
    flex-wrap: wrap;
  }
}
</style>

