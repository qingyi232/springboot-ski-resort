<template>
  <div class="ai-recommendation">
    <div class="recommendation-header">
      <h2>
        <el-icon><MagicStick /></el-icon>
        为您推荐
      </h2>
      <el-tag type="primary" effect="plain" size="small">AI智能推荐</el-tag>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 雪具推荐 -->
      <el-tab-pane label="热门雪具" name="equipment">
        <div v-loading="equipmentLoading" class="recommendation-grid">
          <div v-for="item in recommendations.equipment" :key="item.id" class="recommendation-card">
            <div class="card-image">
              <img :src="item.imageUrl || '/default-equipment.jpg'" :alt="item.equipmentName" />
              <el-tag class="card-tag" type="success" size="small">{{ item.equipmentType }}</el-tag>
            </div>
            <div class="card-content">
              <h4>{{ item.equipmentName }}</h4>
              <p class="brand">{{ item.brand }}</p>
              <div class="price-info">
                <span class="price">¥{{ item.hourlyPrice }}/时</span>
                <span class="daily">¥{{ item.dailyPrice }}/天</span>
              </div>
              <el-button type="primary" size="small" @click="handleRent(item)">立即租赁</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 教练推荐 -->
      <el-tab-pane label="推荐教练" name="coach">
        <div v-loading="coachLoading" class="recommendation-grid">
          <div v-for="coach in recommendations.coaches" :key="coach.id" class="recommendation-card">
            <div class="card-image">
              <img :src="coach.avatarUrl || '/default-coach.jpg'" :alt="coach.coachName" />
              <el-tag class="card-tag" :type="getLevelType(coach.level)" size="small">{{ coach.level }}</el-tag>
            </div>
            <div class="card-content">
              <h4>{{ coach.coachName }}</h4>
              <div class="rating">
                <el-rate v-model="coach.rating" disabled show-score />
              </div>
              <p class="specialty">专长：{{ coach.specialty }}</p>
              <div class="price-info">
                <span class="price">¥{{ coach.hourlyRate }}/课时</span>
              </div>
              <el-button type="primary" size="small" @click="handleBookCoach(coach)">预约教练</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 课程推荐 -->
      <el-tab-pane label="热门课程" name="course">
        <div v-loading="courseLoading" class="recommendation-grid">
          <div v-for="course in recommendations.courses" :key="course.id" class="recommendation-card">
            <div class="card-image">
              <img :src="course.imageUrl || '/default-course.jpg'" :alt="course.courseName" />
              <el-tag class="card-tag" :type="getDifficultyType(course.difficulty)" size="small">
                {{ getDifficultyText(course.difficulty) }}
              </el-tag>
            </div>
            <div class="card-content">
              <h4>{{ course.courseName }}</h4>
              <p class="coach-name">教练：{{ course.coachName }}</p>
              <p class="duration">时长：{{ course.duration }}分钟</p>
              <div class="enrollment">
                <span>{{ course.currentStudents }}/{{ course.maxStudents }}人</span>
                <el-progress :percentage="(course.currentStudents / course.maxStudents * 100)" :show-text="false" />
              </div>
              <div class="price-info">
                <span class="price">¥{{ course.price }}</span>
              </div>
              <el-button type="primary" size="small" @click="handleEnroll(course)">立即报名</el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import { getHomeRecommendations, getAllHotRecommendations } from '@/api/ai'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('equipment')

const equipmentLoading = ref(false)
const coachLoading = ref(false)
const courseLoading = ref(false)

const recommendations = reactive({
  equipment: [],
  coaches: [],
  courses: [],
  products: []
})

// 加载推荐数据
const loadRecommendations = async () => {
  try {
    const userId = localStorage.getItem('userId')
    
    if (userId) {
      // 已登录用户：获取个性化推荐
      const response = await getHomeRecommendations(userId)
      if (response.code === 200) {
        recommendations.equipment = response.data.equipment || []
        recommendations.coaches = response.data.coaches || []
        recommendations.courses = response.data.courses || []
        recommendations.products = response.data.products || []
      }
    } else {
      // 未登录用户：获取热门推荐
      const response = await getAllHotRecommendations()
      if (response.code === 200) {
        recommendations.equipment = response.data.equipment || []
        recommendations.coaches = response.data.coaches || []
        recommendations.courses = response.data.courses || []
      }
    }
  } catch (error) {
    console.error('加载推荐数据失败', error)
  }
}

const handleTabChange = (tabName) => {
  console.log('切换Tab:', tabName)
}

const getLevelType = (level) => {
  const types = { '初级': 'success', '中级': 'warning', '高级': 'danger' }
  return types[level] || 'info'
}

const getDifficultyType = (difficulty) => {
  const types = { 'easy': 'success', 'medium': 'warning', 'hard': 'danger' }
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const texts = { 'easy': '入门', 'medium': '进阶', 'hard': '高级' }
  return texts[difficulty] || difficulty
}

const handleRent = (equipment) => {
  router.push({ name: 'EquipmentRental', params: { id: equipment.id } })
}

const handleBookCoach = (coach) => {
  router.push({ name: 'CoachBooking', params: { id: coach.id } })
}

const handleEnroll = (course) => {
  router.push({ name: 'CourseEnrollment', params: { id: course.id } })
}

onMounted(() => {
  loadRecommendations()
})
</script>

<style scoped>
.ai-recommendation {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.recommendation-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.recommendation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  min-height: 300px;
}

.recommendation-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  background: #fff;
}

.recommendation-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.card-image {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.card-content {
  padding: 16px;
}

.card-content h4 {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.brand, .coach-name, .duration, .specialty {
  font-size: 14px;
  color: #909399;
  margin: 4px 0;
}

.rating {
  margin: 8px 0;
}

.enrollment {
  margin: 12px 0;
}

.enrollment span {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
  display: block;
}

.price-info {
  margin: 12px 0;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.daily {
  font-size: 14px;
  color: #909399;
}

.card-content .el-button {
  width: 100%;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .recommendation-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
}
</style>







