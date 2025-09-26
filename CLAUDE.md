# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Model Context Protocol (MCP) server built with Spring Boot that provides AI agent functionality for integration with tools like VS Code Copilot and Claude Desktop. The server exposes a comprehensive collection of specialized AI agents that can be invoked for domain-specific tasks, similar to Claude Code's built-in agent system.

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
- **AgentService** (`src/main/java/com/jenksy/jenksymcp/service/AgentService.java`): Service class containing MCP agent tools using `@Tool` annotations
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
1. **Primary Source**: Loads agents from `agents/` directory (included in project)
2. **Fallback**: If directory not found, loads default agents: ai-engineer, backend-architect, frontend-developer, code-reviewer, debugger
3. **Agent Format**: Each agent is defined in a markdown file with YAML frontmatter containing name, description, model, and optional tools
4. **System Prompts**: The markdown content after frontmatter serves as the agent's specialized system prompt
5. **Included Agents**: 10 pre-configured agents covering key development domains

## Dependencies

- Spring Boot 3.5.5 with Java 21
- Spring AI 1.0.2 for MCP server support (`spring-ai-starter-mcp-server`)
- Lombok for code generation and logging (`@Slf4j`)
- JUnit 5 for testing
- Spring Boot DevTools for development

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

### Available MCP Tools

Once configured, you'll have access to these tools:
- **get_agents**: List all 20 specialized AI agents
- **find_agents**: Search agents by domain (e.g., "backend", "security", "AI")
- **get_agent_info**: Get detailed agent capabilities and descriptions
- **invoke_agent**: Get specialized agent context and guidance for tasks
- **get_recommended_agents**: Get agent recommendations for specific tasks

### Usage Examples

```javascript
// Get all available agents
get_agents()

// Find security-related agents
find_agents("security")

// Get detailed info about ai-engineer
get_agent_info("ai-engineer")

// Get structured agent guidance for a task
invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a production RAG system for document search",
  "context": "Spring Boot application with 10M+ documents"
})

// Get recommended agents for a task
get_recommended_agents("optimize database queries")
```

**Note**: Update the JAR path to match your actual project location. After building or rebuilding, restart your AI tool to use the updated JAR.