# Contributing to AI Agents MCP Server

Thank you for your interest in contributing! This guide will help you contribute agents, code, documentation, and bug reports.

## Table of Contents

- [Getting Started](#getting-started)
- [Contributing Agents](#contributing-agents)
- [Contributing Code](#contributing-code)
- [Documentation](#documentation)
- [Reporting Issues](#reporting-issues)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Testing](#testing)
- [Release Process](#release-process)

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Set up development environment** (see [Development Setup](#development-setup))
4. **Create a feature branch** for your contribution
5. **Make your changes** following our guidelines
6. **Test thoroughly** (see [Testing](#testing))
7. **Submit a pull request**

## Contributing Agents

**Agents are the heart of this project!** We welcome contributions of new specialized agents.

### Quick Agent Contribution

1. **Create your agent file** in `src/main/resources/agents/`:
   ```bash
   cp src/main/resources/agents/template.md src/main/resources/agents/your-agent.md
   ```

2. **Fill in the agent template** (see [Agent Template](#agent-template))

3. **Test your agent**:
   ```bash
   ./gradlew bootRun
   # Test: get_agent_info("your-agent")
   ```

4. **Submit a pull request** with:
   - Agent file
   - Description of the agent's expertise
   - Example use cases

### Agent Template

Create agents using this YAML frontmatter + markdown format:

```markdown
---
name: your-agent-name
description: Brief description of the agent's expertise and capabilities
tools: tool1, tool2
---

# Your Agent System Prompt

You are a specialized [domain] expert with deep knowledge in [specific areas].

## Core Expertise
- Expertise area 1
- Expertise area 2
- Expertise area 3

## Approach
Your approach to solving problems should:
1. Step-by-step methodology
2. Best practices and standards
3. Practical, actionable recommendations

## Key Principles
- Principle 1: Explanation
- Principle 2: Explanation
- Principle 3: Explanation

When responding to user requests:
- Be specific and actionable
- Include relevant code examples when applicable
- Consider enterprise and production requirements
- Provide security and performance considerations
```

### Agent Quality Guidelines

**Good agents have:**
- **Clear expertise domain** - Specific area of specialization
- **Comprehensive system prompt** - Detailed instructions and context
- **Practical approach** - Actionable, real-world guidance
- **Consistent voice** - Professional, helpful tone
- **Security awareness** - Considers security implications
- **Performance mindset** - Thinks about scalability and efficiency

**Agent naming conventions:**
- Use lowercase with hyphens: `backend-architect`, `security-auditor`
- Be descriptive but concise
- Avoid overly generic names like `helper` or `assistant`

### Agent Categories

We organize agents into these categories:

- **Architecture & Design** - System design, API architecture, databases
- **Programming Languages** - Language-specific expertise (java-pro, python-pro)
- **Quality & Security** - Code review, security auditing, testing
- **Infrastructure** - DevOps, deployment, monitoring, cloud
- **Specialized Domains** - AI/ML, mobile, blockchain, gaming
- **Business & Content** - Documentation, legal, marketing

## Contributing Code

### Areas for Code Contributions

- **Core MCP Server** - Spring Boot application improvements
- **MCP Tool Optimization** - Response format improvements, tool efficiency
- **Agent Management** - Loading, caching, validation
- **Performance** - Caching, optimization, monitoring
- **Security** - Authentication, input validation, audit logging
- **VS Code Integration** - Copilot-specific optimizations
- **Developer Experience** - Tooling, debugging, documentation
- **Testing** - Unit tests, integration tests, agent validation, MCP tool testing

### Code Contribution Process

1. **Check existing issues** - Look for related work or discussions
2. **Create an issue first** - Discuss significant changes before coding
3. **Follow coding standards** - See [Code Style](#code-style)
4. **Write tests** - Include appropriate test coverage
5. **Update documentation** - Keep docs in sync with code changes
6. **Submit pull request** - With clear description and test evidence

## Documentation

### Documentation Contributions

We welcome improvements to:
- **User guides** - Installation, usage, troubleshooting
- **Developer docs** - Architecture, contributing, API reference
- **Agent documentation** - Agent descriptions, examples, use cases
- **Deployment guides** - Production setup, monitoring, security

### Documentation Standards

- **Clear and concise** - Write for your intended audience
- **Complete examples** - Include working code samples
- **Up-to-date** - Ensure accuracy with current codebase
- **Well-organized** - Logical structure and navigation
- **Accessible** - Consider different skill levels

## Reporting Issues

### Before Reporting

1. **Search existing issues** - Check if already reported
2. **Try latest version** - Ensure issue exists in current release
3. **Gather information** - Logs, configuration, steps to reproduce

### Issue Types

- **Bug Reports** - Something doesn't work as expected
- **Feature Requests** - New functionality or improvements
- **Agent Requests** - Suggestions for new specialized agents
- **Documentation Issues** - Unclear or missing documentation

### Bug Report Template

```markdown
## Bug Description
Brief description of the issue

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g., macOS 14.0]
- Java Version: [e.g., 21]
- VS Code Version: [e.g., 1.85.0]
- MCP Server Version: [e.g., 1.0.0]

## Logs
```
Paste relevant logs here
```

## Additional Context
Any other relevant information
```

## MCP Tool Development Guidelines

### MCP Tool Standards (VS Code Optimized)

When developing MCP tools, follow these standards for optimal VS Code Copilot integration:

#### Response Format Standards

**Optimized Response Structure:**
```java
// ✅ Good: Concise, structured response
@Tool(description = "Get task-specific guidance. Use instead of get_agent_prompt for better results.")
public AgentResponse invokeAgent(AgentInvocation invocation) {
    return new AgentResponse(
        agent.name(),
        "mcp-optimized", // Generic identifier instead of specific model
        buildStructuredGuidance(agent, invocation), // Markdown formatted
        "success",
        contextKey
    );
}
```

**Tool Description Best Practices:**
- Include usage guidance to prevent redundant calls
- Recommend preferred alternatives for deprecated tools
- Specify optimal use cases
- Keep descriptions under 100 characters when possible

**Response Size Guidelines:**
- Target 75% reduction from legacy responses
- Use structured markdown (##, ###, bullet points)
- Limit to 3-4 key recommendations
- Include expert context inline, not as separate responses

#### Tool Annotation Patterns

```java
// ✅ Optimized tool with guidance
@Tool(
    description = "Get concise task guidance. Use instead of get_agent_prompt for actionable results.",
    name = "invoke_agent"
)
public AgentResponse invokeAgent(AgentInvocation invocation) {
    // Implementation optimized for VS Code consumption
}

// ✅ Discovery tool with clear purpose
@Tool(
    description = "Get 1-3 best agents for your task. More efficient than browsing all agents.",
    name = "get_recommended_agents"
)
public List<Agent> getRecommendedAgents(String task) {
    // Return focused recommendations (limit 3)
}

// ✅ Legacy tool with deprecation notice
@Tool(
    description = "[LEGACY] Get raw system prompt. PREFER invoke_agent for task-specific guidance.",
    name = "get_agent_prompt"
)
public String getAgentPrompt(String agentName) {
    // Include migration guidance in response
}
```

### Testing MCP Tools

Ensure MCP tools work optimally with different clients:

```java
@Test
void invokeAgent_shouldReturnOptimizedResponse() {
    // Test response size is reasonable
    AgentResponse response = agentService.invokeAgent(invocation);
    assertThat(response.content().length()).isLessThan(2000); // Size limit
    assertThat(response.content()).contains("##"); // Structured markdown
    assertThat(response.model()).isEqualTo("mcp-optimized"); // Generic identifier
}

@Test
void toolDescriptions_shouldIncludeUsageGuidance() {
    // Verify tool descriptions guide optimal usage
    Method method = AgentService.class.getMethod("invokeAgent", AgentInvocation.class);
    Tool annotation = method.getAnnotation(Tool.class);
    assertThat(annotation.description()).contains("Use instead of");
}
```

## Development Setup

### Prerequisites

- **Java 21+** - [Download from Adoptium](https://adoptium.net/)
- **Git** - Version control
- **IDE** - IntelliJ IDEA, VS Code (with GitHub Copilot for testing), or preferred Java IDE
- **VS Code with GitHub Copilot** - For testing MCP tool optimization

### Local Development

1. **Clone the repository**:
   ```bash
   git clone https://github.com/jenksy/agents-mcp.git
   cd agents-mcp
   ```

2. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

3. **Run in development mode**:
   ```bash
   ./gradlew bootRun
   ```

4. **Run tests**:
   ```bash
   ./gradlew test
   ```

### Development Configuration

The application supports development-specific configuration in `application.yml`:

```yaml
spring:
  profiles:
    active: development

jenksy:
  mcp:
    agents:
      hot-reload: true  # Reload agents on file changes

logging:
  level:
    com.jenksy.jenksymcp: DEBUG
```

## Code Style

### Java Code Style

- **Use modern Java 21 features** - Records, pattern matching, switch expressions
- **Follow Spring Boot conventions** - Configuration, service layers, error handling
- **Clear naming** - Descriptive class, method, and variable names
- **Comprehensive logging** - Use SLF4J with appropriate log levels
- **Input validation** - Validate all external inputs
- **Error handling** - Graceful degradation and meaningful error messages

### Code Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum
- **Imports**: Organize and remove unused imports
- **Braces**: Opening brace on same line

### Example Code Style

```java
@Service
@Slf4j
public class AgentService {

    private final Cache<String, Agent> agentCache;

    public AgentService(CacheManager cacheManager) {
        this.agentCache = cacheManager.getCache("agents");
    }

    @Tool(description = "Get agent information by name")
    public Agent getAgentInfo(@NotBlank String agentName) {
        log.debug("Retrieving agent info for: {}", agentName);

        return agentCache.get(agentName, name -> {
            log.info("Loading agent from storage: {}", name);
            return loadAgentFromFile(name);
        });
    }
}
```

## Testing

### Test Categories

- **Unit Tests** - Test individual components in isolation
- **Integration Tests** - Test component interactions
- **Agent Tests** - Validate agent definitions and responses
- **MCP Tool Tests** - Validate tool responses, sizes, format optimization
- **VS Code Integration Tests** - Test actual Copilot integration
- **End-to-End Tests** - Full workflow testing

### Testing Guidelines

- **Test coverage** - Aim for 80%+ coverage of business logic
- **Test naming** - Descriptive test method names
- **Arrange-Act-Assert** - Clear test structure
- **Mock external dependencies** - Use Mockito for external services
- **Test data** - Use realistic test data and scenarios

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "AgentServiceTest"

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Example Test

```java
@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache<String, Agent> agentCache;

    @InjectMocks
    private AgentService agentService;

    @Test
    void shouldReturnAgentInfo_whenAgentExists() {
        // Arrange
        String agentName = "test-agent";
        Agent expectedAgent = new Agent(agentName, "Test agent", "mcp-optimized", List.of(), "Test prompt");
        when(cacheManager.getCache("agents")).thenReturn(agentCache);
        when(agentCache.get(eq(agentName), any())).thenReturn(expectedAgent);

        // Act
        Agent result = agentService.getAgentInfo(agentName);

        // Assert
        assertThat(result).isEqualTo(expectedAgent);
        verify(agentCache).get(eq(agentName), any());
    }
}
```

## Release Process

### Version Numbers

We use [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Release Steps

1. **Update version** in `build.gradle`
2. **Update CHANGELOG.md** with changes
3. **Create release PR** for review
4. **Tag release** after merge:
   ```bash
   git tag v1.2.0
   git push origin v1.2.0
   ```
5. **GitHub Actions** automatically builds and publishes release

## Community Guidelines

### Code of Conduct

- **Be respectful** - Treat all contributors with respect
- **Be inclusive** - Welcome contributors of all backgrounds
- **Be collaborative** - Work together to improve the project
- **Be constructive** - Provide helpful feedback and suggestions

### Getting Help

- **GitHub Discussions** - For questions and community discussion
- **GitHub Issues** - For bug reports and feature requests
- **Documentation** - Check existing docs first
- **Examples** - Look at existing code for patterns

## Recognition

All contributors will be recognized in:
- **README.md** - Contributors section
- **CHANGELOG.md** - Release notes
- **GitHub** - Contributor graphs and statistics

Thank you for contributing to the AI Agents MCP Server! 🎉