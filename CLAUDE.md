# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Important Instructions

**NEVER USE EMOJIS** - This project strictly prohibits the use of emojis in any files, documentation, code comments, or communication. Always use plain text only.

## Project Overview

This is a Model Context Protocol (MCP) server built with Spring Boot that provides AI agent functionality for integration with tools like VS Code Copilot and Claude Desktop. The server exposes 20 specialized AI agents that can be invoked for domain-specific tasks.

**Architecture Features:**

- Spring Boot setup with standard configuration
- 20 specialized AI agents loaded from embedded markdown files
- Basic Caffeine caching for agent responses
- Spring Boot actuator endpoints for health checks

## Development Commands

### Building
```bash
./gradlew clean build
```

This creates two JAR files in `build/libs/`:
- `jenksy-mcp-0.0.1-SNAPSHOT.jar` - Fat JAR with all dependencies (~22MB)
- `jenksy-mcp-0.0.1-SNAPSHOT-plain.jar` - Plain JAR without dependencies

### Running in Development
```bash
./gradlew bootRun
```


### Testing
```bash
./gradlew test
```

### Running the Built JAR
```bash
java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

## Architecture

### Core Components

- **JenksyMcpApplication** (`src/main/java/com/jenksy/jenksymcp/JenksyMcpApplication.java`): Main Spring Boot application that configures MCP tool callbacks
- **AgentService** (`src/main/java/com/jenksy/jenksymcp/service/AgentService.java`): Service class containing MCP agent tools using `@Tool` annotations with basic caching
- **CacheConfig** (`src/main/java/com/jenksy/jenksymcp/config/CacheConfig.java`): Basic Caffeine cache configuration
- **Agent** (`src/main/java/com/jenksy/jenksymcp/record/Agent.java`): Record representing an AI agent with name, description, model, tools, and system prompt
- **AgentInvocation** (`src/main/java/com/jenksy/jenksymcp/record/AgentInvocation.java`): Record for agent invocation requests
- **AgentResponse** (`src/main/java/com/jenksy/jenksymcp/record/AgentResponse.java`): Record for agent responses

### MCP Tool Pattern

Agent tools are implemented using Spring AI's `@Tool` annotation in the AgentService:
- `get_agents`: Returns all available AI agents
- `find_agents`: Find agents by capability or domain
- `get_agent_info`: Get detailed information about a specific agent
- `invoke_agent`: Get specialized agent context and guidance for tasks
- `get_recommended_agents`: Get recommended agents for a specific task

The application registers these tools via `ToolCallbacks.from(agentService)` in the main application class. The `@Bean` method returns a `List<ToolCallback>` that Spring Boot uses to expose MCP functionality.

### Agent Loading

AgentService uses `@PostConstruct` to load AI agents from markdown files:
1. **Primary Source**: Loads agents from `src/main/resources/agents/` directory (embedded in JAR)
2. **Standard Loading**: Loads agents sequentially during application startup
3. **Fallback**: If directory not found, loads 5 default agents: ai-engineer, backend-architect, frontend-developer, code-reviewer, debugger
4. **Agent Format**: Each agent is defined in a markdown file with YAML frontmatter containing name, description, and optional tools
5. **System Prompts**: The markdown content after frontmatter serves as the agent's specialized system prompt
6. **Current Agents**: 20 specialized agents covering development domains: architecture, programming languages, AI/ML, security, DevOps, and tools

## Dependencies

- Spring Boot 3.5.5 with Java 21
- Spring AI 1.0.2 for MCP server support (`spring-ai-starter-mcp-server`)
- Caffeine for agent caching
- Spring Boot Actuator for health checks and metrics
- Spring Boot Validation for input validation
- Lombok for code generation and logging (`@Slf4j`)
- JUnit 5 for testing
- Spring Boot DevTools for development

## Key Architectural Patterns

### MCP Tool Registration
The `JenksyMcpApplication` main class registers MCP tools via `@Bean List<ToolCallback> toolCallbacks(AgentService agentService)` which uses `ToolCallbacks.from(agentService)` to automatically discover all `@Tool` annotated methods in AgentService.

### Agent Caching Strategy
- **Caffeine Cache**: Agent responses are cached with 5-minute expiration and 1000 max size
- **Context Storage**: Agent invocation contexts are stored temporarily with automatic cleanup
- **Performance**: Cache configuration in `CacheConfig` class optimizes repeated agent queries

### Data Models
- **Agent Record**: Immutable data structure with name, description, model, tools list, and system prompt
- **AgentInvocation Record**: Request structure with agentName, task, and context
- **AgentResponse Record**: Response structure with agent info, content, status, and context key

### Agent File Format
Agents use markdown files with YAML frontmatter:
```yaml
---
name: agent-name
description: Agent description
tools: optional,tool,list
---
# Agent system prompt content in markdown
```

**Note**: Model specifications are no longer required as MCP clients handle model selection automatically.


### MCP Client Optimization

This MCP server is optimized for MCP client integration:
- All agents return `"mcp-optimized"` as the model identifier
- MCP clients automatically use their configured model for processing
- No model-specific dependencies or references in agent definitions
- Simplified agent YAML frontmatter without model specifications

## Integration with AI Tools

### Claude Desktop Integration

Configure the MCP server in Claude Desktop's `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/johnjenkins/java-projects/jenksy.me/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### VS Code Copilot Integration

The MCP server can be used with VS Code Copilot extensions that support MCP protocol. Configure your extension to connect to the running MCP server.

### Available MCP Tools (5 Total)

Once configured, you'll have access to these tools:
- **get_agents**: List all 20 specialized AI agents
- **find_agents**: Search agents by domain (e.g., "backend", "security", "AI")
- **get_agent_info**: Get detailed agent capabilities and descriptions
- **invoke_agent**: PRIMARY TOOL - Get specialized agent context and guidance for tasks
- **get_recommended_agents**: Get 1-3 best agent recommendations for specific tasks

### MCP Client Integration Notes

**Important**: MCP clients like VS Code Copilot use natural language interaction, not direct tool calls. Users interact by saying:
- "Please use the ai-engineer agent to help design a RAG system..."
- "Which agents can help with database optimization?"
- "Show me all available agents"

MCP clients automatically translate these natural language requests into the appropriate MCP tool calls behind the scenes.

### Usage Examples for Direct Tool Integration (Claude Desktop)

```javascript
// Get all available agents
get_agents()

// Find security-related agents
find_agents("security")

// Get detailed info about ai-engineer
get_agent_info("ai-engineer")

// Get structured agent guidance for a task (primary workflow)
invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a production RAG system for document search",
  "context": "Spring Boot application with 10M+ documents"
})

// Get recommended agents for a task
get_recommended_agents("optimize database queries")
```

### Agent Development

When adding new agents to `src/main/resources/agents/`:
1. Use descriptive kebab-case filenames (e.g., `security-auditor.md`)
2. Include comprehensive YAML frontmatter with accurate description
3. Write detailed system prompts focused on specific domain expertise
4. Test agent loading by running the application and checking logs for "Loaded X agents from classpath"

**Note**: After building or rebuilding, restart your AI tool to use the updated JAR. Agent files are embedded in the JAR during build.

## Monitoring

### Health Monitoring

**Health Check:**
```bash
curl http://localhost:8080/actuator/health
```
