<template>
  <div class="chatbot-container">
    <!-- 悬浮球 -->
    <div v-if="!isOpen" class="chatbot-fab" @click="openChat">
      <el-icon class="fab-icon"><Service /></el-icon>
      <div class="fab-label">AI客服</div>
    </div>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div v-if="isOpen" class="chatbot-window">
      <!-- 头部 -->
      <div class="chatbot-header">
        <div class="header-left">
          <div class="avatar-wrapper">
            <el-icon :size="24"><Service /></el-icon>
          </div>
          <div class="header-info">
            <div class="title">AI智能客服</div>
            <div class="subtitle">飞跃滑雪场为您服务</div>
          </div>
        </div>
        <el-button 
          class="close-btn" 
          circle 
          size="small" 
          @click="closeChat"
        >
          <el-icon :size="16"><Close /></el-icon>
        </el-button>
      </div>

      <!-- 消息列表 -->
      <div class="chatbot-body" ref="messageContainer">
        <!-- 欢迎消息 -->
        <div class="message assistant welcome-msg">
          <div class="avatar">
            <el-icon><Service /></el-icon>
          </div>
          <div class="message-content">
            <div class="bubble">
              您好！我是飞跃滑雪场的AI智能客服，很高兴为您服务！您可以向我咨询关于课程、雪具租赁、装备购买、场地预订等任何问题。
            </div>
            <div class="time">{{ new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }}</div>
          </div>
        </div>

        <!-- 历史消息 -->
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div v-if="msg.role === 'assistant'" class="avatar">
            <el-icon><Service /></el-icon>
          </div>
          <div class="message-content">
            <div class="bubble">{{ msg.content }}</div>
            <div class="time">{{ msg.time }}</div>
          </div>
          <div v-if="msg.role === 'user'" class="avatar user-avatar">
            <el-icon><User /></el-icon>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="message assistant">
          <div class="avatar">
            <el-icon><Service /></el-icon>
          </div>
          <div class="message-content">
            <div class="bubble typing">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷问题 -->
      <div v-if="showQuickQuestions" class="quick-questions">
        <div class="quick-title">常见问题</div>
        <div class="quick-list">
          <div
            v-for="(q, index) in quickQuestions"
            :key="index"
            class="quick-item"
            @click="sendQuickQuestion(q)"
          >
            {{ q }}
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="chatbot-footer">
        <div class="input-wrapper">
          <el-input
            v-model="userInput"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
            size="large"
          >
            <template #suffix>
              <el-button
                :icon="Promotion"
                circle
                type="primary"
                @click="sendMessage"
                :loading="loading"
                :disabled="!userInput.trim()"
                class="send-btn"
              />
            </template>
          </el-input>
        </div>
        <div class="footer-actions">
          <el-button
            text
            size="small"
            @click="clearHistory"
            :icon="Delete"
          >
            清空对话
          </el-button>
        </div>
      </div>
    </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Service, User, Close, Delete, Promotion } from '@element-plus/icons-vue'
import { chatWithAI } from '@/api/ai'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const isOpen = ref(false)
const userInput = ref('')
const messages = ref([])
const loading = ref(false)
const messageContainer = ref(null)
const showQuickQuestions = ref(true)

const quickQuestions = [
  '有哪些适合初学者的课程？',
  '如何预约教练？',
  '雪具租赁价格如何？',
  '场地开放时间？',
  '装备商城有什么推荐？'
]

// 打开聊天
const openChat = () => {
  isOpen.value = true
  nextTick(() => {
    scrollToBottom()
  })
}

// 关闭聊天
const closeChat = () => {
  isOpen.value = false
}

