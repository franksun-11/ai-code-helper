<template>
  <div class="chat-room">
    <!-- Header -->
    <div class="chat-header">
      <div class="header-content">
        <h1>{{ t('title') }}</h1>
        <p class="subtitle">{{ t('subtitle') }}</p>
      </div>
      <div class="language-selector">
        <label>{{ t('language') }}:</label>
        <select v-model="currentLocale" @change="changeLanguage">
          <option value="en">English</option>
          <option value="zh">中文</option>
        </select>
      </div>
    </div>

    <!-- Chat Messages -->
    <div class="chat-messages" ref="messagesContainer">
      <!-- Welcome Message -->
      <div v-if="messages.length === 0" class="message ai-message">
        <div class="message-avatar ai-avatar">AI</div>
        <div class="message-content">
          <div class="message-bubble">{{ t('welcome') }}</div>
        </div>
      </div>

      <!-- Chat History -->
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']"
      >
        <div v-if="msg.role === 'ai'" class="message-avatar ai-avatar">AI</div>
        <div class="message-content">
          <div class="message-bubble">{{ msg.content }}</div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
        <div v-if="msg.role === 'user'" class="message-avatar user-avatar">
          {{ userInitial }}
        </div>
      </div>

      <!-- Loading Indicator -->
      <div v-if="isLoading" class="message ai-message">
        <div class="message-avatar ai-avatar">AI</div>
        <div class="message-content">
          <div class="message-bubble loading">
            <span class="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </span>
            {{ t('thinking') }}
          </div>
        </div>
      </div>
    </div>

    <!-- Input Area -->
    <div class="chat-input">
      <textarea
        v-model="inputMessage"
        :placeholder="t('placeholder')"
        @keydown.enter.exact.prevent="sendMessage"
        @keydown.enter.shift.exact="inputMessage += '\n'"
        :disabled="isLoading"
        rows="1"
      ></textarea>
      <button @click="sendMessage" :disabled="!inputMessage.trim() || isLoading">
        {{ t('send') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useI18n } from '../i18n'
import { chatWithSSE, getMemoryId } from '../services/api'

const { t, setLocale, getLocale } = useI18n()
const currentLocale = ref(getLocale())

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
const memoryId = ref(0)
const userInitial = ref('U')
const currentEventSource = ref(null)

// 改变语言
const changeLanguage = () => {
  setLocale(currentLocale.value)
}

// 格式化时间
const formatTime = () => {
  const now = new Date()
  return now.toLocaleTimeString('en-US', { 
    hour: '2-digit', 
    minute: '2-digit',
    hour12: false 
  })
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message,
    time: formatTime()
  })

  inputMessage.value = ''
  isLoading.value = true
  scrollToBottom()

  // 创建一个新的 AI 消息对象
  const aiMessageIndex = messages.value.length
  messages.value.push({
    role: 'ai',
    content: '',
    time: formatTime()
  })

  scrollToBottom()

  try {
    // 使用 SSE 调用
    currentEventSource.value = chatWithSSE(
      memoryId.value,
      message,
      // onMessage - 接收数据块
      (chunk) => {
        messages.value[aiMessageIndex].content += chunk
        scrollToBottom()
      },
      // onError
      (error) => {
        console.error('Chat error:', error)
        if (messages.value[aiMessageIndex].content === '') {
          messages.value[aiMessageIndex].content = t('error')
        }
        isLoading.value = false
      },
      // onComplete
      () => {
        isLoading.value = false
        currentEventSource.value = null
      }
    )
  } catch (error) {
    console.error('Error sending message:', error)
    messages.value[aiMessageIndex].content = t('error')
    isLoading.value = false
  }
}

// 初始化
onMounted(() => {
  memoryId.value = getMemoryId()
  console.log('Chat session initialized with memoryId:', memoryId.value)
  
  // 尝试从用户名获取首字母（这里使用默认值）
  userInitial.value = 'U'
})
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #2b2b2b;
  color: #a9b7c6;
  font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* Header */
.chat-header {
  background: #3c3f41;
  padding: 16px 24px;
  border-bottom: 1px solid #323232;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.subtitle {
  margin: 4px 0 0 0;
  font-size: 13px;
  color: #8c8c8c;
}

.language-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.language-selector label {
  font-size: 13px;
  color: #8c8c8c;
}

.language-selector select {
  background: #45494a;
  color: #a9b7c6;
  border: 1px solid #555;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  outline: none;
}

.language-selector select:hover {
  background: #4e5254;
}

/* Messages Area */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #2b2b2b;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #555;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #666;
}

.message {
  display: flex;
  gap: 12px;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.ai-message {
  justify-content: flex-start;
}

.user-message {
  justify-content: flex-end;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-avatar {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-message .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.ai-message .message-bubble {
  background: #313335;
  color: #a9b7c6;
  border: 1px solid #3c3f41;
}

.user-message .message-bubble {
  background: #2d5f8d;
  color: #fff;
}

.message-time {
  font-size: 11px;
  color: #787878;
  padding: 0 4px;
}

.loading {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-dots {
  display: inline-flex;
  gap: 4px;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  background: #667eea;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* Input Area */
.chat-input {
  background: #3c3f41;
  padding: 16px 24px;
  border-top: 1px solid #323232;
  display: flex;
  gap: 12px;
  align-items: flex-end;
  flex-shrink: 0;
}

.chat-input textarea {
  flex: 1;
  background: #45494a;
  color: #a9b7c6;
  border: 1px solid #555;
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 14px;
  font-family: inherit;
  resize: none;
  outline: none;
  max-height: 120px;
  min-height: 44px;
  line-height: 1.5;
}

.chat-input textarea:focus {
  border-color: #4a90e2;
  box-shadow: 0 0 0 2px rgba(74, 144, 226, 0.2);
}

.chat-input textarea:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chat-input button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.chat-input button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.chat-input button:active:not(:disabled) {
  transform: translateY(0);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

