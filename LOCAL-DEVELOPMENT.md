# Local Development Guide

This document describes how to set up and use the Jenksy MCP Server for local development.

## Quick Start

Use standard Spring Boot commands for local development:

```bash
./gradlew bootRun
```

Then access the dashboard at: http://localhost:8080/dashboard

## Basic Configuration

The server runs with standard Spring Boot defaults and includes:
- Standard Spring Boot startup
- Parallel agent loading for faster initialization
- Basic Caffeine caching for agent responses
- Simple dashboard for monitoring and agent testing

### Default Settings

```yaml
server:
  port: 8080

spring:
  cache:
    caffeine:
      spec: maximumSize=1000,expireAfterAccess=5m

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,cache
```

### Custom Configuration

Create `application-local.yml` for personal settings:

```yaml
spring:
  profiles:
    active: local

logging:
  level:
    com.jenksy.jenksymcp: DEBUG     # Detailed logging
```

## Development Workflow

### 1. Standard Development Cycle

```bash
# Make code changes
./gradlew build -x test  # Fast build without tests

# Restart server
./gradlew bootRun
```

### 2. Agent Development

When adding new agents to `src/main/resources/agents/`:
1. Use descriptive kebab-case filenames (e.g., `security-auditor.md`)
2. Include comprehensive YAML frontmatter with accurate description
3. Write detailed system prompts focused on specific domain expertise
4. Test agent loading by running the application and checking logs

### 3. Testing

```bash
# Run all tests
./gradlew test

# Build and test
./gradlew clean build
```

## Monitoring and Health Checks

### Health Monitoring

Check application health:

```bash
curl http://localhost:8080/actuator/health
```

### System Metrics

View basic system metrics:

```bash
curl http://localhost:8080/actuator/metrics
```

### Dashboard

Access the simple web dashboard for:
- Server status and basic metrics
- Agent browsing and testing
- Cache management
- System information

**Dashboard URL**: http://localhost:8080/dashboard

## Integration with VS Code

### Basic Configuration

Update your VS Code Copilot configuration:

```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### Development Tips

1. **Keep Server Running**: Avoid frequent restarts by keeping the MCP server running
2. **Monitor Dashboard**: Use the dashboard to check system status and test agents
3. **Check Logs**: Monitor application logs for agent loading and MCP tool calls
4. **Health Checks**: Use actuator endpoints to verify system health

## Troubleshooting

### Startup Issues

1. **Check Java version**:
   ```bash
   java -version  # Ensure Java 21+
   ```

2. **Verify build**:
   ```bash
   ./gradlew clean build
   ```

3. **Check logs** for agent loading errors

### VS Code Integration Issues

1. **Verify server is running**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Check VS Code configuration**:
   - Ensure GitHub Copilot Chat extension is installed
   - Verify JAR path is correct and absolute
   - Restart VS Code after configuration changes

3. **Test MCP connection**:
   ```
   # In VS Code Copilot
   "Please list all available AI agents"
   ```

### Agent Loading Problems

1. **Check agent files**:
   ```bash
   ls -la src/main/resources/agents/
   ```

2. **Verify agent format** - ensure YAML frontmatter is valid

3. **Monitor agent loading** in application logs

## Development Best Practices

### Local Development Workflow

1. **Use Standard Commands**: Use `./gradlew bootRun` for consistent startup
2. **Monitor Dashboard**: Keep the dashboard open during development for monitoring
3. **Check Health**: Regularly verify system health via actuator endpoints
4. **Agent Testing**: Use dashboard agent testing before VS Code integration

### Performance Considerations

- The server uses standard Spring Boot performance characteristics
- Parallel agent loading utilizes multi-core developer machines
- Basic caching improves response times for repeated agent queries
- Dashboard uses simple HTTP polling for updates

## Contributing

When contributing to local development features:

1. **Maintain Simplicity**: Keep the local development setup simple and standard
2. **Test Changes**: Verify changes work with both dashboard and VS Code integration
3. **Document Updates**: Update this guide when adding new features
4. **Follow Standards**: Use standard Spring Boot practices and conventions

---

This local development setup provides a clean, simple experience while maintaining all the core functionality needed for effective AI agent integration.