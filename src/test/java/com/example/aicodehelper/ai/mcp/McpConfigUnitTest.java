package com.example.aicodehelper.ai.mcp;

import dev.langchain4j.service.tool.ToolProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for McpConfig that doesn't require full Spring context.
 * 
 * This test verifies that the NoClassDefFoundError for 
 * dev.langchain4j.service.IllegalConfigurationException does not occur.
 */
class McpConfigUnitTest {

    @Test
    void testMcpConfigCanBeInstantiated() {
        // Verify that McpConfig can be instantiated without NoClassDefFoundError
        assertDoesNotThrow(() -> {
            McpConfig config = new McpConfig();
            assertNotNull(config, "McpConfig should be instantiable");
        }, "McpConfig should be instantiable without NoClassDefFoundError");
    }

    @Test
    void testMcpToolProviderCanBeCreated() {
        // Verify that mcpToolProvider bean method can be called without NoClassDefFoundError
        // This is the critical test - if IllegalConfigurationException is imported from the wrong package,
        // this will throw NoClassDefFoundError at class loading time
        assertDoesNotThrow(() -> {
            McpConfig config = new McpConfig();
            ToolProvider provider = config.mcpToolProvider();
            assertNotNull(provider, "ToolProvider should be created successfully");
        }, "mcpToolProvider should be created without NoClassDefFoundError");
    }

    @Test
    void testCorrectExceptionClassIsAvailable() {
        // This test verifies that the correct exception class is available
        assertDoesNotThrow(() -> {
            // This will fail at compile time if the import is wrong
            Class<?> exceptionClass = dev.langchain4j.exception.IllegalConfigurationException.class;
            assertNotNull(exceptionClass, "IllegalConfigurationException should be available from dev.langchain4j.exception package");
        }, "dev.langchain4j.exception.IllegalConfigurationException should be available");
    }
}
