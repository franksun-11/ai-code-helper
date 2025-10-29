package com.example.aicodehelper.ai;

import com.example.aicodehelper.ai.guardrail.SafeInputGuardRail;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;

import java.util.List;

@InputGuardrails(SafeInputGuardRail.class)
public interface AiCodeHelperService {
    @SystemMessage("fromResource = 'system-prompt.txt'")
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage("fromResource = 'system-prompt.txt'")
    Report chatForReport(@MemoryId int memoryId, @UserMessage String userMessage);

    // 学习报告
    record Report(String name, List<String> suggestionList){};

    // 返回封装后的结果
    @SystemMessage("fromResource = 'system-prompt.txt'")
    Result<String> chatWithRag(@MemoryId int memoryId, @UserMessage String userMessage);
}
