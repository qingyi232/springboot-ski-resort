<template>
  <div class="ai-recommendation-page">
    <!-- 顶部横幅 -->
    <div class="page-banner">
      <div class="banner-overlay">
        <div class="banner-content">
          <div class="icon-wrapper">
            <el-icon :size="48"><MagicStick /></el-icon>
          </div>
          <h1>AI智能推荐系统</h1>
          <p>基于人工智能的个性化课程与装备推荐</p>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="content-wrapper">
      <!-- 配置卡片 -->
      <el-card class="config-card" :body-style="{ padding: '32px' }">
        <div class="card-header-custom">
          <h2>设置推荐偏好</h2>
          <p>告诉我们您的需求，AI将为您匹配最合适的选择</p>
        </div>

        <el-form :model="config" label-width="100px" class="config-form">
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="推荐类型">
                <el-select v-model="config.type" placeholder="选择类型" size="large" style="width: 100%">
                  <el-option label="课程推荐" value="course">
                    <span><el-icon><Reading /></el-icon> 课程推荐</span>
                  </el-option>
                  <el-option label="装备推荐" value="equipment">
                    <span><el-icon><ShoppingBag /></el-icon> 装备推荐</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="滑雪水平">
                <el-select v-model="config.skillLevel" placeholder="选择水平" size="large" style="width: 100%">
                  <el-option label="初级（零基础）" value="beginner" />
                  <el-option label="中级（有基础）" value="intermediate" />
                  <el-option label="高级（熟练）" value="advanced" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="预算范围">
                <el-input v-model.number="config.budget" placeholder="预算金额" size="large" type="number">
                  <template #suffix>元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="偏好说明">
                <el-input
                  v-model="config.preference"
                  placeholder="例如：想学单板，喜欢户外运动"
                  type="textarea"
                  :rows="3"
                  size="large"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" style="text-align: center; margin-top: 16px;">
              <el-button type="primary" size="large" @click="getRecommendations" :loading="loading" class="submit-btn">
                <el-icon><MagicStick /></el-icon>
                <span>获取AI推荐</span>
              </el-button>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- AI推荐理由 -->
      <transition name="fade-up">
        <el-card v-if="recommendation.reason" class="reason-card" :body-style="{ padding: '28px' }">
          <div class="reason-header">
            <el-icon :size="24" color="#409EFF"><Compass /></el-icon>
            <span>AI分析建议</span>
          </div>
          <div class="reason-content">{{ recommendation.reason }}</div>
        </el-card>
      </transition>

      <!-- 推荐结果 - 课程 -->
      <transition name="fade-up">
        <el-card v-if="config.type === 'course' && recommendation.courses.length > 0" :body-style="{ padding: '28px' }" class="result-card">
          <div class="result-header">
            <h3>为您推荐的课程</h3>
            <span class="count-badge">共{{ recommendation.courses.length }}个</span>
          </div>
          <div class="courses-grid">
            <div v-for="course in recommendation.courses" :key="course.id" class="course-item">
              <div class="course-image">
                <img :src="course.imageUrl || 'https://picsum.photos/seed/course/400/300'" :alt="course.courseName" />
                <div class="course-tags">
                  <el-tag v-if="course.isHot" type="danger" effect="dark" size="small">HOT</el-tag>
                </div>
              </div>
              <div class="course-body">
                <h4>{{ course.courseName }}</h4>
                <div class="course-meta">
                  <el-tag size="small" effect="plain">{{ course.courseType }}</el-tag>
                  <el-tag size="small" type="info" effect="plain">{{ course.difficulty }}</el-tag>
                </div>
                <p class="course-desc">{{ course.description }}</p>
                <div class="course-info">
                  <span><el-icon><User /></el-icon> {{ course.coachName }}</span>
                  <span><el-icon><Clock /></el-icon> {{ course.courseDuration }}小时</span>
                </div>
              </div>
              <div class="course-footer">
                <div class="price-section">
                  <span class="price-label">课程价格</span>
                  <span class="price">¥{{ course.coursePrice }}</span>
                </div>
                <el-button type="primary" @click="bookCourse(course)" size="small">立即预约</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </transition>

      <!-- 推荐结果 - 装备 -->
      <transition name="fade-up">
        <el-card v-if="config.type === 'equipment' && recommendation.products.length > 0" :body-style="{ padding: '28px' }" class="result-card">
          <div class="result-header">
            <h3>为您推荐的装备</h3>
            <span class="count-badge">共{{ recommendation.products.length }}件</span>
          </div>
          <div class="products-grid">
            <div v-for="product in recommendation.products" :key="product.id" class="product-item">
              <div class="product-image">
                <img :src="product.imageUrl || 'https://picsum.photos/seed/product/400/400'" :alt="product.productName" />
                <div class="product-tags">
                  <el-tag v-if="product.isNew" type="success" effect="dark" size="small">NEW</el-tag>
                  <el-tag v-if="product.isHot" type="danger" effect="dark" size="small">HOT</el-tag>
                </div>
              </div>
              <div class="product-body">
                <div class="brand">{{ product.brand }}</div>
                <h4>{{ product.productName }}</h4>
                <p class="spec">{{ product.specification }}</p>
              </div>
              <div class="product-footer">
                <div class="price-section">
                  <span class="price-label">售价</span>
                  <span class="price">¥{{ product.price }}</span>
                </div>
                <el-button type="primary" @click="buyProduct(product)" size="small">查看详情</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </transition>

      <!-- 空状态 -->
      <transition name="fade">
        <el-empty v-if="!loading && !recommendation.reason" description="请设置您的需求并点击获取AI推荐" :image-size="180" />
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, Compass, User, Clock, Reading, ShoppingBag } from '@element-plus/icons-vue'
import { recommendCourses, recommendEquipment } from '@/api/ai'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const config = reactive({
  type: 'course',
  skillLevel: '',
  budget: null,
  preference: ''
})

