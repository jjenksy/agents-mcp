# MCP Tools API Reference

## Overview

The AI Agents MCP Server provides 6 specialized tools for interacting with 20+ domain-expert AI agents. These tools are called automatically by VS Code Copilot when you make natural language requests - you never call them directly.

**Important:** In VS Code Copilot, you interact using natural language. The tool syntax shown in this reference is for understanding what happens behind the scenes, not for direct usage.

## Tool Categories

### 🚀 Primary Tools (Optimized Workflow)

**[`invoke_agent`](#invoke_agent)** ⭐ - Get task-specific guidance (recommended)
**[`get_recommended_agents`](#get_recommended_agents)** - Smart agent discovery (1-3 agents)

### 🔍 Discovery Tools

**[`get_agents`](#get_agents)** - List all available agents
**[`find_agents`](#find_agents)** - Search agents by domain
**[`get_agent_info`](#get_agent_info)** - Get agent details

### ⚠️ Legacy Tools

**[`get_agent_prompt`](#get_agent_prompt)** - Raw system prompt (prefer `invoke_agent`)

---

## Tool Reference

### invoke_agent

**Get concise, task-specific guidance from specialized agents.**

**Optimization Benefits:**
- 75% smaller response size
- Structured markdown format
- Focused recommendations (3-4 steps)
- Automatic context caching
- Expert context included inline

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `agentName` | `string` | ✅ | Name of the agent to invoke |
| `task` | `string` | ✅ | Specific task or objective |
| `context` | `string` | ⭐ | Additional context for better guidance |

#### Response Format

```json
{
  "agentName": "backend-architect",
  "model": "mcp-optimized",
  "content": "## BACKEND-ARCHITECT SPECIALIST\n> Design RESTful APIs...",
  "status": "success",
  "contextKey": "backend-architect_1640995200000"
}
```

#### Response Structure (Markdown)

```markdown
## {AGENT-NAME} SPECIALIST
> {Agent description}

### Task Analysis
**Objective**: {Your task}
**Context**: {Your context}

### Recommended Approach
1. {Actionable step 1}
2. {Actionable step 2}
3. {Actionable step 3}

### Expert Context
```
{Agent system prompt}
```
```

#### How to Use in VS Code Copilot

**Natural Language Request Examples:**

**RAG System Design:**
```
"I need the ai-engineer agent to help design a RAG system for document search in a Spring Boot app with 10M+ documents"
```

**API Design:**
```
"Please use the backend-architect agent to design a user management API with role-based access for a Spring Boot application expecting 100k users in a microservices architecture"
```

**Security Review:**
```
"Can the security-auditor agent review our OAuth implementation for vulnerabilities? We're using Spring Security 6 with PKCE flow for mobile app integration"
```

**What happens behind the scenes:**
VS Code Copilot automatically translates your natural language into the appropriate `invoke_agent` tool call with extracted parameters.

#### Error Responses

```json
{
  "agentName": "unknown-agent",
  "model": "unknown",
  "content": "Error: Agent 'unknown-agent' not found. Use get_agents to see available agents.",
  "status": "error",
  "contextKey": ""
}
```

---

### get_recommended_agents

**Get 1-3 best agents for your task with usage guidance.**

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `task` | `string` | ✅ | Task description for recommendations |

#### Response Format

Returns an array of 1-3 `Agent` objects:

```json
[
  {
    "name": "backend-architect",
    "description": "Design RESTful APIs, microservice boundaries, and database schemas",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a backend system architect..."
  }
]
```

#### How to Use in VS Code Copilot

**Natural Language Requests:**
```
"Which agents can help me optimize database performance?"
"What agents are best for building microservices APIs?"
"Show me agents that can help implement a caching layer"
```

**Behind the scenes:** VS Code Copilot calls `get_recommended_agents` with your query

#### Recommendation Logic

The tool analyzes your task for keywords and returns relevant agents:

| Keywords | Recommended Agents |
|----------|-------------------|
| `api`, `backend`, `database` | backend-architect, database-optimizer |
| `ui`, `frontend`, `react` | frontend-developer, ui-ux-designer |
| `ai`, `llm`, `rag` | ai-engineer, ml-engineer |
| `security`, `audit`, `review` | security-auditor, code-reviewer |
| `debug`, `error`, `bug` | debugger |
| `requirements`, `planning` | requirements-analyst |

---

### get_agents

**List all available agents with their capabilities.**

#### Parameters

None.

#### Response Format

Returns an array of all `Agent` objects:

```json
[
  {
    "name": "ai-engineer",
    "description": "Build production-ready LLM applications, advanced RAG systems, and intelligent agents",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are an AI engineer specializing in..."
  },
  // ... 19 more agents
]
```

#### How to Use in VS Code Copilot

**Natural Language Request:**
```
"Show me all available AI agents"
"List all the agents"
"What agents do you have?"
```

**Behind the scenes:** VS Code Copilot calls `get_agents()`

**When to Use:**
- Browse all available agents
- Understand the full scope of expertise
- Initial exploration of agent capabilities

---

### find_agents

**Search agents by domain keywords.**

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | `string` | ✅ | Search term (domain, technology, or capability) |

#### Response Format

Returns filtered array of `Agent` objects matching the query.

#### How to Use in VS Code Copilot

**Natural Language Requests:**
```
"Show me security-related agents"
"What agents specialize in Java?"
"Find agents that work with databases"
"Which agents handle cloud architecture?"
```

**Behind the scenes:** VS Code Copilot calls `find_agents` with the appropriate search term

#### Search Scope

The search looks through:
- Agent names
- Agent descriptions
- Agent system prompts

---

### get_agent_info

**Get detailed information about a specific agent.**

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `agentName` | `string` | ✅ | Exact name of the agent |

#### Response Format

Returns a single `Agent` object or `null` if not found.

#### How to Use in VS Code Copilot

**Natural Language Requests:**
```
"Tell me about the ai-engineer agent"
"What can the backend-architect agent do?"
"Describe the ai-engineer agent's capabilities"
```

**Behind the scenes:** VS Code Copilot calls `get_agent_info` with the agent name

**When to Use:**
- Get agent capabilities before invoking
- Understand agent specialization
- Verify agent availability

---

### get_agent_prompt

**[LEGACY] Get raw system prompt only.**

> ⚠️ **Deprecated**: Prefer `invoke_agent` for task-specific guidance with context and recommendations.

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `agentName` | `string` | ✅ | Name of the agent |

#### Response Format

Returns a string with deprecation notice and raw system prompt:

```
NOTE: For better results, use invoke_agent with a specific task for contextualized guidance.

Raw System Prompt:
You are an AI engineer specializing in production-grade LLM applications...
```

#### How to Use in VS Code Copilot

**Note:** This is a legacy tool. Use natural language requests instead:

**Instead of trying to get raw prompts, just ask for help:**
```
"Use the ai-engineer agent to help design a RAG system for a Spring Boot app with a vector database"
```

This provides better, more contextual results than accessing raw system prompts.

---

## Best Practices

### Optimal Natural Language Patterns

#### ✅ Direct Agent Request (Recommended)

```
"Please use the backend-architect agent to design a microservices API for user management with Spring Boot and Kubernetes, expecting 1M+ users"
```

**Why this works:** VS Code Copilot extracts the agent name, task, and context from your natural language and makes the appropriate tool call.

#### ✅ Smart Discovery Flow

```
// First, discover agents
"Which agents can help implement a caching strategy?"

// Then use the recommendation
"Use the database-optimizer agent to implement Redis caching for user sessions on our high-traffic e-commerce site with 10k concurrent users"
```

#### ❌ What NOT to Do

```
// Don't try to write tool syntax - it won't work in VS Code
@workspace Use invoke_agent({...})  // This syntax doesn't work

// Don't make vague requests
"Help with stuff"  // Too vague for agents to provide value
```

### Context Best Practices for Natural Language

**✅ Provide Rich Context:**
```
"I need the security-auditor agent to review our authentication system. We're using Spring Security 6 with JWT tokens in a microservices architecture for 100k users, and need PCI compliance."
```

**❌ Avoid Vague Requests:**
```
"Check security for web app"  // Too vague
"Help with authentication"    // Missing context
```

**Key elements to include:**
- Technology stack (Spring Security 6, JWT)
- Architecture (microservices)
- Scale (100k users)
- Requirements (PCI compliance)

### Response Processing

**Optimized responses are structured markdown:**

```markdown
## AI-ENGINEER SPECIALIST
> Build production-ready LLM applications, advanced RAG systems

### Task Analysis
**Objective**: Design a RAG system for document search
**Context**: Spring Boot app with 10M+ documents

### Recommended Approach
1. Design vector database schema with proper indexing
2. Implement chunk-based document processing pipeline
3. Set up embedding model with caching layer

### Expert Context
```
[Agent system prompt for reference]
```
```

---

## Error Handling

### Common Errors

**Agent Not Found:**
```json
{
  "agentName": "unknown-agent",
  "content": "Error: Agent 'unknown-agent' not found. Use get_agents to see available agents.",
  "status": "error"
}
```

**Missing Parameters:**
```json
{
  "agentName": "unknown",
  "content": "Error: Agent name cannot be blank",
  "status": "error"
}
```

### Error Recovery

1. **Verify agent exists**: Use `get_agents()` to list available agents
2. **Check agent name spelling**: Agent names are case-sensitive
3. **Provide required parameters**: Both `agentName` and `task` are required for `invoke_agent`

---

## Performance Considerations

### Caching

- **Agent Info**: Cached for 5 minutes
- **Agent Search Results**: Cached by query
- **Context Sessions**: Auto-expire after 5 minutes of inactivity

### Response Sizes

| Tool | Typical Response Size | Optimization |
|------|----------------------|--------------|
| `invoke_agent` | ~1-2KB | 75% reduction from legacy |
| `get_agents` | ~10-15KB | Cached |
| `get_agent_info` | ~500B | Cached |
| `get_agent_prompt` | ~2-5KB | Legacy (larger) |

### Rate Limiting

The MCP server includes built-in rate limiting and automatic cleanup of expired contexts.

---

## Integration Examples

### VS Code Copilot Integration

**In VS Code Copilot Chat, use natural language:**
```
"Can the java-pro agent help optimize this Stream processing code? We're processing 1M records with memory constraints on Java 21."
```

**Remember:** VS Code Copilot translates your natural language into MCP tool calls automatically. You focus on describing your needs, not learning tool syntax.

### Claude Desktop Integration

**In Claude Desktop, you can use the actual tool syntax:**
```javascript
invoke_agent({
  "agentName": "cloud-architect",
  "task": "Design AWS infrastructure for microservices",
  "context": "15 services, auto-scaling, cost optimization required"
})
```

**Note:** Unlike VS Code Copilot, Claude Desktop exposes the actual MCP tool interface. VS Code Copilot requires natural language instead.

---

## Available Agents

### Architecture & Design
- **architect-review** - Modern architecture patterns, clean architecture, DDD
- **backend-architect** - RESTful APIs, microservice boundaries, database schemas
- **cloud-architect** - AWS/Azure/GCP infrastructure, IaC, FinOps optimization
- **ai-engineer** - LLM applications, RAG systems, agent orchestration
- **requirements-analyst** - Business requirements to structured tickets
- **ui-ux-designer** - Design systems, user research, accessibility

### DevOps & Infrastructure
- **deployment-engineer** - CI/CD pipelines, GitOps workflows, progressive delivery
- **database-optimizer** - Query optimization, indexing strategies, performance tuning

### AI & Machine Learning
- **ml-engineer** - PyTorch, TensorFlow, model serving, MLOps infrastructure
- **prompt-engineer** - Advanced prompting techniques, LLM optimization

### Quality & Security
- **code-reviewer** - AI-powered code analysis, security vulnerabilities
- **security-auditor** - OWASP compliance, threat modeling, security testing

### Programming Languages
- **java-pro** - Modern Java with Spring Boot, enterprise patterns
- **javascript-pro** - Modern JavaScript, ES6+, async patterns, Node.js APIs
- **python-pro** - Modern Python with async patterns and optimization
- **typescript-pro** - Advanced TypeScript with strict type safety
- **sql-pro** - Modern SQL, cloud-native databases, OLTP/OLAP optimization

### Development Tools
- **frontend-developer** - React components, responsive layouts, state management
- **mermaid-expert** - Diagrams for flowcharts, sequences, ERDs, architectures
- **debugger** - Error analysis, test failures, unexpected behavior

---

## Changelog

### v1.1.0 - VS Code Optimization Update

**Major Improvements:**
- ✅ 75% response size reduction for `invoke_agent`
- ✅ Structured markdown output format
- ✅ Smart tool usage guidance in descriptions
- ✅ Deprecated `get_agent_prompt` with migration guidance
- ✅ Enhanced context caching and cleanup

**Breaking Changes:**
- `invoke_agent` response format changed to optimized markdown structure
- `model` field changed from specific model names to "mcp-optimized"

**Migration:**
- Existing integrations continue to work
- New integrations should prefer `invoke_agent` over `get_agent_prompt`
- VS Code Copilot users benefit from automatic optimization