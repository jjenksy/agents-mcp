# AI Agents MCP Server

A Model Context Protocol (MCP) server that provides access to 10+ specialized AI agents for integration with VS Code Copilot, Claude Desktop, and other MCP-compatible tools.

## 🎯 Overview

This MCP server brings Claude Code's powerful agent system to any MCP-compatible tool. Instead of manually crafting prompts, you can invoke specialized agents like `ai-engineer`, `backend-architect`, `security-auditor`, and many others with domain-specific expertise.

### Key Features

- 🤖 **10+ Specialized Agents**: From `ai-engineer` to `database-optimizer`
- 🔍 **Agent Discovery**: Find agents by capability or domain
- 📋 **Task Execution**: Invoke agents with specific tasks and context
- 🎯 **Smart Recommendations**: Get suggested agents for your specific needs
- 🔧 **MCP Protocol**: Compatible with Claude Desktop, VS Code extensions, and more

## 🚀 Quick Start

### 1. Build the Project

```bash
./gradlew clean build
```

### 2. Agents Included

The project includes 10 specialized agents in the `agents/` directory:
- `ai-engineer` - LLM applications and RAG systems
- `backend-architect` - API design and microservices
- `frontend-developer` - React and modern frontend
- `code-reviewer` - Security and performance analysis
- `security-auditor` - Comprehensive security assessment
- `java-pro` - Modern Java and Spring Boot
- `python-pro` - Modern Python development
- `typescript-pro` - Advanced TypeScript patterns
- `database-optimizer` - Database performance tuning
- `debugger` - Error analysis and troubleshooting

### 3. Configure VS Code Copilot

The project includes a pre-configured `.vscode/mcp.json` file for VS Code. To set up:

1. **Install Required Extensions**:
   - GitHub Copilot
   - GitHub Copilot Chat
   - Java Extension Pack (for development)

2. **Open Workspace in VS Code**: Open this project folder in VS Code

3. **Install Recommended Extensions**: VS Code will prompt to install recommended extensions

4. **Restart VS Code**: The MCP server will be automatically detected and loaded

5. **Verify Setup**: Open Copilot Chat and try:
   ```
   @workspace Use get_agents() to see available AI agents
   ```

### 4. Alternative: Configure Claude Desktop

For Claude Desktop, add to your `claude_desktop_config.json`:

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

### 5. Verify Installation

The agent tools will now be available in VS Code Copilot or Claude Desktop.

## 🔧 VS Code Integration

VS Code Copilot automatically detects the `mcp.json` file in your workspace and loads the MCP server.

**Using Agent Tools in VS Code:**

1. **Open Copilot Chat** in VS Code
2. **Use MCP tools directly** in your conversation:
   ```
   @workspace Use get_agents() to see available AI agents
   ```

3. **Get specialized expertise**:
   ```
   @workspace Use get_agent_prompt("security-auditor") and then review this authentication code
   ```

4. **Get task-specific guidance**:
   ```
   @workspace Use invoke_agent with backend-architect to design an API for user management
   ```

**Example VS Code Workflow:**
```javascript
// In Copilot Chat:
// 1. "@workspace Use get_recommended_agents('optimize database')"
// 2. "@workspace Use get_agent_prompt('database-optimizer')"
// 3. "Now using that database optimization expertise, review my SQL queries"

// Or get structured guidance:
// "@workspace Use invoke_agent with ai-engineer to design a RAG system"
```

## 📚 Available MCP Tools

### `get_agents()`
Returns all available agents with their capabilities and descriptions.

### `find_agents(query)`
Search for agents by domain or capability.

**Examples:**
```javascript
find_agents("backend")      // Find backend-related agents
find_agents("security")     // Find security specialists
find_agents("AI")          // Find AI/ML agents
```

### `get_agent_info(agentName)`
Get detailed information about a specific agent including capabilities and expertise areas.

**Example:**
```javascript
get_agent_info("ai-engineer")
```

### `get_agent_prompt(agentName)`
Get the raw system prompt for an agent to use directly in conversations with your AI tool.

**Example:**
```javascript
get_agent_prompt("backend-architect")
```

### `invoke_agent(invocation)`
Get specialized agent context and guidance for a task. Returns structured guidance that your AI tool can use as context.

**Example:**
```javascript
invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a RESTful API for user management",
  "context": "Spring Boot application with JWT authentication"
})
```

### `get_recommended_agents(task)`
Get agent recommendations for a specific task or domain.

**Example:**
```javascript
get_recommended_agents("optimize database performance")
```

## 🤖 Agent Categories

### Architecture & System Design
- `backend-architect` - RESTful APIs, microservices, database design
- `cloud-architect` - AWS/Azure/GCP infrastructure
- `graphql-architect` - GraphQL schemas and federation
- `architect-reviewer` - Architectural analysis and validation

### Programming Languages
- `java-pro` - Modern Java with Spring Boot
- `python-pro` - Python with async patterns and optimization
- `typescript-pro` - Advanced TypeScript and type safety
- `rust-pro` - Systems programming with Rust
- `golang-pro` - Go development and concurrency
- And 13+ more language specialists...

