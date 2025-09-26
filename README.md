# AI Agents MCP Server

**Bring Claude Code's powerful agent system to any MCP-compatible tool.** Access 20 specialized AI agents through VS Code Copilot, Claude Desktop, and other Model Context Protocol applications.

[![Release](https://img.shields.io/github/v/release/jjenksy/agents-mcp)](https://github.com/jjenksy/agents-mcp/releases)
[![Java](https://img.shields.io/badge/Java-21+-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

## Quick Start

**Install with one command:**

```bash
curl -sSL https://github.com/jjenksy/agents-mcp/raw/main/scripts/install.sh | bash
```

**Use with VS Code:**

```bash
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp.jar"]}'
```

**Test it works:**

```
Please list all available AI agents
```

> **How it works**: VS Code Copilot automatically translates your natural language request into MCP tool calls behind the scenes

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
curl -sSL https://github.com/jjenksy/agents-mcp/raw/main/scripts/install.sh | bash
```

The script will:

- Download the latest JAR from GitHub releases
- Install to `~/.jenksy-mcp/jenksy-mcp.jar`
- Optionally configure VS Code automatically

### Option 2: Manual Installation

**Download the latest release:**

```bash
# Download latest JAR from GitHub releases
curl -L https://github.com/jjenksy/agents-mcp/releases/latest/download/jenksy-mcp-latest.jar -o ~/.jenksy-mcp.jar

# Configure VS Code
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp.jar"]}'
```

### Option 3: Build from Source

**For development or custom builds:**

```bash
# Clone and build
git clone https://github.com/jjenksy/agents-mcp.git
cd agents-mcp
./gradlew clean build

# Copy JAR to standard location
mkdir -p ~/.jenksy-mcp
cp build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar ~/.jenksy-mcp/jenksy-mcp.jar

# Configure VS Code
code --add-mcp '{"name":"jenksy-agents","command":"java","args":["-jar","~/.jenksy-mcp/jenksy-mcp.jar"]}'
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
      "args": ["-jar", "/path/to/jenksy-mcp.jar"]
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
      "args": ["-jar", "/path/to/jenksy-mcp.jar"]
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

**Ask naturally in VS Code Copilot:**
```
"Show me all available agents"
"What agents are available for [your-domain]?"
"List agents related to backend development"
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

- **Issues**: [GitHub Issues](https://github.com/jjenksy/agents-mcp/issues)
- **Discussions**: [GitHub Discussions](https://github.com/jjenksy/agents-mcp/discussions)
- **Documentation**: [docs/](docs/)

---

**Transform your development workflow with specialized AI agents!**
