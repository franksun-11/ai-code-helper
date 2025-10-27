package com.example.aicodehelper.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubModelConfig {

    @Value("${langchain4j.github-models.chat-model.api-key:}")
    private String githubToken;

    @Value("${langchain4j.github-models.chat-model.model-name:gpt-4o}")
    private String modelName;

    @Bean
    public ChatLanguageModel GithubChatModel() {
        if (githubToken == null || githubToken.isEmpty()) {
            throw new IllegalStateException("GitHub Token 未设置!请在环境变量中设置 GITHUB_TOKEN");
        }

        return GitHubModelsChatModel.builder()
                .gitHubToken(githubToken)
                .modelName(modelName)
                .logRequestsAndResponses(true)
                .build();
    }
}
