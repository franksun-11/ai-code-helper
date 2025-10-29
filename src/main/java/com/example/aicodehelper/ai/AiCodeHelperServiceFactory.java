package com.example.aicodehelper.ai;

import com.example.aicodehelper.ai.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeHelperServiceFactory {
    @Resource
    private ChatModel GithubChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel GithubStreamingChatModel;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        // 构建AI服务
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(GithubChatModel)
                .streamingChatModel(GithubStreamingChatModel) // 流式输出
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(3)) // 减少到3条消息以降低token使用
                .contentRetriever(contentRetriever) // RAG内容检索器
                .tools(new InterviewQuestionTool())
                .toolProvider(mcpToolProvider)  // mcp工具调用
                .build();
        return aiCodeHelperService;
    }

}
