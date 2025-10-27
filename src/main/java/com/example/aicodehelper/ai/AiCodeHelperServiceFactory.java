package com.example.aicodehelper.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeHelperServiceFactory {
    @Resource
    private ChatLanguageModel GithubChatModel;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        return AiServices.create(AiCodeHelperService.class, GithubChatModel);
    }

}
