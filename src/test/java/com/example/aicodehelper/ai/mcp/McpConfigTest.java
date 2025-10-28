package com.example.aicodehelper.ai.mcp;

import dev.langchain4j.service.tool.ToolProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify McpConfig properly uses IllegalConfigurationException from the correct package.
 * 
 * This test ensures that the NoClassDefFoundError for dev.langchain4j.service.IllegalConfigurationException
 * does not occur when loading the application context.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "langchain4j.github-models.chat-model.api-key=dummy-test-key",
    "langchain4j.github-models.chat-model.model-name=gpt-4o",
    "langchain4j.github-models.embedding-model.model-name=text-embedding-3-small"
})
class McpConfigTest {

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Test
    void testMcpToolProviderBeanExists() {
        // Verify that the application context loads successfully
        assertNotNull(applicationContext, "Application context should load successfully");
        
        // Verify that the mcpToolProvider bean exists
        assertTrue(applicationContext.containsBean("mcpToolProvider"),
                "mcpToolProvider bean should exist");
        
        // Verify that the bean is of the correct type
        Object bean = applicationContext.getBean("mcpToolProvider");
        assertInstanceOf(ToolProvider.class, bean,
                "mcpToolProvider should be an instance of ToolProvider");
    }

    @Test
    void testNoClassDefFoundErrorDoesNotOccur() {
        // This test will fail at context load time if IllegalConfigurationException 
        // is imported from the wrong package (dev.langchain4j.service instead of dev.langchain4j.exception)
        // If we reach this point, it means the context loaded successfully and the fix is working
        assertNotNull(applicationContext,
                "If this assertion passes, it means NoClassDefFoundError did not occur");
    }
}
