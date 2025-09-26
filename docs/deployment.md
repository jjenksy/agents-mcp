# Local Setup Guide

This guide covers setting up and running the Jenksy MCP server locally for development and personal use with AI tools like VS Code Copilot and Claude Desktop.

## Overview

The Jenksy MCP server is a **local development tool** that provides specialized AI agents for your development workflow. It runs on your local machine and integrates with AI tools through the Model Context Protocol (MCP).

**Important**: This is designed for single-user local development, not production deployment.

## Quick Start

### Prerequisites

- Java 21 or later
- Git (for development)
- VS Code with compatible MCP extensions (optional)
- Claude Desktop (optional)

### 1. Build the Application

```bash
# Clone the repository (if not already done)
git clone <repository-url>
cd jenksy-mcp

# Build the application
./gradlew clean build

# Verify build artifacts
ls -la build/libs/
# Should show: jenksy-mcp-0.0.1-SNAPSHOT.jar
```

### 2. Run Locally

```bash
# Run directly with Gradle (for development)
./gradlew bootRun

# Or run the built JAR
java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

The server will start on `localhost:8080` and output available MCP tools.

### 3. Test the Server

```bash
# Check if the server is running
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

## Integration with AI Tools

### VS Code Copilot Integration

The MCP server is optimized for VS Code Copilot integration:

1. **Install MCP Extension**: Install a VS Code extension that supports MCP protocol
2. **Configure Extension**: Point the extension to `localhost:8080` or the running MCP server
3. **Use Natural Language**: Interact using natural language like:
   - "Please use the ai-engineer agent to help design a RAG system..."
   - "Which agents can help with database optimization?"
   - "Show me all available agents"

The extension will automatically translate your requests into MCP tool calls.

### Claude Desktop Integration

Configure Claude Desktop to use the MCP server:

