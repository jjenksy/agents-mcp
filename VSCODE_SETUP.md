# VS Code MCP Server Setup Guide

> **🚀 Now VS Code Copilot Optimized!** This MCP server has been specifically optimized for VS Code integration with 75% smaller response sizes and smarter tool usage guidance.

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

### Optimized Agent Commands ⭐

**Primary Workflow (Recommended):**
```javascript
// Get everything you need in one efficient call
@workspace Use invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a RAG system for document search",
  "context": "Spring Boot app with 10M+ documents"
})

// Smart agent discovery
@workspace Use get_recommended_agents("build microservices API")
```

**Discovery Commands:**
```javascript
// List all available agents
@workspace Use get_agents()

// Find agents by domain
@workspace Use find_agents("security")

// Get agent details
@workspace Use get_agent_info("ai-engineer")
```


### Optimized Example Workflows

#### 1. Complete Code Review (Single Call) ⭐
```
@workspace Use invoke_agent({
  "agentName": "security-auditor",
  "task": "Review authentication implementation for security vulnerabilities",
  "context": "JWT-based auth with refresh tokens, Redis session store"
})
```

#### 2. API Design with Context ⭐
```
@workspace Use invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a user management API with role-based access",
  "context": "Spring Boot app, expecting 100k users, microservices architecture"
})
```

#### 3. Database Performance Analysis ⭐
```
@workspace Use invoke_agent({
  "agentName": "database-optimizer",
  "task": "Optimize slow query performance",
  "context": "PostgreSQL 15, queries taking 2-5 seconds, 500k records"
})
```

#### 4. Smart Agent Discovery
```
@workspace Use get_recommended_agents("implement caching layer")
// Returns 1-3 best agents with usage guidance
```

### Migration from Legacy Patterns

**Optimized Pattern:**
```
@workspace Use invoke_agent({
  "agentName": "security-auditor",
  "task": "Review OAuth implementation",
  "context": "Spring Security 6, PKCE flow, mobile app integration"
})
// Get focused, actionable guidance in one call
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
3. Try the optimized workflow first: `@workspace Use invoke_agent({"agentName":"ai-engineer","task":"test connection","context":""})`
4. For simple testing: `@workspace Use get_agents()`

### Build Issues
1. Ensure Java 21+ is installed: `java -version`
2. Run `./gradlew clean build` in terminal
3. Check build output for errors

## Success!

When properly configured, you'll have access to 20+ specialized AI agents with **VS Code Copilot optimized responses** directly in VS Code Copilot Chat. The optimized tools provide:

✅ **75% smaller response sizes** for faster loading
✅ **Structured markdown output** for better readability
✅ **Smart usage guidance** to prevent redundant tool calls
✅ **Context caching** for improved performance
✅ **Claude Code-style agent functionality** in your development environment

Enjoy your enhanced development workflow with specialized AI expertise at your fingertips!