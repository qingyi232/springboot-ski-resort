<template>
  <div class="product-mall-page">
    <!-- 商品列表 -->
    <div class="page-container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="filters.category" @change="handleFilterChange" size="large">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="滑雪装备">滑雪装备</el-radio-button>
          <el-radio-button label="服装">服装</el-radio-button>
          <el-radio-button label="配件">配件</el-radio-button>
        </el-radio-group>

        <div class="filter-right">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索商品名称或品牌"
            prefix-icon="Search"
            clearable
            @input="handleSearch"
            style="width: 250px; margin-right: 12px;"
            size="large"
          />
          <el-select v-model="filters.sortBy" @change="handleSort" style="width: 150px;" size="large">
            <el-option label="综合排序" value="default" />
            <el-option label="价格升序" value="price_asc" />
            <el-option label="价格降序" value="price_desc" />
            <el-option label="销量最高" value="sales" />
          </el-select>
        </div>
      </div>

      <!-- 商品网格 -->
      <div class="product-grid" v-loading="loading">
        <div v-for="product in products" :key="product.id" class="product-card" @click="viewProduct(product)">
          <div class="product-image">
            <img :src="product.imageUrl || 'https://images.unsplash.com/photo-1587825230749-f2213738776c?w=400&q=80'" :alt="product.productName" />
            <div class="product-tags">
              <span v-if="product.isNew" class="tag new">NEW</span>
              <span v-if="product.isHot" class="tag hot">HOT</span>
            </div>
          </div>
          <div class="product-info">
            <div class="brand-name">{{ product.brand || '未知品牌' }}</div>
            <h3 class="product-name">{{ product.productName }}</h3>
            <div class="product-meta">
              <span class="category">{{ product.category }}</span>
            </div>
            <div class="product-footer">
              <div class="price-box">
                <span class="current-price">¥{{ product.price }}</span>
              </div>
              <button class="add-cart-btn" @click.stop="addToCart(product)">
                <el-icon><ShoppingCart /></el-icon>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && products.length === 0" description="暂无商品" />
    </div>

    <!-- 购物车悬浮按钮 -->
    <div class="cart-float" @click="showCart">
      <el-badge :value="cartCount" :max="99" v-if="cartCount > 0">
        <el-icon :size="28"><ShoppingCart /></el-icon>
      </el-badge>
      <el-icon :size="28" v-else><ShoppingCart /></el-icon>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="productDetailVisible"
      :title="currentProduct?.productName"
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="product-detail" v-if="currentProduct">
        <div class="detail-left">
          <div class="detail-image">
            <img :src="currentProduct.imageUrl" :alt="currentProduct.productName" />
            <div class="detail-tags">
              <span v-if="currentProduct.isNew" class="tag new">NEW</span>
              <span v-if="currentProduct.isHot" class="tag hot">HOT</span>
            </div>
          </div>
        </div>
        <div class="detail-right">
          <div class="detail-brand">{{ currentProduct.brand || '未知品牌' }}</div>
          <h2 class="detail-title">{{ currentProduct.productName }}</h2>
          <div class="detail-category">
            <el-tag type="info">{{ currentProduct.category }}</el-tag>
          </div>
          
          <div class="detail-price-section">
            <div class="price-label">销售价格</div>
            <div class="detail-price">¥{{ currentProduct.price }}</div>
            <div class="price-info" v-if="currentProduct.costPrice">
              <span class="original-price">成本价：¥{{ currentProduct.costPrice }}</span>
            </div>
          </div>

          <div class="detail-info-section">
            <div class="info-row" v-if="currentProduct.model">
              <span class="info-label">型号：</span>
              <span class="info-value">{{ currentProduct.model }}</span>
            </div>
            <div class="info-row" v-if="currentProduct.specification">
              <span class="info-label">规格：</span>
              <span class="info-value">{{ currentProduct.specification }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">库存：</span>
              <span class="info-value" :class="currentProduct.stockQuantity < 10 ? 'stock-low' : ''">
                {{ currentProduct.stockQuantity }} 件
              </span>
            </div>
            <div class="info-row">
              <span class="info-label">已售：</span>
              <span class="info-value">{{ currentProduct.soldQuantity || 0 }} 件</span>
            </div>
          </div>

          <div class="detail-description" v-if="currentProduct.description">
            <div class="desc-title">商品描述</div>
            <div class="desc-content">{{ currentProduct.description }}</div>
          </div>

          <div class="detail-quantity">
            <span class="quantity-label">购买数量：</span>
            <el-input-number
              v-model="buyQuantity"
              :min="1"
              :max="currentProduct.stockQuantity"
              size="large"
            />
          </div>

          <div class="detail-actions">
            <el-button 
              type="primary" 
              size="large" 
              @click="buyNowFromDetail"
              :disabled="currentProduct.stockQuantity < 1"
            >
              <el-icon><ShoppingCart /></el-icon>
              立即购买
            </el-button>
            <el-button 
              size="large" 
              @click="addToCartFromDetail"
              :disabled="currentProduct.stockQuantity < 1"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 购物车抽屉 -->
    <el-drawer v-model="cartDrawerVisible" title="购物车" size="450px">
      <div class="cart-content" v-if="cartItems.length > 0">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <img :src="item.imageUrl" :alt="item.name" class="cart-item-image" />
          <div class="cart-item-info">
            <div class="cart-item-name">{{ item.name }}</div>
            <div class="cart-item-price">¥{{ item.price }}</div>
          </div>
          <div class="cart-item-actions">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="item.stock"
              @change="updateCartItem(item)"
              size="small"
            />
            <el-button
              type="danger"
              text
              @click="removeFromCart(item)"
              style="margin-left: 8px;"
            >
              删除
            </el-button>
          </div>
        </div>
        <div class="cart-summary">
          <div class="summary-item">
            <span>商品总数：</span>
            <span>{{ totalQuantity }} 件</span>
          </div>
          <div class="summary-item total">
            <span>合计：</span>
            <span class="total-price">¥{{ totalPrice }}</span>
          </div>
        </div>
        <div class="cart-actions">
          <el-button @click="clearCart">清空购物车</el-button>
          <el-button type="primary" @click="checkout">去结算</el-button>
        </div>
      </div>
      <el-empty v-else description="购物车是空的" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getProducts, getProductById } from '@/api/product'
