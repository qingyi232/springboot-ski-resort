<template>
  <div class="home">
    <!-- Hero 大图轮播区 -->
    <section class="hero-carousel">
      <el-carousel :interval="5000" height="600px" arrow="always">
        <el-carousel-item v-for="(slide, index) in heroSlides" :key="index">
          <div class="slide-container">
            <img :src="slide.image" :alt="slide.title" class="slide-image" />
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <h1 class="slide-title">{{ slide.title }}</h1>
              <p class="slide-subtitle">{{ slide.subtitle }}</p>
              <div class="slide-actions">
                <el-button type="primary" size="large" @click="router.push(slide.link)">
                  {{ slide.buttonText }}
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 热门课程 - 现代化设计 -->
    <section class="section courses-section">
      <div class="section-header-modern">
        <div class="section-label">COURSES</div>
        <h2 class="section-title-modern">精选课程</h2>
        <p class="section-desc">专业教练团队，个性化教学方案</p>
      </div>
      <div class="courses-grid" v-loading="loading.courses">
        <div 
          class="course-card-modern" 
          v-for="course in hotCourses" 
          :key="course.id"
        >
          <div class="course-image-wrapper" @click="viewCourseDetail(course)">
            <img 
              v-if="course.imageUrl" 
              :src="course.imageUrl" 
              :alt="course.courseName"
              class="course-image"
            />
            <div v-else class="course-image-bg"></div>
            <div class="course-badge" v-if="course.isHot">
              <span class="fire-icon">🔥</span>
              <span>HOT</span>
            </div>
            <div class="course-difficulty">{{ course.difficulty || '中级' }}</div>
          </div>
          <div class="course-content">
            <h3 class="course-title" @click="viewCourseDetail(course)">{{ course.courseName }}</h3>
            <div class="course-meta">
              <div class="meta-item">
                <span class="meta-icon">👨‍🏫</span>
                <span>{{ course.coachName }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">⏱</span>
                <span>{{ course.courseDuration }}h</span>
              </div>
            </div>
            <div class="course-footer-modern">
              <div class="price-wrapper">
                <span class="price-label">课程价格</span>
                <span class="price-value">¥{{ course.coursePrice }}</span>
              </div>
              <button class="book-btn" @click.stop="bookCourse(course)">
                <span>立即预约</span>
                <span class="arrow-icon">→</span>
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="section-footer">
        <button class="view-more-btn" @click="router.push('/front-courses')">
          <span>查看全部课程</span>
          <span class="btn-arrow">→</span>
        </button>
      </div>
    </section>

    <!-- 精选商品 - 高端设计 -->
    <section class="section products-section">
      <div class="section-header-modern">
        <div class="section-label">PRODUCTS</div>
        <h2 class="section-title-modern">装备商城</h2>
        <p class="section-desc">国际品牌装备，专业品质保障</p>
      </div>
      <div class="products-grid" v-loading="loading.products">
        <div 
          class="product-card-modern" 
          v-for="product in hotProducts" 
          :key="product.id"
          @click="viewProductDetail(product)"
        >
          <div class="product-image-container">
            <img 
              v-if="product.imageUrl" 
              :src="product.imageUrl" 
              :alt="product.productName"
              class="product-image"
            />
            <div v-else class="product-bg"></div>
            <div class="product-tags">
              <span class="tag new" v-if="product.isNew">NEW</span>
              <span class="tag hot" v-if="product.isHot">HOT</span>
            </div>
            <div class="quick-view">
              <span>Quick View</span>
            </div>
          </div>
          <div class="product-info-modern">
            <div class="brand-name">{{ product.brand }}</div>
            <h3 class="product-name">{{ product.productName }}</h3>
            <div class="product-meta">
              <span class="category">{{ product.category }}</span>
              <span class="stock-status" :class="product.stockQuantity > 10 ? 'in-stock' : 'low-stock'">
                {{ product.stockQuantity > 10 ? '现货充足' : '库存紧张' }}
              </span>
            </div>
            <div class="product-price-action">
              <div class="price-box">
                <span class="current-price">¥{{ product.price }}</span>
              </div>
              <button class="add-cart-btn">
                <span>加入购物车</span>
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="section-footer">
        <button class="view-more-btn" @click="router.push('/product-mall')">
          <span>探索更多商品</span>
          <span class="btn-arrow">→</span>
        </button>
      </div>
    </section>

    <!-- 雪具租赁 - 极简风格 -->
    <section class="section rental-section">
      <div class="section-header-modern">
        <div class="section-label">EQUIPMENT</div>
        <h2 class="section-title-modern">雪具租赁</h2>
        <p class="section-desc">顶级品牌装备，灵活租赁方案</p>
      </div>
      <div class="rental-grid" v-loading="loading.equipment">
        <div 
          class="rental-card" 
          v-for="equipment in equipmentList" 
          :key="equipment.id"
          :class="{ unavailable: equipment.status !== 1 }"
          @click="router.push('/equipment-rental')"
        >
          <div class="rental-image-area">
            <img 
              v-if="equipment.imageUrl" 
              :src="equipment.imageUrl" 
              :alt="equipment.equipmentName"
              class="rental-image"
            />
            <div v-else class="rental-bg"></div>
            <div class="status-indicator" :class="equipment.status === 1 ? 'available' : 'rented'">
              <span class="status-dot"></span>
              <span>{{ equipment.status === 1 ? '可租赁' : '租赁中' }}</span>
            </div>
          </div>
          <div class="rental-details">
            <h3 class="rental-name">{{ equipment.equipmentName }}</h3>
            <div class="rental-specs">
              <span class="spec-item">{{ equipment.brand }}</span>
              <span class="spec-divider">|</span>
              <span class="spec-item">{{ equipment.model }}</span>
            </div>
            <div class="rental-pricing">
              <div class="price-info">
                <span class="price-amount">¥{{ equipment.rentalPrice }}</span>
                <span class="price-unit">/小时</span>
              </div>
              <button 
                class="rental-btn" 
                :disabled="equipment.status !== 1"
              >
                {{ equipment.status === 1 ? '立即租赁' : '已租出' }}
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="section-footer">
        <button class="view-more-btn" @click="router.push('/equipment-rental')">
          <span>查看全部雪具</span>
          <span class="btn-arrow">→</span>
        </button>
      </div>
    </section>

    <!-- 场地展示 - 卡片式布局 -->
    <section class="section venues-section">
      <div class="section-header-modern">
        <div class="section-label">VENUES</div>
        <h2 class="section-title-modern">场地概览</h2>
        <p class="section-desc">多样化场地选择，满足不同需求</p>
      </div>
      <div class="venues-container" v-loading="loading.venues">
        <div 
          class="venue-card-modern" 
          v-for="venue in venues" 
          :key="venue.id"
        >
          <div class="venue-visual">
            <img 
              v-if="venue.imageUrl" 
              :src="venue.imageUrl" 
              :alt="venue.venueName"
              class="venue-image"
            />
            <div v-else class="venue-bg-pattern"></div>
            <div class="difficulty-badge" :class="getDifficultyClass(venue.difficultyLevel)">
              <span class="difficulty-icon">{{ getDifficultyIcon(venue.difficultyLevel) }}</span>
              <span class="difficulty-text">{{ venue.difficultyLevel }}</span>
            </div>
          </div>
          <div class="venue-details">
            <h3 class="venue-name">{{ venue.venueName }}</h3>
            <div class="venue-type">{{ venue.venueType }}</div>
            <div class="venue-stats">
              <div class="stat">
                <span class="stat-label">长度</span>
                <span class="stat-value">{{ venue.length }}m</span>
              </div>
              <div class="stat">
                <span class="stat-label">宽度</span>
                <span class="stat-value">{{ venue.width }}m</span>
              </div>
              <div class="stat">
                <span class="stat-label">容量</span>
                <span class="stat-value">{{ venue.currentCapacity }}/{{ venue.maxCapacity }}</span>
              </div>
            </div>
            <div class="venue-action">
              <div class="venue-price">
                <span v-if="venue.rentalPrice > 0">¥{{ venue.rentalPrice }}</span>
                <span v-else style="color: #52c41a; font-weight: 600;">免费</span>
                <span class="price-unit" v-if="venue.rentalPrice > 0">/小时</span>
              </div>
              <button class="reserve-btn" @click="router.push('/venues')">预约场地</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 课程预约对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="课程预约" width="600px">
      <el-form :model="bookingForm" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="selectedCourse.courseName" disabled />
        </el-form-item>
        <el-form-item label="教练">
          <el-input v-model="selectedCourse.coachName" disabled />
        </el-form-item>
        <el-form-item label="课程价格">
          <el-input v-model="selectedCourse.coursePrice" disabled>
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="bookingForm.bookingDate"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="bookingForm.contactPhone" placeholder="请输入您的联系电话" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="bookingForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBooking" :loading="booking">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getHotCourses } from '@/api/course'
import { getProductList } from '@/api/product'
import { getEquipmentList } from '@/api/equipment'
import { getVenueList } from '@/api/venue'
import { ElMessage } from 'element-plus'
import { createBooking } from '@/api/booking'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 预约对话框相关
const bookingDialogVisible = ref(false)
const booking = ref(false)
const selectedCourse = ref({})
const bookingForm = reactive({
  bookingDate: '',
  contactPhone: '',
  remark: ''
})

