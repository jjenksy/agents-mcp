# API Reference

Complete reference documentation for the Jenksy MCP server tools and data structures. This guide covers all available MCP tools, their parameters, responses, and usage patterns.

## Table of Contents

1. [Overview](#overview)
2. [Tool Invocation Patterns](#tool-invocation-patterns)
3. [Core Tools](#core-tools)
4. [Data Structures](#data-structures)
5. [Error Handling](#error-handling)
6. [Usage Examples](#usage-examples)
7. [Integration Patterns](#integration-patterns)
8. [Rate Limiting and Caching](#rate-limiting-and-caching)
9. [Best Practices](#best-practices)

## Overview

The Jenksy MCP server exposes 5 core tools through the Model Context Protocol (MCP) for AI tool integration. These tools provide access to 20 specialized AI agents covering domains like backend architecture, AI engineering, security auditing, and more.

### Available Tools

| Tool | Purpose | Usage Pattern |
|------|---------|---------------|
| `get_agents` | List all available agents | Discovery and browsing |
| `find_agents` | Search agents by domain/keywords | Targeted discovery |
| `get_agent_info` | Get detailed agent information | Capability verification |
| `invoke_agent` | Get specialized task guidance | Task-specific expertise |
| `get_recommended_agents` | Get agent recommendations | Smart discovery |

### Tool Architecture

```
AI Tool (VS Code/Claude Desktop)
    ↓ MCP Protocol
Jenksy MCP Server
    ↓ Spring AI @Tool annotations
AgentService
    ↓ Agent loading and management
20 Specialized Agents
```

## Tool Invocation Patterns

### Natural Language Integration (VS Code Copilot)

VS Code Copilot integrates MCP tools through natural language rather than direct tool calls:

```javascript
// User types in VS Code:
"Show me all available agents for backend development"

// VS Code Copilot automatically translates to:
find_agents("backend")

// User asks:
"Get me detailed guidance from the ai-engineer for building a RAG system"

// Copilot translates to:
invoke_agent({
  "agentName": "ai-engineer",
  "task": "building a RAG system",
  "context": ""
})
```

### Direct Tool Calls (Claude Desktop)

Claude Desktop supports direct MCP tool invocation:

```javascript
// Direct tool call syntax
get_agents()

find_agents("security")

invoke_agent({
  "agentName": "security-auditor",
  "task": "Review authentication implementation",
  "context": "Spring Boot REST API with JWT"
})
```

## Core Tools

### get_agents

Lists all available AI agents in the system.

**Signature:**
```typescript
get_agents(): Agent[]
```

**Parameters:**
None

**Returns:**
Array of `Agent` objects containing all available agents.

**Response Example:**
```json
[
  {
    "name": "ai-engineer",
    "description": "Build production-ready LLM applications, advanced RAG systems, and intelligent agents",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are an AI engineer specializing in production-grade LLM applications..."
  },
  {
    "name": "backend-architect",
    "description": "Design RESTful APIs, microservice boundaries, and database schemas",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a backend system architect..."
  }
]
```

**Use Cases:**
- Initial discovery of available agents
- Browsing agent capabilities
- Building agent selection interfaces
- Validating agent availability

**Performance Notes:**
- Cached response (cache duration: 5 minutes)
- Typically returns 20 agents
- Response size: ~50KB

---

### find_agents

Search for agents using domain keywords or capability descriptions.

**Signature:**
```typescript
find_agents(query: string): Agent[]
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Search keywords (domain, technology, capability) |

**Search Behavior:**
- Case-insensitive search
- Matches against agent name, description, and system prompt
- Returns agents in relevance order

**Returns:**
Array of matching `Agent` objects.

**Example Queries:**
```javascript
// Domain-based search
find_agents("backend")
find_agents("frontend")
find_agents("security")
find_agents("ai")

// Technology-specific search
find_agents("react")
find_agents("java")
find_agents("kubernetes")

// Capability-based search
find_agents("api design")
find_agents("code review")
find_agents("performance optimization")
```

**Response Example:**
```json
[
  {
    "name": "backend-architect",
    "description": "Design RESTful APIs, microservice boundaries, and database schemas",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a backend system architect..."
  },
  {
    "name": "java-pro",
    "description": "Java expert specializing in Spring Framework, microservices, and enterprise patterns",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a Java programming expert..."
  }
]
```

**Performance Notes:**
- Cached response per unique query
- Average response time: <100ms
- Returns 0-20 agents depending on query specificity

---

### get_agent_info

Retrieve detailed information about a specific agent.

**Signature:**
```typescript
get_agent_info(agentName: string): Agent | null
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `agentName` | string | Yes | Exact agent name (case-sensitive) |

**Returns:**
`Agent` object if found, `null` if agent doesn't exist.

**Example Usage:**
```javascript
// Get specific agent information
get_agent_info("ai-engineer")
get_agent_info("security-auditor")
get_agent_info("frontend-developer")

// Invalid agent name returns null
get_agent_info("non-existent-agent") // Returns null
```

**Response Example:**
```json
{
  "name": "ai-engineer",
  "description": "Build production-ready LLM applications, advanced RAG systems, and intelligent agents. Implements vector search, multimodal AI, agent orchestration, and enterprise AI integrations.",
  "model": "mcp-optimized",
  "tools": [],
  "systemPrompt": "You are an AI engineer specializing in production-grade LLM applications, generative AI systems, and intelligent agent architectures.\n\n## Purpose\nExpert AI engineer specializing in LLM application development..."
}
```

**Use Cases:**
- Validate agent capabilities before invocation
- Display agent details in user interfaces
- Check agent model preferences
- Access raw system prompts for direct use

**Performance Notes:**
- Cached response per agent name
- Response time: <50ms
- System prompt can be 3000+ characters for complex agents

---

### invoke_agent

Get specialized, task-specific guidance from an AI agent.

**Signature:**
```typescript
invoke_agent(invocation: AgentInvocation): AgentResponse
```

**Parameters:**
`AgentInvocation` object with the following properties:

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `agentName` | string | Yes | Name of agent to invoke |
| `task` | string | Yes | Description of the task or problem |
| `context` | string | No | Additional context about the project/environment |

**Returns:**
`AgentResponse` object with structured guidance.

**Request Example:**
```json
{
  "agentName": "ai-engineer",
  "task": "Build a production RAG system for enterprise knowledge base",
  "context": "Spring Boot application handling 10M+ documents, needs to support real-time queries with sub-second response times"
}
```

**Response Structure:**
```json
{
  "agentName": "ai-engineer",
  "model": "mcp-optimized",
  "response": "## AI-ENGINEER SPECIALIST\n> Build production-ready LLM applications...",
  "status": "success",
  "contextKey": "ai-engineer_1234567890"
}
```

**Response Format:**
The response includes structured guidance with:

1. **Agent Header**: Identifies the specialist and capabilities
2. **Task Analysis**: Breakdown of the specific objective
3. **Recommended Approach**: Step-by-step methodology
4. **Expert Context**: Raw system prompt for detailed guidance

**Example Response Content:**
```markdown
## AI-ENGINEER SPECIALIST
> Build production-ready LLM applications, advanced RAG systems, and intelligent agents

### Task Analysis
**Objective**: Build a production RAG system for enterprise knowledge base
**Context**: Spring Boot application handling 10M+ documents, needs to support real-time queries with sub-second response times

### Recommended Approach
1. Design for production scalability and reliability
2. Include monitoring, evaluation metrics, and observability
3. Optimize for cost efficiency and resource usage

### Expert Context
```
You are an AI engineer specializing in production-grade LLM applications...
[Full system prompt content]
```
```

**Error Responses:**
```json
{
  "agentName": "unknown",
  "model": "unknown",
  "response": "Error: Agent name cannot be blank",
  "status": "error",
  "contextKey": ""
}
```

**Use Cases:**
- Get specialized guidance for complex tasks
- Receive structured recommendations with implementation details
- Access expert-level system prompts for direct conversation use
- Obtain domain-specific best practices and methodologies

**Performance Notes:**
- Response time: 100-500ms depending on agent complexity
- Context automatically expires after 5 minutes
- Response size: 2-10KB depending on agent system prompt length

---

### get_recommended_agents

Get 1-3 best agent recommendations for a specific task.

**Signature:**
```typescript
get_recommended_agents(task: string): Agent[]
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `task` | string | Yes | Description of the task or domain |

**Returns:**
Array of 1-3 recommended `Agent` objects, ordered by relevance.

**Recommendation Logic:**
The system analyzes task keywords to suggest the most relevant agents:

| Task Keywords | Recommended Agents |
|---------------|-------------------|
| "api", "backend", "database" | backend-architect, java-pro |
| "ui", "frontend", "react" | frontend-developer, typescript-pro |
| "ai", "llm", "rag" | ai-engineer |
| "review", "security", "audit" | code-reviewer, security-auditor |
| "bug", "debug", "error" | debugger |
| "requirements", "planning" | requirements-analyst |

**Example Usage:**
```javascript
// Backend development task
get_recommended_agents("Design a REST API for user management")

// Frontend development task
get_recommended_agents("Build a responsive dashboard with React")

// AI/ML task
get_recommended_agents("Implement semantic search with vector embeddings")

// Security task
get_recommended_agents("Audit authentication system for vulnerabilities")

// General development task
get_recommended_agents("Optimize application performance")
```

**Response Example:**
```json
[
  {
    "name": "backend-architect",
    "description": "Design RESTful APIs, microservice boundaries, and database schemas",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a backend system architect..."
  },
  {
    "name": "java-pro",
    "description": "Java expert specializing in Spring Framework, microservices, and enterprise patterns",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a Java programming expert..."
  }
]
```

**Fallback Behavior:**
If no specific recommendations match, returns the 3 most versatile agents:
- ai-engineer
- backend-architect
- code-reviewer

**Use Cases:**
- Smart agent discovery without browsing full list
- Quick access to relevant expertise
- Guided agent selection for new users
- Task-specific agent suggestions in IDE integrations

**Performance Notes:**
- Cached response per unique task description
- Response time: <100ms
- Always returns 1-3 agents (never empty)

## Data Structures

### Agent

Represents a specialized AI agent with domain expertise.

```typescript
interface Agent {
  name: string;           // Unique identifier (kebab-case)
  description: string;    // Brief capability summary
  model: string;         // Model identifier (always "mcp-optimized" for VS Code)
  tools: string[];       // Available tools (currently unused)
  systemPrompt: string;  // Detailed expertise and instructions
}
```

**Field Details:**

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `name` | string | Unique agent identifier | `"ai-engineer"` |
| `description` | string | 1-2 sentence capability summary | `"Build production-ready LLM applications..."` |
| `model` | string | Model identifier for VS Code compatibility | `"mcp-optimized"` |
| `tools` | string[] | Reserved for future tool integrations | `[]` |
| `systemPrompt` | string | Comprehensive instructions (1000-8000 chars) | Full system prompt content |

### AgentInvocation

Request structure for agent invocation.

```typescript
interface AgentInvocation {
  agentName: string;  // Required: Agent to invoke
  task: string;       // Required: Task description
  context: string;    // Optional: Additional context
}
```

**Validation Rules:**
- `agentName`: Must not be blank, must match existing agent
- `task`: Must not be blank, should be descriptive
- `context`: Optional, provides additional project/environment details

**Example:**
```json
{
  "agentName": "backend-architect",
  "task": "Design a microservices architecture for e-commerce platform",
  "context": "Spring Boot, PostgreSQL, expected 100k daily users, needs to support international markets"
}
```

### AgentResponse

Structured response from agent invocation.

```typescript
interface AgentResponse {
  agentName: string;    // Agent that provided the response
  model: string;        // Model identifier (typically "mcp-optimized")
  response: string;     // Formatted guidance content
  status: string;       // "success" or "error"
  contextKey: string;   // Session context identifier
}
```

**Status Values:**
- `"success"`: Agent provided guidance successfully
- `"error"`: Validation failed or agent not found

**Response Content Format:**
The `response` field contains structured Markdown content:
- Agent header with specialization
- Task analysis section
- Recommended approach with numbered steps
- Expert context with full system prompt

**Context Key:**
Format: `{agentName}_{timestamp}`
- Used for session tracking
- Automatically expires after 5 minutes
- Can be used for follow-up context in future integrations

## Error Handling

### Common Error Scenarios

**Agent Not Found:**
```json
{
  "agentName": "invalid-agent",
  "model": "unknown",
  "response": "Error: Agent 'invalid-agent' not found. Use get_agents to see available agents.",
  "status": "error",
  "contextKey": ""
}
```

**Missing Required Parameters:**
```json
{
  "agentName": "unknown",
  "model": "unknown",
  "response": "Error: Agent name cannot be blank",
  "status": "error",
  "contextKey": ""
}
```

**Empty Task Description:**
```json
{
  "agentName": "ai-engineer",
  "model": "unknown",
  "response": "Error: Task description cannot be blank",
  "status": "error",
  "contextKey": ""
}
```

### Error Response Patterns

All error responses follow this structure:
- `status`: Always `"error"`
- `response`: Human-readable error message
- `agentName`: Agent name if provided, otherwise `"unknown"`
- `model`: `"unknown"` for error responses
- `contextKey`: Empty string for errors

### Best Practices for Error Handling

1. **Always check status field:**
   ```javascript
   const response = invoke_agent(invocation);
   if (response.status === "error") {
     console.error("Agent invocation failed:", response.response);
     return;
   }
   ```

2. **Validate agent existence before invocation:**
   ```javascript
   const agent = get_agent_info(agentName);
   if (!agent) {
     console.error("Agent not found:", agentName);
     return;
   }
   ```

3. **Provide meaningful task descriptions:**
   ```javascript
   // Good
   const task = "Design a caching strategy for high-traffic API endpoints";

   // Avoid
   const task = "help with caching";
   ```

## Usage Examples

### Example 1: Discover and Use Backend Agent

```javascript
// Step 1: Find relevant agents
const backendAgents = find_agents("backend api design");

// Step 2: Get detailed information about preferred agent
const architect = get_agent_info("backend-architect");

// Step 3: Invoke agent with specific task
const response = invoke_agent({
  agentName: "backend-architect",
  task: "Design REST API for a multi-tenant SaaS application",
  context: "Spring Boot, PostgreSQL, microservices architecture, needs to support 1000+ tenants"
});

// Step 4: Use the guidance
if (response.status === "success") {
  console.log("Agent guidance:", response.response);
  // Display structured guidance to developer
}
```

### Example 2: Smart Agent Recommendations

```javascript
// Get recommendations for AI-related task
const recommendedAgents = get_recommended_agents(
  "Build a chatbot with context awareness and function calling"
);

// Use the top recommendation
if (recommendedAgents.length > 0) {
  const topAgent = recommendedAgents[0];
  const guidance = invoke_agent({
    agentName: topAgent.name,
    task: "Build a chatbot with context awareness and function calling",
    context: "Node.js application, needs to integrate with existing CRM"
  });
}
```

### Example 3: Security Review Workflow

```javascript
// Find security-related agents
const securityAgents = find_agents("security audit review");

// Get recommendations for security task
const recommended = get_recommended_agents(
  "Review authentication implementation for security vulnerabilities"
);

// Invoke security auditor
const securityGuidance = invoke_agent({
  agentName: "security-auditor",
  task: "Review JWT authentication implementation",
  context: "Spring Security, Redis session store, mobile and web clients"
});

// Also get code review perspective
const codeReview = invoke_agent({
  agentName: "code-reviewer",
  task: "Review authentication code for security and best practices",
  context: "Spring Security, JWT tokens, password reset functionality"
});
```

### Example 4: VS Code Copilot Integration Pattern

When using VS Code Copilot, interactions are more natural:

```javascript
// User types in VS Code:
"I need help designing a microservices architecture"

// VS Code Copilot internally:
const agents = get_recommended_agents("microservices architecture design");
const guidance = invoke_agent({
  agentName: agents[0].name,
  task: "design microservices architecture",
  context: extractProjectContext() // VS Code can provide project context
});

// Copilot presents guidance naturally in the conversation
```

## Integration Patterns

### MCP Client Integration

**Basic Tool Registration:**
```typescript
// Pseudo-code for MCP client integration
const mcpClient = new MCPClient("ws://localhost:8080/mcp");

// Tools are automatically discovered from server
const availableTools = await mcpClient.getTools();
// Returns: ["get_agents", "find_agents", "get_agent_info", "invoke_agent", "get_recommended_agents"]
```

**Tool Invocation:**
```typescript
// Direct tool call
const agents = await mcpClient.callTool("get_agents", {});

// Parameterized tool call
const backendAgents = await mcpClient.callTool("find_agents", {
  query: "backend"
});

// Complex invocation
const response = await mcpClient.callTool("invoke_agent", {
  agentName: "ai-engineer",
  task: "Implement RAG system",
  context: "Python, FastAPI, 1M documents"
});
```

### VS Code Extension Integration

**Natural Language Processing:**
```javascript
// Extension processes natural language
function processUserQuery(query) {
  const keywords = extractKeywords(query);

  if (keywords.includes("agents") && keywords.includes("list")) {
    return callTool("get_agents", {});
  }

  if (keywords.includes("help") || keywords.includes("guidance")) {
    const task = extractTask(query);
    const agents = callTool("get_recommended_agents", { task });
    return callTool("invoke_agent", {
      agentName: agents[0].name,
      task: task,
      context: getProjectContext()
    });
  }
}
```

### Claude Desktop Configuration

**claude_desktop_config.json:**
```json
{
  "mcpServers": {
    "jenksy-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/username/path/to/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Usage in Claude Desktop:**
```javascript
// User can directly invoke tools
get_agents()

find_agents("ai engineering")

invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a recommendation system",
  "context": "E-commerce platform, 1M+ products, real-time personalization"
})
```

## Rate Limiting and Caching

### Caching Strategy

**Tool-Level Caching:**
- `get_agents`: 5-minute cache (data rarely changes)
- `find_agents`: Per-query cache, 5-minute expiration
- `get_agent_info`: Per-agent cache, 5-minute expiration
- `get_recommended_agents`: Per-task cache, 5-minute expiration
- `invoke_agent`: No caching (always fresh guidance)

**Cache Implementation:**
```java
// Caffeine cache with automatic expiration
private final Cache<String, Object> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .build();
```

**Context Management:**
```java
// Agent contexts expire automatically
private final Cache<String, String> agentContexts = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .build();
```

### Performance Characteristics

**Response Times (typical):**
- `get_agents`: 10-50ms (cached)
- `find_agents`: 20-100ms (search + cache)
- `get_agent_info`: 5-30ms (cached)
- `get_recommended_agents`: 10-80ms (logic + cache)
- `invoke_agent`: 100-500ms (processing + formatting)

**Memory Usage:**
- Base application: ~100MB
- Agent data: ~5MB (20 agents with system prompts)
- Cache overhead: ~10MB (typical usage)
- Context storage: ~1MB (active sessions)

### Rate Limiting Considerations

**Built-in Protections:**
- Context cache limits (1000 entries max)
- Automatic cleanup of expired contexts
- Query cache to reduce redundant processing

**Recommended Client-Side Limits:**
- Max 10 requests per second per client
- Batch `get_agent_info` calls when possible
- Cache agent lists locally for 5+ minutes
- Debounce rapid-fire `find_agents` calls

## Best Practices

### Optimal Tool Usage

**1. Use the Right Tool for the Job:**
```javascript
// Discovery phase - start broad
const allAgents = get_agents();

// Narrow down - search by domain
const relevantAgents = find_agents("backend api");

// Quick access - get recommendations
const recommended = get_recommended_agents("design REST API");

// Deep dive - get detailed capabilities
const agentInfo = get_agent_info("backend-architect");

// Implementation - get specific guidance
const guidance = invoke_agent({
  agentName: "backend-architect",
  task: "Design user authentication API",
  context: "Spring Boot, JWT, microservices"
});
```

**2. Provide Rich Context:**
```javascript
// Good context
const response = invoke_agent({
  agentName: "ai-engineer",
  task: "Build a recommendation system for e-commerce",
  context: "Python/FastAPI backend, 1M+ products, 100k daily users, needs real-time personalization with collaborative filtering, budget constraints favor efficiency over accuracy"
});

// Minimal context (still works, but less specific guidance)
const response = invoke_agent({
  agentName: "ai-engineer",
  task: "Build a recommendation system",
  context: ""
});
```

**3. Handle Errors Gracefully:**
```javascript
function safeAgentInvocation(agentName, task, context = "") {
  try {
    // Validate agent exists first
    const agent = get_agent_info(agentName);
    if (!agent) {
      throw new Error(`Agent '${agentName}' not found`);
    }

    // Invoke with error checking
    const response = invoke_agent({ agentName, task, context });

    if (response.status === "error") {
      throw new Error(response.response);
    }

    return response;
  } catch (error) {
    console.error("Agent invocation failed:", error.message);

    // Fallback to recommendations
    const recommended = get_recommended_agents(task);
    if (recommended.length > 0) {
      console.log("Recommended agents:", recommended.map(a => a.name));
    }

    return null;
  }
}
```

### Performance Optimization

**1. Minimize Tool Calls:**
```javascript
// Efficient: Single call for multiple agents
const allAgents = get_agents();
const backendAgents = allAgents.filter(a =>
  a.description.toLowerCase().includes("backend")
);

// Inefficient: Multiple individual calls
const backendArchitect = get_agent_info("backend-architect");
const javaPro = get_agent_info("java-pro");
const sqlPro = get_agent_info("sql-pro");
```

**2. Cache Results Appropriately:**
```javascript
// Client-side caching for static data
let agentCache = null;
let cacheTimestamp = null;

function getCachedAgents() {
  const now = Date.now();
  const cacheAge = 5 * 60 * 1000; // 5 minutes

  if (!agentCache || !cacheTimestamp || (now - cacheTimestamp) > cacheAge) {
    agentCache = get_agents();
    cacheTimestamp = now;
  }

  return agentCache;
}
```

**3. Batch Operations When Possible:**
```javascript
// Process multiple tasks efficiently
async function getMultipleGuidance(tasks) {
  const recommendations = await Promise.all(
    tasks.map(task => get_recommended_agents(task))
  );

  const invocations = recommendations.map((agents, index) => ({
    agentName: agents[0].name,
    task: tasks[index],
    context: getContextForTask(tasks[index])
  }));

  const responses = await Promise.all(
    invocations.map(inv => invoke_agent(inv))
  );

  return responses;
}
```

### Integration Best Practices

**1. Error Recovery:**
```javascript
function robustAgentSearch(query, maxAttempts = 3) {
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      const results = find_agents(query);
      if (results.length > 0) {
        return results;
      }

      // Try broader search terms
      const broaderQuery = query.split(' ')[0]; // First word only
      const broaderResults = find_agents(broaderQuery);
      if (broaderResults.length > 0) {
        return broaderResults;
      }

    } catch (error) {
      console.warn(`Search attempt ${attempt} failed:`, error.message);
      if (attempt === maxAttempts) {
        // Final fallback
        return get_recommended_agents(query);
      }
    }
  }
}
```

**2. User Experience Optimization:**
```javascript
// Progressive disclosure pattern
function discoverAgents(userQuery) {
  // Step 1: Quick recommendations
  const quick = get_recommended_agents(userQuery);
  showQuickResults(quick);

  // Step 2: Comprehensive search (async)
  setTimeout(() => {
    const comprehensive = find_agents(userQuery);
    showComprehensiveResults(comprehensive);
  }, 0);

  // Step 3: Full catalog (if needed)
  return {
    quickResults: quick,
    getFullCatalog: () => get_agents()
  };
}
```

**3. Context Awareness:**
```javascript
// Extract project context for better guidance
function getProjectContext() {
  const context = [];

  // Technology stack detection
  if (hasFile("package.json")) {
    context.push("Node.js project");
  }
  if (hasFile("pom.xml") || hasFile("build.gradle")) {
    context.push("Java project");
  }
  if (hasFile("requirements.txt") || hasFile("pyproject.toml")) {
    context.push("Python project");
  }

  // Framework detection
  if (hasFile("spring-boot-starter")) {
    context.push("Spring Boot");
  }
  if (hasFile("react")) {
    context.push("React");
  }

  // Scale indicators
  const fileCount = getFileCount();
  if (fileCount > 1000) {
    context.push("Large codebase");
  }

  return context.join(", ");
}
```

This API reference provides comprehensive documentation for integrating with the Jenksy MCP server. Use this guide to build robust, efficient integrations that leverage the full capabilities of the specialized agent system.