import { createSalesOrder } from '@/api/sales-order'
import { useUserStore } from '@/stores/user'
import { debounce } from '@/utils/performance'

// 获取用户信息
const userStore = useUserStore()

// 筛选条件
const filters = ref({
  category: '',
  keyword: '',
  sortBy: 'default'
})

// 商品列表
const products = ref([])
const loading = ref(false)
const pagination = ref({
  page: 1,
  pageSize: 50  // 增加到50，可以显示所有商品
})
const total = ref(0)

// 购物车
const cartDrawerVisible = ref(false)
const cartItems = ref([])

// 商品详情
const productDetailVisible = ref(false)
const currentProduct = ref(null)
const buyQuantity = ref(1)

// 购物车数量
const cartCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 购物车商品总数
const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 购物车总价
const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

// 加载商品列表
const loadProducts = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.value.page,
      pageSize: pagination.value.pageSize
    }
    
    // 只有当有值时才添加这些参数
    if (filters.value.category) {
      params.category = filters.value.category
    }
    if (filters.value.keyword) {
      params.productName = filters.value.keyword
    }
    
    const needCondition = filters.value.category || filters.value.keyword
    const url = needCondition ? '/api/v1/products/page/condition' : '/api/v1/products/page'
    const response = await request({ url, method: 'get', params })
    if (response.code === 200) {
      products.value = response.data.list || response.data.records || []
      total.value = response.data.total || 0
      
      // 客户端排序
      if (filters.value.sortBy && filters.value.sortBy !== 'default') {
        sortProducts()
      }
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

// 客户端排序
const sortProducts = () => {
  if (filters.value.sortBy === 'price_asc') {
    products.value.sort((a, b) => a.price - b.price)
  } else if (filters.value.sortBy === 'price_desc') {
    products.value.sort((a, b) => b.price - a.price)
  } else if (filters.value.sortBy === 'sales') {
    products.value.sort((a, b) => (b.soldCount || 0) - (a.soldCount || 0))
  }
}

// 筛选变化
const handleFilterChange = () => {
  pagination.value.page = 1
  loadProducts()
}

// 搜索（防抖）
const handleSearch = debounce(() => {
  pagination.value.page = 1
  loadProducts()
}, 300)

// 排序变化
const handleSort = () => {
  sortProducts()
}

// 分页大小变化
const handleSizeChange = () => {
  pagination.value.page = 1
  loadProducts()
}

// 页码变化
const handlePageChange = () => {
  loadProducts()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 查看商品详情
const viewProduct = (product) => {
  currentProduct.value = product
  buyQuantity.value = 1
  productDetailVisible.value = true
}

// 从详情页加入购物车
const addToCartFromDetail = () => {
  if (!currentProduct.value) return
  
  const existingItem = cartItems.value.find(item => item.id === currentProduct.value.id)
  
  if (existingItem) {
    const newQuantity = existingItem.quantity + buyQuantity.value
    if (newQuantity <= (currentProduct.value.stockQuantity || 99)) {
      existingItem.quantity = newQuantity
      ElMessage.success(`已增加商品数量：${buyQuantity.value}件`)
    } else {
      ElMessage.warning('库存不足')
      return
    }
  } else {
    cartItems.value.push({
      id: currentProduct.value.id,
      name: currentProduct.value.productName || currentProduct.value.name,
      price: currentProduct.value.price,
      imageUrl: currentProduct.value.imageUrl,
      stock: currentProduct.value.stockQuantity || currentProduct.value.stock || 99,
      quantity: buyQuantity.value
    })
    ElMessage.success(`已加入购物车：${buyQuantity.value}件`)
  }
  
  // 保存到本地存储
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
  
  // 关闭详情对话框
  productDetailVisible.value = false
}

// 从详情页立即购买
const buyNowFromDetail = () => {
  if (!currentProduct.value) return
  
  addToCartFromDetail()
  setTimeout(() => {
    checkout()
  }, 300)
}

// 加入购物车
const addToCart = (product) => {
  const existingItem = cartItems.value.find(item => item.id === product.id)
  
  if (existingItem) {
    if (existingItem.quantity < (product.stockQuantity || product.stock || 99)) {
      existingItem.quantity++
      ElMessage.success('已增加商品数量')
    } else {
      ElMessage.warning('库存不足')
    }
  } else {
    cartItems.value.push({
      id: product.id,
      name: product.productName || product.name,
      price: product.price,
      imageUrl: product.imageUrl,
      stock: product.stockQuantity || product.stock || 99,
      quantity: 1
    })
    ElMessage.success('已加入购物车')
  }
  
  // 保存到本地存储
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

// 立即购买
const buyNow = (product) => {
  addToCart(product)
  setTimeout(() => {
    checkout()
  }, 300)
}

// 显示购物车
const showCart = () => {
  cartDrawerVisible.value = true
}

// 更新购物车商品
const updateCartItem = (item) => {
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

// 从购物车移除
const removeFromCart = (item) => {
  const index = cartItems.value.findIndex(i => i.id === item.id)
  if (index > -1) {
    cartItems.value.splice(index, 1)
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
    ElMessage.success('已移除')
  }
}

// 清空购物车
const clearCart = () => {
  cartItems.value = []
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
  ElMessage.success('购物车已清空')
}

// 去结算
const checkout = async () => {
  console.log('=== 开始结算流程 ===')
  console.log('购物车商品数量：', cartItems.value.length)
  console.log('购物车商品：', cartItems.value)
  
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }
  
  try {
    // 计算总金额和总数量
    console.log('totalPrice.value：', totalPrice.value)
    console.log('totalPrice.value 类型：', typeof totalPrice.value)
    
    const totalAmount = parseFloat(totalPrice.value)
    console.log('totalAmount：', totalAmount)
    console.log('totalAmount 是否为 NaN：', isNaN(totalAmount))
    
    const totalItems = cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
    console.log('totalItems：', totalItems)
    
    // 生成订单详情
    const itemsHtml = cartItems.value.map(item => 
      `<tr><td>${item.name}</td><td>×${item.quantity}</td><td>¥${(item.price * item.quantity).toFixed(2)}</td></tr>`
    ).join('')
    
    console.log('准备弹出订单确认对话框...')
    const { value: paymentMethod } = await ElMessageBox.prompt(
      `<div style="text-align: left;">
        <p><strong>订单详情：</strong></p>
        <table style="width: 100%; border-collapse: collapse; margin: 10px 0;">
          <thead><tr><th>商品</th><th>数量</th><th>小计</th></tr></thead>
          <tbody>${itemsHtml}</tbody>
        </table>
        <p style="font-size: 16px; font-weight: bold; margin-top: 10px;">总计：¥${totalAmount.toFixed(2)} (${totalItems}件)</p>
        <p style="margin-top: 15px;">请输入支付方式：</p>
      </div>`,
      '确认结算',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        inputPlaceholder: '输入支付方式（如：微信支付、支付宝）',
        inputPattern: /.+/,
        inputErrorMessage: '请输入支付方式'
      }
    )
    
    console.log('用户输入的支付方式：', paymentMethod)
    
    // 创建订单数据
    const orderData = {
      userId: userStore.userInfo.id,  // 修复：字段名是 id，不是 userId
      userName: userStore.userInfo.username,
      totalAmount: totalAmount,
      discountAmount: 0,
      actualAmount: totalAmount,
      status: 1, // 1=待发货（已支付）
      orderStatus: 1,
      paymentStatus: 1, // 1=已支付
      paymentMethod: paymentMethod,
      paymentTime: new Date().toISOString().slice(0, 19).replace('T', ' '), // 当前时间 YYYY-MM-DD HH:mm:ss
      shippingStatus: 0, // 0=未发货
      remark: `购买商品：${cartItems.value.map(item => `${item.name}×${item.quantity}`).join(', ')}`
    }
    
    console.log('准备创建订单：', orderData)
    
    // 调用后端API创建订单
    const response = await createSalesOrder(orderData)
    console.log('订单创建成功，订单ID：', response.data)
    
    // 支付成功
    ElMessage.success(`支付成功！订单金额：¥${totalAmount.toFixed(2)}`)
    
    // 清空购物车
    clearCart()
    cartDrawerVisible.value = false
    
    console.log('=== 结算完成 ===')
    
  } catch (error) {
    console.error('=== 结算失败 ===')
    console.error('错误类型：', typeof error)
    console.error('错误内容：', error)
    console.error('错误堆栈：', error.stack)
    
    if (error !== 'cancel') {
      ElMessage.error(`结算失败：${error.message || error}`)
    } else {
      console.log('用户取消了结算')
    }
  }
}

// 组件挂载
onMounted(() => {
  loadProducts()
  
  // 从本地存储加载购物车
  const savedCart = localStorage.getItem('cart')
  if (savedCart) {
    try {
      cartItems.value = JSON.parse(savedCart)
    } catch (error) {
      console.error('加载购物车失败:', error)
    }
  }
})
</script>

<style scoped>
.product-mall-page {
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
}

.filter-right {
  display: flex;
  align-items: center;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.product-card {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-8px);
  border-color: #1890ff;
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.12);
}

.product-image {
  position: relative;
  height: 260px;
  background: #f5f5f5;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-tags {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 11px;
  font-weight: 600;
  color: white;
}

.tag.hot {
  background: #ff4d4f;
}

.tag.new {
  background: #1890ff;
}

.product-info {
  padding: 20px;
}

.brand-name {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  margin-bottom: 16px;
}

.category {
  font-size: 13px;
  color: #8c8c8c;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-box {
  display: flex;
  flex-direction: column;
}

.current-price {
  font-size: 24px;
  font-weight: 700;
  color: #ff4d4f;
}

.add-cart-btn {
  width: 40px;
  height: 40px;
  padding: 0;
  background: #1890ff;
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.add-cart-btn:hover {
  background: #40a9ff;
  transform: scale(1.1);
}

/* 购物车悬浮按钮 */
.cart-float {
  position: fixed;
  right: 24px;
  bottom: 120px; /* 调整到聊天按钮上方，避免重叠 */
  width: 64px;
  height: 64px;
  background: #1890ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.4);
  transition: all 0.3s ease;
  z-index: 999;
}

.cart-float:hover {
  transform: scale(1.1);
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.6);
}

/* 购物车内容 */
.cart-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
}