1. **Locate Configuration**: Find your Claude Desktop config file:
   - macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`
   - Windows: `%APPDATA%\Claude\claude_desktop_config.json`
   - Linux: `~/.config/claude/claude_desktop_config.json`

2. **Add MCP Server**: Add this configuration:

```json
{
  "mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Important**: Replace `/absolute/path/to/jenksy-mcp` with your actual project path.

3. **Restart Claude Desktop**: Close and restart Claude Desktop to load the MCP server.

## Available Features

### MCP Tools (5 Total)

Once configured, you'll have access to these tools:

- **get_agents**: List all 20 specialized AI agents
- **find_agents**: Search agents by domain (e.g., "backend", "security", "AI")
- **get_agent_info**: Get detailed agent capabilities and descriptions
- **invoke_agent**: Get specialized agent context and guidance for tasks
- **get_recommended_agents**: Get 1-3 best agent recommendations for specific tasks

### Specialized Agents (20 Total)

The server includes 20 domain-specific agents covering:

- **Architecture**: ai-engineer, backend-architect, frontend-developer
- **Programming**: python-expert, javascript-ninja, go-developer, rust-expert
- **AI/ML**: ml-engineer, data-scientist
- **Security**: security-auditor, devops-engineer
- **Tools**: code-reviewer, debugger, performance-optimizer
- **And more**: database-architect, mobile-developer, etc.

## Local Configuration

### Custom Port

Run on a different port if 8080 is busy:

```bash
# Using Gradle
./gradlew bootRun --args='--server.port=8090'

# Using JAR
java -jar -Dserver.port=8090 build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

### Development Mode

For development with auto-restart:

```bash
# Enable dev tools (already included in dependencies)
./gradlew bootRun

# Files will auto-reload when changed
```

### Logging Configuration

Create `src/main/resources/application-local.yml` for custom logging:

```yaml
logging:
  level:
    com.jenksy.jenksymcp: DEBUG
    root: INFO
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

server:
  port: 8080
```

Run with local profile:
```bash
java -jar -Dspring.profiles.active=local build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

## Performance for Local Use

### Memory Settings

For local development, default settings are usually sufficient:

```bash
# Light usage (default)
java -jar jenksy-mcp.jar

# Heavy usage with multiple agents
java -jar -Xms256m -Xmx512m jenksy-mcp.jar
```

### Agent Caching

The server uses Caffeine cache for better performance:
- **5-minute expiration** for agent responses
- **1000 max entries** for agent data
- **Automatic cleanup** of old contexts

No configuration needed - works out of the box.

## Local Troubleshooting

### Common Issues

**1. Server Won't Start**
```bash
# Check if port 8080 is in use
lsof -i :8080
# or
netstat -an | grep 8080

# Kill process using port 8080
kill -9 <PID>

# Or use a different port
java -jar -Dserver.port=8090 jenksy-mcp.jar
```

**2. Java Version Issues**
```bash
# Check Java version
java -version
# Must be Java 21 or later

# On macOS with SDKMAN
sdk list java
sdk install java 21.0.1-oracle
sdk use java 21.0.1-oracle
```

**3. Build Issues**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Check for permission issues
chmod +x gradlew
```

**4. MCP Connection Issues**

For Claude Desktop:
- Verify the JAR path is absolute in the config
- Check Claude Desktop logs for connection errors
- Restart Claude Desktop after config changes

For VS Code:
- Check extension settings point to correct localhost port
- Verify the MCP server is running (`curl localhost:8080/actuator/health`)
- Check VS Code extension logs

### Health Checks

```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health info
curl http://localhost:8080/actuator/health | jq

# Check available MCP tools
curl http://localhost:8080/actuator/info
```

### Log Analysis

```bash
# View recent logs when running with JAR
java -jar jenksy-mcp.jar 2>&1 | tail -f

# Search for specific errors
grep -i error application.log

# Check startup time
grep "Started JenksyMcpApplication" application.log
```

### Performance Monitoring

```bash
# Monitor local resource usage
top -p $(pgrep java)

# Check memory usage
ps aux | grep java

# Monitor network connections
netstat -an | grep :8080
```

## Development Workflow

### Making Changes

1. **Modify Code**: Edit source files in `src/`
2. **Test Locally**: Run `./gradlew bootRun` for quick testing
3. **Build**: Run `./gradlew build` to create new JAR
4. **Update AI Tools**: Restart Claude Desktop or VS Code to use updated JAR

### Adding New Agents

1. **Create Agent File**: Add new `.md` file in `src/main/resources/agents/`
2. **Use Template**:
   ```yaml
   ---
   name: my-new-agent
   description: Description of what this agent does
   tools: optional,tool,list
   ---
   # Agent system prompt content here
   ```
3. **Rebuild**: Run `./gradlew build`
4. **Test**: Restart server and test with `get_agents` tool

### Local Testing

```bash
# Test with curl (if you have tools that accept HTTP)
curl -X POST localhost:8080/mcp/call \
  -H "Content-Type: application/json" \
  -d '{"method": "get_agents"}'

# Test agent loading
grep "Loaded.*agents" logs/application.log
```

## Best Practices for Local Use

1. **Keep It Simple**: Use default settings unless you need specific configuration
2. **Regular Updates**: Rebuild after making changes to agents or code
3. **Monitor Resources**: The server is lightweight but monitor if running many agents
4. **Backup Configs**: Keep your Claude Desktop config backed up
5. **Use Version Control**: Keep your agent modifications in git

## Getting Help

If you encounter issues:

1. **Check Logs**: Look for error messages in console output
2. **Verify Java**: Ensure Java 21+ is installed and in PATH
3. **Test Connectivity**: Use curl to verify server is responding
4. **Restart Everything**: Sometimes a fresh start resolves connection issues
5. **Check Paths**: Ensure all file paths are absolute and correct

This guide focuses on local development use. The Jenksy MCP server is not intended for production deployment or multi-user environments.