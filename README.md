# AI Agents MCP Server

**Bring Claude Code's powerful agent system to any MCP-compatible tool.** Access 20 specialized AI agents through VS Code Copilot, Claude Desktop, and other Model Context Protocol applications.

[![Release](https://img.shields.io/github/v/release/jenksy/agents-mcp)](https://github.com/jenksy/agents-mcp/releases)
[![Java](https://img.shields.io/badge/Java-21+-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

## Quick Start

**Install with one command:**

```bash
curl -sSL https://github.com/jenksy/agents-mcp/raw/main/scripts/install.sh | bash
```

**Use with VS Code:**

```bash
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp.jar"]}'
```

**Test it works:**

```
@workspace Use get_agents()
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

- **20 Specialized Agents** - Domain experts for every development need
- **VS Code Copilot Optimized** - 75% smaller responses, smarter tool guidance
- **MCP Protocol** - Works with VS Code Copilot, Claude Desktop, and more
- **Production Ready** - Caching, monitoring, automated deployment
- **Easy Installation** - One-line install script with VS Code integration
- **Extensible** - Add custom agents with simple markdown files

## Installation Options

### Option 1: Install Script (Recommended)

```bash
# Download and run installer
curl -sSL https://github.com/jenksy/agents-mcp/raw/main/scripts/install.sh | bash
```

The script will:
- Download the latest JAR from GitHub releases
- Install to `~/.jenksy-mcp/jenksy-mcp.jar`
- Optionally configure VS Code automatically

### Option 2: Manual Installation

**Once a release is available:**

```bash
# Download latest JAR (after first release is created)
curl -L https://github.com/jenksy/agents-mcp/releases/latest/download/jenksy-mcp-latest.jar -o ~/.jenksy-mcp.jar

# Configure VS Code
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp.jar"]}'
```

**For now, build from source:**

```bash
# Clone and build
git clone https://github.com/jenksy/agents-mcp.git
cd agents-mcp
./gradlew clean build

# Copy JAR to standard location
mkdir -p ~/.jenksy-mcp
cp build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar ~/.jenksy-mcp/jenksy-mcp.jar

# Configure VS Code
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp/jenksy-mcp.jar"]}'
```

## Usage Examples

### Optimized Single-Call Workflow ⭐

```javascript
// Get specialized guidance in one efficient call
@workspace Use invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a user management API",
  "context": "Spring Boot app with JWT auth, expecting 100k users"
})
```

### Smart Agent Discovery

```javascript
// Get targeted recommendations (1-3 agents)
@workspace Use get_recommended_agents("optimize database performance")

// Search by domain when you need broader options
@workspace Use find_agents("security")
```

### Migration from Legacy Usage

```javascript
// ✅ NEW: Get everything in one optimized call
@workspace Use invoke_agent({
  "agentName": "ai-engineer",
  "task": "Implement vector search",
  "context": "Existing Spring Boot REST API"
})

// ❌ OLD: Multiple calls (less efficient)
// Use invoke_agent instead for better results
```

## VS Code Integration

VS Code Copilot requires global MCP server configuration. Use one of these methods:

### VS Code CLI (Recommended)

```bash
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp.jar"]}'
```

### Manual Global Configuration

1. Open VS Code Command Palette (`Cmd+Shift+P` on Mac, `Ctrl+Shift+P` on Windows/Linux)
2. Run: `Preferences: Open User Settings (JSON)`
3. Add the MCP server configuration:

```json
{
  "github.copilot.chat.mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/jenksy-mcp.jar"
      ]
    }
  }
}
```

**Note:** Replace `/path/to/jenksy-mcp.jar` with your actual JAR path. After adding, restart VS Code.

## Claude Desktop Integration

Add to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/jenksy-mcp.jar"
      ]
    }
  }
}
```

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

## MCP Tools Reference

> **🚀 VS Code Copilot Optimized**: These tools have been optimized specifically for VS Code integration with reduced response sizes and smarter usage guidance.

