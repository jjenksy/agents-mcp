# Troubleshooting Guide

This guide provides solutions to common issues encountered when running the Jenksy MCP server, along with diagnostic procedures and debugging techniques.

## Table of Contents

1. [Quick Diagnostics](#quick-diagnostics)
2. [Common Issues](#common-issues)
3. [Application Startup Problems](#application-startup-problems)
4. [Agent Loading Issues](#agent-loading-issues)
5. [MCP Integration Problems](#mcp-integration-problems)
6. [Performance Issues](#performance-issues)
7. [Memory and Resource Problems](#memory-and-resource-problems)
8. [Network and Connectivity Issues](#network-and-connectivity-issues)
9. [Configuration Issues](#configuration-issues)
10. [Debugging Techniques](#debugging-techniques)
11. [Log Analysis](#log-analysis)
12. [Getting Help](#getting-help)

## Quick Diagnostics

### Health Check Script

Run this script to get a quick overview of system status:

```bash
#!/bin/bash
# quick-diagnosis.sh

echo "=== Jenksy MCP Quick Diagnostics ==="
echo "Timestamp: $(date)"
echo

echo "=== System Information ==="
echo "OS: $(uname -a)"
echo "Memory: $(free -h | grep Mem)"
echo "Disk: $(df -h / | tail -1)"
echo "Java: $(java -version 2>&1 | head -1)"
echo

echo "=== Service Status ==="
if command -v systemctl >/dev/null; then
    systemctl is-active jenksy-mcp 2>/dev/null || echo "Service not found or not running"
fi

echo "=== Port Check ==="
netstat -tulpn 2>/dev/null | grep :8080 || echo "Port 8080 not in use"

echo "=== Health Endpoint ==="
curl -s -f http://localhost:8080/actuator/health 2>/dev/null || echo "Health endpoint not responding"

echo "=== Recent Errors ==="
if [ -f "/var/log/jenksy-mcp/application.log" ]; then
    tail -20 /var/log/jenksy-mcp/application.log | grep -i error | tail -5
else
    echo "Log file not found"
fi
```

### Immediate Actions Checklist

When experiencing issues, check these items first:

- [ ] Is Java 21+ installed? (`java -version`)
- [ ] Is the application running? (`ps aux | grep jenksy`)
- [ ] Is port 8080 available? (`netstat -tulpn | grep 8080`)
- [ ] Are there recent errors in logs? (`tail -f /var/log/jenksy-mcp/application.log`)
- [ ] Is there sufficient memory? (`free -h`)
- [ ] Is there sufficient disk space? (`df -h`)

## Common Issues

### Issue: "Address already in use" Error

**Symptoms:**
```
java.net.BindException: Address already in use
```

**Causes:**
- Port 8080 is already occupied by another process
- Previous instance didn't shut down properly

**Solutions:**
1. **Find process using port 8080:**
   ```bash
   lsof -i :8080
   netstat -tulpn | grep :8080
   ```

2. **Kill the process:**
   ```bash
   # If safe to kill
   kill -9 <PID>

   # Or stop service properly
   sudo systemctl stop jenksy-mcp
   ```

3. **Use different port:**
   ```bash
   java -jar -Dserver.port=8081 jenksy-mcp.jar
   ```

### Issue: Java Version Compatibility

**Symptoms:**
```
UnsupportedClassVersionError: com/jenksy/jenksymcp/JenksyMcpApplication has been compiled by a more recent version of the Java Runtime
```

**Solution:**
Install Java 21 or later:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# CentOS/RHEL
sudo yum install java-21-openjdk

# macOS with Homebrew
brew install openjdk@21

# Verify installation
java -version
```

### Issue: "No agents found" Warning

**Symptoms:**
```
WARN AgentService - Agents not found in classpath or filesystem, loading default agents
```

**Causes:**
- Agents directory missing from classpath
- Incorrect agent file format
- File permissions issues

**Solutions:**
1. **Verify agent files exist:**
   ```bash
   # In JAR deployment
   jar -tf jenksy-mcp.jar | grep agents/

   # In development
   ls -la src/main/resources/agents/
   ```

2. **Check file permissions:**
   ```bash
   ls -la src/main/resources/agents/
   # Should be readable by application user
   ```

3. **Validate agent file format:**
   ```bash
   head -10 src/main/resources/agents/ai-engineer.md
   # Should start with YAML frontmatter
   ```

## Application Startup Problems

### Slow Startup

**Symptoms:**
- Application takes more than 30 seconds to start
- Long delays during agent loading

**Diagnosis:**
```bash
# Enable startup profiling
java -jar -XX:+PrintGCDetails -XX:+PrintGCTimeStamps jenksy-mcp.jar

# Monitor startup logs
tail -f /var/log/jenksy-mcp/application.log | grep -E "(Starting|Started|Loading)"
```

**Solutions:**
1. **Increase memory allocation:**
   ```bash
   java -jar -Xms512m -Xmx1024m jenksy-mcp.jar
   ```

2. **Use G1 garbage collector:**
   ```bash
   java -jar -XX:+UseG1GC jenksy-mcp.jar
   ```

3. **Optimize agent loading:**
   - Reduce number of agent files
   - Simplify agent system prompts
   - Check disk I/O performance

### Startup Crashes

**Symptoms:**
- Application exits immediately after starting
- "OutOfMemoryError" during startup

**Diagnosis:**
```bash
# Check crash logs
dmesg | grep java
journalctl -u jenksy-mcp | grep -i "killed\|crashed\|error"

# Generate heap dump on OOM
java -jar -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/ jenksy-mcp.jar
```

**Solutions:**
1. **Increase heap size:**
   ```bash
   java -jar -Xms1g -Xmx2g jenksy-mcp.jar
   ```

2. **Check system resources:**
   ```bash
   free -h
   ulimit -a
   ```

3. **Review configuration:**
   - Remove unnecessary features
   - Optimize caching settings

## Agent Loading Issues

### Agent Parsing Errors

**Symptoms:**
```
ERROR AgentService - Error loading agent from resource: agent-name
java.lang.RuntimeException: Invalid YAML frontmatter
```

**Diagnosis:**
1. **Validate YAML syntax:**
   ```bash
   # Extract frontmatter
   sed -n '/^---$/,/^---$/p' src/main/resources/agents/problematic-agent.md

   # Validate with Python
   python3 -c "
   import yaml
   with open('agent.md') as f:
       content = f.read()
       frontmatter = content.split('---')[1]
       yaml.safe_load(frontmatter)
   "
   ```

2. **Check for special characters:**
   ```bash
   grep -P "[\x80-\xFF]" src/main/resources/agents/*.md
   ```

**Solutions:**
1. **Fix YAML format:**
   ```yaml
   # Correct format
   ---
   name: agent-name
   description: "Agent description"
   ---
   ```

2. **Escape special characters:**
   ```yaml
   description: "Agent with \"quotes\" and special chars"
   ```

### Missing Agents in API Response

**Symptoms:**
- `get_agents()` returns fewer agents than expected
- Specific agents not found by `find_agents()`

**Diagnosis:**
```bash
# Check agent loading logs
grep "Loaded.*agents" /var/log/jenksy-mcp/application.log

# Verify agent files
find src/main/resources/agents/ -name "*.md" | wc -l
```

**Solutions:**
1. **Verify file naming:**
   - Use `.md` extension
   - Avoid spaces in filenames
   - Use kebab-case naming

2. **Check file content:**
   - Must have YAML frontmatter
   - Must have system prompt content
   - Validate encoding (UTF-8)

## MCP Integration Problems

### VS Code Copilot Not Recognizing MCP Tools

**Symptoms:**
- MCP tools not available in VS Code
- No agent-related suggestions

**Diagnosis:**
1. **Check MCP server connectivity:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Verify MCP tool exposure:**
   ```bash
   # Check application logs for tool registration
   grep -i "tool\|mcp" /var/log/jenksy-mcp/application.log
   ```

**Solutions:**
1. **Restart VS Code and MCP server:**
   ```bash
   sudo systemctl restart jenksy-mcp
   # Then restart VS Code
   ```

2. **Check VS Code extension configuration:**
   - Verify MCP extension is enabled
   - Check server connection settings
   - Review extension logs

### Claude Desktop Integration Issues

**Symptoms:**
- Tools not appearing in Claude Desktop
- "Server not responding" errors

**Diagnosis:**
1. **Verify configuration:**
   ```json
   // claude_desktop_config.json
   {
     "mcpServers": {
       "jenksy-agents": {
         "command": "java",
         "args": [
           "-jar",
           "/path/to/jenksy-mcp-0.0.1-SNAPSHOT.jar"
         ]
       }
     }
   }
   ```

2. **Check server startup:**
   ```bash
   # Test direct execution
   java -jar /path/to/jenksy-mcp-0.0.1-SNAPSHOT.jar
   ```

**Solutions:**
1. **Update JAR path:**
   - Use absolute paths in configuration
   - Verify JAR file exists and is executable

2. **Check process permissions:**
   ```bash
   ls -la /path/to/jenksy-mcp-0.0.1-SNAPSHOT.jar
   # Should be readable by user running Claude Desktop
   ```

### MCP Protocol Errors

**Symptoms:**
```
MCP protocol error: Invalid tool definition
```

**Solutions:**
1. **Verify tool annotations:**
   ```java
   @Tool(description = "Valid description", name = "valid_name")
   public String toolMethod(String parameter) {
       // Implementation
   }
   ```

2. **Check method signatures:**
   - Use supported parameter types
   - Provide clear descriptions
   - Follow naming conventions

## Performance Issues

### High CPU Usage

**Symptoms:**
- CPU usage consistently above 80%
- Slow response times

**Diagnosis:**
```bash
# Monitor CPU usage
top -p $(pgrep -f jenksy-mcp)
htop

# Profile application
jstack <PID> > thread-dump.txt
jcmd <PID> VM.gc
```

**Solutions:**
1. **Optimize JVM settings:**
   ```bash
   java -jar -XX:+UseG1GC -XX:MaxGCPauseMillis=200 jenksy-mcp.jar
   ```

2. **Review agent complexity:**
   - Simplify complex system prompts
   - Reduce agent count if necessary
   - Optimize caching configuration

### Slow Response Times

**Symptoms:**
- Agent invocation takes more than 5 seconds
- Tool calls timeout

**Diagnosis:**
```bash
# Monitor response times
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080/actuator/health

# Check for blocking operations
jstack <PID> | grep -A 5 -B 5 BLOCKED
```

**Solutions:**
1. **Enable response caching:**
   ```yaml
   spring:
     cache:
       type: caffeine
       caffeine:
         spec: maximumSize=1000,expireAfterAccess=300s
   ```

2. **Optimize agent loading:**
   - Preload agents at startup
   - Use lazy loading for large system prompts
   - Implement agent context caching

## Memory and Resource Problems

### OutOfMemoryError

**Symptoms:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Immediate Actions:**
1. **Increase heap size:**
   ```bash
   java -jar -Xms1g -Xmx2g jenksy-mcp.jar
   ```

2. **Generate heap dump:**
   ```bash
   jcmd <PID> GC.dump /tmp/heapdump.hprof
   ```

**Long-term Solutions:**
1. **Optimize caching:**
   ```yaml
   # Reduce cache sizes
   spring:
     cache:
       caffeine:
         spec: maximumSize=500,expireAfterAccess=120s
   ```

2. **Monitor memory usage:**
   ```bash
   # Add memory monitoring
   java -jar -XX:+PrintGCDetails -XX:+PrintGCTimeStamps jenksy-mcp.jar
   ```

### Memory Leaks

**Symptoms:**
- Memory usage continuously increases
- Frequent garbage collection

**Diagnosis:**
```bash
# Monitor memory over time
watch -n 5 'ps -p <PID> -o pid,vsz,rss,pmem'

# Analyze heap usage
jcmd <PID> VM.gc
jcmd <PID> GC.heap_info
```

**Solutions:**
1. **Review cache expiration:**
   ```java
   // Ensure proper cache cleanup
   @PreDestroy
   public void cleanup() {
       agentContexts.invalidateAll();
   }
   ```

2. **Monitor specific components:**
   - Agent context cache
   - System prompt storage
   - Tool callback registrations

## Network and Connectivity Issues

### Connection Refused

**Symptoms:**
```
Connection refused: connect
```

**Diagnosis:**
```bash
# Check if service is running
ps aux | grep jenksy
systemctl status jenksy-mcp

# Verify port binding
netstat -tulpn | grep :8080
ss -tlnp | grep :8080
```

**Solutions:**
1. **Start the service:**
   ```bash
   sudo systemctl start jenksy-mcp
   ```

2. **Check firewall rules:**
   ```bash
   # Ubuntu/Debian
   sudo ufw status
   sudo ufw allow 8080

   # CentOS/RHEL
   sudo firewall-cmd --list-ports
   sudo firewall-cmd --add-port=8080/tcp --permanent
   ```

### SSL/TLS Issues

**Symptoms:**
- Certificate errors in HTTPS connections
- SSL handshake failures

**Solutions:**
1. **Use self-signed certificate for testing:**
   ```bash
   keytool -genkeypair -alias jenksy-mcp -keyalg RSA -keysize 2048 \
           -storetype PKCS12 -keystore keystore.p12 -validity 365
   ```

2. **Configure SSL in application:**
   ```yaml
   server:
     ssl:
       key-store: classpath:keystore.p12
       key-store-password: changeit
       key-store-type: PKCS12
   ```

## Configuration Issues

### Invalid Configuration Properties

**Symptoms:**
```
Property 'xyz' is invalid
Configuration binding failed
```

**Diagnosis:**
```bash
# Validate YAML syntax
python3 -c "import yaml; yaml.safe_load(open('application.yml'))"

# Check property names
grep -n ":" application.yml | head -20
```

**Solutions:**
1. **Fix YAML syntax:**
   ```yaml
   # Correct indentation
   spring:
     application:
       name: jenksy-mcp
   ```

2. **Verify property names:**
   - Check Spring Boot documentation
   - Use IDE validation
   - Test with minimal configuration

### Profile-Specific Issues

**Symptoms:**
- Different behavior in different environments
- Configuration not loading as expected

**Solutions:**
1. **Check active profiles:**
   ```bash
   java -jar -Dspring.profiles.active=prod jenksy-mcp.jar
   ```

2. **Verify profile-specific files:**
   ```bash
   ls -la application-*.yml
   # Should match profile names
   ```

## Debugging Techniques

### Enable Debug Logging

**Application Debug Logging:**
```yaml
logging:
  level:
    com.jenksy.jenksymcp: DEBUG
    org.springframework.ai: DEBUG
    root: INFO
```

**JVM Debug Information:**
```bash
java -jar -XX:+PrintGCDetails \
     -XX:+PrintGCTimeStamps \
     -XX:+HeapDumpOnOutOfMemoryError \
     jenksy-mcp.jar
```

### Remote Debugging

**Enable Remote Debugging:**
```bash
java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     jenksy-mcp.jar
```

**Connect with IDE:**
- IntelliJ IDEA: Run -> Attach to Process
- VS Code: Use Java Extension Pack
- Eclipse: Debug -> Debug Configurations -> Remote Java Application

### Profiling Tools

**JProfiler Configuration:**
```bash
java -jar -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849 \
     jenksy-mcp.jar
```

**VisualVM Monitoring:**
```bash
# Enable JMX for VisualVM
java -jar -Dcom.sun.management.jmxremote \
     -Dcom.sun.management.jmxremote.port=9999 \
     -Dcom.sun.management.jmxremote.authenticate=false \
     -Dcom.sun.management.jmxremote.ssl=false \
     jenksy-mcp.jar
```

## Log Analysis

### Log Patterns and Commands

**Find Startup Issues:**
```bash
grep -E "(ERROR|WARN|Starting|Started)" /var/log/jenksy-mcp/application.log
```

**Agent Loading Analysis:**
```bash
grep -i "agent" /var/log/jenksy-mcp/application.log | grep -E "(Loading|Loaded|Error)"
```

**Performance Analysis:**
```bash
# Find slow operations
grep -E "took [0-9]{4,}" /var/log/jenksy-mcp/application.log

# Memory warnings
grep -i "memory\|gc\|heap" /var/log/jenksy-mcp/application.log
```

**Real-time Monitoring:**
```bash
# Monitor all activity
tail -f /var/log/jenksy-mcp/application.log

# Filter for errors only
tail -f /var/log/jenksy-mcp/application.log | grep -i error

# Monitor specific agent activity
tail -f /var/log/jenksy-mcp/application.log | grep -i "invoking agent"
```

### Log Rotation and Management

**Configure Log Rotation:**
```bash
# Create logrotate configuration
cat > /etc/logrotate.d/jenksy-mcp << 'EOF'
/var/log/jenksy-mcp/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 jenksy-mcp jenksy-mcp
    postrotate
        systemctl reload jenksy-mcp
    endscript
}
EOF
```

### Structured Log Analysis

**Extract Specific Information:**
```bash
# Extract agent invocation statistics
grep "Invoking agent" /var/log/jenksy-mcp/application.log | \
    awk '{print $4}' | sort | uniq -c | sort -nr

# Response time analysis
grep "Response time" /var/log/jenksy-mcp/application.log | \
    awk '{print $NF}' | sort -n
```

## Getting Help

### Gathering Diagnostic Information

Before seeking help, collect this information:

**System Information:**
```bash
# System details
uname -a
java -version
free -h
df -h

# Application version
java -jar jenksy-mcp.jar --version 2>/dev/null || echo "Version not available"

# Service status
systemctl status jenksy-mcp
```

**Configuration:**
```bash
# Sanitized configuration (remove sensitive data)
cat application.yml | grep -v password | grep -v secret
```

**Recent Logs:**
```bash
# Last 100 lines with timestamps
tail -100 /var/log/jenksy-mcp/application.log
```

### Support Channels

1. **GitHub Issues**: Create detailed issue with reproduction steps
2. **Documentation**: Check existing documentation and FAQ
3. **Community Forums**: Search for similar issues
4. **Stack Overflow**: Tag questions with relevant technologies

### Creating Effective Bug Reports

Include the following information:

1. **Environment Details:**
   - Operating system and version
   - Java version
   - Application version
   - Deployment method (JAR, Docker, etc.)

2. **Problem Description:**
   - What you expected to happen
   - What actually happened
   - Steps to reproduce

3. **Diagnostic Information:**
   - Relevant log entries
   - Configuration files (sanitized)
   - Error messages with full stack traces

4. **Attempted Solutions:**
   - What you've already tried
   - Any workarounds found

This troubleshooting guide should help you diagnose and resolve most common issues with the Jenksy MCP server. If you encounter issues not covered here, use the debugging techniques to gather more information before seeking additional help.