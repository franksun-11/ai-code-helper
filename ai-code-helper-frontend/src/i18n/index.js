import { reactive } from 'vue'

const messages = {
  en: {
    title: 'AI Code Helper',
    subtitle: 'Your Programming Learning & Interview Assistant',
    placeholder: 'Ask me anything about programming...',
    send: 'Send',
    thinking: 'AI is thinking...',
    error: 'Error occurred, please try again',
    welcome: 'Hello! I\'m your AI programming assistant. Feel free to ask me any questions about programming learning or job interviews!',
    language: 'Language'
  },
  zh: {
    title: 'AI 编程小助手',
    subtitle: '编程学习与求职面试助手',
    placeholder: '向我提问编程相关的任何问题...',
    send: '发送',
    thinking: 'AI 正在思考...',
    error: '发生错误，请重试',
    welcome: '你好！我是你的 AI 编程助手。请随时向我提问编程学习或求职面试相关的问题！',
    language: '语言'
  }
}

const state = reactive({
  locale: 'en',
  messages
})

export function useI18n() {
  const t = (key) => {
    return state.messages[state.locale][key] || key
  }

  const setLocale = (locale) => {
    if (state.messages[locale]) {
      state.locale = locale
      localStorage.setItem('locale', locale)
    }
  }

  const getLocale = () => {
    return state.locale
  }

  // 从 localStorage 恢复语言设置
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale && state.messages[savedLocale]) {
    state.locale = savedLocale
  }

  return {
    t,
    setLocale,
    getLocale
  }
}

