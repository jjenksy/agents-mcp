# Jenksy MCP Server

**A lightweight Model Context Protocol server for local development workflows.** Provides 20 specialized AI agents optimized for VS Code Copilot integration with a simple monitoring dashboard.

[![Java](https://img.shields.io/badge/Java-21+-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.2-blue)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

## Quick Start

**Prerequisites:**
- Java 21+
- Gradle (included via wrapper)

**1. Clone and Build:**
```bash
git clone <repository-url>
cd jenksy-mcp
./gradlew clean build
```

**2. Start the Server:**
```bash
./gradlew bootRun
```

**3. Configure VS Code Copilot:**
```bash
# Add to VS Code settings.json or use CLI
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","$(pwd)/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"]}'
```

**4. Test Integration:**
```
Please list all available AI agents
```

**5. Access Development Dashboard:**
```
http://localhost:8080/dashboard
```

## What You Get

Instead of manually crafting prompts, invoke specialized agents with domain expertise:

- **ai-engineer** - Production LLM apps, RAG systems, agent orchestration
- **architect-review** - Modern architecture patterns, clean architecture, DDD
- **cloud-architect** - AWS/Azure/GCP infrastructure, IaC, FinOps optimization
- **code-reviewer** - AI-powered code analysis, security vulnerabilities
- **deployment-engineer** - CI/CD pipelines, GitOps workflows, container security
- **ml-engineer** - PyTorch, TensorFlow, model serving, MLOps
- **prompt-engineer** - Advanced prompting techniques, LLM optimization
- **ui-ux-designer** - Design systems, accessibility, user research
- And 12+ more specialized agents covering JavaScript, SQL, Mermaid diagrams, requirements analysis, and more

## Key Features

- **20 Specialized Agents** - Domain experts covering architecture, languages, AI/ML, security, and more
- **Local Development Dashboard** - Basic monitoring, agent testing, and system metrics
- **VS Code Copilot Optimized** - Responses designed for seamless integration
- **Simple Setup** - Standard Spring Boot application with minimal configuration
- **Agent Caching** - Caffeine-based caching for improved response times
- **Health Monitoring** - Spring Boot actuator endpoints for system status

## Local Development Setup

### Development Workflow

**Daily Development Cycle:**
```bash
# 1. Start server
./gradlew bootRun

# 2. Monitor via dashboard
open http://localhost:8080/dashboard

# 3. Make changes to agents or code
# 4. Quick rebuild (development)
./gradlew build -x test

# 5. Restart server
./gradlew bootRun
```

### Basic Monitoring

**Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

**System Metrics:**
```bash
curl http://localhost:8080/actuator/metrics
```

### Configuration

**Custom Configuration:**
Create `application-local.yml`:
```yaml
spring:
  profiles:
    active: local

logging:
  level:
    com.jenksy.jenksymcp: DEBUG     # Detailed logging
```

## VS Code Integration

**Local Development Integration:**

### VS Code Configuration

**Option 1: Use Project Path**
```bash
cd /path/to/jenksy-mcp
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","$(pwd)/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"]}'
```

**Option 2: Manual Configuration**
Add to VS Code `settings.json`:
```json
{
  "github.copilot.chat.mcpServers": {
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

### Verification

**Check MCP Connection:**
```bash
# Ensure server is running
curl http://localhost:8080/actuator/health

# Test in VS Code Copilot
"Show me all available AI agents"
```

### Local Development Tips

1. **Keep Server Running**: Avoid frequent restarts by keeping the MCP server running
2. **Monitor Dashboard**: Use the dashboard to check system status and test agents
3. **Check Logs**: Monitor application logs for agent loading and MCP tool calls
4. **Health Checks**: Use actuator endpoints to verify system health

## Claude Desktop Integration (Optional)

**For Local Development Testing:**
Add to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "jenksy-agents-local": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Note**: This is primarily for testing MCP functionality. VS Code Copilot is the main target for this local development tool.

## Available Agents

### Architecture & Design

- **architect-review** - Modern architecture patterns, clean architecture, microservices, DDD
- **backend-architect** - RESTful APIs, microservices, database schemas
- **cloud-architect** - AWS/Azure/GCP multi-cloud infrastructure, IaC, FinOps optimization
- **ai-engineer** - LLM applications, RAG systems, agent orchestration
- **requirements-analyst** - Transform business requirements into structured tickets with acceptance criteria, dependencies, and implementation details
- **ui-ux-designer** - Design systems, user research, accessibility standards

### DevOps & Infrastructure

- **deployment-engineer** - CI/CD pipelines, GitOps workflows, progressive delivery
- **database-optimizer** - Query optimization, indexing strategies, performance tuning

### AI & Machine Learning

- **ml-engineer** - PyTorch, TensorFlow, model serving, MLOps infrastructure
- **prompt-engineer** - Advanced prompting techniques, LLM optimization

### Quality & Security

- **code-reviewer** - AI-powered code analysis, security vulnerabilities, performance optimization
- **security-auditor** - OWASP compliance, threat modeling, security testing

### Programming Languages

- **java-pro** - Modern Java with Spring Boot, enterprise patterns
- **javascript-pro** - Modern JavaScript, ES6+, async patterns, Node.js APIs
- **python-pro** - Modern Python with async patterns and optimization
- **typescript-pro** - Advanced TypeScript with strict type safety
- **sql-pro** - Modern SQL, cloud-native databases, OLTP/OLAP optimization

### Development Tools

- **frontend-developer** - React components, responsive layouts, state management
- **mermaid-expert** - Diagrams for flowcharts, sequences, ERDs, and architectures
- **debugger** - Error analysis, test failures, unexpected behavior

## How to Use Agents in VS Code Copilot

> **Important**: VS Code Copilot uses natural language! You don't call MCP tools directly - just describe what you need, and Copilot translates your request into the appropriate tool calls.

### Natural Language Usage Examples

#### Getting Agent Help

**Instead of calling tools directly, just ask naturally:**

```
"Please use the backend-architect agent to design a RESTful API for user management with JWT authentication in Spring Boot"
```

**What happens behind the scenes:**
1. VS Code Copilot understands your request
2. It automatically calls the appropriate MCP tool (`invoke_agent`)
3. The agent's specialized guidance is used to formulate the response
4. You get expert advice without needing to know the tool syntax

**More Examples:**

```
"I need the ai-engineer agent to help design a RAG system for document search in a Spring Boot app with 10M+ documents"

"Can the security-auditor agent review this authentication implementation for vulnerabilities?"

"Use the database-optimizer agent to help optimize these slow PostgreSQL queries"
```

### Discovering the Right Agent

**Ask for recommendations naturally:**

```
"Which agents can help me optimize database performance?"

"What agents are available for security auditing?"

"Show me agents that can help with React performance optimization"
```

VS Code Copilot will automatically use the `get_recommended_agents` tool to suggest the best agents for your task.

### Browsing Available Agents

**List all agents:**
```
"Show me all available AI agents"
"What agents do you have?"
"List all the specialized agents"
```

**Search by domain:**
```
"Show me agents related to backend development"
"What security-focused agents are available?"
"Find agents that can help with database optimization"
```

**Get specific agent details:**
```
"Tell me about the ai-engineer agent"
"What can the backend-architect agent do?"
"Describe the security-auditor agent's capabilities"
```

### Best Practices for Natural Language Requests

**✅ Good: Specific, Context-Rich Requests**

```
"Please use the ai-engineer agent to help me build a RAG system for document search in a Spring Boot application with 10M+ documents that needs sub-second response times"
```

This gives VS Code Copilot everything it needs to make an effective MCP tool call with proper context.

**❌ Avoid: Vague Requests**

```
"Help with AI stuff"
"Fix my code"
```

These don't provide enough context for the agents to give useful guidance.

**✅ Good: Progressive Refinement**

```
1. "Which agents can help with database optimization?"
2. "Use the database-optimizer agent to help optimize PostgreSQL queries for a user search feature with 2M records"
3. "Can you focus on indexing strategies for the email and username columns?"
```

## Local Development Guide

### System Requirements

- **Java 21+**
- **Spring Boot 3.5.5**
- **Gradle** (wrapper included)

### Development Commands

**Standard Gradle Commands:**
```bash
./gradlew bootRun                       # Start the server
./gradlew test                          # Run all tests
./gradlew build -x test                 # Fast build without tests
./gradlew clean build                   # Full clean build
```

**Monitoring:**
```bash
# Check health status
curl http://localhost:8080/actuator/health

# Monitor system metrics
curl http://localhost:8080/actuator/metrics

# Access dashboard
open http://localhost:8080/dashboard
```

### Configuration

**Optional JVM Settings:**
```bash
export JAVA_OPTS="-Xms128m -Xmx512m"
./gradlew bootRun
```

### Agent Development

**Adding New Agents:**
1. Create new `.md` file in `src/main/resources/agents/`
2. Follow the YAML frontmatter format:
```yaml
---
name: my-new-agent
description: Agent description
---
# System prompt content here
```
3. Rebuild and restart server
4. Test via dashboard or MCP tools

## Documentation

- **[Contributing Guide](CONTRIBUTING.md)** - How to contribute agents and code
- **[Local Development Guide](LOCAL-DEVELOPMENT.md)** - Detailed local development setup and optimization
- **[Agent Creation Guide](docs/creating-agents.md)** - Create and customize agents
- **[VS Code Setup Guide](VSCODE_SETUP.md)** - Complete VS Code Copilot integration
- **[MCP Troubleshooting](MCP_TROUBLESHOOTING.md)** - Common MCP integration issues
- **[Manual Agent Usage](MANUAL_AGENT_USAGE.md)** - Direct agent usage examples

## Local Development Troubleshooting

### Startup Issues

1. **Check Java version:**
   ```bash
   java -version  # Ensure Java 21+
   ```

2. **Verify build:**
   ```bash
   ./gradlew clean build
   ```

### Performance Issues

1. **Monitor system health:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Check dashboard:**
   ```
   http://localhost:8080/dashboard
   ```

### VS Code MCP Integration Issues

1. **Verify server is running:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Check VS Code configuration:**
   - Ensure GitHub Copilot Chat extension is installed
   - Verify JAR path is correct and absolute
   - Restart VS Code after configuration changes

3. **Test MCP connection:**
   ```
   # In VS Code Copilot
   "Please list all available AI agents"
   ```

### Agent Loading Problems

1. **Check agent files:**
   ```bash
   ls -la src/main/resources/agents/
   ```

2. **Verify agent format:**
   ```bash
   head -10 src/main/resources/agents/ai-engineer.md
   ```

3. **Monitor agent loading:**
   ```bash
   # Look for agent loading logs
   grep "Loaded.*agents" logs/application.log
   ```

## Contributing

We welcome contributions! Focus areas include:

- **Adding Specialized Agents**: Create domain-specific agents for development workflows
- **Dashboard Enhancements**: Improve monitoring and developer experience
- **VS Code Integration**: Improve MCP integration and developer ergonomics
- **Documentation**: Keep documentation accurate and helpful

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## Architecture Overview

This MCP server provides a clean local development experience with:

- **Spring Boot 3.5.5** with standard configuration
- **Java 21** support
- **Caffeine Caching** for agent response optimization
- **Simple Dashboard** with basic monitoring and agent testing
- **Parallel Agent Loading** for faster startup
- **Standard Spring Boot** actuator endpoints for health monitoring

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Enhance your local development workflow with specialized AI agent integration!**
