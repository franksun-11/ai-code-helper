# MCP Configuration and NoClassDefFoundError Fix

## Problem
When adding Model Context Protocol (MCP) support to the application, you may encounter the following error:

```
java.lang.NoClassDefFoundError: dev/langchain4j/service/IllegalConfigurationException
	at com.example.aicodehelper.ai.mcp.McpConfig.mcpToolProvider(McpConfig.java:32)
```

## Root Cause
The error occurs because the exception class `IllegalConfigurationException` is being imported from the wrong package:

- **INCORRECT** (causes NoClassDefFoundError): `dev.langchain4j.service.IllegalConfigurationException`
- **CORRECT**: `dev.langchain4j.exception.IllegalConfigurationException`

## Solution
Always use the correct import for langchain4j exceptions:

```java
import dev.langchain4j.exception.IllegalConfigurationException;  // CORRECT
```

**NOT:**

```java
import dev.langchain4j.service.IllegalConfigurationException;  // WRONG - This class doesn't exist!
```

## Example
See `src/main/java/com/example/aicodehelper/ai/mcp/McpConfig.java` for a working example of proper exception handling in langchain4j configuration classes.

## Verification
To verify the correct package, check the langchain4j JAR contents:
```bash
jar tf ~/.m2/repository/dev/langchain4j/langchain4j/0.36.2/langchain4j-0.36.2.jar | grep IllegalConfiguration
```

Output should show:
```
dev/langchain4j/exception/IllegalConfigurationException.class
```

## Additional Notes
- The `dev.langchain4j.service` package contains service-related classes like `AiServices`, `UserMessage`, etc.
- The `dev.langchain4j.exception` package contains exception classes including `IllegalConfigurationException`
- MCP (Model Context Protocol) support requires proper integration with langchain4j's ToolProvider interface