.cart-item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.cart-item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cart-item-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.cart-item-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.cart-item-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}

.cart-summary {
  margin-top: auto;
  padding: 20px;
  border-top: 2px solid #ebeef5;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-item.total {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
}

.cart-actions {
  display: flex;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.cart-actions .el-button {
  flex: 1;
}

/* 商品详情样式 */
.product-detail {
  display: flex;
  gap: 40px;
}

.detail-left {
  flex: 1;
  max-width: 400px;
}

.detail-image {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-tags {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
}

.detail-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-brand {
  font-size: 14px;
  color: #8c8c8c;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.detail-title {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.detail-category {
  margin-bottom: 8px;
}

.detail-price-section {
  padding: 20px;
  background: #f7f8fa;
  border-radius: 12px;
}

.price-label {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.detail-price {
  font-size: 36px;
  font-weight: 700;
  color: #ff4d4f;
  margin-bottom: 8px;
}

.price-info {
  display: flex;
  gap: 16px;
}

.original-price {
  font-size: 14px;
  color: #8c8c8c;
  text-decoration: line-through;
}

.detail-info-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.info-row {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-label {
  width: 80px;
  color: #8c8c8c;
  font-weight: 500;
}

.info-value {
  color: #262626;
  font-weight: 600;
}

.stock-low {
  color: #ff4d4f !important;
}

.detail-description {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.desc-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.desc-content {
  font-size: 14px;
  color: #595959;
  line-height: 1.6;
}

.detail-quantity {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.quantity-label {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.detail-actions .el-button {
  flex: 1;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

/* 响应式 */
@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
  
  .product-detail {
    flex-direction: column;
  }
  
  .detail-left {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .gradient-text {
    font-size: 32px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-left,
  .filter-right {
    width: 100%;
  }
  
  .filter-right {
    flex-direction: column;
  }
  
  .filter-right .el-input,
  .filter-right .el-select {
    width: 100% !important;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .cart-float {
    right: 20px;
    bottom: 20px;
    width: 56px;
    height: 56px;
  }
}
</style>

