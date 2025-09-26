# VS Code MCP Integration Guide

Setup and troubleshooting guide for integrating the Jenksy MCP Server with VS Code Copilot. This guide covers local development workflows and common troubleshooting scenarios.

## Table of Contents

1. [Quick Setup](#quick-setup)
2. [Local Development Configuration](#local-development-configuration)
3. [Basic Configuration](#basic-configuration)
4. [Usage Patterns](#usage-patterns)
5. [Troubleshooting](#troubleshooting)
6. [Configuration Options](#configuration-options)
7. [Monitoring and Debugging](#monitoring-and-debugging)
8. [Best Practices](#best-practices)

## Quick Setup

### Prerequisites

- **VS Code**: Latest version recommended
- **Java 21+**: Required for the MCP server
- **GitHub Copilot**: Active subscription with Chat extension

### Installation Steps

**1. Install Required Extensions**:
```bash
# Install via VS Code CLI
code --install-extension GitHub.copilot
code --install-extension GitHub.copilot-chat

# Or install via VS Code Extensions marketplace:
# - GitHub Copilot (required)
# - GitHub Copilot Chat (required)
```

**2. Build and Start MCP Server**:
```bash
# Navigate to project directory
cd /path/to/jenksy-mcp

# Build the project
./gradlew clean build

# Start the server
./gradlew bootRun
```

**3. Configure MCP Server**:
```bash
# Option 1: Using VS Code CLI (Recommended)
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","'$(pwd)'/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"]}'

# Option 2: Manual configuration (see below)
```

### Manual Configuration

Add to VS Code User Settings (`settings.json`):
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-Xms128m",
        "-Xmx512m",
        "-XX:+UseG1GC",
        "-jar",
        "/absolute/path/to/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Important**: Replace `/absolute/path/to/jenksy-mcp/` with your actual project path.

## Local Development Configuration

### Development Workflow Integration

**Optimal Development Setup**:
```bash
# Terminal 1: Start MCP server with monitoring
./gradlew bootRun

# Terminal 2: Monitor server status
watch -n 5 'curl -s http://localhost:8080/actuator/health'

# Terminal 3: Development work
# VS Code with Copilot integration
```

### Project-Specific Configuration

**Workspace Settings** (`.vscode/settings.json`):
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-Xms128m",
        "-Xmx512m",
        "-XX:+UseG1GC",
        "-jar",
        "${workspaceFolder}/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  },
  "github.copilot.chat.welcomeMessage": "Welcome! You have access to 20 specialized AI agents. Try asking: 'Which agents can help with backend development?'"
}
```

### Environment-Specific Configurations

**Development Environment**:
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents-dev": {
      "command": "java",
      "args": [
        "-Xms128m",
        "-Xmx256m",
        "-Dspring.profiles.active=development",
        "-Djenksy.mcp.agents.hot-reload=true",
        "-jar",
        "${workspaceFolder}/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Production Testing Environment**:
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents-prod": {
      "command": "java",
      "args": [
        "-Xms256m",
        "-Xmx1g",
        "-XX:+UseG1GC",
        "-Dspring.profiles.active=production",
        "-jar",
        "${workspaceFolder}/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Basic Configuration

### Optional JVM Configuration

**Optional JVM Arguments for VS Code Integration**:
```json
{
  "args": [
    "-Xms128m",                           // Initial heap size
    "-Xmx512m",                           // Maximum heap size
    "-jar", "path/to/jar"
  ]
}
```

### Resource Monitoring

**Monitor Performance During Development**:
```bash
# Check startup performance
curl http://localhost:8080/actuator/local-dev | jq .startup

# Monitor memory usage
curl http://localhost:8080/actuator/local-dev | jq .memory

# Real-time monitoring
watch -n 5 'curl -s http://localhost:8080/api/dashboard/status | jq "{health: .health, memory: .memory.usedPercentage, agents: .agentCount}"'
```

### VS Code Performance Tips

1. **Keep Server Running**: Avoid frequent MCP server restarts
2. **Monitor Resource Usage**: Use dashboard to track performance
3. **Optimize Queries**: Use specific agent requests instead of general queries
4. **Cache Utilization**: Let the server cache agent responses

## Usage Patterns

### Natural Language Agent Invocation

**VS Code Copilot automatically translates natural language into MCP tool calls:**

**Agent Discovery**:
```
"Show me all available AI agents"
"What agents can help with backend development?"
"List agents related to security"
"Find agents for database optimization"
```

**Agent-Specific Requests**:
```
"Please use the ai-engineer agent to help design a RAG system for document search in a Spring Boot application"

"Can the security-auditor agent review this authentication implementation for vulnerabilities?"

"Use the backend-architect agent to design a microservices architecture for an e-commerce platform"

"Have the database-optimizer agent suggest improvements for these slow PostgreSQL queries"
```

**Multi-Agent Workflows**:
```
"Which agents would be best for building a secure AI-powered web application?"

"Get recommendations from the requirements-analyst agent for breaking down this epic into user stories"

"Use the code-reviewer agent to analyze this code for performance issues"
```

### Advanced Usage Patterns

**Context-Rich Requests**:
```
"Please use the ai-engineer agent to help design a production RAG system with the following requirements:
- 10M+ documents
- Sub-second query response
- Spring Boot backend
- PostgreSQL + Redis architecture
- Support for multiple file formats"
```

**Iterative Development**:
```
1. "Which agents can help with API design?"
2. "Use the backend-architect agent to design a RESTful API for user management"
3. "Now use the security-auditor agent to review the proposed authentication approach"
4. "Finally, use the code-reviewer agent to validate the implementation approach"
```

## Troubleshooting

### Common Connection Issues

**1. MCP Server Not Found**:
```bash
# Verify server is running
curl http://localhost:8080/actuator/health

# Check server process
ps aux | grep java | grep jenksy-mcp

# Restart server if needed
./gradlew bootRun
```

**2. Invalid Configuration**:
```bash
# Validate VS Code settings
code --list-extensions | grep copilot

# Check MCP configuration
cat ~/.vscode-server/data/User/settings.json | jq '.["github.copilot.chat.mcpServers"]'

# Test configuration manually
java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar --help
```

**3. Java Version Issues**:
```bash
# Check Java version
java -version

# Verify Java 21+ is installed
java --version | grep -E "(21|[2-9][0-9])"

# Update JAVA_HOME if needed
export JAVA_HOME=/path/to/java21
```

### Performance Issues

**1. Slow Response Times**:
```bash
# Check server performance
curl http://localhost:8080/actuator/local-dev | jq .startup

# Monitor memory usage
curl http://localhost:8080/api/dashboard/status | jq .memory

# Check for memory pressure
curl http://localhost:8080/api/dashboard/metrics | jq .system.freeMemory
```

**2. High Memory Usage**:
```bash
# Reduce heap size in configuration
# Change -Xmx512m to -Xmx256m

# Clear caches
curl -X POST http://localhost:8080/api/dashboard/cache/clear

# Trigger garbage collection
curl -X POST http://localhost:8080/api/dashboard/gc
```

### Agent-Specific Issues

**1. Agent Not Found**:
```
# In VS Code Copilot Chat:
"Show me all available agents"

# Verify specific agent exists:
"Tell me about the ai-engineer agent"
```

**2. Agent Response Issues**:
```bash
# Test agent directly via API
curl -X POST http://localhost:8080/api/dashboard/agents/test \
  -H "Content-Type: application/json" \
  -d '{"agentName":"ai-engineer","task":"Test task"}'

# Check agent loading logs
grep "Loaded.*agents" logs/application.log
```

### VS Code Integration Issues

**1. Copilot Chat Not Recognizing MCP**:
```bash
# Restart VS Code
# Ensure GitHub Copilot Chat extension is enabled
# Verify MCP server configuration in settings.json
# Check VS Code developer console for errors
```

**2. Extension Conflicts**:
```bash
# Disable other AI extensions temporarily
# Check extension logs in VS Code
# Ensure only GitHub Copilot extensions are active for MCP
```

## Advanced Configuration

### Multi-Environment Setup

**Development and Production Configurations**:
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-dev": {
      "command": "java",
      "args": [
        "-Xms128m", "-Xmx256m",
        "-Dspring.profiles.active=development",
        "-jar", "${workspaceFolder}/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    },
    "jenksy-prod": {
      "command": "java",
      "args": [
        "-Xms256m", "-Xmx1g",
        "-Dspring.profiles.active=production",
        "-jar", "/path/to/production/jenksy-mcp.jar"
      ]
    }
  }
}
```

### Custom Agent Development Workflow

**Hot Reload Configuration for Agent Development**:
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents-dev": {
      "command": "java",
      "args": [
        "-Dspring.profiles.active=development",
        "-Djenksy.mcp.agents.hot-reload=true",
        "-jar", "${workspaceFolder}/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### Debug Configuration

**Enable Debug Logging**:
```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-debug": {
      "command": "java",
      "args": [
        "-Dlogging.level.com.jenksy.jenksymcp=DEBUG",
        "-Dspring.profiles.active=development",
        "-jar", "${workspaceFolder}/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Monitoring and Debugging

### Real-time Monitoring Setup

**Dashboard Monitoring**:
```bash
# Open monitoring dashboard
open http://localhost:8080/dashboard

# Monitor via command line
curl -s http://localhost:8080/api/dashboard/status | jq .
```

**VS Code Integration Monitoring**:
1. Open VS Code Developer Tools (`Help > Toggle Developer Tools`)
2. Monitor Console for MCP-related messages
3. Check Network tab for MCP communication
4. Review GitHub Copilot extension logs

### Log Analysis

**Server Logs**:
```bash
# Monitor server logs
tail -f logs/application.log

# Filter for MCP-specific logs
grep -i "mcp\|tool\|agent" logs/application.log

# Monitor agent invocations
grep "Invoking agent" logs/application.log
```

**VS Code Logs**:
```bash
# VS Code extension logs (macOS)
tail -f ~/Library/Application\ Support/Code/logs/*/exthost*/exthost.log

# Filter for Copilot-related logs
grep -i "copilot\|mcp" ~/Library/Application\ Support/Code/logs/*/exthost*/exthost.log
```

### Performance Profiling

**MCP Performance Analysis**:
```bash
# Analyze agent response times
curl -s http://localhost:8080/api/dashboard/metrics | jq .performance

# Monitor memory trends
watch -n 10 'curl -s http://localhost:8080/actuator/local-dev | jq .memory'

# Profile agent loading
grep "Agent.*loaded in" logs/application.log
```

## Best Practices

### Development Workflow

1. **Keep Server Running**: Start MCP server once and keep it running during development
2. **Monitor Performance**: Use dashboard to track resource usage and performance
3. **Use Specific Requests**: Request specific agents rather than general queries
4. **Leverage Caching**: Let the server cache responses for better performance
5. **Regular Health Checks**: Verify server health periodically

### Agent Interaction

1. **Provide Context**: Include relevant context in agent requests
2. **Use Progressive Refinement**: Start broad, then narrow down to specific needs
3. **Combine Agents**: Use multiple agents for complex tasks
4. **Validate Responses**: Test agent recommendations before implementation
5. **Iterate and Improve**: Refine requests based on response quality

### Performance Optimization

1. **Resource Monitoring**: Keep an eye on memory and CPU usage
2. **Appropriate Heap Sizing**: Configure JVM heap based on your system
3. **Profile Regularly**: Use monitoring tools to identify bottlenecks
4. **Clean Builds**: Occasionally rebuild the server for optimal performance
5. **Update Dependencies**: Keep Java and VS Code extensions updated

### Troubleshooting Strategy

1. **Systematic Diagnosis**: Check server → configuration → VS Code → extensions
2. **Use Monitoring Tools**: Leverage dashboard and logs for insights
3. **Test Incrementally**: Isolate issues by testing components separately
4. **Document Solutions**: Keep track of fixes for future reference
5. **Community Support**: Use GitHub issues for unresolved problems

---

This comprehensive guide should enable seamless integration of the Jenksy MCP Server with VS Code Copilot for local development workflows. The optimizations and troubleshooting strategies ensure reliable performance and developer productivity.

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