// 真实滑雪图片轮播数据（使用Unsplash高质量免费图片）
const heroSlides = ref([
  {
    image: 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=1920&q=80&auto=format&fit=crop',
    title: '飞跃滑雪场 - 畅享冰雪激情',
    subtitle: '专业雪道 · 顶级教练 · 完善设施',
    buttonText: '立即预约',
    link: '/front-courses'
  },
  {
    image: 'https://images.unsplash.com/photo-1605540436563-5bca919ae766?w=1920&q=80&auto=format&fit=crop',
    title: '专业滑雪课程',
    subtitle: '从零基础到高级进阶 · 一对一私教指导',
    buttonText: '查看课程',
    link: '/front-courses'
  },
  {
    image: 'https://images.unsplash.com/photo-1519904981063-b0cf448d479e?w=1920&q=80&auto=format&fit=crop',
    title: '顶级装备租赁',
    subtitle: '国际品牌 · 专业维护 · 优惠价格',
    buttonText: '浏览装备',
    link: '/product-mall'
  },
  {
    image: 'https://images.unsplash.com/photo-1483794344563-d27a8d18014e?w=1920&q=80&auto=format&fit=crop',
    title: '多样化雪道选择',
    subtitle: '初级 · 中级 · 高级雪道 满足不同需求',
    buttonText: '场地预约',
    link: '/venues'
  }
])

