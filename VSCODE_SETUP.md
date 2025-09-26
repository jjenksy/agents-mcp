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

### How to Use Agents ⭐

**Important:** You interact with agents using natural language! VS Code Copilot automatically translates your requests into MCP tool calls behind the scenes.

**Primary Workflow (Recommended):**

```
"Please use the ai-engineer agent to design a RAG system for document search in a Spring Boot app with 10M+ documents"
```

**What happens:**
1. Copilot understands your natural language request
2. It automatically calls the appropriate MCP tool (`invoke_agent`)
3. The agent's expertise guides the response
4. You get specialized advice without knowing tool syntax

**Agent Discovery:**

```
"Which agents can help me build microservices APIs?"
"Show me all available AI agents"
"What agents specialize in security?"
"Tell me about the ai-engineer agent"
```


### Real-World Example Workflows

#### 1. Complete Code Review ⭐

**Just ask naturally:**
```
"I need the security-auditor agent to review my authentication implementation for security vulnerabilities. It uses JWT-based auth with refresh tokens and Redis for session storage."
```

#### 2. API Design with Context ⭐

```
"Please use the backend-architect agent to design a user management API with role-based access control for a Spring Boot application expecting 100k users in a microservices architecture"
```

#### 3. Database Performance Analysis ⭐

```
"Can the database-optimizer agent help me optimize slow query performance? I'm using PostgreSQL 15, and my queries are taking 2-5 seconds with 500k records."
```

#### 4. Smart Agent Discovery

```
"Which agents can help me implement a caching layer?"
"What agents are best for microservices architecture?"
"Show me agents that specialize in performance optimization"
```

### Natural Language is the Way

**How to request agent help:**
```
"Use the security-auditor agent to review my OAuth implementation. It's using Spring Security 6 with PKCE flow for mobile app integration."
```

**Why this works better:**
- More natural and intuitive
- VS Code Copilot handles the technical details
- You focus on describing your needs, not learning tool syntax
- The agent still provides focused, actionable guidance

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
1. Remember: Use natural language, not direct tool calls
2. Check Copilot Chat is properly connected
3. Try a simple request first: "Show me all available AI agents"
4. Then try: "Use the ai-engineer agent to help me test the connection"

### Build Issues
1. Ensure Java 21+ is installed: `java -version`
2. Run `./gradlew clean build` in terminal
3. Check build output for errors

## Success!

When properly configured, you'll have access to 20+ specialized AI agents through natural language in VS Code Copilot Chat. The experience provides:

✅ **Natural language interaction** - Just describe what you need
✅ **Automatic tool translation** - Copilot handles MCP calls for you
✅ **75% smaller responses** - Optimized for faster loading
✅ **Structured expert guidance** - Clear, actionable recommendations
✅ **Context-aware responses** - Agents understand your specific situation
✅ **Claude Code-style functionality** - Professional AI expertise in VS Code

**Remember:** You don't need to learn tool syntax - just ask for help naturally, and VS Code Copilot will engage the right agent with the right context!

Enjoy your enhanced development workflow with specialized AI expertise at your fingertips!