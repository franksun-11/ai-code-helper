package com.example.aicodehelper.ai.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.Set;
// 自定义输入过滤器，用于检测用户输入中的敏感词
public class SafeInputGuardRail implements InputGuardrail {

    // 敏感词集合
    private static final Set<String> sensitiveWords = Set.of("kill", "evil");

    /**
     * 检测用户输入是否安全
     */
    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        // 获取用户输入并转换为小写以确保大小写不敏感
        String inputText = userMessage.singleText().toLowerCase();
        // 实用正则表达式分割输入文本为单词
        String[] words = inputText.split("\\W+");
        // 遍历所有单词，检查是否包含敏感词
        for (String word : words) {
            if (sensitiveWords.contains(word)) {
                return fatal("Sensitive word detected: " + word);
            }
        }
        return success();
    }
}
