<template>
  <div class="equipment-rental-page">
    <!-- 顶部横幅 - 简约风格 -->
    <div class="page-banner">
      <div class="banner-bg"></div>
      <div class="banner-content">
        <div class="banner-label">EQUIPMENT RENTAL</div>
        <h1>雪具租赁</h1>
        <p>专业滑雪装备租赁 · 按小时计费 · 安全便捷</p>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="content-container">
      <!-- 筛选栏 -->
      <div class="filter-section">
        <div class="filter-tabs">
          <div 
            class="filter-tab" 
            :class="{ active: filters.type === '' }"
            @click="filters.type = ''; handleFilterChange()"
          >
            <span>全部装备</span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: filters.type === '单板' }"
            @click="filters.type = '单板'; handleFilterChange()"
          >
            <span>单板滑雪</span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: filters.type === '双板' }"
            @click="filters.type = '双板'; handleFilterChange()"
          >
            <span>双板滑雪</span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: filters.type === '雪镜' }"
            @click="filters.type = '雪镜'; handleFilterChange()"
          >
            <span>雪镜护目</span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: filters.type === '头盔' }"
            @click="filters.type = '头盔'; handleFilterChange()"
          >
            <span>头盔保护</span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: filters.type === '雪杖' }"
            @click="filters.type = '雪杖'; handleFilterChange()"
          >
            <span>雪杖配件</span>
          </div>
        </div>

        <div class="filter-actions">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索雪具名称..."
            prefix-icon="Search"
            clearable
            @input="handleSearch"
            class="search-input"
          />
          <el-select v-model="filters.sortBy" @change="handleSort" class="sort-select">
            <el-option label="综合排序" value="default" />
            <el-option label="价格由低到高" value="price_asc" />
            <el-option label="价格由高到低" value="price_desc" />
          </el-select>
        </div>
      </div>

      <!-- 雪具网格 -->
      <div class="equipment-grid" v-loading="loading">
        <div v-for="equipment in equipmentList" :key="equipment.id" class="equipment-card">
          <div class="card-image">
            <img :src="equipment.imageUrl || 'https://picsum.photos/seed/' + equipment.id + '/500/400'" :alt="equipment.equipmentName" />
            <div class="stock-indicator" :class="equipment.stockQuantity > 5 ? 'available' : 'low'">
              <span class="stock-dot"></span>
              <span>库存{{ equipment.stockQuantity }}件</span>
            </div>
          </div>
          <div class="card-body">
            <div class="equipment-type-tag">{{ equipment.equipmentType }}</div>
            <h3 class="equipment-name">{{ equipment.equipmentName }}</h3>
            <p class="equipment-desc">{{ equipment.description }}</p>
          </div>
          <div class="card-footer">
            <div class="price-box">
              <span class="price-amount">¥{{ equipment.rentalPrice }}</span>
              <span class="price-unit">/小时</span>
            </div>
            <el-button 
              type="primary" 
              @click="rentEquipment(equipment)"
              :disabled="equipment.stockQuantity < 1"
              class="rent-btn"
            >
              {{ equipment.stockQuantity > 0 ? '立即租赁' : '已租完' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && equipmentList.length === 0" class="empty-state">
        <div class="empty-icon">📦</div>
        <div class="empty-text">暂无可租赁雪具</div>
        <div class="empty-tip">请尝试调整筛选条件</div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          :current-page="pagination.page"
          :page-size="pagination.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 租赁对话框 -->
    <el-dialog 
      v-model="rentalDialogVisible" 
      title="雪具租赁" 
      width="650px"
      :close-on-click-modal="false"
    >
      <div class="rental-dialog-content" v-if="currentEquipment">
        <div class="dialog-equipment-info">
          <img :src="currentEquipment.imageUrl || 'https://picsum.photos/seed/' + currentEquipment.id + '/400/400'" :alt="currentEquipment.equipmentName" class="dialog-image" />
          <div class="dialog-info">
            <h2>{{ currentEquipment.equipmentName }}</h2>
            <el-tag size="large">{{ currentEquipment.equipmentType }}</el-tag>
            <p class="dialog-desc">{{ currentEquipment.description }}</p>
            <div class="dialog-stock">剩余库存: <span>{{ currentEquipment.stockQuantity }}</span>件</div>
          </div>
        </div>

        <el-divider />

        <el-form :model="rentalForm" label-width="110px" class="rental-form">
          <el-form-item label="租赁时长">
            <el-input-number 
              v-model="rentalForm.hours" 
              :min="1" 
              :max="24" 
              size="large"
              class="form-input-number"
            />
            <span class="unit-text">小时</span>
          </el-form-item>
          <el-form-item label="租赁数量">
            <el-input-number 
              v-model="rentalForm.quantity" 
              :min="1" 
              :max="currentEquipment.stockQuantity" 
              size="large"
              class="form-input-number"
            />
            <span class="unit-text">件</span>
          </el-form-item>
          <el-form-item label="取货时间">
            <el-date-picker
              v-model="rentalForm.pickupDate"
              type="datetime"
              placeholder="选择取货时间"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              size="large"
              style="width: 100%;"
            />
          </el-form-item>
          <el-form-item label="预计归还时间">
            <el-input :value="expectedReturnTime" readonly size="large" class="readonly-input" />
          </el-form-item>
        </el-form>

        <div class="rental-summary-box">
          <div class="summary-row">
            <span class="label">租金单价</span>
            <span class="value">¥{{ currentEquipment.rentalPrice }}/小时</span>
          </div>
          <div class="summary-row">
            <span class="label">租赁时长</span>
            <span class="value">{{ rentalForm.hours }}小时</span>
          </div>
          <div class="summary-row">
            <span class="label">租赁数量</span>
            <span class="value">{{ rentalForm.quantity }}件</span>
          </div>
          <el-divider />
          <div class="summary-row total-row">
            <span class="label">合计金额</span>
            <span class="value total-amount">¥{{ totalPrice }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rentalDialogVisible = false" size="large">取消</el-button>
          <el-button type="primary" @click="confirmRental" size="large" class="confirm-btn">
            <span>确认租赁</span>
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableEquipment } from '@/api/equipment'
import { createRentalOrder } from '@/api/rental-order'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const loading = ref(false)
const equipmentList = ref([])
const total = ref(0)

const filters = reactive({
  type: '',
  keyword: '',
  sortBy: 'default'
})

const pagination = reactive({
  page: 1,
  pageSize: 12
})

const rentalDialogVisible = ref(false)
const currentEquipment = ref(null)
const rentalForm = reactive({
  hours: 2,
  quantity: 1,
  pickupDate: ''
})

// 计算总价
const totalPrice = computed(() => {
  if (!currentEquipment.value) return 0
  return (currentEquipment.value.rentalPrice * rentalForm.hours * rentalForm.quantity).toFixed(2)
})

// 计算预计归还时间
const expectedReturnTime = computed(() => {
  if (!rentalForm.pickupDate) return '请先选择取货时间'
  const pickupTime = new Date(rentalForm.pickupDate)
  const returnTime = new Date(pickupTime.getTime() + rentalForm.hours * 60 * 60 * 1000)
  return returnTime.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
})

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 加载雪具列表
const loadEquipmentList = async () => {
  loading.value = true
  try {
    const response = await getAvailableEquipment()
    let list = response.data || []

    // 筛选类型
    if (filters.type) {
      list = list.filter(item => item.equipmentType === filters.type)
    }

    // 搜索关键词
    if (filters.keyword) {
      const keyword = filters.keyword.toLowerCase()
      list = list.filter(item => 
        item.equipmentName.toLowerCase().includes(keyword) ||
        item.description.toLowerCase().includes(keyword)
      )
    }

    // 排序
    if (filters.sortBy === 'price_asc') {
      list.sort((a, b) => a.rentalPrice - b.rentalPrice)
    } else if (filters.sortBy === 'price_desc') {
      list.sort((a, b) => b.rentalPrice - a.rentalPrice)
    }

    total.value = list.length
    equipmentList.value = list

  } catch (error) {
    console.error('加载雪具列表失败:', error)
    ElMessage.error('加载失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 筛选变化
const handleFilterChange = () => {
  pagination.page = 1
  loadEquipmentList()
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadEquipmentList()
}

// 排序
const handleSort = () => {
  loadEquipmentList()
}

// 分页变化
const handlePageChange = (page) => {
  pagination.page = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
  loadEquipmentList()
}

// 租赁雪具
const rentEquipment = (equipment) => {
  console.log('=== 检查登录状态 ===')
  console.log('token:', userStore.token)
  console.log('userInfo:', userStore.userInfo)
  console.log('isLoggedIn:', userStore.isLoggedIn)
  
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  currentEquipment.value = equipment
  rentalForm.hours = 2
  rentalForm.quantity = 1
  
  // 设置默认取货时间为明天9点
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  tomorrow.setHours(9, 0, 0, 0)
  rentalForm.pickupDate = tomorrow.toISOString().slice(0, 19).replace('T', ' ')
  
  rentalDialogVisible.value = true
}

// 确认租赁
const confirmRental = async () => {
  if (!rentalForm.pickupDate) {
    ElMessage.warning('请选择取货时间')
    return
  }

  try {
    // 计算租赁结束时间
    const startTime = new Date(rentalForm.pickupDate)
    const endTime = new Date(startTime.getTime() + rentalForm.hours * 60 * 60 * 1000)
    
    const orderData = {
      userId: userStore.userInfo.userId || userStore.userInfo.id,
      equipmentId: currentEquipment.value.id,
      equipmentName: currentEquipment.value.equipmentName,
      rentalStartTime: rentalForm.pickupDate,
      rentalEndTime: endTime.toISOString().slice(0, 19).replace('T', ' '),
      rentalHours: rentalForm.hours,
      rentalPrice: currentEquipment.value.rentalPrice,
      totalAmount: parseFloat(totalPrice.value),
      deposit: 0,
      orderStatus: 0,
      paymentStatus: 0
    }

    console.log('=== 创建租赁订单详细信息 ===')
    console.log('完整订单数据:', JSON.stringify(orderData, null, 2))
    console.log('userId:', orderData.userId, typeof orderData.userId)
    console.log('equipmentId:', orderData.equipmentId, typeof orderData.equipmentId)
    console.log('rentalStartTime:', orderData.rentalStartTime, typeof orderData.rentalStartTime)
    console.log('rentalEndTime:', orderData.rentalEndTime, typeof orderData.rentalEndTime)

    const response = await createRentalOrder(orderData)
    
    if (response.code === 200) {
      ElMessage.success('租赁成功！请按时取货')
      rentalDialogVisible.value = false
      
      // 跳转到订单页面
      setTimeout(() => {
        router.push('/my-orders')
      }, 1500)
    } else {
      ElMessage.error('租赁失败: ' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('租赁失败:', error)
    ElMessage.error('租赁失败: ' + (error.message || '未知错误'))
  }
}

onMounted(() => {
  loadEquipmentList()
})
</script>

<style scoped>
.equipment-rental-page {
  min-height: 100vh;
  background: #f5f8fa;
}

/* 顶部横幅 - 清冷简约风格 */
.page-banner {
  position: relative;
  height: 280px;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  overflow: hidden;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(135deg, rgba(226, 232, 240, 0.3) 0%, rgba(248, 250, 252, 0.3) 100%),
    url('https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=1920&q=80') center/cover;
  opacity: 0.15;
}

.banner-content {
  position: relative;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  z-index: 1;
}

.banner-label {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 3px;
  color: #64748b;
  margin-bottom: 16px;
  text-transform: uppercase;
}

.banner-content h1 {
  font-size: 48px;
  font-weight: 300;
  color: #1e293b;
  margin: 0 0 16px 0;
  letter-spacing: 2px;
}

.banner-content p {
  font-size: 16px;
  color: #64748b;
  margin: 0;
  letter-spacing: 1px;
}

/* 内容容器 */
.content-container {
  max-width: 1400px;
  margin: -40px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
}

/* 筛选栏 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.filter-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-tab {
  padding: 12px 24px;
  border-radius: 8px;
  background: #f8fafc;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.filter-tab:hover {
  background: #f1f5f9;
  color: #475569;
}

.filter-tab.active {
  background: #1e293b;
  color: white;
  border-color: #1e293b;
}

.filter-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.sort-select {
  width: 180px;
}

/* 雪具网格 */
.equipment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.equipment-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}

.equipment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

.card-image {
  position: relative;
  height: 240px;
  overflow: hidden;
  background: #f8fafc;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.equipment-card:hover .card-image img {
  transform: scale(1.08);
}

.stock-indicator {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(10px);
}

.stock-indicator.available {
  background: rgba(34, 197, 94, 0.9);
  color: white;
}

.stock-indicator.low {
  background: rgba(251, 146, 60, 0.9);
  color: white;
}

.stock-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: white;
}

.card-body {
  padding: 20px;
}

.equipment-type-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 6px;
  background: #f1f5f9;
  color: #475569;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 12px;
  letter-spacing: 0.5px;
}

.equipment-name {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.equipment-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafbfc;
}

.price-box {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-amount {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -1px;
}

.price-unit {
  font-size: 14px;
  color: #64748b;
}

.rent-btn {
  min-width: 100px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 18px;
  color: #475569;
  font-weight: 500;
  margin-bottom: 8px;
}

.empty-tip {
  font-size: 14px;
  color: #94a3b8;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 对话框 */
.rental-dialog-content {
  padding: 8px 0;
}

.dialog-equipment-info {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.dialog-image {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.dialog-info {
  flex: 1;
}

.dialog-info h2 {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px 0;
}

.dialog-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 12px 0;
}

.dialog-stock {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.dialog-stock span {
  color: #22c55e;
  font-weight: 700;
  font-size: 18px;
}

.rental-form {
  margin: 20px 0;
}

.form-input-number {
  width: 150px;
}

.unit-text {
  margin-left: 12px;
  color: #64748b;
  font-size: 14px;
}

.readonly-input {
  background: #f8fafc;
}

.rental-summary-box {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  padding: 24px;
  margin-top: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 15px;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-row .label {
  color: #64748b;
}

.summary-row .value {
  color: #1e293b;
  font-weight: 600;
}

.total-row {
  font-size: 18px;
  padding-top: 16px;
}

.total-amount {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -1px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.confirm-btn {
  min-width: 120px;
}

@media (max-width: 768px) {
  .banner-content h1 {
    font-size: 32px;
  }
  
  .filter-tabs {
    justify-content: center;
  }
  
  .filter-actions {
    flex-direction: column;
  }
  
  .search-input,
  .sort-select {
    width: 100%;
    max-width: none;
  }
  
  .equipment-grid {
    grid-template-columns: 1fr;
  }
  
  .dialog-equipment-info {
    flex-direction: column;
  }
  
  .dialog-image {
    width: 100%;
    height: 200px;
  }
}
</style>
