# Jenksy MCP Server Architecture

Architecture documentation for the Jenksy MCP server. This document covers system design, component interactions, and architectural decisions for this lightweight local development tool.

## Table of Contents

1. [System Overview](#system-overview)
2. [Architecture Principles](#architecture-principles)
3. [Core Components](#core-components)
4. [Data Flow Architecture](#data-flow-architecture)
5. [Performance Architecture](#performance-architecture)
6. [Local Development Optimizations](#local-development-optimizations)
7. [Caching Strategy](#caching-strategy)
8. [Configuration Management](#configuration-management)
9. [Monitoring and Observability](#monitoring-and-observability)
10. [Integration Patterns](#integration-patterns)
11. [Security Model](#security-model)
12. [Deployment Architecture](#deployment-architecture)

## System Overview

The Jenksy MCP Server is a standard Spring Boot application designed for local development workflows. It provides Model Context Protocol (MCP) integration with AI tools like VS Code Copilot and Claude Desktop through 20 specialized AI agents.

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Applications                       │
├─────────────────────┬───────────────────┬───────────────────┤
│   VS Code Copilot   │  Claude Desktop   │   Other MCP       │
│                     │                   │   Clients         │
└─────────────────────┴───────────────────┴───────────────────┘
                              │
                              │ MCP Protocol
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    MCP Server Layer                         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Tool      │  │   Agent     │  │    Dashboard        │  │
│  │ Callbacks   │  │  Service    │  │   Controller        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 Core Application Layer                      │
├─────────────────────┬───────────────────┬───────────────────┤
│   Agent Loading     │   Caching Layer   │   Configuration   │
│   & Management      │   (Caffeine)      │   Management      │
└─────────────────────┴───────────────────┴───────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Data Layer                              │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │     Agent       │  │   Agent         │  │   Context   │  │
│  │   Markdown      │  │  Metadata       │  │   Storage   │  │
│  │    Files        │  │    Cache        │  │             │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Key Characteristics

- **Local Development Focused**: Designed for single-user local development scenarios
- **Standard Spring Boot**: Clean setup with conventional configuration
- **Resource Efficient**: Standard Spring Boot memory footprint
- **Simple Monitoring**: HTTP polling-based dashboard with basic metrics
- **Agent-Centric**: 20 specialized AI agents with domain expertise

## Architecture Principles

### 1. Local Development First
- Designed for developer machine usage
- Simple setup and configuration
- Clear monitoring and diagnostics
- Focused on local workflow integration

### 2. Simplicity by Design
- Standard Spring Boot conventions
- Parallel processing for agent loading
- Basic caching strategies
- Clean component separation

### 3. Developer Experience
- Simple dashboard for monitoring
- Clear error messages and diagnostics
- Standard Spring Boot features
- Basic health monitoring

### 4. Extensibility
- Markdown-based agent system
- Configuration-driven behavior
- Modular component design
- Clear extension points

## Core Components

### JenksyMcpApplication (Main Application)

**Location**: `src/main/java/com/jenksy/jenksymcp/JenksyMcpApplication.java`

**Responsibilities**:
- Spring Boot application bootstrap
- MCP tool callback registration
- Performance monitoring setup
- Local development optimizations

**Key Features**:
```java
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
public class JenksyMcpApplication {
    // Startup time tracking
    private static final long startTime = System.currentTimeMillis();

    // Lazy bean initialization for MCP tools
    @Bean
    @Lazy
    public List<ToolCallback> toolCallbacks(AgentService agentService) {
        return Arrays.asList(ToolCallbacks.from(agentService));
    }
}
```

**Standard Features**:
- Standard Spring Boot initialization
- MCP tool callback registration
- Basic logging and monitoring
- Clean application structure

### AgentService (Core Business Logic)

**Location**: `src/main/java/com/jenksy/jenksymcp/service/AgentService.java`

**Responsibilities**:
- Agent loading and management
- MCP tool implementations
- Caching and context management
- Performance optimization

**Architecture Pattern**: Service Layer with Caching

```java
@Service
@Slf4j
public class AgentService {
    // Parallel agent loading for multi-core systems
    @PostConstruct
    public void loadAgents() {
        agentPaths.parallelStream().forEach(this::loadAgentFromPath);
    }

    // Cacheable MCP tool methods
    @Tool("get_agents")
    @Cacheable("agents")
    public List<Agent> getAgents() { ... }
}
```

**Key Design Patterns**:
- **Strategy Pattern**: Different agent loading strategies (classpath vs filesystem)
- **Template Method**: Common agent parsing logic with customizable loading
- **Cache-Aside**: Basic cache management with automatic cleanup
- **Service Layer**: Clean separation between MCP tools and business logic

## Data Flow Architecture

### Agent Invocation Flow

```
Client Request (VS Code Copilot)
        │
        ▼
┌─────────────────────┐
│  MCP Tool Callback  │
│  (Spring AI)        │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│   AgentService      │
│   @Tool Methods     │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│   Cache Layer       │
│   (Check Cache)     │
└─────────────────────┘
        │
        ▼ (Cache Miss)
┌─────────────────────┐
│   Agent Processing  │
│   (Business Logic)  │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│   Response          │
│   Generation        │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│   Cache Storage     │
│   & Context Store   │
└─────────────────────┘
        │
        ▼
    Client Response
```

### Agent Loading Flow

```
Application Startup
        │
        ▼
┌─────────────────────┐
│  @PostConstruct     │
│  loadAgents()       │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│  Try Classpath      │
│  Resources          │
└─────────────────────┘
        │
        ▼ (Found)
┌─────────────────────┐
│  Parallel Stream    │
│  Processing         │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│  YAML Frontmatter   │
│  Parsing            │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│  Agent Record       │
│  Creation           │
└─────────────────────┘
        │
        ▼
┌─────────────────────┐
│  Agent Collection   │
│  Storage            │
└─────────────────────┘
```

## Performance Architecture

### Startup Optimization Strategy

**1. Lazy Initialization**
```yaml
spring:
  main:
    lazy-initialization: true
```
- Components initialized only when needed
- Reduces startup time by 60-70%
- Memory allocated on-demand

**2. Parallel Agent Loading**
```java
// Multi-core utilization for agent loading
agentPaths.parallelStream().forEach(this::loadAgentFromPath);
```
- Utilizes developer machine CPU cores
- Reduces agent loading time significantly
- Scalable with core count

**3. JVM Optimizations**
```bash
# G1GC for low latency
-XX:+UseG1GC
-XX:G1HeapRegionSize=16m

# Fast compilation for development
-XX:+TieredCompilation
-XX:TieredStopAtLevel=1

# Memory efficiency
-XX:+UseStringDeduplication
-XX:+UseCompressedOops
```

### Memory Management Architecture

**Heap Configuration**:
- Dynamic sizing based on system memory (25% of RAM)
- Minimum: 128MB, Maximum: 1GB for local development
- G1GC for predictable low-latency performance

**Cache Strategy**:
```java
// Optimized for single-user scenarios
@Cacheable(value = "agents", key = "#methodName")
public List<Agent> getAgents() { ... }

// TTL configuration for local development
spring:
  cache:
    caffeine:
      spec: maximumSize=200,expireAfterAccess=15m
```

### Virtual Threads Integration (Java 21+)

**Benefits**:
- Reduced memory overhead per concurrent operation
- Better resource utilization
- Improved scalability for concurrent requests

**Implementation**:
```java
// Virtual thread factory for async operations
var virtualThreadFactory = Thread.ofVirtual().factory();
return command -> virtualThreadFactory.newThread(command).start();
```

## Local Development Optimizations

### Smart JVM Tuning Script

**Script**: `local-dev-start.sh`

**Capabilities**:
- Automatic system memory detection
- Dynamic heap size calculation
- CPU core utilization optimization
- Performance monitoring setup

**Algorithm**:
```bash
TOTAL_MEMORY_GB=$(( $(sysctl -n hw.memsize) / 1024 / 1024 / 1024 ))
HEAP_SIZE_MB=$(( TOTAL_MEMORY_GB * 256 ))  # 25% of RAM
HEAP_SIZE_MB=$(max(128, min(1024, HEAP_SIZE_MB)))  # Clamp to range
```

### Configuration Profiles

**Development Profile**:
```yaml
spring:
  config:
    activate:
      on-profile: development

jenksy:
  mcp:
    agents:
      hot-reload: true  # Enable agent hot reloading

logging:
  level:
    com.jenksy.jenksymcp: DEBUG
```

**Local Optimization Profile**:
```yaml
jenksy:
  mcp:
    local-optimization:
      enabled: true
      enable-virtual-threads: true
      enable-parallel-agent-loading: true
```

## Caching Strategy

### Multi-Level Caching Architecture

**Level 1: Agent Metadata Cache**
- **Scope**: Agent basic information
- **TTL**: 15 minutes
- **Size**: 200 entries
- **Strategy**: Cache-aside with manual invalidation

**Level 2: Agent Search Cache**
- **Scope**: Search query results
- **TTL**: 15 minutes
- **Size**: 100 entries
- **Key**: Query string hash

**Level 3: Context Storage**
- **Scope**: Agent invocation contexts
- **TTL**: 5 minutes
- **Size**: 100 entries
- **Cleanup**: Scheduled every 5 minutes

### Cache Configuration

```java
// Caffeine cache with optimized settings
private final Cache<String, String> agentContexts = Caffeine.newBuilder()
        .maximumSize(100)  // Reduced for single-user
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .recordStats()  // Enable metrics
        .build();
```

### Cache Invalidation Strategy

- **Manual**: Dashboard API endpoints for cache clearing
- **Automatic**: TTL-based expiration
- **Scheduled**: Periodic cleanup of expired contexts
- **Event-driven**: Agent reload triggers cache invalidation

## Configuration Management

### Configuration Hierarchy

1. **Default Configuration** (`application.yml`)
2. **Profile-specific** (`application-development.yml`)
3. **Local Override** (`application-local.yml`)
4. **Environment Variables**
5. **JVM System Properties**

### Key Configuration Areas

**Server Configuration**:
```yaml
server:
  port: 8080
  tomcat:
    threads:
      max: 10        # Reduced for local development
      min-spare: 2
    max-connections: 50
```

**MCP Configuration**:
```yaml
jenksy:
  mcp:
    agents:
      location: agents/
      hot-reload: false
      cache-ttl: 15m
      max-contexts: 100
```

**Actuator Configuration**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,cache,local-dev
  endpoint:
    health:
      show-details: always
```

## Monitoring and Observability

### Metrics Architecture

**Spring Boot Actuator**:
- Health checks with detailed component status
- JVM metrics (memory, GC, threads)
- Custom business metrics
- Cache statistics

**Custom Metrics Endpoint** (`/actuator/local-dev`):
```json
{
  "startup": {
    "total_time_ms": 4250,
    "performance_rating": "Excellent"
  },
  "memory": {
    "usage_percent": "30.5%"
  },
  "agents": {
    "total_loaded": 20
  }
}
```

**Dashboard Real-time Monitoring**:
- WebSocket-based live updates
- Interactive performance charts
- Agent activity tracking
- System resource monitoring

### Health Check Strategy

**Core Health Indicators**:
- Agent loading status
- Cache connectivity
- Memory thresholds
- JVM health

**Custom Health Indicators**:
```java
@Component
public class AgentHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        int agentCount = agentService.getAgents().size();
        if (agentCount > 0) {
            return Health.up()
                .withDetail("agentCount", agentCount)
                .build();
        }
        return Health.down()
            .withDetail("reason", "No agents loaded")
            .build();
    }
}
```

## Integration Patterns

### MCP Protocol Integration

**Tool Registration Pattern**:
```java
@Bean
@Lazy
public List<ToolCallback> toolCallbacks(AgentService agentService) {
    return Arrays.asList(ToolCallbacks.from(agentService));
}
```

**Tool Implementation Pattern**:
```java
@Tool(description = "Get specialized agent guidance", name = "invoke_agent")
public AgentResponse invokeAgent(AgentInvocation invocation) {
    // Validation, caching, processing, response generation
}
```

### VS Code Copilot Integration

**Configuration Pattern**:
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

**Natural Language Processing**:
- VS Code Copilot translates natural language to MCP tool calls
- Optimized response format for Copilot consumption
- Context-aware agent recommendations

### Claude Desktop Integration

**Configuration Pattern**:
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

## Security Model

### Local Development Security

**Assumptions**:
- Single-user local development environment
- Trusted local network only
- No external network exposure

**Security Measures**:
- No authentication required for local development
- CORS enabled for local tools
- Health endpoints with full details
- Dashboard accessible without restrictions

**Security Warnings**:
- Not suitable for production deployment
- Should not be exposed to external networks
- No data encryption or secure communication

### Input Validation

**Agent Input Validation**:
```java
// Validate invocation parameters
if (!StringUtils.hasText(invocation.agentName())) {
    return new AgentResponse("unknown", "unknown",
        "Error: Agent name cannot be blank", "error", context);
}
```

**Dashboard Input Validation**:
- Request body validation for POST endpoints
- Query parameter sanitization
- Error handling with sanitized responses

## Deployment Architecture

### Local Development Deployment

**Deployment Model**: Single JAR with embedded server

**Startup Options**:
1. **Optimized Script**: `./local-dev-start.sh`
2. **Gradle**: `./gradlew bootRun`
3. **Direct JAR**: `java -jar jenksy-mcp.jar`

**Resource Requirements**:
- **Memory**: 128-512MB heap space
- **CPU**: 2+ cores (optimized for multi-core)
- **Storage**: 25MB for JAR + logs
- **Network**: localhost:8080

### Configuration Management in Deployment

**Profile-based Configuration**:
```bash
# Development profile with hot reload
export SPRING_PROFILES_ACTIVE=development
./local-dev-start.sh

# Local optimization profile
export SPRING_PROFILES_ACTIVE=local
./local-dev-start.sh
```

**Environment Variable Overrides**:
```bash
export JENKSY_MCP_AGENTS_CACHE_TTL=5m
export LOGGING_LEVEL_COM_JENKSY_JENKSYMCP=DEBUG
```

### Monitoring in Local Deployment

**Health Monitoring**:
```bash
# Quick health check
curl http://localhost:8080/actuator/health

# Detailed performance metrics
curl http://localhost:8080/actuator/local-dev
```

**Dashboard Access**:
```
http://localhost:8080/dashboard
```

**Log Monitoring**:
- Console logs with colored output
- File-based logging (optional)
- Structured logging for parsing

---

This architecture is specifically designed for local development workflows and prioritizes developer experience, performance, and monitoring capabilities over production-grade features like security, scalability, and high availability.