### Infrastructure & Operations
- `devops-troubleshooter` - Production debugging and log analysis
- `kubernetes-architect` - Container orchestration and GitOps
- `database-optimizer` - Query optimization and performance
- `incident-responder` - Critical incident management
- `terraform-specialist` - Infrastructure as Code

### Quality & Security
- `code-reviewer` - Code quality and security analysis
- `security-auditor` - Vulnerability assessment and OWASP compliance
- `test-automator` - Comprehensive test suite creation
- `performance-engineer` - Application profiling and optimization

### AI & Data
- `ai-engineer` - LLM applications, RAG systems, agent orchestration
- `ml-engineer` - ML pipelines, model serving, feature engineering
- `data-scientist` - Data analysis and statistical modeling
- `mlops-engineer` - ML infrastructure and experiment tracking

### Business & Content
- `business-analyst` - Metrics analysis and KPI tracking
- `content-marketer` - SEO-optimized content creation
- `legal-advisor` - Privacy policies and legal compliance
- `customer-support` - Support automation and ticket handling

## 💡 Usage Examples

### Example 1: Get Agent Context for VS Code Copilot

```javascript
// First, find the right agent
get_recommended_agents("design REST API")

// Get specialized context
invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a social media API with posts, comments, and likes",
  "context": "Spring Boot app, PostgreSQL database, expecting 100k users"
})
```

**Returns structured guidance:**
- Agent's specialized system prompt
- Domain-specific approach patterns
- Task-specific context
- Expert-level guidance your AI tool can use

### Example 2: Direct System Prompt Usage

```javascript
// Get the raw system prompt for direct use
get_agent_prompt("security-auditor")

// Use with your AI tool: "You are a security auditor specializing in..."
```

**Perfect for:**
- VS Code Copilot conversations
- Claude Desktop sessions
- Any MCP-compatible AI tool

### Example 3: Multi-Agent Workflow

```javascript
// Step 1: Get architecture guidance
invoke_agent({
  "agentName": "backend-architect",
  "task": "Design user service architecture"
})

// Step 2: Get security review
invoke_agent({
  "agentName": "security-auditor",
  "task": "Review the proposed architecture for vulnerabilities"
})

// Step 3: Get implementation guidance
get_agent_prompt("java-pro")
```

### Example 4: Agent Discovery

```javascript
// Find agents by domain
find_agents("machine learning")
// Returns: ai-engineer, ml-engineer, mlops-engineer, data-scientist

// Get details about specific agent
get_agent_info("ai-engineer")
// Returns full agent capabilities and description
```

## 🔧 Development

### Running in Development

```bash
./gradlew bootRun
```

### Running Tests

```bash
./gradlew test
```

### Rebuilding After Changes

```bash
./gradlew clean build
# Restart Claude Desktop to use updated JAR
```

## 📁 Project Structure

```
src/main/java/com/jenksy/jenksymcp/
├── JenksyMcpApplication.java      # Main Spring Boot app
├── service/
│   └── AgentService.java          # MCP agent tools
└── record/
    ├── Agent.java                 # Agent data model
    ├── AgentInvocation.java       # Invocation request
    └── AgentResponse.java         # Agent response
```

## 🌟 Agent Highlights

### Most Versatile
- **ai-engineer**: Production LLM apps, RAG systems, agent orchestration
- **backend-architect**: API design, microservices, system architecture
- **code-reviewer**: Security-focused code analysis and best practices

### Most Specialized
- **blockchain-developer**: Web3, smart contracts, DeFi protocols
- **minecraft-bukkit-pro**: Minecraft server plugin development
- **seo-snippet-hunter**: Content optimization for featured snippets

### Mission Critical
- **incident-responder**: Production outage management
- **security-auditor**: Vulnerability assessment and compliance
- **performance-engineer**: System optimization and profiling

## 🔍 Troubleshooting

### Agent Not Found
```bash
# List all available agents
get_agents()

# Search for similar agents
find_agents("your-domain")
```

### Getting Better Results
1. **Be specific**: Include tech stack, constraints, and requirements
2. **Provide context**: Background information helps agents give better advice
3. **Use the right agent**: Check agent descriptions to match your need

### Integration Issues
- Ensure JAR path in config is absolute and correct
- Restart Claude Desktop after configuration changes
- Check that the `temp-agents` directory exists with agent files

## 📄 License

This project extends the Jenksy educational platform and integrates with the open-source agents collection from https://github.com/wshobson/agents.

## 🤝 Contributing

To add custom agents:
1. Create markdown files in `temp-agents/` directory
2. Follow the agent format with YAML frontmatter
3. Rebuild the project

## 📞 Support

For issues with:
- **MCP Server**: Check logs and configuration
- **Agent Responses**: Verify agent exists with `get_agent_info()`
- **Integration**: Ensure MCP protocol compatibility

Transform your development workflow with specialized AI agents! 🚀