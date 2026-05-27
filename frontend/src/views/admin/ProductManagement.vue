<template>
  <div class="product-management">
    <div class="page-header">
      <h1>商品管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加商品
      </el-button>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="商品名称/品牌" clearable @input="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable @change="handleSearch" style="width: 150px;">
            <el-option label="单板" value="单板" />
            <el-option label="双板" value="双板" />
            <el-option label="雪服" value="雪服" />
            <el-option label="护具" value="护具" />
            <el-option label="配件" value="配件" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable @change="handleSearch" style="width="120px;">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="products" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <div>售价：¥{{ row.price }}</div>
            <div style="font-size: 12px; color: #909399;" v-if="row.originalPrice">原价：¥{{ row.originalPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80">
          <template #default="{ row }">
            <el-tag :type="row.stock < 10 ? 'danger' : 'success'">{{ row.stock }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.isHot" type="danger" size="small" style="margin-right: 4px;">热销</el-tag>
            <el-tag v-if="row.isNew" type="primary" size="small">新品</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="showEditDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
            <el-button type="danger" text @click="deleteProduct(row)"><el-icon><Delete /></el-icon> 删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadProducts"
          @current-change="loadProducts"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="resetForm">
      <el-form :model="productForm" :rules="productRules" ref="productFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="productForm.name" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="productForm.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="productForm.category" placeholder="请选择分类" style="width: 100%;">
                <el-option label="单板" value="单板" />
                <el-option label="双板" value="双板" />
                <el-option label="雪服" value="雪服" />
                <el-option label="护具" value="护具" />
                <el-option label="配件" value="配件" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格">
              <el-input v-model="productForm.specification" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="productForm.price" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价">
              <el-input-number v-model="productForm.originalPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="productForm.stock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品标签">
              <el-checkbox v-model="productForm.isHot">热销</el-checkbox>
              <el-checkbox v-model="productForm.isNew" style="margin-left: 20px;">新品</el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="productForm.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品图片">
          <el-input v-model="productForm.imageUrl" placeholder="请输入图片URL" />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            提示：可以输入图片URL地址
          </div>
        </el-form-item>

        <el-form-item label="商品标签">
          <el-input v-model="productForm.tags" placeholder="多个标签用逗号分隔，如：防水,透气,保暖" />
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="4" placeholder="请输入商品详细描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getProducts, createProduct, updateProduct, deleteProduct as deleteProductApi, updateProductStatus } from '@/api/product'
import { debounce, formatDateTime } from '@/utils/performance'

const searchForm = reactive({ keyword: '', category: '', status: null })
const products = ref([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10 })
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑商品' : '添加商品')
const submitting = ref(false)
const productFormRef = ref(null)

const productForm = reactive({
  id: null, name: '', brand: '', category: '', specification: '',
  price: 0, originalPrice: 0, stock: 0, imageUrl: '',
  tags: '', description: '', isHot: false, isNew: false, status: 1
})

const productRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const loadProducts = async () => {
  try {
    loading.value = true
    const response = await getProducts({ ...pagination.value, ...searchForm })
    if (response.code === 200) {
      products.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadProducts()
}, 300)

const resetSearch = () => {
  Object.assign(searchForm, { keyword: '', category: '', status: null })
  pagination.value.page = 1
  loadProducts()
}

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(productForm, {
    ...row,
    tags: Array.isArray(row.tags) ? row.tags.join(',') : row.tags || ''
  })
  dialogVisible.value = true
}

const resetForm = () => {
  productFormRef.value?.resetFields()
  Object.assign(productForm, {
    id: null, name: '', brand: '', category: '', specification: '',
    price: 0, originalPrice: 0, stock: 0, imageUrl: '',
    tags: '', description: '', isHot: false, isNew: false, status: 1
  })
}

const submitForm = async () => {
  try {
    await productFormRef.value.validate()
    submitting.value = true
    
    const data = {
      ...productForm,
      tags: productForm.tags ? productForm.tags.split(',').map(t => t.trim()).filter(t => t) : []
    }
    
    const apiFunc = isEdit.value ? updateProduct : createProduct
    const params = isEdit.value ? [productForm.id, data] : [data]
    const response = await apiFunc(...params)
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadProducts()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleStatusChange = async (row) => {
  try {
    const response = await updateProductStatus(row.id, row.status)
    if (response.code === 200) {
      ElMessage.success(row.status === 1 ? '商品已上架' : '商品已下架')
    }
  } catch (error) {
    ElMessage.error('更新状态失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

const deleteProduct = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除商品"${row.name}"吗？`, '删除商品', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const response = await deleteProductApi(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadProducts()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => loadProducts())
</script>

<style scoped>
.product-management { padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: bold; color: #303133; margin: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: -18px; }
.table-card { min-height: calc(100vh - 280px); }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>