const hotCourses = ref([])
const hotProducts = ref([])
const equipmentList = ref([])
const venues = ref([])

const loading = ref({
  courses: false,
  products: false,
  equipment: false,
  venues: false
})

onMounted(() => {
  loadHotCourses()
  loadHotProducts()
  loadEquipment()
  loadVenues()
})

const loadHotCourses = async () => {
  loading.value.courses = true
  try {
    const res = await getHotCourses(4)
    if (res.code === 200) {
      hotCourses.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载课程失败')
  } finally {
    loading.value.courses = false
  }
}

const loadHotProducts = async () => {
  loading.value.products = true
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 4, isHot: 1 })
    if (res.code === 200) {
      hotProducts.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value.products = false
  }
}

const loadEquipment = async () => {
  loading.value.equipment = true
  try {
    const res = await getEquipmentList({ pageNum: 1, pageSize: 4 })
    if (res.code === 200) {
      equipmentList.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载雪具失败')
  } finally {
    loading.value.equipment = false
  }
}

const loadVenues = async () => {
  loading.value.venues = true
  try {
    const res = await getVenueList({ pageNum: 1, pageSize: 3 })
    if (res.code === 200) {
      venues.value = res.data.list || []
    }
  } catch (error) {
    ElMessage.error('加载场地失败')
  } finally {
    loading.value.venues = false
  }
}

const viewCourseDetail = (course) => {
  ElMessage({
    message: `课程名称：${course.courseName}\n教练：${course.coachName}\n价格：¥${course.coursePrice}\n时长：${course.courseDuration}小时\n难度：${course.difficulty}`,
    type: 'info',
    duration: 5000,
    showClose: true
  })
}

// 预约课程
const bookCourse = (course) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  selectedCourse.value = course
  bookingForm.bookingDate = ''
  bookingForm.contactPhone = userStore.userInfo?.phone || ''
  bookingForm.remark = ''
  bookingDialogVisible.value = true
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 确认预约
const confirmBooking = async () => {
  if (!bookingForm.bookingDate) {
    ElMessage.warning('请选择预约时间')
    return
  }
  
  if (!bookingForm.contactPhone) {
    ElMessage.warning('请输入联系方式')
    return
  }
  
  try {
    booking.value = true
    
    // 处理日期时间格式
    const bookingDateTime = new Date(bookingForm.bookingDate)
    const bookingDateOnly = bookingDateTime.toISOString().split('T')[0] // yyyy-MM-dd
    const bookingTimeOnly = bookingDateTime.toTimeString().split(' ')[0] // HH:mm:ss
    
    // 计算结束时间（假设课程时长）
    const courseDuration = selectedCourse.value.courseDuration || 2
    const endDateTime = new Date(bookingDateTime.getTime() + courseDuration * 60 * 60 * 1000)
    const endTimeOnly = endDateTime.toTimeString().split(' ')[0]
    
    const bookingData = {
      userId: userStore.userInfo.id,
      userName: userStore.userInfo.realName || userStore.userInfo.username,
      coachId: selectedCourse.value.coachId,
      coachName: selectedCourse.value.coachName,
      bookingDate: bookingDateOnly,
      startTime: bookingTimeOnly,
      endTime: endTimeOnly,
      hours: courseDuration,
      totalAmount: selectedCourse.value.coursePrice,
      status: 0, // 待确认
      paymentStatus: 0, // 未支付
      remark: `课程：${selectedCourse.value.courseName}，联系电话：${bookingForm.contactPhone}${bookingForm.remark ? '，备注：' + bookingForm.remark : ''}`
    }
    
    const response = await createBooking(bookingData)
    
    if (response.code === 200) {
      ElMessage.success('预约成功！请等待确认')
      bookingDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '预约失败')
    }
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error(error.message || '预约失败')
  } finally {
    booking.value = false
  }
}

