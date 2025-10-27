package com.example.aicodehelper.ai;

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
        String result3 = aiCodeHelperService.chat(2, "我叫什么名字?");
        System.out.println(result3);  // "我不知道你叫什么"
    }
}  