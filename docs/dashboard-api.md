# Dashboard API Reference

Complete reference for the local development dashboard REST API endpoints. The dashboard provides basic monitoring, agent management, and system information for local development.

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Server Status Endpoints](#server-status-endpoints)
4. [Agent Management Endpoints](#agent-management-endpoints)
5. [Performance Monitoring Endpoints](#performance-monitoring-endpoints)
6. [Cache Management Endpoints](#cache-management-endpoints)
7. [System Management Endpoints](#system-management-endpoints)
8. [Polling-based Updates](#polling-based-updates)
9. [Error Responses](#error-responses)
10. [Usage Examples](#usage-examples)

## Overview

The dashboard API provides basic monitoring and management capabilities for the local MCP server. All endpoints return JSON responses and support CORS for local development. The dashboard uses simple HTTP polling for updates rather than real-time WebSocket connections.

**Base URL:** `http://localhost:8080`

**Content-Type:** `application/json`

## Authentication

**Local Development**: No authentication required. All endpoints are accessible for local development monitoring.

**Security Note**: This dashboard is designed for local development only and should not be exposed to external networks.

## Server Status Endpoints

### GET `/api/dashboard/status`

Get comprehensive server status including health, memory usage, and system information.

**Response:**
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "uptime": "2h 15m 30s",
  "startTime": "2025-01-15T08:14:30.123Z",
  "health": "UP",
  "healthDetails": {
    "status": "UP"
  },
  "memory": {
    "used": 157286400,
    "max": 536870912,
    "committed": 268435456,
    "usedPercentage": 29
  },
  "jvm": {
    "version": "21.0.1",
    "vendor": "Eclipse Adoptium",
    "runtime": "OpenJDK Runtime Environment"
  },
  "agentCount": 20
}
```

**Usage:**
```bash
curl http://localhost:8080/api/dashboard/status | jq .
```

## Agent Management Endpoints

### GET `/api/dashboard/agents`

List all available AI agents with their metadata.

**Response:**
```json
[
  {
    "name": "ai-engineer",
    "description": "Build production-ready LLM applications, advanced RAG systems, and intelligent agents",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are an AI engineer specializing in..."
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

### GET `/api/dashboard/agents/search`

Search agents by query string (name, description, or system prompt content).

**Parameters:**
- `query` (required): Search term

**Example:**
```bash
curl "http://localhost:8080/api/dashboard/agents/search?query=security"
```

**Response:**
```json
[
  {
    "name": "security-auditor",
    "description": "OWASP compliance, threat modeling, security testing",
    "model": "mcp-optimized",
    "tools": [],
    "systemPrompt": "You are a security auditing expert..."
  }
]
```

### POST `/api/dashboard/agents/test`

Test an agent with a specific task to verify functionality.

**Request Body:**
```json
{
  "agentName": "ai-engineer",
  "task": "Design a RAG system for document search"
}
```

**Response:**
```json
{
  "agent": "ai-engineer",
  "task": "Design a RAG system for document search",
  "status": "success",
  "timestamp": "2025-01-15T10:30:00",
  "responseTime": "250ms",
  "message": "Agent test completed successfully"
}
```

**Usage:**
```bash
curl -X POST http://localhost:8080/api/dashboard/agents/test \
  -H "Content-Type: application/json" \
  -d '{"agentName":"ai-engineer","task":"Design a RAG system"}'
```

## Performance Monitoring Endpoints

### GET `/api/dashboard/metrics`

Get comprehensive performance metrics including cache statistics, system metrics, and performance indicators.

**Response:**
```json
{
  "caches": {
    "agents": {
      "name": "agents",
      "status": "active"
    },
    "agentSearch": {
      "name": "agentSearch",
      "status": "active"
    }
  },
  "system": {
    "processors": 8,
    "freeMemory": 134217728,
    "totalMemory": 268435456,
    "maxMemory": 536870912
  },
  "performance": {
    "avgResponseTime": 125,
    "requestCount": 450,
    "errorRate": 1.2
  }
}
```

### GET `/actuator/local-dev`

Get detailed local development metrics including startup performance and optimization status.

**Response:**
```json
{
  "startup": {
    "total_time_ms": 4250,
    "context_time_ms": 3800,
    "post_startup_ms": 450,
    "performance_rating": "Excellent",
    "formatted_time": "PT4.25S"
  },
  "memory": {
    "max_mb": 512,
    "used_mb": 156,
    "free_mb": 356,
    "usage_percent": "30.5%"
  },
  "agents": {
    "total_loaded": 20,
    "agent_names": ["ai-engineer", "backend-architect", "..."]
  },
  "recommendations": {
    "overall": "Excellent performance for local development!",
    "startup": "Excellent startup time! Configuration is well optimized."
  }
}
```

## Cache Management Endpoints

### POST `/api/dashboard/cache/clear`

Clear application caches to free memory or reset cached data.

**Request Body (Optional):**
```json
{
  "cacheName": "agents"  // Optional: clear specific cache
}
```

**Response:**
```json
{
  "message": "Cache cleared: agents",
  "timestamp": "2025-01-15T10:30:00"
}
```

**Clear All Caches:**
```bash
curl -X POST http://localhost:8080/api/dashboard/cache/clear \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Clear Specific Cache:**
```bash
curl -X POST http://localhost:8080/api/dashboard/cache/clear \
  -H "Content-Type: application/json" \
  -d '{"cacheName":"agentSearch"}'
```

## System Management Endpoints

### POST `/api/dashboard/gc`

Trigger garbage collection to free up memory.

**Response:**
```json
{
  "memoryBefore": 167772160,
  "memoryAfter": 134217728,
  "memoryFreed": 33554432,
  "timestamp": "2025-01-15T10:30:00",
  "message": "Garbage collection triggered"
}
```

**Usage:**
```bash
curl -X POST http://localhost:8080/api/dashboard/gc
```

## Polling-based Updates

### HTTP Polling Pattern

The dashboard uses simple HTTP polling to refresh data. Recommended polling intervals:

- **Server Status**: Every 5-10 seconds
- **Agent List**: On demand or every 30 seconds
- **System Metrics**: Every 10-15 seconds
- **Cache Status**: Every 30 seconds

**Example Polling Implementation:**
```javascript
// Poll server status every 5 seconds
setInterval(async () => {
  try {
    const response = await fetch('/api/dashboard/status');
    const status = await response.json();
    updateDashboard(status);
  } catch (error) {
    console.error('Failed to fetch status:', error);
  }
}, 5000);

// Poll metrics every 10 seconds
setInterval(async () => {
  try {
    const response = await fetch('/api/dashboard/metrics');
    const metrics = await response.json();
    updateMetrics(metrics);
  } catch (error) {
    console.error('Failed to fetch metrics:', error);
  }
}, 10000);
```

## Error Responses

All endpoints return consistent error responses:

**400 Bad Request:**
```json
{
  "error": "agentName and task are required"
}
```

**404 Not Found:**
```json
{
  "error": "Agent not found: unknown-agent"
}
```

**500 Internal Server Error:**
```json
{
  "error": "Failed to get server status: Connection timeout"
}
```

## Usage Examples

### Complete Dashboard Monitoring Script

```bash
#!/bin/bash

# Check server health
echo "=== Server Status ==="
curl -s http://localhost:8080/api/dashboard/status | jq .health

# Get performance metrics
echo -e "\n=== Performance Metrics ==="
curl -s http://localhost:8080/api/dashboard/metrics | jq .performance

# Check startup performance
echo -e "\n=== Startup Performance ==="
curl -s http://localhost:8080/actuator/local-dev | jq .startup

# List available agents
echo -e "\n=== Available Agents ==="
curl -s http://localhost:8080/api/dashboard/agents | jq '.[].name'

# Test an agent
echo -e "\n=== Testing AI Engineer Agent ==="
curl -s -X POST http://localhost:8080/api/dashboard/agents/test \
  -H "Content-Type: application/json" \
  -d '{"agentName":"ai-engineer","task":"Test task"}' | jq .

# Clear caches if memory usage is high
MEMORY_USAGE=$(curl -s http://localhost:8080/api/dashboard/status | jq .memory.usedPercentage)
if (( $(echo "$MEMORY_USAGE > 80" | bc -l) )); then
  echo -e "\n=== Clearing Caches (High Memory Usage) ==="
  curl -s -X POST http://localhost:8080/api/dashboard/cache/clear | jq .message
fi
```

### JavaScript Dashboard Integration

```javascript
class DashboardAPI {
  constructor(baseUrl = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
  }

  async getStatus() {
    const response = await fetch(`${this.baseUrl}/api/dashboard/status`);
    return await response.json();
  }

  async getAgents() {
    const response = await fetch(`${this.baseUrl}/api/dashboard/agents`);
    return await response.json();
  }

  async searchAgents(query) {
    const response = await fetch(
      `${this.baseUrl}/api/dashboard/agents/search?query=${encodeURIComponent(query)}`
    );
    return await response.json();
  }

  async testAgent(agentName, task) {
    const response = await fetch(`${this.baseUrl}/api/dashboard/agents/test`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ agentName, task })
    });
    return await response.json();
  }

  async clearCache(cacheName = null) {
    const body = cacheName ? { cacheName } : {};
    const response = await fetch(`${this.baseUrl}/api/dashboard/cache/clear`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    return await response.json();
  }

  startPolling(interval = 5000) {
    const pollStatus = async () => {
      try {
        const status = await this.getStatus();
        return status;
      } catch (error) {
        console.error('Polling failed:', error);
        return null;
      }
    };

    return setInterval(pollStatus, interval);
  }
}

// Usage example
const api = new DashboardAPI();

// Monitor dashboard
api.getStatus().then(status => {
  console.log('Server health:', status.health);
  console.log('Memory usage:', status.memory.usedPercentage + '%');
});

// Start polling for updates
const pollingInterval = api.startPolling(5000);

// Stop polling when done
// clearInterval(pollingInterval);
```

## Best Practices

1. **Polling Frequency**: Limit status polling to every 5-10 seconds to avoid overwhelming the server
2. **Cache Management**: Monitor memory usage and clear caches when usage exceeds 80%
3. **Error Handling**: Always handle network errors and server downtime gracefully
4. **Polling Cleanup**: Clear intervals when components are unmounted or no longer needed
5. **Performance Monitoring**: Use standard actuator endpoints for system monitoring

## Security Considerations

- **Local Only**: Dashboard API is designed for local development only
- **No Authentication**: Endpoints are unprotected for local convenience
- **CORS Enabled**: Cross-origin requests are allowed for local development tools
- **Production Warning**: Never expose these endpoints to external networks in production

---

**Note**: This dashboard API is specifically designed for local development workflows and uses simple HTTP polling for updates. It should not be used in production environments without proper authentication and security measures.