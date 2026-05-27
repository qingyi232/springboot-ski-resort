<template>
  <div class="staff-repair">
    <div class="page-header">
      <h1>维修工单管理</h1>
    </div>

    <el-card shadow="never">
      <el-table :data="repairs" v-loading="loading" stripe>
        <el-table-column prop="repairNo" label="工单编号" width="200" />
        <el-table-column prop="userName" label="报修用户" width="100" />
        <el-table-column prop="equipmentName" label="雪具名称" width="160" />
        <el-table-column prop="repairType" label="类型" width="100" />
        <el-table-column label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="['info','','warning','danger'][row.priority]" size="small">
              {{ ['低','普通','高','紧急'][row.priority] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultDescription" label="故障描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="['warning','','success','info'][row.status]" size="small">
              {{ ['待处理','处理中','已完成','已关闭'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedStaffName" label="处理人" width="100" />
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="primary" text size="small" @click="handleAssign(row)">接单</el-button>
            <el-button v-if="row.status === 1" type="success" text size="small" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status <= 1" type="danger" text size="small" @click="handleClose(row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && repairs.length === 0" description="暂无维修工单" />
    </el-card>

    <!-- 完成维修对话框 -->
    <el-dialog v-model="completeDialogVisible" title="完成维修" width="500px">
      <el-form :model="completeForm" label-width="80px">
        <el-form-item label="维修结果">
          <el-input v-model="completeForm.repairResult" type="textarea" :rows="3" placeholder="请输入维修结果" />
        </el-form-item>
        <el-form-item label="维修费用">
          <el-input-number v-model="completeForm.repairCost" :min="0" :precision="2" style="width: 200px;" />
          <span style="margin-left: 8px; color: #909399;">元</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getAllRepairs, assignRepairStaff, completeRepair, updateRepairStatus } from '@/api/repair'

const userStore = useUserStore()
const loading = ref(false)
const repairs = ref([])
const completeDialogVisible = ref(false)
const currentRepair = ref(null)
const completeForm = reactive({ repairResult: '', repairCost: 0 })

const loadRepairs = async () => {
  loading.value = true
  try {
    const res = await getAllRepairs()
    if (res.code === 200) repairs.value = res.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleAssign = async (row) => {
  try {
    await ElMessageBox.confirm('确认接单处理此维修工单？', '接单确认')
    await assignRepairStaff(row.id, {
      staffId: userStore.userInfo.id,
      staffName: userStore.userInfo.realName || userStore.userInfo.username
    })
    ElMessage.success('接单成功')
    loadRepairs()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

const handleComplete = (row) => {
  currentRepair.value = row
  completeForm.repairResult = ''
  completeForm.repairCost = 0
  completeDialogVisible.value = true
}

const submitComplete = async () => {
  try {
    await completeRepair(currentRepair.value.id, completeForm)
    ElMessage.success('维修完成')
    completeDialogVisible.value = false
    loadRepairs()
  } catch (e) { ElMessage.error('操作失败') }
}

const handleClose = async (row) => {
  try {
    await ElMessageBox.confirm('确认关闭此工单？', '关闭确认')
    await updateRepairStatus(row.id, 3)
    ElMessage.success('工单已关闭')
    loadRepairs()
  } catch (e) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

onMounted(() => loadRepairs())
</script>

<style scoped>
.staff-repair { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
</style>
