<template>
  <div class="image-upload">
    <div class="upload-methods">
      <el-radio-group v-model="uploadMethod" @change="handleMethodChange">
        <el-radio label="url">图片URL</el-radio>
        <el-radio label="file">本地上传</el-radio>
      </el-radio-group>
    </div>

    <!-- URL输入方式 -->
    <div v-if="uploadMethod === 'url'" class="url-input-wrapper">
      <el-input
        v-model="imageUrl"
        placeholder="请输入图片URL（如: https://example.com/image.jpg）"
        @change="handleUrlChange"
      >
        <template #append>
          <el-button @click="handleUrlConfirm">确认</el-button>
        </template>
      </el-input>
      <div v-if="imageUrl" class="preview-wrapper">
        <div class="preview-image">
          <img :src="imageUrl" alt="预览图" @error="handleImageError" />
          <div class="preview-actions">
            <el-button size="small" type="danger" @click="handleClear">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 文件上传方式 -->
    <div v-else class="file-upload-wrapper">
      <el-upload
        class="image-uploader"
        :action="uploadAction"
        :show-file-list="false"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :headers="uploadHeaders"
        accept="image/*"
      >
        <div v-if="imageUrl" class="upload-preview">
          <img :src="imageUrl" alt="预览图" />
          <div class="upload-overlay">
            <el-icon><Upload /></el-icon>
            <span>点击重新上传</span>
          </div>
        </div>
        <div v-else class="upload-placeholder">
          <el-icon class="upload-icon"><Plus /></el-icon>
          <div class="upload-text">点击上传图片</div>
          <div class="upload-tip">支持 JPG, PNG, GIF 格式，大小不超过5MB</div>
        </div>
      </el-upload>
      <el-button v-if="imageUrl" size="small" type="danger" @click="handleClear" class="clear-btn">
        <el-icon><Delete /></el-icon> 清除图片
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload, Delete } from '@element-plus/icons-vue'
import { saveImageUrl } from '@/api/upload'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const uploadMethod = ref('url')
const imageUrl = ref(props.modelValue || '')
const uploadAction = ref('/api/v1/upload/image')
const uploadHeaders = ref({
  'Authorization': localStorage.getItem('token') || ''
})

// 监听父组件传入的值变化
watch(() => props.modelValue, (newVal) => {
  imageUrl.value = newVal || ''
})

// 切换上传方式
const handleMethodChange = () => {
  // 清空当前图片
  // imageUrl.value = ''
  // emit('update:modelValue', '')
}

// URL输入变化
const handleUrlChange = () => {
  // 可以在这里做实时验证
}

// 确认URL
const handleUrlConfirm = async () => {
  if (!imageUrl.value) {
    ElMessage.warning('请输入图片URL')
    return
  }

  // 简单验证URL格式
  if (!imageUrl.value.startsWith('http://') && !imageUrl.value.startsWith('https://')) {
    ElMessage.warning('请输入有效的图片URL（以http://或https://开头）')
    return
  }

  try {
    // 保存URL（后端验证）
    const res = await saveImageUrl(imageUrl.value)
    if (res.code === 200) {
      emit('update:modelValue', imageUrl.value)
      ElMessage.success('图片URL保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存图片URL失败:', error)
    // 即使保存失败也可以使用这个URL
    emit('update:modelValue', imageUrl.value)
    ElMessage.success('图片URL已设置')
  }
}

// 图片加载失败
const handleImageError = () => {
  ElMessage.error('图片加载失败，请检查URL是否正确')
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    imageUrl.value = response.data
    emit('update:modelValue', imageUrl.value)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('上传失败，请重试')
}

// 清除图片
const handleClear = () => {
  imageUrl.value = ''
  emit('update:modelValue', '')
  ElMessage.success('已清除图片')
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-methods {
  margin-bottom: 16px;
}

.url-input-wrapper {
  width: 100%;
}

.preview-wrapper {
  margin-top: 16px;
}

.preview-image {
  position: relative;
  width: 200px;
  height: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-actions {
  position: absolute;
  top: 8px;
  right: 8px;
}

.file-upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.image-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.image-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.upload-placeholder {
  width: 200px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.upload-icon {
  font-size: 48px;
  color: #8c939d;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  text-align: center;
  padding: 0 16px;
}

.upload-preview {
  position: relative;
  width: 200px;
  height: 200px;
}

.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
  font-size: 14px;
}

.upload-overlay .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-preview:hover .upload-overlay {
  opacity: 1;
}

.clear-btn {
  width: 200px;
}
</style>