const viewProductDetail = (product) => {
  router.push('/product-mall')
}

const getDifficultyType = (level) => {
  const typeMap = {
    '初级': 'success',
    '中级': 'warning',
    '高级': 'danger'
  }
  return typeMap[level] || 'info'
}

const getDifficultyClass = (level) => {
  const classMap = {
    '初级': 'beginner',
    '中级': 'intermediate',
    '高级': 'advanced'
  }
  return classMap[level] || 'beginner'
}

const getDifficultyIcon = (level) => {
  const iconMap = {
    '初级': '⛷',
    '中级': '🏂',
    '高级': '🎿'
  }
  return iconMap[level] || '⛷'
}
</script>

<style scoped>
.home {
  background: #ffffff;
  min-height: 100vh;
}

/* ========== Hero Carousel ========== */
.hero-carousel {
  margin: 0;
  width: 100%;
}

.hero-carousel :deep(.el-carousel__container) {
  height: 600px;
}

.slide-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.slide-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to right,
    rgba(0, 0, 0, 0.5) 0%,
    rgba(0, 0, 0, 0.3) 50%,
    transparent 100%
  );
}

.slide-content {
  position: absolute;
  top: 50%;
  left: 10%;
  transform: translateY(-50%);
  max-width: 600px;
  z-index: 2;
  animation: slideContentFadeIn 1s ease-out;
}

@keyframes slideContentFadeIn {
  from {
    opacity: 0;
    transform: translateY(-40%);
  }
  to {
    opacity: 1;
    transform: translateY(-50%);
  }
}

.slide-title {
  font-size: 52px;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 20px;
  line-height: 1.2;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3);
}

.slide-subtitle {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.95);
  margin-bottom: 40px;
  line-height: 1.6;
  text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
}

.slide-actions {
  display: flex;
  gap: 20px;
}

.slide-actions .el-button {
  padding: 16px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
}

.slide-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(24, 144, 255, 0.4);
}

/* Carousel控制按钮样式优化 */
.hero-carousel :deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  width: 50px;
  height: 50px;
  font-size: 20px;
}

.hero-carousel :deep(.el-carousel__arrow:hover) {
  background: rgba(255, 255, 255, 0.5);
}

.hero-carousel :deep(.el-carousel__indicator) {
  padding: 8px;
}

.hero-carousel :deep(.el-carousel__indicator .el-carousel__button) {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
}

.hero-carousel :deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background: #ffffff;
  width: 32px;
  border-radius: 6px;
}

/* ========== Section Styles ========== */
.section {
  padding: 80px 0;
  background: #fafafa;
}

