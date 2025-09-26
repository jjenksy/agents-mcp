# MCP Tools API Reference

## Overview

The AI Agents MCP Server provides 6 specialized tools for interacting with 20+ domain-expert AI agents. These tools have been **optimized for VS Code Copilot integration** with 75% smaller responses and smarter usage guidance.

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

#### Usage Examples

**Basic Usage:**
```javascript
@workspace Use invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a RAG system for document search",
  "context": "Spring Boot app with 10M+ documents"
})
```

**API Design:**
```javascript
@workspace Use invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a user management API with role-based access",
  "context": "Spring Boot, expecting 100k users, microservices architecture"
})
```

**Security Review:**
```javascript
@workspace Use invoke_agent({
  "agentName": "security-auditor",
  "task": "Review OAuth implementation for vulnerabilities",
  "context": "Spring Security 6, PKCE flow, mobile app integration"
})
```

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
    "model": "opus",
    "tools": [],
    "systemPrompt": "You are a backend system architect..."
  }
]
```

#### Usage Examples

```javascript
@workspace Use get_recommended_agents("optimize database performance")
@workspace Use get_recommended_agents("build microservices API")
@workspace Use get_recommended_agents("implement caching layer")
```

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
    "model": "opus",
    "tools": [],
    "systemPrompt": "You are an AI engineer specializing in..."
  },
  // ... 19 more agents
]
```

#### Usage Examples

```javascript
@workspace Use get_agents()
```

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

#### Usage Examples

```javascript
@workspace Use find_agents("security")     // Security-related agents
@workspace Use find_agents("java")         // Java specialists
@workspace Use find_agents("database")     // Database experts
@workspace Use find_agents("cloud")        // Cloud architects
```

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

#### Usage Examples

```javascript
@workspace Use get_agent_info("ai-engineer")
@workspace Use get_agent_info("backend-architect")
```

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

#### Migration Guide

**Old Pattern:**
```javascript
@workspace Use get_agent_prompt("ai-engineer")
// Then manually apply the system prompt
```

**New Optimized Pattern:**
```javascript
@workspace Use invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a RAG system",
  "context": "Spring Boot app with vector database"
})
```

---

## Best Practices

### Optimal Usage Patterns

#### ✅ Single-Call Workflow (Recommended)

```javascript
// Get everything you need in one efficient call
@workspace Use invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a microservices API for user management",
  "context": "Spring Boot, Kubernetes, expecting 1M+ users"
})
```

#### ✅ Smart Discovery

```javascript
// Get targeted recommendations
@workspace Use get_recommended_agents("implement caching strategy")

// Then use the recommended agent
@workspace Use invoke_agent({
  "agentName": "database-optimizer",
  "task": "Implement Redis caching for user sessions",
  "context": "High-traffic e-commerce site, 10k concurrent users"
})
```

#### ❌ Inefficient Multiple Calls

```javascript
// Avoid: Multiple calls for the same information
@workspace Use get_agent_info("ai-engineer")
@workspace Use get_agent_prompt("ai-engineer")
@workspace Use invoke_agent({...})  // Much of this info is redundant
```

### Context Best Practices

**Provide Rich Context:**
```javascript
invoke_agent({
  "agentName": "security-auditor",
  "task": "Review authentication system",
  "context": "Spring Security 6, JWT tokens, microservices, 100k users, PCI compliance required"
})
```

**Avoid Vague Context:**
```javascript
invoke_agent({
  "agentName": "security-auditor",
  "task": "check security",
  "context": "web app"
})
```

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

```javascript
// In VS Code Copilot Chat
@workspace Use invoke_agent({
  "agentName": "java-pro",
  "task": "Optimize this Stream processing code",
  "context": "Processing 1M records, memory constraints, Java 21"
})
```

### Claude Desktop Integration

```javascript
// In Claude Desktop
invoke_agent({
  "agentName": "cloud-architect",
  "task": "Design AWS infrastructure for microservices",
  "context": "15 services, auto-scaling, cost optimization required"
})
```

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