package com.example.aicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatModel GithubChatModel;

    private static final String SYSTEM_PROMPT = """
            """ ;
    // 简单对话
    public String chat(String message){
        SystemMessage systemMessage = SystemMessage.from(SYSTEM_PROMPT);
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse response = GithubChatModel.chat(systemMessage, userMessage);
        String text = response.aiMessage().text();
        log.info("AI Response: {}", text);
        return text;
    }



    // 简单对话-自定义用户消息
    public String chat(UserMessage userMessage) {
        ChatResponse response = GithubChatModel.chat(userMessage);
        String text = response.aiMessage().text();
        log.info("AI Response: {}", text);
        return text;
    }
}
