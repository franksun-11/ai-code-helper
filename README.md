# AI Code Helper

## About This Project

AI Code Helper is an intelligent coding assistant that leverages advanced AI models to help developers with their coding tasks. Built with a modern tech stack combining Spring Boot backend and Vue.js frontend, it provides real-time streaming responses through Server-Sent Events (SSE) for a smooth conversational experience.

### Target Users

- **Software Developers**: Get instant help with coding questions, debugging, and implementation guidance
- **Students**: Learn programming concepts and get explanations for complex code
- **Technical Teams**: Enhance productivity with AI-powered code assistance and documentation
- **Anyone Learning to Code**: Receive interactive help and explanations in multiple languages

### Key Features

- Real-time streaming responses with SSE
- Multi-language support (English and Chinese)
- Markdown rendering with syntax highlighting
- Context-aware responses using RAG (Retrieval-Augmented Generation)
- Persistent conversation memory
- Clean, modern UI inspired by ChatGPT

<!-- Add your screenshots here -->
![demo](/picture/demo.png)

## Tech Stack

### Backend

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17+
- **AI Integration**: LangChain4j
- **AI Models**:
  - GitHub Models API (GPT-4o-mini for chat)
  - text-embedding-3-small (for embeddings)
- **Streaming**: Server-Sent Events (SSE)
- **Build Tool**: Maven

### Frontend

- **Framework**: Vue 3 (Composition API)
- **Build Tool**: Vite
- **HTTP Client**: Axios
- **Markdown Parser**: Marked
- **Syntax Highlighting**: Highlight.js
- **Internationalization**: Custom i18n implementation

### Additional Technologies

- **RAG**: Document-based context enhancement
- **MCP**: Model Context Protocol for external tool integration
- **Vector Store**: For semantic search and context retrieval

## Project Structure

```
ai-code-helper/
├── src/main/java/com/example/aicodehelper/
│   ├── ai/
│   │   ├── controller/
│   │   │   └── AiController.java          # SSE streaming endpoint
│   │   └── service/
│   │       └── AiCodeHelperService.java   # AI service logic
│   ├── config/
│   │   └── [Configuration files]
│   └── AiCodeHelperApplication.java       # Main application entry
├── src/main/resources/
│   ├── application.yml                     # Application configuration
│   └── [Other resources]
├── ai-code-helper-frontend/
│   ├── src/
│   │   ├── components/
│   │   │   └── ChatRoom.vue               # Main chat interface
│   │   ├── services/
│   │   │   └── api.js                     # SSE client & API calls
│   │   ├── i18n.js                        # Internationalization
│   │   ├── App.vue                        # Root component
│   │   └── main.js                        # Vue app entry
│   ├── package.json                        # Frontend dependencies
│   └── vite.config.js                     # Vite configuration
├── pom.xml                                 # Maven dependencies
└── README.md                               # This file
```

## Quick Start

### Prerequisites

- **Java**: JDK 17 or higher
- **Node.js**: v16 or higher
- **Maven**: 3.6+ (or use Maven wrapper)
- **GitHub Models API Key**: Get from [GitHub Models](https://github.com/marketplace/models)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ai-code-helper
   ```

2. **Configure API Key**

   Edit `src/main/resources/application.yml` and add your GitHub API token:
   ```yaml
   langchain4j:
     open-ai:
       chat-model:
         api-key: ${GITHUB_TOKEN:your-github-token-here}
   ```

3. **Build and run the backend**
   ```bash
   # Using Maven wrapper (recommended)
   ./mvnw clean install
   ./mvnw spring-boot:run

   # Or using Maven
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verify backend is running**

   The backend should start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ai-code-helper-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Access the application**

   Open your browser and navigate to `http://localhost:5173` (or the port shown in terminal)

### Production Build

**Frontend:**
```bash
cd ai-code-helper-frontend
npm run build
```

The built files will be in `dist/` directory.

**Backend:**
```bash
./mvnw clean package
java -jar target/ai-code-helper-*.jar
```

## Configuration

### Backend Configuration

Edit `src/main/resources/application.yml`:

```yaml
langchain4j:
  open-ai:
    chat-model:
      base-url: https://models.inference.ai.azure.com
      api-key: ${GITHUB_TOKEN}
      model-name: gpt-4o-mini
      temperature: 0.7
      top-p: 1.0
      max-tokens: 4096
    embedding-model:
      base-url: https://models.inference.ai.azure.com
      api-key: ${GITHUB_TOKEN}
      model-name: text-embedding-3-small
```

### Frontend Configuration

The API endpoint is configured in `src/services/api.js`:
```javascript
const BASE_URL = 'http://localhost:8080'
```

Modify this if your backend runs on a different host/port.

## Usage

1. **Start a conversation**: Type your coding question in the input box
2. **Press Enter or click Send**: The AI will respond in real-time with streaming output
3. **Switch languages**: Use the language selector in the header to switch between English and Chinese
4. **View formatted responses**: Responses support Markdown formatting including:
   - **Bold text**
   - Headers
   - Code blocks with syntax highlighting
   - Numbered and bullet lists
   - Tables and links

## Troubleshooting

### Backend won't start

- Verify Java version: `java -version` (should be 17+)
- Check if port 8080 is available
- Ensure GitHub API token is correctly configured

### Frontend won't connect

- Verify backend is running on `http://localhost:8080`
- Check browser console for CORS errors
- Ensure `api.js` has correct backend URL

### Markdown not rendering

- Hard refresh browser: `Ctrl+F5` (Windows) or `Cmd+Shift+R` (Mac)
- Check browser console for JavaScript errors
- Verify `marked` and `highlight.js` are installed

### No spaces in English words

- This was a known issue with SSE stripping whitespace
- Fixed in latest version by wrapping data in JSON objects
- Update to latest code and rebuild frontend

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

**Note**: This project uses GitHub Models API which may have rate limits. Please check GitHub's documentation for current limits and pricing.
