package com.example.aicodehelper.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public interface AiCodeHelperService {
    @SystemMessage("fromResource = 'system-prompt.txt'")
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage("fromResource = 'system-prompt.txt'")
    Report chatForReport(@MemoryId int memoryId, @UserMessage String userMessage);

    // 学习报告
    record Report(String name, List<String> suggestionList){};
}