### Primary Tools (Optimized Workflow)

#### `invoke_agent(invocation)` ⭐ **Recommended**
Get concise, task-specific guidance from specialized agents. **75% smaller responses** optimized for VS Code Copilot consumption.

**Optimized Response Format:**
- Structured markdown for better readability
- Focused recommendations (3-4 actionable steps)
- Expert context included inline
- Automatic context caching

**Example:**
```javascript
@workspace Use invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a RESTful API for user management",
  "context": "Spring Boot application with JWT authentication"
})
```

#### `get_recommended_agents(task)`
Get 1-3 best agents for your task with usage guidance. More efficient than browsing all agents.

**Example:**
```javascript
@workspace Use get_recommended_agents("optimize database performance")
```

### Discovery Tools

#### `get_agents()`
List all available agents. Use `find_agents` to search by domain, or `invoke_agent` for task-specific guidance.

#### `find_agents(query)`
Search agents by domain keywords. Use `invoke_agent` after finding the right agent for task-specific guidance.

**Examples:**
```javascript
@workspace Use find_agents("backend")      // Find backend-related agents
@workspace Use find_agents("security")     // Find security specialists
```

#### `get_agent_info(agentName)`
Get agent capabilities and description. Use `invoke_agent` for actionable task guidance instead of just information.

**Example:**
```javascript
@workspace Use get_agent_info("ai-engineer")
```


### Optimized Usage Patterns

**Best Practice - Single Tool Call:**
```javascript
// ✅ Optimized: Get everything you need in one call
@workspace Use invoke_agent({
  "agentName": "ai-engineer",
  "task": "Build a RAG system for document search",
  "context": "Spring Boot app with 10M+ documents"
})
```

**Avoid - Multiple Tool Calls:**
```javascript
// ❌ Inefficient: Multiple calls for the same information
@workspace Use get_agent_info("ai-engineer")
@workspace Use invoke_agent({...})
```

## Development

### Requirements

- Java 21+
- Spring Boot 3.5.5
- Gradle (wrapper included)

### Running in Development

```bash
./gradlew bootRun
```

### Running Tests

```bash
./gradlew test
```

### Creating a Release

The project uses automated GitHub Actions for releases:

```bash
# Create and push a version tag to trigger release
git tag v1.0.0
git push origin v1.0.0
```

This will:
- Build the JAR automatically
- Create a GitHub release with downloadable assets
- Generate checksums for security verification
- Update release notes automatically

**Note**: The release workflow only runs on version tags (like `v1.0.0`), not regular pushes to main.

### Manual Build

```bash
./gradlew clean build
# JAR created at: build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

## Documentation

- **[Contributing Guide](CONTRIBUTING.md)** - How to contribute agents and code
- **[Agent Creation Guide](docs/creating-agents.md)** - Create custom agents
- **[Deployment Guide](docs/deployment.md)** - Production deployment
- **[Troubleshooting](docs/troubleshooting.md)** - Common issues and solutions
- **[API Reference](docs/api-reference.md)** - Complete MCP tools documentation

## Troubleshooting

### Agent Not Found
```bash
# List all available agents
get_agents()

# Search for similar agents
find_agents("your-domain")
```

### VS Code Not Connecting
1. Ensure GitHub Copilot Chat extension is installed
2. Verify JAR path in configuration is correct
3. Restart VS Code after configuration changes
4. Check that Java 21+ is installed

### Performance Issues
The server includes built-in caching and monitoring. Check application logs for performance metrics.

## Contributing

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on:

- Adding new agents
- Improving existing agents
- Contributing code
- Reporting issues

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/jenksy/agents-mcp/issues)
- **Discussions**: [GitHub Discussions](https://github.com/jenksy/agents-mcp/discussions)
- **Documentation**: [docs/](docs/)

---

**Transform your development workflow with specialized AI agents!**