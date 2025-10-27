package com.example.aicodehelper;

import com.example.aicodehelper.ai.AiCodeHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperApplicationTests {

    @Resource
    private AiCodeHelper aiCodeHelper;
    @Test
    void chat() {
        aiCodeHelper.chat("我是一个Java初学者");
    }

    @Test
    void chatWithMessage() {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片中的内容"),
                ImageContent.from("https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Photo_The_american_actress_Lee_Remick_1960_-_Touring_Club_Italiano_04_0852.jpg/800px-Photo_The_american_actress_Lee_Remick_1960_-_Touring_Club_Italiano_04_0852.jpg")
        );
        aiCodeHelper.chat(userMessage);
    }
}
