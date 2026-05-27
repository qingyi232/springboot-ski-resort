<template>
  <div class="staff-rental">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>租赁办理</span>
        </div>
      </template>

      <!-- 办理租赁表单 -->
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入顾客手机号" @blur="searchUser">
                <template #append>
                  <el-button @click="searchUser">查询</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户信息">
              <el-tag v-if="selectedUser" type="success">{{ selectedUser.realName || selectedUser.username }}（{{ selectedUser.phone }}）</el-tag>
              <el-tag v-else type="info">未查询到用户</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="租赁雪具" prop="equipmentId">
              <el-select v-model="formData.equipmentId" placeholder="请选择雪具" style="width: 100%" @change="onEquipmentChange" filterable>
                <el-option
                  v-for="item in availableEquipments"
                  :key="item.id"
                  :label="`${item.equipmentName}（${item.equipmentType} - ¥${item.rentalPrice}/小时）`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租赁时长" prop="rentalHours">
              <el-input-number v-model="formData.rentalHours" :min="1" :max="72" @change="calcAmount" />
              <span style="margin-left: 10px">小时</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="租赁价格">
              <span style="font-size: 16px; color: #409EFF">¥{{ currentPrice }}/小时</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计金额">
              <span style="font-size: 20px; font-weight: bold; color: #E6A23C">¥{{ totalAmount.toFixed(2) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-select v-model="formData.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
                <el-option label="微信支付" value="微信支付" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="现金" value="现金" />
                <el-option label="银行卡" value="银行卡" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading" size="large">
            确认办理
          </el-button>
          <el-button @click="handleReset" size="large">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 今日办理记录 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>今日办理记录</span>
      </template>
      <el-table :data="todayOrders" border v-loading="loading" max-height="400">
        <el-table-column prop="orderNo" label="订单编号" width="150" />
        <el-table-column prop="equipmentName" label="雪具名称" width="150" />
        <el-table-column prop="rentalHours" label="时长(小时)" width="100" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.orderStatus === 0" type="info">待支付</el-tag>
            <el-tag v-else-if="row.orderStatus === 1" type="warning">使用中</el-tag>
            <el-tag v-else-if="row.orderStatus === 2" type="success">已归还</el-tag>
            <el-tag v-else type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="办理时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.orderStatus === 1" type="warning" size="small" @click="handleReturn(row)">办理归还</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableEquipment } from '@/api/equipment'
import { createRentalOrder, getRentalOrders, returnRentalOrder } from '@/api/rental'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const selectedUser = ref(null)
const availableEquipments = ref([])
const todayOrders = ref([])
const currentPrice = ref(0)
const totalAmount = ref(0)

const formData = reactive({
  phone: '',
  equipmentId: null,
  rentalHours: 1,
  paymentMethod: '现金'
})

const formRules = {
  phone: [{ required: true, message: '请输入顾客手机号', trigger: 'blur' }],
  equipmentId: [{ required: true, message: '请选择雪具', trigger: 'change' }],
  rentalHours: [{ required: true, message: '请输入租赁时长', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}

onMounted(() => {
  loadEquipments()
  loadTodayOrders()
})

const loadEquipments = async () => {
  try {
    const res = await getAvailableEquipment()
    if (res.code === 200) {
      availableEquipments.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载雪具列表失败')
  }
}

const loadTodayOrders = async () => {
  loading.value = true
  try {
    const res = await getRentalOrders({ pageNum: 1, pageSize: 50 })
    if (res.code === 200) {
      todayOrders.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载订单记录失败')
  } finally {
    loading.value = false
  }
}

const searchUser = async () => {
  if (!formData.phone) return
  try {
    const res = await request({ url: '/api/v1/users/phone/' + formData.phone, method: 'get' })
    if (res.code === 200 && res.data) {
      selectedUser.value = res.data
    } else {
      selectedUser.value = null
      ElMessage.warning('未找到该手机号对应的用户')
    }
  } catch (error) {
    selectedUser.value = null
    ElMessage.warning('未找到该手机号对应的用户')
  }
}

const onEquipmentChange = (id) => {
  const eq = availableEquipments.value.find(e => e.id === id)
  currentPrice.value = eq ? eq.rentalPrice : 0
  calcAmount()
}

const calcAmount = () => {
  totalAmount.value = currentPrice.value * formData.rentalHours
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    if (!selectedUser.value) {
      ElMessage.warning('请先查询并确认用户信息')
      return
    }

    submitLoading.value = true
    try {
      const eq = availableEquipments.value.find(e => e.id === formData.equipmentId)
      const startTime = new Date()
      const endTime = new Date(startTime.getTime() + formData.rentalHours * 60 * 60 * 1000)
      const formatTime = (d) => d.toISOString().slice(0, 19).replace('T', ' ')
      const orderData = {
        userId: selectedUser.value.id,
        equipmentId: formData.equipmentId,
        equipmentName: eq?.equipmentName || '',
        rentalStartTime: formatTime(startTime),
        rentalEndTime: formatTime(endTime),
        rentalHours: formData.rentalHours,
        rentalPrice: currentPrice.value,
        totalAmount: totalAmount.value,
        deposit: 0,
        paymentMethod: formData.paymentMethod,
        orderStatus: 1,
        paymentStatus: 1
      }
      const res = await createRentalOrder(orderData)
      if (res.code === 200) {
        ElMessage.success('租赁办理成功')
        handleReset()
        loadTodayOrders()
        loadEquipments()
      } else {
        ElMessage.error(res.msg || '办理失败')
      }
    } catch (error) {
      ElMessage.error('办理失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleReturn = (row) => {
  ElMessageBox.confirm(`确认办理归还"${row.equipmentName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      const res = await returnRentalOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('归还成功')
        loadTodayOrders()
        loadEquipments()
      } else {
        ElMessage.error(res.msg || '归还失败')
      }
    } catch (error) {
      ElMessage.error('归还失败')
    }
  }).catch(() => {})
}

const handleReset = () => {
  formData.phone = ''
  formData.equipmentId = null
  formData.rentalHours = 1
  formData.paymentMethod = '现金'
  selectedUser.value = null
  currentPrice.value = 0
  totalAmount.value = 0
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