const recommendation = reactive({
  reason: '',
  courses: [],
  products: []
})

const loading = ref(false)

const getRecommendations = async () => {
  if (!config.skillLevel) {
    ElMessage.warning('请选择您的滑雪水平')
    return
  }

  try {
    loading.value = true
    
    const request = {
      userId: userStore.userInfo?.id,
      type: config.type,
      skillLevel: config.skillLevel,
      budget: config.budget,
      preference: config.preference
    }

    let response
    if (config.type === 'course') {
      response = await recommendCourses(request)
      recommendation.courses = response.data.courses || []
      recommendation.products = []
    } else {
      response = await recommendEquipment(request)
      recommendation.products = response.data.products || []
      recommendation.courses = []
    }

    recommendation.reason = response.data.reason || ''
    
    ElMessage.success('AI推荐已生成')
  } catch (error) {
    console.error('获取推荐失败:', error)
    ElMessage.error('获取推荐失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const bookCourse = (course) => {
  router.push({ path: '/front-courses', query: { courseId: course.id } })
}

const buyProduct = (product) => {
  router.push({ path: '/product-mall', query: { productId: product.id } })
}
</script>

<style scoped>
.ai-recommendation-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f5f9 0%, #e8f0f5 50%, #f5f8fa 100%);
}

.page-banner {
  position: relative;
  height: 280px;
  background: url('https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=1920&q=80') center/cover;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.5) 100%);
  backdrop-filter: blur(1px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-content {
  text-align: center;
  color: white;
  z-index: 1;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  backdrop-filter: blur(10px);
  margin-bottom: 20px;
  border: 2px solid rgba(255, 255, 255, 0.4);
}

.banner-content h1 {
  font-size: 36px;
  font-weight: 300;
  margin: 0 0 12px 0;
  letter-spacing: 2px;
}

.banner-content p {
  font-size: 16px;
  opacity: 0.95;
  font-weight: 300;
  letter-spacing: 1px;
}

.content-wrapper {
  max-width: 1400px;
  margin: 40px auto 0;
  padding: 0 24px 48px;
  position: relative;
  z-index: 2;
}

.config-card {
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
  margin-bottom: 24px;
}

.card-header-custom {
  margin-bottom: 32px;
  text-align: center;
}

.card-header-custom h2 {
  font-size: 24px;
  font-weight: 500;
  color: #1976d2;
  margin: 0 0 8px 0;
}

.card-header-custom p {
  color: #64748b;
  font-size: 14px;
  margin: 0;
}

.config-form {
  max-width: 100%;
}

.submit-btn {
  min-width: 200px;
  height: 48px;
  font-size: 16px;
  border-radius: 24px;
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(25, 118, 210, 0.4);
}

.reason-card {
  border-radius: 16px;
  background: linear-gradient(135deg, #fff 0%, #f8fafc 100%);
  border: 2px solid #e3f2fd;
  box-shadow: 0 4px 24px rgba(25, 118, 210, 0.08);
  margin-bottom: 24px;
}

.reason-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 16px;
}

.reason-content {
  font-size: 15px;
  line-height: 1.8;
  color: #475569;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  border-left: 4px solid #1976d2;
}

.result-card {
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e8f0f5;
}

.result-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #0d47a1;
  margin: 0;
}

.count-badge {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1976d2;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.course-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e8f0f5;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.course-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(25, 118, 210, 0.15);
  border-color: #1976d2;
}

.course-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-item:hover .course-image img {
  transform: scale(1.05);
}

.course-tags {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
}

.course-body {
  padding: 20px;
  flex: 1;
}

.course-body h4 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px 0;
}

.course-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.course-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-info {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #94a3b8;
}

.course-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-footer {
  padding: 16px 20px;
  border-top: 1px solid #e8f0f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
}

.price-section {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.price-label {
  font-size: 12px;
  color: #94a3b8;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #1976d2;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.product-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e8f0f5;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.product-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(25, 118, 210, 0.15);
  border-color: #1976d2;
}

.product-image {
  position: relative;
  height: 260px;
  overflow: hidden;
  background: #f8fafc;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-item:hover .product-image img {
  transform: scale(1.05);
}

.product-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
  flex-direction: column;
  align-items: flex-start;
}

.product-body {
  padding: 20px;
  flex: 1;
}

.brand {
  font-size: 12px;
  color: #1976d2;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
  font-weight: 600;
}

.product-body h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.spec {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.product-footer {
  padding: 16px 20px;
  border-top: 1px solid #e8f0f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
}

/* 动画 */
.fade-up-enter-active {
  animation: fadeInUp 0.5s ease;
}

.fade-enter-active {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .page-banner {
    height: 200px;
  }
  
  .banner-content h1 {
    font-size: 24px;
  }
  
  .courses-grid,
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>