.section:nth-child(even) {
  background: #ffffff;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(ellipse at 20% 30%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(147, 51, 234, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(99, 102, 241, 0.08) 0%, transparent 70%),
    linear-gradient(180deg, #0a0e27 0%, #0f1729 50%, #1a1f3a 100%);
}

/* 动态网格背景 */
.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

@keyframes gridMove {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

/* 光束扫描效果 */
.light-beam {
  position: absolute;
  width: 2px;
  height: 100%;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    rgba(59, 130, 246, 0.5) 50%,
    transparent 100%
  );
  animation: beamScan 8s ease-in-out infinite;
  opacity: 0.3;
}

.beam-1 {
  left: 20%;
  animation-delay: 0s;
}

.beam-2 {
  left: 50%;
  animation-delay: 2.7s;
}

.beam-3 {
  left: 80%;
  animation-delay: 5.4s;
}

@keyframes beamScan {
  0%, 100% { transform: translateY(-100%); opacity: 0; }
  50% { transform: translateY(100%); opacity: 0.3; }
}

/* 装饰圆圈 */
.decoration-circle {
  position: absolute;
  border: 1px solid rgba(59, 130, 246, 0.1);
  border-radius: 50%;
  animation: circleFloat 20s ease-in-out infinite;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: 10%;
  left: -10%;
  animation-duration: 25s;
}

.circle-2 {
  width: 600px;
  height: 600px;
  bottom: -20%;
  right: -15%;
  animation-duration: 30s;
  animation-delay: 5s;
}

.circle-3 {
  width: 300px;
  height: 300px;
  top: 50%;
  right: 10%;
  animation-duration: 20s;
  animation-delay: 10s;
}

@keyframes circleFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-30px, 30px) scale(0.9); }
}

/* 发光球体 */
.glow-orb {
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  filter: blur(100px);
  animation: orbPulse 10s ease-in-out infinite;
  opacity: 0.15;
}

.orb-1 {
  background: radial-gradient(circle, #3b82f6 0%, transparent 70%);
  top: -20%;
  left: 10%;
  animation-duration: 12s;
}

.orb-2 {
  background: radial-gradient(circle, #8b5cf6 0%, transparent 70%);
  bottom: -20%;
  right: 10%;
  animation-duration: 15s;
  animation-delay: 3s;
}

@keyframes orbPulse {
  0%, 100% { transform: scale(1); opacity: 0.15; }
  50% { transform: scale(1.3); opacity: 0.25; }
}

.snow-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(2px 2px at 20% 30%, white, transparent),
    radial-gradient(2px 2px at 60% 70%, white, transparent),
    radial-gradient(1px 1px at 50% 50%, white, transparent),
    radial-gradient(1px 1px at 80% 10%, white, transparent),
    radial-gradient(2px 2px at 90% 60%, white, transparent);
  background-size: 200% 200%;
  animation: snowfall 20s linear infinite;
  opacity: 0.3;
}

@keyframes snowfall {
  0% { transform: translateY(0); }
  100% { transform: translateY(50px); }
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(59, 130, 246, 0.1) 0%, 
    transparent 40%,
    rgba(147, 51, 234, 0.1) 100%);
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 1200px;
  padding: 0 40px;
}

.hero-badge {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 28px;
  background: 
    linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(147, 51, 234, 0.1) 100%),
    rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 50px;
  color: #e5e7eb;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 40px;
  animation: fadeInDown 1s ease-out, badgeGlow 3s ease-in-out infinite;
  box-shadow: 
    0 4px 24px rgba(59, 130, 246, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

@keyframes badgeGlow {
  0%, 100% { box-shadow: 0 4px 24px rgba(59, 130, 246, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.1); }
  50% { box-shadow: 0 4px 32px rgba(59, 130, 246, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.2); }
}

.badge-pulse {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  animation: badgePulse 3s ease-in-out infinite;
}

@keyframes badgePulse {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(200%); }
}

.badge-icon {
  font-size: 22px;
  filter: drop-shadow(0 0 8px rgba(59, 130, 246, 0.5));
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

.badge-text {
  letter-spacing: 0.5px;
}

.badge-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: skewX(-20deg);
  animation: badgeShine 5s ease-in-out infinite;
}

@keyframes badgeShine {
  0% { left: -100%; }
  20% { left: 150%; }
  100% { left: 150%; }
}

.hero-title {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
  animation: fadeInUp 1s ease-out 0.2s both;
}

.title-main {
  font-size: 90px;
  font-weight: 900;
  line-height: 1;
  margin-bottom: 4px;
}

