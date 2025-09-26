# Agent Creation Guide

This guide covers how to create custom AI agents for the Jenksy MCP server. Agents are specialized AI assistants that provide domain-specific expertise through the Model Context Protocol (MCP).

## Table of Contents

1. [Overview](#overview)
2. [Agent Structure](#agent-structure)
3. [Creating Your First Agent](#creating-your-first-agent)
4. [YAML Frontmatter Reference](#yaml-frontmatter-reference)
5. [System Prompt Best Practices](#system-prompt-best-practices)
6. [Advanced Agent Features](#advanced-agent-features)
7. [Testing Your Agent](#testing-your-agent)
8. [Examples](#examples)
9. [Troubleshooting](#troubleshooting)

## Overview

Agents in the Jenksy MCP system are defined as Markdown files with YAML frontmatter. Each agent consists of:

- **YAML frontmatter**: Metadata including name, description, model preference, and tools
- **System prompt**: The core instructions that define the agent's behavior and expertise

The system currently includes 20 specialized agents covering domains like AI engineering, backend architecture, frontend development, security auditing, and more.

## Agent Structure

Every agent file follows this structure:

```markdown
---
name: agent-name
description: Brief description of the agent's capabilities
tools: optional,tool,list
---

# Agent's specialized system prompt content goes here
The system prompt defines the agent's personality, expertise, and behavior.
It should be comprehensive and specific to the agent's domain.
```

**Note**: Model specifications have been removed as VS Code Copilot handles model selection automatically.

### File Naming Convention

- Use kebab-case for filenames: `my-agent.md`
- Place files in `src/main/resources/agents/`
- The filename (without .md) becomes the default agent name if not specified in frontmatter

## Creating Your First Agent

### Step 1: Create the Agent File

Create a new file in `src/main/resources/agents/` following the naming convention:

```bash
touch src/main/resources/agents/data-scientist.md
```

### Step 2: Add YAML Frontmatter

```yaml
---
name: data-scientist
description: Expert in data analysis, machine learning, and statistical modeling
tools:
---
```

### Step 3: Write the System Prompt

The system prompt should be comprehensive and define:

- The agent's role and expertise
- Specific capabilities and knowledge areas
- Behavioral traits and approach
- Response patterns and methodologies

```markdown
You are a data scientist specializing in statistical analysis, machine learning, and data-driven insights.

## Purpose
Expert data scientist with deep knowledge of statistical methods, machine learning algorithms, and data visualization techniques. Transforms raw data into actionable business insights.

## Capabilities

### Statistical Analysis
- Descriptive and inferential statistics
- Hypothesis testing and A/B testing
- Time series analysis and forecasting
- Regression analysis and correlation studies

### Machine Learning
- Supervised learning: classification and regression
- Unsupervised learning: clustering and dimensionality reduction
- Model selection and hyperparameter tuning
- Feature engineering and selection

## Response Approach
1. **Understand the data problem** and business context
2. **Recommend appropriate methods** based on data type and objectives
3. **Provide code examples** in Python/R with explanations
4. **Suggest validation strategies** and performance metrics
5. **Consider ethical implications** of data usage and model bias
```

### Step 4: Test and Validate

After creating your agent, rebuild the application and test it:

```bash
./gradlew clean build
java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

## YAML Frontmatter Reference

### Required Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `name` | string | Unique identifier for the agent | `data-scientist` |
| `description` | string | Brief summary of agent capabilities | `Expert in data analysis and ML` |

### Optional Fields

| Field | Type | Description | Default | Example |
|-------|------|-------------|---------|---------|
| `model` | string | Preferred AI model | `sonnet` | `opus`, `sonnet`, `haiku` |
| `tools` | string | Comma-separated list of tools | empty | `calculator,web-search` |

### Model Options

- **opus**: Most capable model for complex reasoning and code generation
- **sonnet**: Balanced performance and speed (default)
- **haiku**: Fastest model for simple tasks

### Tools Integration

Currently, the tools field is reserved for future functionality. The MCP server focuses on providing specialized system prompts rather than tool integrations.

## System Prompt Best Practices

### Structure Your Prompt

1. **Clear Role Definition**: Start with a clear statement of the agent's role
2. **Purpose Section**: Explain the agent's primary function
3. **Capabilities Section**: Detail specific skills and knowledge areas
4. **Behavioral Traits**: Define how the agent should interact
5. **Response Approach**: Provide a step-by-step methodology

### Writing Guidelines

**Be Specific**: Use concrete examples and detailed descriptions
```markdown
# Good
- Implement OAuth 2.0 flows with PKCE for SPA applications
- Design RESTful APIs following OpenAPI 3.0 specifications

# Avoid
- Handle authentication
- Create APIs
```

**Use Action-Oriented Language**: Focus on what the agent does
```markdown
# Good
You analyze code for security vulnerabilities and performance bottlenecks.

# Avoid
You are someone who knows about security and performance.
```

**Include Domain Knowledge**: Reference specific tools, frameworks, and methodologies
```markdown
### Frontend Frameworks
- React 18+ with hooks, context, and concurrent features
- Next.js 14 with App Router and server components
- TypeScript for type safety and developer experience
```

**Define Response Patterns**: Specify how the agent should structure responses
```markdown
## Response Approach
1. **Analyze the requirement** for scalability and performance needs
2. **Design the architecture** with specific technology recommendations
3. **Provide implementation examples** with best practices
4. **Include testing strategies** and monitoring considerations
```

### Content Guidelines

**Length**: System prompts should be comprehensive (1000-3000+ words for complex domains)
**Depth**: Cover both high-level concepts and implementation details
**Relevance**: Stay focused on the agent's specific domain
**Currency**: Reference current tools, frameworks, and best practices

## Advanced Agent Features

### Multi-Domain Expertise

For agents covering multiple related domains:

```markdown
## Capabilities

### Backend Development
- RESTful API design and implementation
- Database schema design and optimization
- Microservices architecture patterns

### DevOps Integration
- CI/CD pipeline configuration
- Container orchestration with Kubernetes
- Infrastructure as Code with Terraform
```

### Specialized Methodologies

Include specific frameworks and methodologies:

```markdown
## Security Methodology
1. **Threat Modeling**: STRIDE analysis and attack tree creation
2. **Code Review**: OWASP Top 10 and secure coding practices
3. **Penetration Testing**: Automated scanning and manual verification
4. **Compliance**: SOC 2, GDPR, and industry-specific requirements
```

### Context-Aware Responses

Design agents to adapt based on project context:

```markdown
## Response Adaptation
- **Startup Context**: Focus on MVP development and rapid iteration
- **Enterprise Context**: Emphasize scalability, security, and compliance
- **Open Source**: Consider community standards and contribution guidelines
```

## Testing Your Agent

### Manual Testing

1. **Rebuild the application** after adding your agent
2. **Start the MCP server** and verify agent loading in logs
3. **Test agent discovery** using MCP tools:
   - `get_agents()` - Verify your agent appears in the list
   - `find_agents("your-domain")` - Test domain-based search
   - `get_agent_info("your-agent")` - Validate metadata
4. **Test agent invocation** with various tasks

### Integration Testing

Test your agent through connected tools:

```javascript
// Claude Desktop or VS Code Copilot
invoke_agent({
  "agentName": "data-scientist",
  "task": "Analyze customer churn data and recommend retention strategies",
  "context": "E-commerce platform with 100k monthly active users"
})
```

### Validation Checklist

- [ ] Agent appears in `get_agents()` response
- [ ] Description accurately reflects capabilities
- [ ] System prompt is comprehensive and well-structured
- [ ] Agent provides domain-specific guidance
- [ ] No syntax errors in YAML frontmatter
- [ ] File naming follows convention

## Examples

### Example 1: Security Auditor

```markdown
---
name: security-auditor
description: Elite security expert specializing in vulnerability assessment, penetration testing, and security architecture review
---

You are a security auditor specializing in comprehensive security assessments and vulnerability analysis.

## Purpose
Elite cybersecurity professional with expertise in vulnerability assessment, penetration testing, and security architecture review. Provides actionable security recommendations for enterprise applications and infrastructure.

## Capabilities

### Vulnerability Assessment
- OWASP Top 10 and SANS Top 25 vulnerability analysis
- Static Application Security Testing (SAST) with SonarQube, Checkmarx
- Dynamic Application Security Testing (DAST) with OWASP ZAP, Burp Suite
- Infrastructure vulnerability scanning with Nessus, Qualys

### Penetration Testing
- Web application penetration testing methodologies
- Network penetration testing and lateral movement
- API security testing and authentication bypass
- Social engineering and phishing assessment

### Security Architecture
- Zero Trust architecture design and implementation
- Secure software development lifecycle (SSDLC) integration
- Cloud security posture management (AWS, Azure, GCP)
- Container and Kubernetes security hardening

## Response Approach
1. **Assess the attack surface** and identify potential entry points
2. **Prioritize vulnerabilities** by risk level and business impact
3. **Provide specific remediation** with implementation guidance
4. **Recommend security controls** and monitoring strategies
5. **Include compliance considerations** for relevant standards
```

### Example 2: Performance Engineer

```markdown
---
name: performance-engineer
description: Specialist in application performance optimization, load testing, and scalability engineering
---

You are a performance engineer specializing in application optimization and scalability analysis.

## Purpose
Expert performance engineer focused on optimizing application speed, scalability, and resource efficiency. Provides data-driven recommendations for performance improvements.

## Capabilities

### Performance Analysis
- Application profiling with JProfiler, VisualVM, Chrome DevTools
- Database query optimization and index analysis
- Memory leak detection and garbage collection tuning
- Network latency and throughput optimization

### Load Testing
- Load testing with JMeter, Gatling, Artillery
- Stress testing and capacity planning
- Performance benchmarking and regression testing
- Real user monitoring (RUM) and synthetic monitoring

### Scalability Engineering
- Horizontal and vertical scaling strategies
- Caching strategies with Redis, Memcached
- CDN optimization and edge computing
- Microservices performance considerations

## Response Approach
1. **Identify performance bottlenecks** through profiling and monitoring
2. **Quantify performance impact** with specific metrics
3. **Recommend optimization strategies** with implementation priorities
4. **Design load testing scenarios** for validation
5. **Establish monitoring and alerting** for ongoing performance tracking
```

## Troubleshooting

### Common Issues

**Agent Not Loading**
- Check YAML frontmatter syntax (use YAML validator)
- Verify file is in correct directory: `src/main/resources/agents/`
- Ensure file has `.md` extension
- Check application logs for parsing errors

**Agent Not Found in Search**
- Verify agent name matches exactly (case-sensitive)
- Check that description contains expected keywords
- Rebuild application after adding agent

**Invalid YAML Frontmatter**
- Use proper YAML syntax with colons and spacing
- Quote values containing special characters
- Ensure proper indentation (spaces, not tabs)

### Debugging Tips

**Enable Debug Logging**
Add to `application.yaml`:
```yaml
logging:
  level:
    com.jenksy.jenksymcp.service.AgentService: DEBUG
```

**Validate YAML Syntax**
Use online YAML validators or command-line tools:
```bash
python -c "import yaml; yaml.safe_load(open('agent.md').read().split('---')[1])"
```

**Check Agent Loading**
Monitor application startup logs for agent loading messages:
```
[INFO] Loading agents from classpath and filesystem
[INFO] Loaded 21 agents from classpath
[DEBUG] Loaded agent from classpath: data-scientist
```

### Best Practices for Debugging

1. **Start Simple**: Create minimal agent first, then add complexity
2. **Test Incrementally**: Validate each section as you build
3. **Use Examples**: Base new agents on existing successful ones
4. **Monitor Logs**: Check for parsing errors and warnings
5. **Validate Responses**: Test agent behavior with various tasks

## Contributing Agents

### Guidelines for Contribution

1. **Follow naming conventions** and structure requirements
2. **Ensure comprehensive coverage** of the domain
3. **Include practical examples** and use cases
4. **Test thoroughly** before submitting
5. **Document any special requirements** or dependencies

### Quality Standards

- System prompts should be 1000+ words for complex domains
- Include specific tools, frameworks, and methodologies
- Provide clear response patterns and approaches
- Focus on practical, actionable guidance
- Maintain professional tone without unnecessary complexity

### Submission Process

1. Create agent file following this guide
2. Test locally with various scenarios
3. Submit via pull request with description
4. Include test cases and expected behaviors
5. Update documentation as needed

This guide provides the foundation for creating effective AI agents in the Jenksy MCP system. For additional help, refer to existing agents in the `src/main/resources/agents/` directory as examples.