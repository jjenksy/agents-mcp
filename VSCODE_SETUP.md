# VS Code MCP Server Setup Guide

## Quick Setup

### 1. Prerequisites
- VS Code installed
- Java 21+ installed
- GitHub Copilot subscription

### 2. Install Required Extensions
VS Code will prompt you to install recommended extensions when you open the workspace:
- **GitHub Copilot** (required)
- **GitHub Copilot Chat** (required)
- Java Extension Pack (for development)

### 3. Open Workspace
1. Open this folder (`jenksy-mcp`) in VS Code
2. Accept the prompt to install recommended extensions
3. Restart VS Code after extensions are installed

### 4. Verify MCP Server Configuration
The `.vscode/mcp.json` file is pre-configured:
```json
{
  "mcpServers": {
    "ai-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/johnjenkins/java-projects/jenksy.me/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Using AI Agents in VS Code

### Open Copilot Chat
- Press `Ctrl+Shift+I` (Windows/Linux) or `Cmd+Shift+I` (Mac)
- Or use Command Palette: `> GitHub Copilot: Open Chat`

### Basic Agent Commands
```javascript
// List all available agents
@workspace Use get_agents()

// Find agents by domain
@workspace Use find_agents("security")

// Get agent details
@workspace Use get_agent_info("ai-engineer")

// Get raw system prompt
@workspace Use get_agent_prompt("backend-architect")

// Get structured guidance
@workspace Use invoke_agent with ai-engineer to design a RAG system
```

### Example Workflows

#### 1. Code Review with Security Focus
```
@workspace Use get_agent_prompt("security-auditor")
Now review this authentication code using security expertise
```

#### 2. API Design Guidance
```
@workspace Use invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a user management API",
  "context": "Spring Boot app with JWT auth"
})
```

#### 3. Performance Optimization
```
@workspace Use get_agent_prompt("database-optimizer")
Now analyze these SQL queries for performance issues
```

## Development Commands

### Build and Run (Using VS Code Tasks)
- **Build**: `Ctrl+Shift+P` → `Tasks: Run Task` → `build`
- **Run MCP Server**: `Tasks: Run Task` → `run-mcp-server`
- **Test**: `Tasks: Run Task` → `test`

### Debug Configuration
Launch configurations are pre-configured:
- **Launch MCP Server**: Debug the Spring Boot application
- **Launch MCP Server (JAR)**: Debug the built JAR file

## Available VS Code Files

- `.vscode/mcp.json` - MCP server configuration
- `.vscode/settings.json` - Workspace settings
- `.vscode/tasks.json` - Build and run tasks
- `.vscode/launch.json` - Debug configurations
- `.vscode/extensions.json` - Recommended extensions

## Available AI Agents

1. **ai-engineer** - LLM applications, RAG systems
2. **backend-architect** - API design, microservices
3. **frontend-developer** - React, modern frontend
4. **code-reviewer** - Security, performance analysis
5. **security-auditor** - OWASP compliance, security
6. **java-pro** - Modern Java, Spring Boot
7. **python-pro** - Modern Python development
8. **typescript-pro** - Advanced TypeScript
9. **database-optimizer** - Database performance
10. **debugger** - Error analysis, troubleshooting

## Troubleshooting

### MCP Server Not Loading
1. Check that GitHub Copilot Chat extension is installed and enabled
2. Verify the JAR file exists: `build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar`
3. Rebuild if needed: Run the `build` task
4. Restart VS Code completely

### Agent Commands Not Working
1. Ensure you're using `@workspace` prefix
2. Check Copilot Chat is properly connected
3. Try a simple command first: `@workspace Use get_agents()`

### Build Issues
1. Ensure Java 21+ is installed: `java -version`
2. Run `./gradlew clean build` in terminal
3. Check build output for errors

## Success!

When properly configured, you'll have access to 10+ specialized AI agents directly in VS Code Copilot Chat, giving you Claude Code-style agent functionality in your development environment!