.title-letter {
  display: inline-block;
  background: linear-gradient(135deg, #ffffff 0%, #60a5fa 50%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 30px rgba(96, 165, 250, 0.5));
  animation: titleWave 3s ease-in-out infinite;
  animation-delay: calc(var(--i, 0) * 0.1s);
}

.title-letter:nth-child(1) { --i: 0; }
.title-letter:nth-child(2) { --i: 1; }
.title-letter:nth-child(3) { --i: 2; }
.title-letter:nth-child(4) { --i: 3; }
.title-letter:nth-child(5) { --i: 4; }

@keyframes titleWave {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.title-sub {
  font-size: 22px;
  font-weight: 400;
  letter-spacing: 8px;
  text-transform: uppercase;
  display: flex;
  justify-content: center;
  gap: 2px;
}

.letter {
  display: inline-block;
  color: #64748b;
  animation: letterFade 4s ease-in-out infinite;
  animation-delay: calc(var(--j, 0) * 0.05s);
  transition: all 0.3s ease;
}

.letter:hover {
  color: #3b82f6;
  transform: scale(1.2) translateY(-5px);
}

.letter:nth-child(1) { --j: 0; }
.letter:nth-child(2) { --j: 1; }
.letter:nth-child(3) { --j: 2; }
.letter:nth-child(4) { --j: 3; }
.letter:nth-child(5) { --j: 4; }
.letter:nth-child(6) { --j: 5; }
.letter:nth-child(7) { --j: 6; }
.letter:nth-child(8) { --j: 7; }
.letter:nth-child(9) { --j: 8; }
.letter:nth-child(10) { --j: 9; }

@keyframes letterFade {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.letter.space {
  width: 12px;
}

/* 特色标签 */
.hero-features {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 40px;
  animation: fadeInUp 1s ease-out 0.3s both;
}

.feature-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 25px;
  font-size: 13px;
  color: #cbd5e1;
  transition: all 0.3s ease;
}

.feature-tag:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.2);
}

.feature-icon {
  font-size: 16px;
  filter: drop-shadow(0 0 6px rgba(59, 130, 246, 0.5));
}

.hero-subtitle {
  font-size: 20px;
  color: #cbd5e1;
  margin-bottom: 50px;
  line-height: 1.8;
  animation: fadeInUp 1s ease-out 0.4s both;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 80px;
  animation: fadeInUp 1s ease-out 0.6s both;
}

.hero-btn {
  position: relative;
  padding: 18px 48px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 16px;
  border: none;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  transition: all 0.4s ease;
}

.btn-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
}

.btn-icon {
  font-size: 20px;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
}

.btn-arrow {
  margin-left: 4px;
  transition: transform 0.3s ease;
}

.hero-btn.primary {
  box-shadow: 
    0 10px 40px rgba(59, 130, 246, 0.3),
    0 2px 8px rgba(59, 130, 246, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.hero-btn.primary .btn-bg {
  background: 
    linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.1) 0%, transparent 100%);
}

.hero-btn.primary::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.2) 50%, transparent 100%);
  transform: translateX(-100%) skewX(-15deg);
  transition: transform 0.6s;
}

.hero-btn.primary:hover::before {
  transform: translateX(100%) skewX(-15deg);
}

