<template>
  <div class="front-equipment-page">
    <!-- 装备列表 -->
    <div class="page-container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilterChange" size="large">
          <el-radio-button label="all">全部装备</el-radio-button>
          <el-radio-button label="单板">单板</el-radio-button>
          <el-radio-button label="双板">双板</el-radio-button>
          <el-radio-button label="雪镜">雪镜</el-radio-button>
          <el-radio-button label="头盔">头盔</el-radio-button>
          <el-radio-button label="雪杖">雪杖</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 装备网格 -->
      <div class="equipment-grid" v-loading="loading">
        <div v-for="equipment in equipmentList" :key="equipment.id" class="equipment-card" :class="{'unavailable': equipment.status !== 1}">
          <div class="equipment-image">
            <img :src="equipment.imageUrl || 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=600&q=80'" :alt="equipment.equipmentName" />
            <div class="status-badge" :class="equipment.status === 1 ? 'available' : 'rented'">
              {{ equipment.status === 1 ? '✓ 可租赁' : '✗ 租赁中' }}
            </div>
          </div>
          <div class="equipment-info">
            <h3 class="equipment-name">{{ equipment.equipmentName }}</h3>
            <div class="equipment-meta">
              <span class="brand">{{ equipment.brand }}</span>
              <span class="model">{{ equipment.model }}</span>
            </div>
            
            <div class="equipment-specs">
              <div class="spec-row">
                <span class="spec-label">类型：</span>
                <span class="spec-value">{{ equipment.equipmentType }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">尺寸：</span>
                <span class="spec-value">{{ equipment.size }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">库存：</span>
                <span class="spec-value">{{ equipment.availableQuantity }}/{{ equipment.stockQuantity }}</span>
              </div>
            </div>

            <div class="equipment-footer">
              <div class="price-info">
                <span class="price">¥{{ equipment.rentalPrice }}</span>
                <span class="unit">/小时</span>
              </div>
              <button class="rent-btn" :disabled="equipment.status !== 1" @click="handleRent(equipment)">
                {{ equipment.status === 1 ? '立即租赁' : '已租完' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && equipmentList.length === 0" description="暂无装备" />
    </div>

    <!-- 租赁对话框 -->
    <el-dialog v-model="rentalDialogVisible" title="雪具租赁" width="600px">
      <el-form :model="rentalForm" label-width="100px">
        <el-form-item label="雪具名称">
          <el-input v-model="selectedEquipment.equipmentName" disabled />
        </el-form-item>
        <el-form-item label="品牌型号">
          <el-input :value="`${selectedEquipment.brand} ${selectedEquipment.model}`" disabled />
        </el-form-item>
        <el-form-item label="租赁价格">
          <el-input v-model="selectedEquipment.rentalPrice" disabled>
            <template #prepend>¥</template>
            <template #append>/小时</template>
          </el-input>
        </el-form-item>
        <el-form-item label="租赁时长">
          <el-input-number
            v-model="rentalForm.hours"
            :min="1"
            :max="24"
            @change="calculateTotal"
          />
          <span style="margin-left: 12px;">小时</span>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="rentalForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="rentalForm.contactPhone" placeholder="请输入您的联系电话" />
        </el-form-item>
        <el-form-item label="租金">
          <el-input :value="`¥${rentalForm.totalAmount}`" disabled />
        </el-form-item>
        <el-form-item label="押金">
          <el-input :value="`¥${rentalForm.deposit}`" disabled />
        </el-form-item>
        <el-form-item label="总计">
          <el-input :value="`¥${(rentalForm.totalAmount + rentalForm.deposit).toFixed(2)}`" disabled>
            <template #prepend>💰</template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rentalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRental" :loading="renting">确认租赁</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getEquipmentList } from '@/api/equipment'
import { addRentalOrder } from '@/api/rental-order'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const renting = ref(false)
const equipmentList = ref([])
const filterType = ref('all')
const rentalDialogVisible = ref(false)
const selectedEquipment = ref({})
const rentalForm = reactive({
  hours: 4,
  startTime: '',
  contactPhone: '',
  totalAmount: 0,
  deposit: 300
})

onMounted(() => {
  loadEquipment()
})

const loadEquipment = async () => {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 100 }
    const res = await getEquipmentList(params)
    if (res.code === 200) {
      let list = res.data.list || []
      if (filterType.value !== 'all') {
        list = list.filter(e => e.equipmentType === filterType.value)
      }
      equipmentList.value = list
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => loadEquipment()

// 打开租赁对话框
const handleRent = (equipment) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (equipment.status !== 1) {
    ElMessage.warning('该雪具暂不可租赁')
    return
  }
  
  selectedEquipment.value = equipment
  rentalForm.hours = 4
  rentalForm.startTime = new Date()
  rentalForm.contactPhone = userStore.userInfo?.phone || ''
  calculateTotal()
  rentalDialogVisible.value = true
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 计算总金额
const calculateTotal = () => {
  rentalForm.totalAmount = (selectedEquipment.value.rentalPrice || 0) * rentalForm.hours
}

// 确认租赁
const confirmRental = async () => {
  if (!rentalForm.startTime) {
    ElMessage.warning('请选择开始时间')
    return
  }
  
  if (!rentalForm.contactPhone) {
    ElMessage.warning('请输入联系方式')
    return
  }
  
  try {
    renting.value = true
    
    // 计算结束时间
    const endTime = new Date(rentalForm.startTime)
    endTime.setHours(endTime.getHours() + rentalForm.hours)
    
    const rentalData = {
      userId: userStore.userInfo.id,
      equipmentId: selectedEquipment.value.id,
      equipmentName: selectedEquipment.value.equipmentName,
      rentalStartTime: rentalForm.startTime,
      rentalEndTime: endTime,
      rentalHours: rentalForm.hours,
      rentalPrice: selectedEquipment.value.rentalPrice,
      totalAmount: rentalForm.totalAmount,
      deposit: rentalForm.deposit,
      orderStatus: 0 // 待支付
    }
    
    const response = await addRentalOrder(rentalData)
    
    if (response.code === 200) {
      ElMessage.success('租赁订单创建成功！请前往【我的订单】支付')
      rentalDialogVisible.value = false
      // 可以跳转到订单页面
      // router.push('/my-orders')
    } else {
      ElMessage.error(response.message || '租赁失败')
    }
  } catch (error) {
    console.error('租赁失败:', error)
    ElMessage.error('租赁失败')
  } finally {
    renting.value = false
  }
}
</script>

<style scoped>
.front-equipment-page {
  background: #ffffff;
  min-height: calc(100vh - 80px);
  padding: 40px 0;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 50px;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
}

/* 装备网格 */
.equipment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.equipment-card {
  display: flex;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.equipment-card:hover {
  transform: translateY(-4px);
  border-color: #1890ff;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.12);
}

.equipment-card.unavailable {
  opacity: 0.6;
}

.equipment-image {
  position: relative;
  width: 140px;
  flex-shrink: 0;
  background: #f0f2f5;
  overflow: hidden;
}

.equipment-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-badge {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 6px;
  font-size: 11px;
  font-weight: 600;
  text-align: center;
  color: white;
}

.status-badge.available {
  background: #52c41a;
}

.status-badge.rented {
  background: #8c8c8c;
}

.equipment-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.equipment-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.equipment-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 13px;
}

.brand {
  color: #1890ff;
  font-weight: 600;
}

.model {
  color: #8c8c8c;
}

.equipment-specs {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 13px;
}

.spec-row {
  display: flex;
}

.spec-label {
  color: #8c8c8c;
  width: 50px;
}

.spec-value {
  color: #262626;
  font-weight: 500;
}

.equipment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price {
  font-size: 22px;
  font-weight: 700;
  color: #ff4d4f;
}

.unit {
  font-size: 11px;
  color: #8c8c8c;
}

.rent-btn {
  padding: 8px 20px;
  background: #1890ff;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.rent-btn:hover:not(:disabled) {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.rent-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}
</style>
