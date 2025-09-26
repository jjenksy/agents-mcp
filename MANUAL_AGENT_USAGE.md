# Manual Agent Usage Guide

> **🚀 Important Update**: VS Code Copilot uses natural language interaction, not direct tool syntax! You speak naturally, and Copilot translates to MCP tool calls automatically.

This guide explains how MCP tools actually work in different environments and provides manual fallback methods.

## Quick Agent Access

### 1. Use Claude Desktop (Direct Tool Syntax Works)
Claude Desktop exposes the actual MCP tool interface:

```javascript
// Claude Desktop allows direct tool calls
invoke_agent({
  "agentName": "backend-architect",
  "task": "Design a REST API for user management",
  "context": "Spring Boot application with PostgreSQL, expecting 100k users"
})

// Other tools work directly too
get_recommended_agents("optimize database performance")
get_agents()
get_agent_info("ai-engineer")
```

**Note:** This direct syntax ONLY works in Claude Desktop, not in VS Code Copilot!

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

## How VS Code Copilot Actually Works ⭐

VS Code Copilot uses **natural language**, not tool syntax:

### Natural Language Interaction
```
// ✅ CORRECT: Natural language requests
"Please use the ai-engineer agent to design a RAG system for document search in a Spring Boot app with 10M documents needing <200ms response time"

// ✅ CORRECT: Discovery through conversation
"Which agents can help implement microservices security?"

// ❌ WRONG: This syntax doesn't work in VS Code
@workspace Use invoke_agent({...})  // Won't work!
```

### What Happens Behind the Scenes
When you type naturally, VS Code Copilot:
1. Parses your natural language
2. Identifies the agent and task
3. Extracts context from your description
4. Calls the appropriate MCP tool automatically
5. Uses the agent's response to guide its answer

### Key Differences Between Environments

| Environment | How to Use | Example |
|------------|------------|----------|
| **VS Code Copilot** | Natural language only | "Use the ai-engineer agent to help..." |
| **Claude Desktop** | Direct tool syntax | `invoke_agent({...})` |
| **Manual (Copy/Paste)** | Paste agent prompt | Copy prompt from agents/ directory |

### Benefits of Natural Language in VS Code
- **More intuitive** - Just describe what you need
- **No syntax to learn** - Copilot handles the technical parts
- **Context preserved** - Natural conversation flow
- **Automatic optimization** - Copilot manages tool calls efficiently