// 发送消息
const sendMessage = async () => {
  const message = userInput.value.trim()
  if (!message || loading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })

  userInput.value = ''
  showQuickQuestions.value = false
  loading.value = true

  nextTick(() => {
    scrollToBottom()
  })

  try {
    const response = await chatWithAI({
      userId: userStore.userInfo?.id,
      message: message,
      sessionId: getSessionId()
    })

    messages.value.push({
      role: 'assistant',
      content: response.data.message || '抱歉，我暂时无法回答这个问题。',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })

    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请稍后重试')
    
    messages.value.push({
      role: 'assistant',
      content: '抱歉，网络连接出现问题，请稍后再试。',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } finally {
    loading.value = false
  }
}

// 发送快捷问题
const sendQuickQuestion = (question) => {
  userInput.value = question
  sendMessage()
}

// 清空对话
const clearHistory = () => {
  messages.value = []
  showQuickQuestions.value = true
  ElMessage.success('对话已清空')
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

// 获取或生成sessionId
const getSessionId = () => {
  let sessionId = sessionStorage.getItem('chatSessionId')
  if (!sessionId) {
    sessionId = 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    sessionStorage.setItem('chatSessionId', sessionId)
  }
  return sessionId
}

onMounted(() => {
  // 可以加载历史消息
})
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  right: 0;
  bottom: 0;
  z-index: 1000;
  pointer-events: none;
}

.chatbot-container > * {
  pointer-events: auto;
}

/* 悬浮球 */
.chatbot-fab {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(25, 118, 210, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  overflow: visible;
}

.chatbot-fab:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(25, 118, 210, 0.5);
}

.fab-icon {
  font-size: 28px;
  margin-bottom: 2px;
}

.fab-label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 聊天窗口 */
.chatbot-window {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 420px;
  height: 620px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 1001;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

/* 头部 */
.chatbot-header {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-wrapper {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.subtitle {
  font-size: 12px;
  opacity: 0.9;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.4);
  color: white;
  transition: all 0.3s ease;
  width: 32px;
  height: 32px;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
  transform: rotate(90deg);
}

/* 消息区域 */
.chatbot-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.chatbot-body::-webkit-scrollbar {
  width: 6px;
}

.chatbot-body::-webkit-scrollbar-track {
  background: transparent;
}

.chatbot-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.chatbot-body::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeInUp 0.3s ease;
}

.message.assistant {
  justify-content: flex-start;
}

.message.user {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.3);
}

.user-avatar {
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  box-shadow: 0 2px 8px rgba(100, 116, 139, 0.3);
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message.user .message-content {
  align-items: flex-end;
}

.bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message.assistant .bubble {
  background: white;
  color: #334155;
  border-bottom-left-radius: 4px;
}

.message.user .bubble {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.welcome-msg .bubble {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #0d47a1;
  border: 1px solid #90caf9;
}

.time {
  font-size: 11px;
  color: #94a3b8;
  padding: 0 4px;
}

.message.user .time {
  text-align: right;
}

/* 打字指示器 */
.typing {
  background: white !important;
  padding: 16px 20px !important;
}

.typing-indicator {
  display: flex;
  gap: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #94a3b8;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

/* 快捷问题 */
.quick-questions {
  padding: 12px 20px;
  background: white;
  border-top: 1px solid #e8f0f5;
}

.quick-title {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 600;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-item {
  padding: 8px 14px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e8f0f5 100%);
  border: 1px solid #cbd5e1;
  border-radius: 16px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-item:hover {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-color: #1976d2;
  color: #1976d2;
  transform: translateY(-2px);
}

/* 输入区域 */
.chatbot-footer {
  background: white;
  border-top: 1px solid #e8f0f5;
  padding: 16px 20px;
}

.input-wrapper {
  margin-bottom: 8px;
}

.send-btn {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  border: none;
  width: 36px;
  height: 36px;
}

.send-btn:hover {
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 100%);
}

.footer-actions {
  display: flex;
  justify-content: center;
}

/* 动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .chatbot-window {
    right: 12px;
    bottom: 12px;
    width: calc(100vw - 24px);
    height: calc(100vh - 100px);
    max-width: 420px;
    max-height: 620px;
  }
  
  .chatbot-fab {
    right: 16px;
    bottom: 16px;
  }
}
</style>
