<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增商品</el-button>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="productCode" label="商品编号" width="120" />
        <el-table-column prop="productName" label="商品名称" width="180" />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 60px; height: 45px; border-radius: 4px" fit="cover" :preview-src-list="[row.imageUrl]" preview-teleported />
            <span v-else style="color: #999; font-size: 12px">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存" width="80" />
        <el-table-column prop="soldQuantity" label="已售" width="80" />
        <el-table-column prop="isHot" label="热门" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isHot" type="danger" size="small">热门</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isNew" label="新品" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isNew" type="success" size="small">新品</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">上架</el-tag>
            <el-tag v-else type="info">下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="商品编号" prop="productCode">
          <el-input v-model="formData.productCode" placeholder="请输入商品编号" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="formData.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="雪具装备" value="雪具装备" />
            <el-option label="服装配饰" value="服装配饰" />
            <el-option label="护具用品" value="护具用品" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="formData.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input-number v-model="formData.stockQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片" prop="imageUrl">
          <ImageUpload v-model="formData.imageUrl" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-checkbox v-model="formData.isHot">热门</el-checkbox>
          <el-checkbox v-model="formData.isNew">新品</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, createProduct, updateProduct, deleteProduct } from '@/api/product'
import ImageUpload from '@/components/ImageUpload.vue'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 对话框控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const formRef = ref(null)
const formData = ref({
  id: null,
  productCode: '',
  productName: '',
  category: '',
  brand: '',
  price: 0,
  stockQuantity: 0,
  imageUrl: '',
  description: '',
  status: 1,
  isHot: false,
  isNew: false
})

const formRules = {
  productCode: [{ required: true, message: '请输入商品编号', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
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

// 新增商品
const handleAdd = () => {
  dialogTitle.value = '新增商品'
  formData.value = {
    id: null,
    productCode: '',
    productName: '',
    category: '',
    brand: '',
    price: 0,
    stockQuantity: 0,
    imageUrl: '',
    description: '',
    status: 1,
    isHot: false,
    isNew: false
  }
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 保存商品
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      let res
      if (formData.value.id) {
        res = await updateProduct(formData.value.id, formData.value)
      } else {
        res = await createProduct(formData.value)
      }
      
      if (res.code === 200) {
        ElMessage.success(formData.value.id ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      }
    } catch (error) {
      // 拦截器已处理错误提示
    } finally {
      loading.value = false
    }
  })
}

// 删除商品
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除商品"${row.productName}"吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    loading.value = true
    try {
      const res = await deleteProduct(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
