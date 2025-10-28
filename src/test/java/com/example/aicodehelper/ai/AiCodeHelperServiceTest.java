package com.example.aicodehelper.ai;

import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat(1, "Hello, AI!");
        System.out.println(result);
    }
    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat(1, "Hello, Im a Java beginner, weibo");
        System.out.println(result);
        result = aiCodeHelperService.chat(1, "Hi, who am i?");
        System.out.println(result);
        // 不同用户的独立会话
        String result3 = aiCodeHelperService.chat(2, "Hi, who am i?");
        System.out.println(result3);  // "我不知道你叫什么"
    }

    @Test
    void chatForReport() {
        String userMessage = "你好，请帮我生成一份关于人工智能发展的报告";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(1, userMessage);
        System.out.println(report);
    }

    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag(1, "怎么学习java, 有哪些常见面试题？");
        System.out.println(result.sources());
        System.out.println(result.content());
    }

    @Test
    void chatWithTools() {
        String result = aiCodeHelperService.chat(1, "有哪些常见的计算机网络面试题？");
        System.out.println(result);
    }

}