package com.example.aicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatLanguageModel GithubChatModel;

    private static final String SYSTEM_PROMPT = """
            """ ;
    // 简单对话
    public String chat(String message){
        SystemMessage systemMessage = SystemMessage.from(SYSTEM_PROMPT);
        UserMessage userMessage = UserMessage.from(message);
        Response<AiMessage> response = GithubChatModel.generate(systemMessage, userMessage);
        String text = response.content().text();
        log.info("AI Response: {}", text);
        return text;
    }

    // 简单对话-自定义用户消息
    public String chat(UserMessage userMessage) {
        Response<AiMessage> response = GithubChatModel.generate(userMessage);
        String text = response.content().text();
        log.info("AI Response: {}", text);
        return text;
    }
}
