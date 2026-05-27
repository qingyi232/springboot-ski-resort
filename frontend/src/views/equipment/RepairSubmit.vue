<template>
  <div class="repair-page">
    <div class="page-container">
      <h2>雪具报修</h2>
      <p class="page-desc">雪具出现故障？提交报修工单，工作人员会尽快处理</p>

      <el-card shadow="never" style="margin-bottom: 24px;">
        <el-form :model="repairForm" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="选择雪具" prop="equipmentId">
            <el-select v-model="repairForm.equipmentId" placeholder="请选择需要维修的雪具" style="width: 100%;" @change="handleEquipmentChange" filterable>
              <el-option v-for="item in equipmentList" :key="item.id" :label="`${item.equipmentName}（${item.equipmentCode}）`" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="维修类型">
            <el-radio-group v-model="repairForm.repairType">
              <el-radio label="普通维修">普通维修</el-radio>
              <el-radio label="紧急维修">紧急维修</el-radio>
              <el-radio label="保养">保养</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="优先级">
            <el-select v-model="repairForm.priority" style="width: 200px;">
              <el-option label="低" :value="0" />
              <el-option label="普通" :value="1" />
              <el-option label="高" :value="2" />
              <el-option label="紧急" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="故障描述" prop="faultDescription">
            <el-input v-model="repairForm.faultDescription" type="textarea" :rows="4" placeholder="请详细描述故障情况，如：雪板底部有裂痕、固定器松动等" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitRepair" :loading="submitting">提交报修</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <h3>我的报修记录</h3>
      <el-table :data="myRepairs" v-loading="loading" stripe>
        <el-table-column prop="repairNo" label="工单编号" width="200" />
        <el-table-column prop="equipmentName" label="雪具名称" width="180" />
        <el-table-column prop="repairType" label="类型" width="100" />
        <el-table-column label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="['info','','warning','danger'][row.priority]" size="small">
              {{ ['低','普通','高','紧急'][row.priority] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="['warning','','success','info'][row.status]" size="small">
              {{ ['待处理','处理中','已完成','已关闭'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedStaffName" label="处理人" width="100" />
        <el-table-column prop="repairResult" label="维修结果" min-width="150" show-overflow-tooltip />
        <el-table-column label="维修费用" width="100">
          <template #default="{ row }">¥{{ row.repairCost || 0 }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" />
      </el-table>
      <el-empty v-if="!loading && myRepairs.length === 0" description="暂无报修记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { createRepairOrder, getRepairsByUserId } from '@/api/repair'
import request from '@/utils/request'

const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)
const loading = ref(false)
const equipmentList = ref([])
const myRepairs = ref([])

const repairForm = reactive({
  equipmentId: null,
  equipmentName: '',
  repairType: '普通维修',
  priority: 1,
  faultDescription: ''
})

const rules = {
  equipmentId: [{ required: true, message: '请选择雪具', trigger: 'change' }],
  faultDescription: [{ required: true, message: '请描述故障情况', trigger: 'blur' }]
}

const loadEquipment = async () => {
  try {
    const res = await request({ url: '/api/v1/equipment/list', method: 'get' })
    if (res.code === 200) equipmentList.value = res.data || []
  } catch (e) { console.error(e) }
}

const loadMyRepairs = async () => {
  if (!userStore.userInfo?.id) return
  loading.value = true
  try {
    const res = await getRepairsByUserId(userStore.userInfo.id)
    if (res.code === 200) myRepairs.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleEquipmentChange = (id) => {
  const eq = equipmentList.value.find(e => e.id === id)
  repairForm.equipmentName = eq ? eq.equipmentName : ''
}

const submitRepair = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = {
      ...repairForm,
      userId: userStore.userInfo.id,
      userName: userStore.userInfo.realName || userStore.userInfo.username
    }
    const res = await createRepairOrder(data)
    if (res.code === 200) {
      ElMessage.success('报修提交成功，工作人员会尽快处理')
      resetForm()
      loadMyRepairs()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('提交失败')
  } finally { submitting.value = false }
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(repairForm, { equipmentId: null, equipmentName: '', repairType: '普通维修', priority: 1, faultDescription: '' })
}

onMounted(() => { loadEquipment(); loadMyRepairs() })
</script>

<style scoped>
.repair-page { padding: 24px; min-height: calc(100vh - 80px); }
.page-container { max-width: 1000px; margin: 0 auto; }
.page-desc { color: #909399; margin-bottom: 24px; }
h3 { margin: 32px 0 16px; }
</style>
