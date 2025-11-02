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
          <div class="message-bubble markdown-content" v-html="renderMarkdown(msg.content)"></div>
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
import { marked } from 'marked'
import 'highlight.js/styles/atom-one-dark.css'
import hljs from 'highlight.js'

// Configure marked with simple settings
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (err) {}
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true,
  gfm: true,
  headerIds: false,
  mangle: false
})

const { t, setLocale, getLocale } = useI18n()
const currentLocale = ref(getLocale())

// Simple markdown rendering function
const renderMarkdown = (content) => {
  if (!content) return ''
  try {
    // Simple preprocessing to ensure proper markdown formatting
    let processed = content
      // Fix bold: remove extra spaces around **
      .replace(/\*\*\s+/g, '**')
      .replace(/\s+\*\*/g, '**')
      // Ensure headers have proper line breaks
      .replace(/([^\n])(#{1,6}\s)/g, '$1\n\n$2')
      .replace(/(#{1,6}\s[^\n]+)([^\n])/g, '$1\n\n$2')
      // AGGRESSIVE: Split numbered lists even if they're in the same paragraph
      // This handles: "1.item 2.item 3.item" -> "1.item\n2.item\n3.item"
      .replace(/(\d+\.)/g, '\n$1')
      // AGGRESSIVE: Split bullet lists 
      .replace(/([-•]\s)/g, '\n$1')
      // Clean up: remove multiple consecutive newlines (max 2)
      .replace(/\n{3,}/g, '\n\n')
      // Ensure proper spacing before first list item
      .replace(/([^\n])(\n\d+\.)/g, '$1\n\n$2')
      .replace(/([^\n])(\n[-•]\s)/g, '$1\n\n$2')

    const result = marked.parse ? marked.parse(processed) : marked(processed)
    return result
  } catch (error) {
    console.error('Markdown render error:', error)
    // Fallback: convert line breaks to <br>
    return content.replace(/\n/g, '<br>')
  }
}

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
}

/* Markdown content should render normally */
.message-bubble.markdown-content {
  white-space: normal;
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

/* Simple Markdown Styles - Like ChatGPT */
.message-bubble :deep(p) {
  margin: 0.5em 0;
  line-height: 1.7;
}

.message-bubble :deep(p:first-child) {
  margin-top: 0;
}

.message-bubble :deep(p:last-child) {
  margin-bottom: 0;
}

/* Add spacing between paragraphs and lists */
.message-bubble :deep(p + ul),
.message-bubble :deep(p + ol) {
  margin-top: 0.8em;
}

.message-bubble :deep(ul + p),
.message-bubble :deep(ol + p) {
  margin-top: 0.8em;
}

/* Bold text - Simple and clean */
.message-bubble :deep(strong) {
  font-weight: 700;
  color: #fff;
}

.message-bubble :deep(em) {
  font-style: italic;
}

/* Headers - Simple with just bold and size */
.message-bubble :deep(h1),
.message-bubble :deep(h2),
.message-bubble :deep(h3),
.message-bubble :deep(h4),
.message-bubble :deep(h5),
.message-bubble :deep(h6) {
  font-weight: 700;
  color: #fff;
  margin: 1em 0 0.5em 0;
  line-height: 1.3;
}

.message-bubble :deep(h1) {
  font-size: 1.5em;
}

.message-bubble :deep(h2) {
  font-size: 1.3em;
}

.message-bubble :deep(h3) {
  font-size: 1.15em;
}

.message-bubble :deep(h4),
.message-bubble :deep(h5),
.message-bubble :deep(h6) {
  font-size: 1.05em;
}

/* Code */
.message-bubble :deep(code) {
  background: #1e1e1e;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #e06c75;
}

.message-bubble :deep(pre) {
  background: #1e1e1e;
  border: 1px solid #444;
  border-radius: 6px;
  padding: 12px;
  overflow-x: auto;
  margin: 8px 0;
}

.message-bubble :deep(pre code) {
  background: none;
  padding: 0;
  color: #abb2bf;
  font-size: 13px;
  line-height: 1.5;
}

/* Lists */
.message-bubble :deep(ul),
.message-bubble :deep(ol) {
  margin: 12px 0;
  padding-left: 24px;
  line-height: 1.8;
}

.message-bubble :deep(li) {
  margin: 6px 0;
  display: list-item;
}

.message-bubble :deep(ol) {
  list-style-type: decimal;
}

.message-bubble :deep(ul) {
  list-style-type: disc;
}

/* Blockquotes */
.message-bubble :deep(blockquote) {
  border-left: 3px solid #667eea;
  padding-left: 12px;
  margin: 8px 0;
  color: #8c8c8c;
  font-style: italic;
}

/* Links */
.message-bubble :deep(a) {
  color: #4a90e2;
  text-decoration: none;
}

.message-bubble :deep(a:hover) {
  text-decoration: underline;
}

/* Horizontal rule */
.message-bubble :deep(hr) {
  border: none;
  border-top: 1px solid #444;
  margin: 12px 0;
}

/* Tables */
.message-bubble :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 8px 0;
}

.message-bubble :deep(table th),
.message-bubble :deep(table td) {
  border: 1px solid #444;
  padding: 6px 12px;
  text-align: left;
}

.message-bubble :deep(table th) {
  background: #3c3f41;
  font-weight: 600;
}
</style>