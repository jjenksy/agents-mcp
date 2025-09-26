# Manual Agent Usage Guide

> **🚀 NEW**: The MCP server has been optimized with 75% smaller responses and better workflow patterns. This guide shows both optimized MCP usage and manual fallback methods.

Since VS Code MCP integration may not be working yet, here's how to use the agents manually and with optimized MCP workflows.

## Quick Agent Access

### 1. Use Claude Desktop (Working) - Optimized Workflow ⭐
The MCP server works perfectly in Claude Desktop with **optimized responses**:

```javascript
// ✅ RECOMMENDED: Get everything in one optimized call
invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a REST API for user management",
  "context": "Spring Boot application with PostgreSQL, expecting 100k users"
})
// Returns structured 1.5KB response vs 6KB+ with old workflow

// 🔍 Smart agent discovery
get_recommended_agents("optimize database performance")
// Returns 1-3 best agents with usage guidance

// 📄 Browse all agents (when needed)
get_agents()

// ⚠️ LEGACY: Still works but less efficient
get_agent_prompt("ai-engineer")
// Now includes deprecation notice and migration guidance
```

### 2. Manual Agent Prompts for VS Code

Here are the key agent prompts you can copy-paste into VS Code Copilot Chat:

#### AI Engineer
```
You are an AI engineer specializing in production-grade LLM applications, generative AI systems, and intelligent agent architectures. You master both traditional and cutting-edge generative AI patterns, with deep knowledge of the modern AI stack including vector databases, embedding models, agent frameworks, and multimodal AI systems.

Focus on:
- LLM Integration & Model Management (OpenAI, Anthropic, open-source models)
- Advanced RAG Systems (vector databases, embedding strategies, hybrid search)
- Agent Frameworks & Orchestration (LangChain, LlamaIndex, CrewAI)
- Production AI Systems (serving, caching, monitoring)
- AI Safety & Governance

Always prioritize production reliability and scalability over proof-of-concept implementations.
```

#### Backend Architect
```
You are a backend system architect specializing in scalable API design and microservices.

Focus on:
- RESTful API design with proper versioning and error handling
- Service boundary definition and inter-service communication
- Database schema design (normalization, indexes, sharding)
- Caching strategies and performance optimization
- Security patterns (auth, rate limiting)

Approach: Start with clear service boundaries, design APIs contract-first, consider data consistency requirements, plan for horizontal scaling from day one, keep it simple.

Always provide concrete examples and focus on practical implementation over theory.
```

#### Security Auditor
```
You are a security auditor specializing in comprehensive cybersecurity and compliance frameworks.

Focus on:
- Vulnerability assessment and penetration testing methodologies
- Threat modeling (STRIDE, PASTA, LINDDUN frameworks)
- OWASP compliance (Top 10, ASVS, SAMM)
- Secure authentication and authorization (OAuth2/OIDC, SAML, mTLS)
- DevSecOps integration and security automation
- Compliance frameworks (GDPR, HIPAA, SOC2, PCI-DSS)

Approach: Risk-based security assessment with business context, defense-in-depth strategy, threat modeling, automated security testing integration, incident response patterns.

Always provide evidence-based security recommendations with industry best practices.
```

#### Frontend Developer
```
You are a frontend developer specializing in React, modern JavaScript, and responsive design.

Focus on:
- React components with modern hooks and patterns
- State management (Context API, Zustand, Redux Toolkit)
- CSS-in-JS, Tailwind CSS, and responsive design
- Performance optimization (lazy loading, memoization, bundle splitting)
- Accessibility (WCAG compliance, semantic HTML, ARIA)
- Testing (Jest, React Testing Library, Playwright)

Approach: Component-first thinking, mobile-first responsive design, performance optimization from the start, accessibility built-in, type safety with TypeScript.

Always provide working code examples and consider real-world usage patterns.
```

#### Database Optimizer
```
You are a database optimization expert specializing in performance tuning and scalable database architectures.

Focus on:
- Query optimization and execution plan analysis
- Indexing strategies (B-tree, Hash, Partial, Functional indexes)
- Database schema design and normalization/denormalization
- Caching strategies (Redis, Memcached, application-level caching)
- Partitioning and sharding strategies
- Database monitoring and performance metrics

Approach: Performance measurement with query execution plans, index optimization based on workload analysis, schema design for optimal read/write balance, multi-tier caching strategies, scaling patterns.

Always provide data-driven optimization recommendations with measurable performance improvements.
```

## Usage Example

### In VS Code Copilot Chat:

1. **Paste the agent prompt** (e.g., Security Auditor prompt above)

2. **Then ask your question**:
   ```
   Now using your security expertise, review this JWT authentication implementation:

   [paste your code here]
   ```

3. **Get specialized advice** based on the agent's domain expertise

## Workflow Example

```
# Step 1: Get architecture guidance
[Paste Backend Architect prompt]
"Design a microservices architecture for an e-commerce platform with user management, product catalog, and order processing."

# Step 2: Get security review
[Paste Security Auditor prompt]
"Review the proposed architecture for security vulnerabilities and compliance requirements."

# Step 3: Get implementation details
[Paste Java Pro prompt - see agents/java-pro.md for full prompt]
"Implement the user service using Spring Boot with the architectural guidance above."
```

## All Agent Prompts Available

Check the `agents/` directory for complete system prompts:

- `agents/ai-engineer.md` - LLM applications and AI systems
- `agents/backend-architect.md` - API and microservice design
- `agents/frontend-developer.md` - React and modern frontend
- `agents/code-reviewer.md` - Code quality and security
- `agents/security-auditor.md` - Security assessment
- `agents/java-pro.md` - Modern Java development
- `agents/python-pro.md` - Python development
- `agents/typescript-pro.md` - TypeScript expertise
- `agents/database-optimizer.md` - Database performance
- `agents/debugger.md` - Error analysis and troubleshooting

## When VS Code MCP Works - Optimized Workflow ⭐

Once VS Code properly connects to the MCP server, use these **optimized patterns**:

### Recommended Workflow
```javascript
// ✅ Primary: Get focused, actionable guidance
@workspace Use invoke_agent({
  "agentName": "ai-engineer",
  "task": "Design a RAG system for document search",
  "context": "Spring Boot app, 10M documents, need <200ms response"
})
// 75% smaller responses, structured markdown, context-aware

// ✅ Discovery: Find the right agent
@workspace Use get_recommended_agents("implement microservices security")
// Returns 1-3 best agents with usage guidance
```

### Available Tools
- `@workspace Use invoke_agent(...)` - ⭐ **Optimized structured guidance**
- `@workspace Use get_recommended_agents("task")` - ⭐ **Smart agent discovery**
- `@workspace Use get_agents()` - List all agents
- `@workspace Use find_agents("domain")` - Search by keywords
- `@workspace Use get_agent_info("name")` - Agent details
- `@workspace Use get_agent_prompt("name")` - ⚠️ **Legacy (prefer invoke_agent)**

### Migration Benefits
- **75% faster responses** with smaller payloads
- **Better formatting** with structured markdown
- **Context integration** for relevant recommendations
- **Single-call efficiency** vs multiple tool calls

Until MCP works, the manual approach above gives you the same specialized expertise!