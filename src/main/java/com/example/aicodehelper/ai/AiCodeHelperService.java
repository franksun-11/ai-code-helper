package com.example.aicodehelper.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AiCodeHelperService {
    @SystemMessage("fromResource = 'system-prompt.txt'")
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