.hero-btn.primary:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 
    0 15px 60px rgba(59, 130, 246, 0.5),
    0 5px 15px rgba(59, 130, 246, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.hero-btn.primary:hover .btn-arrow {
  transform: translateX(6px);
  animation: arrowBounce 0.6s ease-in-out infinite;
}

@keyframes arrowBounce {
  0%, 100% { transform: translateX(6px); }
  50% { transform: translateX(10px); }
}

.hero-btn.secondary {
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  border: 1.5px solid rgba(255, 255, 255, 0.2);
}

.hero-btn.secondary .btn-bg {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
}

.hero-btn.secondary:hover {
  transform: translateY(-4px);
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 
    0 12px 40px rgba(139, 92, 246, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.hero-btn.secondary:hover .btn-bg {
  background: rgba(255, 255, 255, 0.12);
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  animation: fadeInUp 1s ease-out 0.8s both;
}

.stat-item {
  position: relative;
  text-align: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  transition: all 0.4s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-8px) scale(1.05);
  box-shadow: 0 16px 48px rgba(59, 130, 246, 0.2);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 12px;
  filter: drop-shadow(0 0 12px rgba(59, 130, 246, 0.5));
  animation: iconBounce 3s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.stat-number {
  font-size: 52px;
  font-weight: 900;
  background: linear-gradient(135deg, #ffffff 0%, #60a5fa 50%, #a78bfa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
  filter: drop-shadow(0 0 20px rgba(96, 165, 250, 0.3));
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-weight: 600;
  margin-bottom: 12px;
}

.stat-bar {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #3b82f6, transparent);
  border-radius: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-item:hover .stat-bar {
  opacity: 1;
  animation: barGlow 1.5s ease-in-out infinite;
}

@keyframes barGlow {
  0%, 100% { box-shadow: 0 0 5px #3b82f6; }
  50% { box-shadow: 0 0 20px #3b82f6, 0 0 40px #3b82f6; }
}

.stat-divider {
  width: 2px;
  height: 80px;
  background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.2), transparent);
  position: relative;
}

.divider-dot {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 6px;
  background: #3b82f6;
  border-radius: 50%;
  box-shadow: 0 0 10px #3b82f6;
  animation: dotPulse 2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  50% { transform: translate(-50%, -50%) scale(1.5); opacity: 0.5; }
}

.scroll-indicator {
  position: absolute;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  animation: indicatorBounce 2.5s ease-in-out infinite;
}

.scroll-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.mouse {
  width: 28px;
  height: 46px;
  border: 2.5px solid rgba(255, 255, 255, 0.4);
  border-radius: 14px;
  position: relative;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.wheel {
  width: 4px;
  height: 10px;
  background: linear-gradient(to bottom, #3b82f6, #8b5cf6);
  border-radius: 3px;
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  animation: wheelScroll 2s ease-in-out infinite;
  box-shadow: 0 0 8px rgba(59, 130, 246, 0.6);
}

@keyframes wheelScroll {
  0%, 20% { transform: translate(-50%, 0); opacity: 1; }
  80%, 100% { transform: translate(-50%, 16px); opacity: 0; }
}

.scroll-arrows {
  display: flex;
  flex-direction: column;
  gap: -8px;
}

.arrow-down {
  color: rgba(255, 255, 255, 0.4);
  font-size: 16px;
  animation: arrowDown 2s ease-in-out infinite;
}

.arrow-down:nth-child(1) { animation-delay: 0s; }
.arrow-down:nth-child(2) { animation-delay: 0.3s; }
.arrow-down:nth-child(3) { animation-delay: 0.6s; }

@keyframes arrowDown {
  0%, 20%, 100% { opacity: 0; transform: translateY(0); }
  40% { opacity: 1; transform: translateY(8px); }
  60% { opacity: 0; transform: translateY(16px); }
}

.scroll-text {
  color: #94a3b8;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes indicatorBounce {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(-12px); }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-header-modern {
  text-align: center;
  margin-bottom: 60px;
}

.section-label {
  font-size: 13px;
  font-weight: 700;
  color: #1890ff;
  letter-spacing: 3px;
  text-transform: uppercase;
  margin-bottom: 12px;
}

.section-title-modern {
  font-size: 42px;
  font-weight: 700;
  color: #262626;
  margin-bottom: 16px;
  letter-spacing: -0.5px;
}

.section-desc {
  font-size: 16px;
  color: #8c8c8c;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.8;
}

.section-footer {
  text-align: center;
  margin-top: 60px;
}

.view-more-btn {
  padding: 16px 48px;
  background: #ffffff;
  border: 2px solid #1890ff;
  border-radius: 12px;
  color: #1890ff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.view-more-btn:hover {
  background: #1890ff;
  color: #ffffff;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
}

.btn-arrow {
  transition: transform 0.3s ease;
}

.view-more-btn:hover .btn-arrow {
  transform: translateX(4px);
}

/* ========== Courses Section ========== */
.courses-section {
  background: #ffffff;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 50px;
}

.course-card-modern {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.course-card-modern:hover {
  transform: translateY(-8px);
  border-color: #1890ff;
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.12);
}

.course-image-wrapper {
  position: relative;
  height: 220px;
  background: #f0f2f5;
  overflow: hidden;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.course-image-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e6f4ff 0%, #d9f7be 100%);
}

.course-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.fire-icon {
  font-size: 14px;
}

.course-difficulty {
  position: absolute;
  bottom: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.course-content {
  padding: 24px;
}

.course-title {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 12px;
  line-height: 1.4;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #8c8c8c;
}

.meta-icon {
  font-size: 16px;
}

.course-footer-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.price-wrapper {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.price-label {
  font-size: 12px;
  color: #8c8c8c;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #ff4d4f;
}

.book-btn {
  padding: 8px 20px;
  background: #1890ff;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.book-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.arrow-icon {
  transition: transform 0.3s ease;
}

.book-btn:hover .arrow-icon {
  transform: translateX(4px);
}

/* ========== Products Section ========== */
.products-section {
  background: #fafafa;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 50px;
}

.product-card-modern {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.product-card-modern:hover {
  transform: translateY(-8px);
  border-color: #1890ff;
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.12);
}

.product-image-container {
  position: relative;
  height: 260px;
  background: #f5f5f5;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.product-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #fff1f0 0%, #e6f7ff 100%);
}

.product-tags {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.tag.new {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.tag.hot {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.quick-view {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  color: white;
  text-align: center;
  padding: 12px;
  font-size: 13px;
  font-weight: 600;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.product-card-modern:hover .quick-view {
  transform: translateY(0);
}

.product-info-modern {
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
  line-height: 1.5;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.category {
  font-size: 13px;
  color: #94a3b8;
}

.stock-status {
  font-size: 11px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 10px;
  text-transform: uppercase;
}

.stock-status.in-stock {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.stock-status.low-stock {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.product-price-action {
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
  padding: 8px 20px;
  background: #1890ff;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-cart-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* ========== Rental Section ========== */
.rental-section {
  background: #ffffff;
}

.rental-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.rental-card {
  display: flex;
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.rental-card:hover {
  border-color: #1890ff;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.12);
  transform: translateY(-4px);
}

.rental-card.unavailable {
  opacity: 0.5;
}

.rental-image-area {
  position: relative;
  width: 140px;
  background: linear-gradient(135deg, #f0f2f5 0%, #e6f4ff 100%);
}

.rental-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.rental-bg {
  width: 100%;
  height: 100%;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 10px,
    rgba(59, 130, 246, 0.03) 10px,
    rgba(59, 130, 246, 0.03) 20px
  );
}

.status-indicator {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  backdrop-filter: blur(10px);
}

.status-indicator.available {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.status-indicator.rented {
  background: rgba(107, 114, 128, 0.2);
  color: #9ca3af;
  border: 1px solid rgba(107, 114, 128, 0.3);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.rental-details {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.rental-name {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 10px;
}

.rental-specs {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #8c8c8c;
}

.spec-item {
  display: inline-block;
}

.spec-divider {
  color: rgba(255, 255, 255, 0.2);
}

.rental-pricing {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-amount {
  font-size: 24px;
  font-weight: 700;
  color: #ff4d4f;
}

.price-unit {
  font-size: 12px;
  color: #64748b;
}

.rental-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.rental-btn:not(:disabled):hover {
  transform: scale(1.05);
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
}

.rental-btn:disabled {
  background: rgba(107, 114, 128, 0.2);
  color: #6b7280;
  cursor: not-allowed;
}

/* ========== Venues Section ========== */
.venues-section {
  background: #fafafa;
}

.venues-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 30px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.venue-card-modern {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.venue-card-modern:hover {
  transform: translateY(-8px);
  border-color: #1890ff;
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.12);
}

.venue-visual {
  position: relative;
  height: 160px;
  background: linear-gradient(135deg, #f0f2f5 0%, #bae0ff 100%);
  overflow: hidden;
}

.venue-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.venue-bg-pattern {
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(30deg, transparent 12%, rgba(99, 102, 241, 0.05) 12%, rgba(99, 102, 241, 0.05) 50%, transparent 50%),
    linear-gradient(150deg, transparent 12%, rgba(59, 130, 246, 0.05) 12%, rgba(59, 130, 246, 0.05) 50%, transparent 50%);
  background-size: 60px 60px;
}

.difficulty-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 13px;
  backdrop-filter: blur(10px);
}

.difficulty-badge.beginner {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.difficulty-badge.intermediate {
  background: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.difficulty-badge.advanced {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.difficulty-icon {
  font-size: 16px;
}

.venue-details {
  padding: 24px;
}

.venue-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.venue-type {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 20px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.venue-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 11px;
  color: #64748b;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: 700;
  color: #262626;
}

.venue-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.venue-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.venue-price > span:first-child {
  font-size: 24px;
  font-weight: 700;
  color: #ff4d4f;
}

.reserve-btn {
  padding: 10px 24px;
  background: #1890ff;
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reserve-btn:hover {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
}
</style>
