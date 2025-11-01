import axios from 'axios'

// 生成唯一的聊天室 ID
export function generateChatId() {
  return Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 获取或创建 memoryId
export function getMemoryId() {
  let memoryId = sessionStorage.getItem('memoryId')
  if (!memoryId) {
    // 生成一个简单的数字 ID
    memoryId = Date.now().toString().slice(-8)
    sessionStorage.setItem('memoryId', memoryId)
  }
  return parseInt(memoryId)
}

/**
 * 使用 SSE 方式调用聊天接口
 * @param {number} memoryId - 会话 ID
 * @param {string} message - 用户消息
 * @param {function} onMessage - 接收消息块的回调函数
 * @param {function} onError - 错误处理回调
 * @param {function} onComplete - 完成时的回调
 */
export function chatWithSSE(memoryId, message, onMessage, onError, onComplete) {
  const url = `/api/ai/chat?memoryId=${memoryId}&message=${encodeURIComponent(message)}`
  
  const eventSource = new EventSource(url)
  
  eventSource.onmessage = (event) => {
    try {
      const data = event.data
      console.log('Raw event.data:', JSON.stringify(data), '(length:', data.length + ')')

      if (data !== null && data !== undefined) {
        // Backend sends TEXT_PLAIN, so event.data is the raw string with spaces preserved
        onMessage(data)
      }
    } catch (error) {
      console.error('Error processing SSE message:', error)
    }
  }
  
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    eventSource.close()
    if (onError) {
      onError(error)
    }
    if (onComplete) {
      onComplete()
    }
  }
  
  // SSE 不会自动知道何时结束，我们需要通过其他方式判断
  // 这里假设连接关闭时消息接收完成
  eventSource.addEventListener('error', () => {
    eventSource.close()
    if (onComplete) {
      onComplete()
    }
  })
  
  return eventSource
}

