# Agent Management Guide

Comprehensive guide for managing AI agents in the Jenksy MCP Server local development environment. This covers agent lifecycle, testing, optimization, and best practices for agent development workflows.

## Table of Contents

1. [Overview](#overview)
2. [Agent Lifecycle Management](#agent-lifecycle-management)
3. [Local Development Workflow](#local-development-workflow)
4. [Agent Testing and Validation](#agent-testing-and-validation)
5. [Performance Optimization](#performance-optimization)
6. [Hot Reload Development](#hot-reload-development)
7. [Dashboard-Based Management](#dashboard-based-management)
8. [Agent Creation Workflow](#agent-creation-workflow)
9. [Troubleshooting and Debugging](#troubleshooting-and-debugging)
10. [Best Practices](#best-practices)

## Overview

The Jenksy MCP Server provides comprehensive agent management capabilities designed for local development workflows. With 20 specialized agents covering domains from AI engineering to security auditing, the system supports the full agent development lifecycle.

### Agent Management Features

- **Hot Reload**: Instant agent updates without server restart
- **Interactive Testing**: Dashboard-based agent testing with immediate feedback
- **Performance Monitoring**: Agent loading time and memory usage tracking
- **Validation**: YAML frontmatter and system prompt validation
- **Dashboard Integration**: Web-based agent browsing and management
- **Search and Discovery**: Domain-based agent search capabilities

## Agent Lifecycle Management

### 1. Development Phase

**Location**: `src/main/resources/agents/`

**File Structure**:
```
src/main/resources/agents/
├── ai-engineer.md
├── backend-architect.md
├── security-auditor.md
├── frontend-developer.md
└── [custom-agent].md
```

**Development Commands**:
```bash
# Create new agent
touch src/main/resources/agents/my-new-agent.md

# Edit agent content
vim src/main/resources/agents/my-new-agent.md

# Validate agent format
head -20 src/main/resources/agents/my-new-agent.md
```

### 2. Testing Phase

**Dashboard Testing**:
1. Access: http://localhost:8080/dashboard
2. Navigate to Agent Management section
3. Search for your agent
4. Use "Test Agent" functionality
5. Validate response and performance

**API Testing**:
```bash
# Test agent info retrieval
curl "http://localhost:8080/api/dashboard/agents/search?query=my-new-agent"

# Test agent functionality
curl -X POST http://localhost:8080/api/dashboard/agents/test \
  -H "Content-Type: application/json" \
  -d '{"agentName":"my-new-agent","task":"Test task description"}'
```

### 3. Integration Phase

**MCP Integration Testing**:
```
# In VS Code Copilot
"Please use the my-new-agent agent to help with [specific task]"

# In Claude Desktop
invoke_agent({
  "agentName": "my-new-agent",
  "task": "Specific task description",
  "context": "Relevant context information"
})
```

### 4. Production Phase

**Performance Validation**:
```bash
# Check agent loading performance
curl http://localhost:8080/actuator/local-dev | jq .agents

# Monitor memory impact
curl http://localhost:8080/api/dashboard/metrics | jq .system
```

## Local Development Workflow

### Hot Reload Development Setup

**Enable Hot Reload**:
```bash
# Set development profile
export SPRING_PROFILES_ACTIVE=development

# Start with hot reload enabled
./gradlew bootRun
```

**Configuration**:
```yaml
# application-development.yml
spring:
  config:
    activate:
      on-profile: development

jenksy:
  mcp:
    agents:
      hot-reload: true

logging:
  level:
    com.jenksy.jenksymcp: DEBUG
```

### Development Iteration Cycle

1. **Edit Agent File**:
   ```bash
   vim src/main/resources/agents/my-agent.md
   ```

2. **Trigger Reload** (with hot reload enabled):
   - File system watch automatically detects changes
   - Agent reloaded without server restart
   - Dashboard shows updated agent immediately

3. **Test Changes**:
   ```bash
   # Quick validation
   curl "http://localhost:8080/api/dashboard/agents/search?query=my-agent"

   # Interactive testing via dashboard
   open http://localhost:8080/dashboard
   ```

4. **Validate Performance**:
   ```bash
   # Check reload time
   grep "agent reload" logs/application.log

   # Monitor memory usage
   curl http://localhost:8080/actuator/local-dev | jq .memory
   ```

### Rapid Prototyping Workflow

**Quick Agent Creation**:
```bash
#!/bin/bash
# create-agent.sh - Quick agent creation script

AGENT_NAME=$1
AGENT_DESC=$2

if [ -z "$AGENT_NAME" ] || [ -z "$AGENT_DESC" ]; then
    echo "Usage: ./create-agent.sh <name> <description>"
    exit 1
fi

cat > "src/main/resources/agents/${AGENT_NAME}.md" << EOF
---
name: ${AGENT_NAME}
description: ${AGENT_DESC}
---

You are a ${AGENT_NAME} specialist focused on ${AGENT_DESC}.

## Purpose
[Define the agent's primary purpose and scope]

## Capabilities
[List specific capabilities and expertise areas]

## Approach
[Describe the agent's methodology and approach]

## Best Practices
[Include relevant best practices and guidelines]
EOF

echo "Created agent: ${AGENT_NAME}"
echo "Edit: src/main/resources/agents/${AGENT_NAME}.md"
echo "Test: http://localhost:8080/dashboard"
```

## Agent Testing and Validation

### Dashboard-Based Testing

**Interactive Testing Features**:
- **Agent Selection**: Dropdown with all available agents
- **Task Input**: Multi-line text area for test scenarios
- **Response Timing**: Execution time measurement
- **Result Display**: Formatted response with status indicators
- **History Tracking**: Previous test results and comparisons

**Testing Workflow**:
1. Open dashboard: http://localhost:8080/dashboard
2. Click "Test Agent" button
3. Select agent from dropdown
4. Enter test task description
5. Review response and timing
6. Iterate based on results

### API-Based Testing

**Automated Testing Script**:
```bash
#!/bin/bash
# agent-test-suite.sh - Comprehensive agent testing

AGENTS=("ai-engineer" "backend-architect" "security-auditor" "frontend-developer")
TEST_TASK="Design a secure authentication system"

echo "=== Agent Testing Suite ==="

for agent in "${AGENTS[@]}"; do
    echo "Testing agent: $agent"

    START_TIME=$(date +%s%N)

    RESPONSE=$(curl -s -X POST http://localhost:8080/api/dashboard/agents/test \
        -H "Content-Type: application/json" \
        -d "{\"agentName\":\"$agent\",\"task\":\"$TEST_TASK\"}")

    END_TIME=$(date +%s%N)
    DURATION=$(( (END_TIME - START_TIME) / 1000000 ))  # Convert to milliseconds

    STATUS=$(echo "$RESPONSE" | jq -r .status)

    echo "  Status: $STATUS"
    echo "  Response Time: ${DURATION}ms"
    echo "  ---"
done
```

### Validation Framework

**Agent Validation Checklist**:
- [ ] YAML frontmatter is valid
- [ ] Required fields present (name, description)
- [ ] System prompt is comprehensive
- [ ] Agent loads without errors
- [ ] Response time < 1 second
- [ ] Memory usage < 10MB increase
- [ ] Dashboard test passes
- [ ] MCP integration works

**Automated Validation**:
```bash
#!/bin/bash
# validate-agent.sh - Agent validation script

AGENT_FILE=$1

if [ ! -f "$AGENT_FILE" ]; then
    echo "Error: Agent file not found: $AGENT_FILE"
    exit 1
fi

echo "Validating agent: $AGENT_FILE"

# Check YAML frontmatter
if ! head -20 "$AGENT_FILE" | grep -q "^---$"; then
    echo "❌ Invalid YAML frontmatter format"
    exit 1
fi

# Extract agent name
AGENT_NAME=$(head -20 "$AGENT_FILE" | sed -n '/^name:/p' | sed 's/name: *//')

if [ -z "$AGENT_NAME" ]; then
    echo "❌ Agent name not found in frontmatter"
    exit 1
fi

# Check if agent loads
curl -s "http://localhost:8080/api/dashboard/agents/search?query=$AGENT_NAME" | \
    jq -e '.[] | select(.name == "'$AGENT_NAME'")' > /dev/null

if [ $? -eq 0 ]; then
    echo "✅ Agent loads successfully"
else
    echo "❌ Agent failed to load"
    exit 1
fi

# Performance test
TEST_RESPONSE=$(curl -s -X POST http://localhost:8080/api/dashboard/agents/test \
    -H "Content-Type: application/json" \
    -d "{\"agentName\":\"$AGENT_NAME\",\"task\":\"Test validation\"}")

if echo "$TEST_RESPONSE" | jq -e '.status == "success"' > /dev/null; then
    echo "✅ Agent test passed"
else
    echo "❌ Agent test failed"
    exit 1
fi

echo "✅ Agent validation complete: $AGENT_NAME"
```

## Performance Optimization

### Agent Loading Performance

**Parallel Loading Optimization**:
```java
// AgentService implements parallel loading
agentPaths.parallelStream().forEach(this::loadAgentFromPath);
```

**Benefits**:
- Utilizes multiple CPU cores
- Reduces total loading time
- Scales with core count
- Maintains individual agent isolation

**Monitoring**:
```bash
# Monitor agent loading performance
curl http://localhost:8080/actuator/local-dev | jq .agents

# Check individual agent metrics
tail -f logs/application.log | grep "Loaded agent"
```

### Memory Optimization

**Agent Memory Footprint**:
- Base agent record: ~1KB
- System prompt: 2-10KB
- Total per agent: ~5-15KB
- 20 agents: ~100-300KB total

**Optimization Strategies**:
1. **Efficient Data Structures**: Immutable records for agent metadata
2. **String Interning**: Automatic string deduplication via G1GC
3. **Weak References**: Non-critical cached data uses weak references
4. **Lazy Loading**: Agent content loaded only when accessed

### Cache Optimization

**Agent Caching Strategy**:
```java
// Optimized cache configuration
@Cacheable(value = "agents", key = "#methodName")
public List<Agent> getAgents() { ... }

// TTL configuration for development
spring:
  cache:
    caffeine:
      spec: maximumSize=200,expireAfterAccess=15m
```

**Cache Performance Monitoring**:
```bash
# Cache statistics
curl http://localhost:8080/api/dashboard/metrics | jq .caches

# Cache management
curl -X POST http://localhost:8080/api/dashboard/cache/clear \
  -H "Content-Type: application/json" \
  -d '{"cacheName":"agents"}'
```

## Hot Reload Development

### Configuration Setup

**Enable Hot Reload**:
```yaml
# application-development.yml
jenksy:
  mcp:
    agents:
      hot-reload: true
      watch-interval: 2s  # File system polling interval
```

**File System Watching**:
```java
// Automatic file system monitoring
@EventListener
public void onFileSystemChange(FileSystemEvent event) {
    if (event.getPath().endsWith(".md")) {
        reloadAgent(event.getPath());
    }
}
```

### Hot Reload Workflow

1. **File Modification Detection**:
   - File system watcher monitors `src/main/resources/agents/`
   - Changes detected within 2 seconds
   - Only `.md` files trigger reload

2. **Agent Revalidation**:
   - YAML frontmatter parsing
   - System prompt validation
   - Dependency checking

3. **Cache Invalidation**:
   - Agent metadata cache cleared
   - Search cache invalidated
   - Context cache preserved

4. **Dashboard Notification**:
   - Dashboard notification on next poll
   - Agent list update on refresh
   - Status indicator refresh

### Hot Reload Limitations

**Supported Changes**:
- Agent description updates
- System prompt modifications
- YAML frontmatter changes
- New agent creation

**Unsupported Changes**:
- Agent name changes (require restart)
- Structural configuration changes
- Java code modifications

## Dashboard-Based Management

### Agent Browser Interface

**Features**:
- **Grid View**: Visual agent cards with descriptions
- **Search Functionality**: Interactive search by name, description, or content
- **Filtering**: Filter by domain, capabilities, or tags
- **Sorting**: Sort by name, creation date, or usage frequency

**Usage**:
1. Navigate to http://localhost:8080/dashboard
2. Access "Agent Management" section
3. Browse available agents
4. Use search to find specific agents
5. Click on agent cards for detailed information

### Agent Testing Interface

**Interactive Testing**:
- **Agent Selection**: Dropdown with autocomplete
- **Task Input**: Rich text editor with syntax highlighting
- **Context Provision**: Optional context field for complex scenarios
- **Response Display**: Formatted output with timing metrics
- **History**: Previous test results and comparisons

**Testing Scenarios**:
```javascript
// Example test scenarios for different agents

// AI Engineer
{
  "agentName": "ai-engineer",
  "task": "Design a RAG system for document search with 10M+ documents",
  "context": "Spring Boot application, PostgreSQL database, Redis cache"
}

// Backend Architect
{
  "agentName": "backend-architect",
  "task": "Design microservices architecture for e-commerce platform",
  "context": "High-traffic system, 100k+ concurrent users"
}

// Security Auditor
{
  "agentName": "security-auditor",
  "task": "Review authentication implementation for vulnerabilities",
  "context": "JWT-based auth with refresh tokens, Spring Security"
}
```

### Basic Monitoring

**Agent Activity Tracking**:
- **Invocation Metrics**: Count and frequency of agent calls
- **Performance Tracking**: Response times and resource usage
- **Error Monitoring**: Failed invocations and error patterns
- **Usage Analytics**: Most popular agents and use cases

**HTTP Polling Updates**:
```javascript
// Regular dashboard updates via polling
setInterval(async () => {
  const response = await fetch('/api/dashboard/status');
  const status = await response.json();
  updateDashboard(status);
}, 5000);

// Poll for agent metrics
setInterval(async () => {
  const response = await fetch('/api/dashboard/metrics');
  const metrics = await response.json();
  updateAgentMetrics(metrics);
}, 10000);

    if (data.type === 'agent_reload') {
        refreshAgentList();
    }
};
```

## Agent Creation Workflow

### Step-by-Step Creation Process

**1. Planning Phase**:
```bash
# Define agent requirements
AGENT_DOMAIN="database-optimization"
AGENT_DESCRIPTION="SQL query optimization and database performance tuning"
AGENT_EXPERTISE="PostgreSQL, MySQL, MongoDB performance optimization"
```

**2. File Creation**:
```bash
# Create agent file
touch "src/main/resources/agents/${AGENT_DOMAIN}.md"
```

**3. Structure Definition**:
```markdown
---
name: database-optimizer-pro
description: Advanced database performance optimization and query tuning specialist
tools: sql-analyzer,performance-profiler
---

# Database Optimization Specialist

You are an expert database performance optimization specialist with deep knowledge of relational and NoSQL databases.

## Core Expertise

### Query Optimization
- SQL query analysis and optimization
- Index strategy and implementation
- Query execution plan analysis
- Performance bottleneck identification

### Database Performance
- Memory and storage optimization
- Connection pool management
- Caching strategies
- Replication and sharding

## Approach
1. **Analysis**: Examine current performance metrics
2. **Identification**: Locate bottlenecks and inefficiencies
3. **Optimization**: Implement targeted improvements
4. **Validation**: Measure and verify performance gains
5. **Documentation**: Provide clear implementation guidance
```

**4. Testing and Validation**:
```bash
# Validate agent format
./validate-agent.sh src/main/resources/agents/database-optimizer-pro.md

# Test via dashboard
open http://localhost:8080/dashboard

# Test via API
curl -X POST http://localhost:8080/api/dashboard/agents/test \
  -H "Content-Type: application/json" \
  -d '{"agentName":"database-optimizer-pro","task":"Optimize slow SELECT queries"}'
```

### Agent Template System

**Base Template**:
```markdown
---
name: {{AGENT_NAME}}
description: {{AGENT_DESCRIPTION}}
tools: {{OPTIONAL_TOOLS}}
---

# {{AGENT_TITLE}}

You are a {{AGENT_EXPERTISE}} specialist.

## Purpose
{{DEFINE_PURPOSE}}

## Core Capabilities
{{LIST_CAPABILITIES}}

## Methodology
{{DESCRIBE_APPROACH}}

## Best Practices
{{INCLUDE_GUIDELINES}}

## Common Scenarios
{{PROVIDE_EXAMPLES}}
```

**Template Variables**:
- `{{AGENT_NAME}}`: kebab-case identifier
- `{{AGENT_DESCRIPTION}}`: Short description for discovery
- `{{AGENT_TITLE}}`: Human-readable title
- `{{AGENT_EXPERTISE}}`: Domain expertise area
- `{{OPTIONAL_TOOLS}}`: Comma-separated tool list

### Automated Agent Generation

**Generation Script**:
```bash
#!/bin/bash
# generate-agent.sh - Automated agent generation

TEMPLATE_FILE="templates/agent-template.md"
OUTPUT_DIR="src/main/resources/agents"

echo "Agent Generator"
echo "==============="

read -p "Agent name (kebab-case): " AGENT_NAME
read -p "Agent description: " AGENT_DESC
read -p "Domain expertise: " AGENT_EXPERTISE
read -p "Optional tools (comma-separated): " AGENT_TOOLS

# Generate agent file
sed -e "s/{{AGENT_NAME}}/$AGENT_NAME/g" \
    -e "s/{{AGENT_DESCRIPTION}}/$AGENT_DESC/g" \
    -e "s/{{AGENT_EXPERTISE}}/$AGENT_EXPERTISE/g" \
    -e "s/{{OPTIONAL_TOOLS}}/$AGENT_TOOLS/g" \
    "$TEMPLATE_FILE" > "$OUTPUT_DIR/$AGENT_NAME.md"

echo "Generated agent: $OUTPUT_DIR/$AGENT_NAME.md"
echo "Next steps:"
echo "1. Edit the generated file to add specific content"
echo "2. Test via dashboard: http://localhost:8080/dashboard"
echo "3. Validate with: ./validate-agent.sh $OUTPUT_DIR/$AGENT_NAME.md"
```

## Troubleshooting and Debugging

### Common Issues

**1. Agent Not Loading**:
```bash
# Check agent file format
head -20 src/main/resources/agents/problem-agent.md

# Verify YAML frontmatter
grep -A 10 "^---$" src/main/resources/agents/problem-agent.md

# Check server logs
grep "problem-agent" logs/application.log
```

**2. Hot Reload Not Working**:
```bash
# Verify development profile
curl http://localhost:8080/actuator/info | jq .activeProfiles

# Check file system permissions
ls -la src/main/resources/agents/

# Monitor file changes
tail -f logs/application.log | grep "agent reload"
```

**3. Performance Issues**:
```bash
# Check agent loading time
curl http://localhost:8080/actuator/local-dev | jq .agents

# Monitor memory usage
curl http://localhost:8080/api/dashboard/status | jq .memory

# Profile agent loading
grep "Loaded.*agents" logs/application.log
```

### Debug Mode

**Enable Debug Logging**:
```yaml
# application-development.yml
logging:
  level:
    com.jenksy.jenksymcp.service.AgentService: TRACE
    org.springframework.cache: DEBUG
```

**Debug Commands**:
```bash
# Enable debug mode
export LOGGING_LEVEL_COM_JENKSY_JENKSYMCP=DEBUG

# Monitor agent operations
tail -f logs/application.log | grep -E "(Loading|Caching|Agent)"

# Check cache statistics
curl http://localhost:8080/actuator/metrics/cache.gets | jq .
```

### Performance Profiling

**Agent Loading Profiler**:
```java
// Enable in AgentService for detailed timing
@PostConstruct
public void loadAgents() {
    long startTime = System.currentTimeMillis();

    // Profile individual agent loading
    agentPaths.forEach(path -> {
        long agentStart = System.currentTimeMillis();
        loadAgentFromPath(path);
        long agentTime = System.currentTimeMillis() - agentStart;
        log.debug("Agent {} loaded in {}ms", path.getFileName(), agentTime);
    });

    long totalTime = System.currentTimeMillis() - startTime;
    log.info("All agents loaded in {}ms", totalTime);
}
```

## Best Practices

### Agent Design Principles

1. **Single Responsibility**: Each agent should focus on one domain
2. **Clear Scope**: Define boundaries and limitations explicitly
3. **Comprehensive Expertise**: Cover the domain thoroughly
4. **Practical Guidance**: Provide actionable recommendations
5. **Context Awareness**: Consider integration and constraints

### Development Workflow

1. **Iterative Development**: Start simple, enhance incrementally
2. **Continuous Testing**: Test after each significant change
3. **Performance Monitoring**: Watch resource usage and timing
4. **Documentation**: Maintain clear agent descriptions
5. **Version Control**: Track agent evolution and changes

### Performance Optimization

1. **Efficient Prompts**: Keep system prompts focused and concise
2. **Cache Utilization**: Leverage caching for repeated operations
3. **Resource Monitoring**: Monitor memory and CPU usage
4. **Lazy Loading**: Load agent content only when needed
5. **Cleanup**: Remove unused or redundant agents

### Quality Assurance

1. **Validation**: Use automated validation scripts
2. **Testing Suite**: Maintain comprehensive test scenarios
3. **Peer Review**: Review agent content and structure
4. **User Feedback**: Gather feedback from actual usage
5. **Continuous Improvement**: Refine based on metrics and feedback

---

This agent management guide provides comprehensive coverage of the agent development lifecycle in the Jenksy MCP Server local development environment, focusing on practical workflows, performance optimization, and developer productivity.