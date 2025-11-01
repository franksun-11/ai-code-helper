# AI Code Helper Frontend

AI 编程小助手前端项目 - 基于 Vue3 + Vite 构建

## 功能特性

- 🤖 智能 AI 对话助手，帮助编程学习和面试准备
- 💬 实时流式对话（SSE 方式）
- 🌍 多语言支持（中文/英文）
- 🎨 IntelliJ 风格的深色主题界面
- 📱 响应式设计
- 💾 会话记忆功能

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Axios** - HTTP 请求库
- **SSE (Server-Sent Events)** - 实时通信

## 项目结构

```
ai-code-helper-frontend/
├── src/
│   ├── components/
│   │   └── ChatRoom.vue      # 聊天室主组件
│   ├── services/
│   │   └── api.js            # API 服务和 SSE 实现
│   ├── i18n/
│   │   └── index.js          # 多语言配置
│   ├── App.vue               # 根组件
│   └── main.js               # 入口文件
├── index.html
├── vite.config.js            # Vite 配置
└── package.json
```

## 开始使用

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

项目将在 http://localhost:5173 启动

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run preview
```

## 后端接口配置

后端接口地址已在 `vite.config.js` 中配置代理：

```javascript
proxy: {
  '/api': {
    target: 'https://localhost:8081',
    changeOrigin: true,
    secure: false
  }
}
```

确保后端服务运行在 `https://localhost:8081`

## 使用说明

1. 打开应用后，系统会自动生成一个会话 ID（memoryId）
2. 在输入框中输入编程相关的问题
3. 点击"发送"按钮或按 Enter 键发送消息
4. AI 将实时流式返回回答
5. 可以通过右上角切换语言（中文/English）

## 快捷键

- `Enter` - 发送消息
- `Shift + Enter` - 换行

## 特性说明

### 会话记忆
- 每个浏览器会话会自动生成唯一的 memoryId
- 同一会话中的对话会保持上下文连贯性
- 关闭浏览器标签页后会话 ID 会重置

### 实时流式响应
- 使用 SSE（Server-Sent Events）技术
- AI 回答会逐字显示，提供更好的用户体验

### 多语言支持
- 支持中文和英文界面
- 语言选择会保存在浏览器本地存储中
- 默认语言为英文

## 浏览器兼容性

- Chrome/Edge (推荐)
- Firefox
- Safari
- 需要支持 ES2015+ 和 EventSource API

## 开发建议

- 使用 VS Code + Volar 插件获得最佳开发体验
- 建议禁用 Vetur 插件以避免冲突

## License

MIT

