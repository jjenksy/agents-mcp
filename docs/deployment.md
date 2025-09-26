# Deployment Guide

This guide covers production deployment strategies for the Jenksy MCP server, including containerization, cloud deployment, monitoring, and operational best practices.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Building for Production](#building-for-production)
4. [Deployment Options](#deployment-options)
5. [Docker Deployment](#docker-deployment)
6. [Cloud Deployments](#cloud-deployments)
7. [Configuration Management](#configuration-management)
8. [Monitoring and Observability](#monitoring-and-observability)
9. [Security Considerations](#security-considerations)
10. [Performance Optimization](#performance-optimization)
11. [Backup and Recovery](#backup-and-recovery)
12. [Troubleshooting](#troubleshooting)

## Overview

The Jenksy MCP server is a Spring Boot application that can be deployed in various environments. This guide covers deployment patterns from simple single-server setups to enterprise-grade distributed deployments.

### Deployment Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   AI Tools      │    │  Load Balancer  │    │  MCP Server     │
│ (Claude Desktop │◄──►│    (nginx)      │◄──►│   Cluster       │
│  VS Code, etc.) │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                               ┌─────────────────┐
                                               │   Monitoring    │
                                               │ (Prometheus)    │
                                               └─────────────────┘
```

## Prerequisites

### System Requirements

**Minimum Requirements**
- Java 21 or later
- 512MB RAM
- 1 CPU core
- 100MB disk space

**Recommended for Production**
- Java 21 (LTS)
- 2GB RAM
- 2 CPU cores
- 1GB disk space
- SSD storage

### Software Dependencies

- **Java Runtime**: OpenJDK 21+ or Oracle JDK 21+
- **Application Server**: Embedded Tomcat (included)
- **Process Manager**: systemd, Docker, or Kubernetes
- **Reverse Proxy**: nginx, Apache, or cloud load balancer

## Building for Production

### Standard JAR Build

```bash
# Clean and build the application
./gradlew clean build

# Verify build artifacts
ls -la build/libs/
# jenksy-mcp-0.0.1-SNAPSHOT.jar       (Fat JAR - recommended)
# jenksy-mcp-0.0.1-SNAPSHOT-plain.jar (Plain JAR)
```

### Production Build Configuration

Create production-specific configuration:

```bash
# Create production profile
mkdir -p src/main/resources/config
```

**src/main/resources/config/application-prod.yml**
```yaml
server:
  port: 8080
  servlet:
    context-path: /
  compression:
    enabled: true
    mime-types: application/json,text/plain

spring:
  profiles:
    active: prod

logging:
  level:
    root: INFO
    com.jenksy.jenksymcp: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/jenksy-mcp/application.log
    max-size: 100MB
    max-history: 30

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

### Build with Production Profile

```bash
# Build with production profile
./gradlew clean build -Pprofile=prod

# Run with production profile
java -jar -Dspring.profiles.active=prod build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

## Deployment Options

### 1. Standalone Server Deployment

**Direct JAR Execution**
```bash
# Basic deployment
java -jar jenksy-mcp-0.0.1-SNAPSHOT.jar

# With custom configuration
java -jar -Dspring.profiles.active=prod \
     -Dserver.port=8080 \
     jenksy-mcp-0.0.1-SNAPSHOT.jar
```

**Systemd Service**

Create service file: `/etc/systemd/system/jenksy-mcp.service`
```ini
[Unit]
Description=Jenksy MCP Server
After=network.target

[Service]
Type=simple
User=jenksy-mcp
Group=jenksy-mcp
WorkingDirectory=/opt/jenksy-mcp
ExecStart=/usr/bin/java -jar \
    -Dspring.profiles.active=prod \
    -Xms512m -Xmx1024m \
    /opt/jenksy-mcp/jenksy-mcp-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=jenksy-mcp

[Install]
WantedBy=multi-user.target
```

**Service Management**
```bash
# Create user and directories
sudo useradd -r -s /bin/false jenksy-mcp
sudo mkdir -p /opt/jenksy-mcp /var/log/jenksy-mcp
sudo chown jenksy-mcp:jenksy-mcp /opt/jenksy-mcp /var/log/jenksy-mcp

# Deploy application
sudo cp build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar /opt/jenksy-mcp/

# Enable and start service
sudo systemctl enable jenksy-mcp
sudo systemctl start jenksy-mcp
sudo systemctl status jenksy-mcp
```

### 2. Reverse Proxy Setup

**nginx Configuration**

`/etc/nginx/sites-available/jenksy-mcp`
```nginx
upstream jenksy_mcp {
    server 127.0.0.1:8080;
    keepalive 32;
}

server {
    listen 80;
    server_name mcp.yourdomain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name mcp.yourdomain.com;

    # SSL configuration
    ssl_certificate /etc/ssl/certs/mcp.yourdomain.com.crt;
    ssl_certificate_key /etc/ssl/private/mcp.yourdomain.com.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";

    location / {
        proxy_pass http://jenksy_mcp;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;

        # Buffer configuration
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
    }

    # Health check endpoint
    location /actuator/health {
        proxy_pass http://jenksy_mcp;
        access_log off;
    }
}
```

**Enable nginx Configuration**
```bash
sudo ln -s /etc/nginx/sites-available/jenksy-mcp /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## Docker Deployment

### Dockerfile

```dockerfile
# Multi-stage build for smaller image
FROM openjdk:21-jdk-slim AS builder

WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM openjdk:21-jre-slim

# Create app user
RUN addgroup --system --gid 1001 jenksy && \
    adduser --system --uid 1001 --gid 1001 jenksy

# Install required packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    tini && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/jenksy-mcp-*-SNAPSHOT.jar app.jar

# Create directories and set permissions
RUN mkdir -p /app/logs && \
    chown -R jenksy:jenksy /app

# Switch to non-root user
USER jenksy

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Use tini as init system
ENTRYPOINT ["/usr/bin/tini", "--"]

# Start application
CMD ["java", "-jar", "app.jar"]

# Expose port
EXPOSE 8080

# Labels
LABEL maintainer="your-email@domain.com"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Jenksy MCP Server"
```

### Docker Compose

**docker-compose.yml**
```yaml
version: '3.8'

services:
  jenksy-mcp:
    build: .
    container_name: jenksy-mcp
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JVM_OPTS=-Xms512m -Xmx1024m
    volumes:
      - ./logs:/app/logs
      - ./config:/app/config:ro
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - mcp-network
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"

  nginx:
    image: nginx:alpine
    container_name: mcp-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/ssl:ro
    depends_on:
      - jenksy-mcp
    networks:
      - mcp-network

networks:
  mcp-network:
    driver: bridge
```

### Docker Commands

```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f jenksy-mcp

# Scale horizontally
docker-compose up -d --scale jenksy-mcp=3

# Update deployment
docker-compose pull
docker-compose up -d --force-recreate
```

## Cloud Deployments

### AWS Deployment

**EC2 with Application Load Balancer**

1. **Launch EC2 Instance**
```bash
# User data script for automatic setup
#!/bin/bash
yum update -y
yum install -y java-21-amazon-corretto

# Create application user
useradd -r jenksy-mcp
mkdir -p /opt/jenksy-mcp /var/log/jenksy-mcp
chown jenksy-mcp:jenksy-mcp /opt/jenksy-mcp /var/log/jenksy-mcp

# Download and install application
cd /opt/jenksy-mcp
curl -O https://github.com/your-org/jenksy-mcp/releases/latest/download/jenksy-mcp.jar
chown jenksy-mcp:jenksy-mcp jenksy-mcp.jar

# Install systemd service
cat > /etc/systemd/system/jenksy-mcp.service << 'EOF'
[Unit]
Description=Jenksy MCP Server
After=network.target

[Service]
Type=simple
User=jenksy-mcp
WorkingDirectory=/opt/jenksy-mcp
ExecStart=/usr/bin/java -jar jenksy-mcp.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl enable jenksy-mcp
systemctl start jenksy-mcp
```

2. **Application Load Balancer Setup**
```bash
# Create target group
aws elbv2 create-target-group \
    --name jenksy-mcp-targets \
    --protocol HTTP \
    --port 8080 \
    --vpc-id vpc-12345678 \
    --health-check-path /actuator/health

# Register targets
aws elbv2 register-targets \
    --target-group-arn arn:aws:elasticloadbalancing:region:account:targetgroup/jenksy-mcp-targets \
    --targets Id=i-1234567890abcdef0
```

**ECS Fargate Deployment**

Task Definition:
```json
{
  "family": "jenksy-mcp",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::account:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "jenksy-mcp",
      "image": "your-registry/jenksy-mcp:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3
      },
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/jenksy-mcp",
          "awslogs-region": "us-west-2",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ]
    }
  ]
}
```

### Azure Deployment

**Container Instances**
```bash
# Create resource group
az group create --name jenksy-mcp-rg --location eastus

# Deploy container
az container create \
    --resource-group jenksy-mcp-rg \
    --name jenksy-mcp \
    --image your-registry/jenksy-mcp:latest \
    --dns-name-label jenksy-mcp-unique \
    --ports 8080 \
    --memory 1 \
    --cpu 1 \
    --environment-variables SPRING_PROFILES_ACTIVE=prod
```

**App Service**
```bash
# Create App Service plan
az appservice plan create \
    --name jenksy-mcp-plan \
    --resource-group jenksy-mcp-rg \
    --sku B1 \
    --is-linux

# Create web app
az webapp create \
    --resource-group jenksy-mcp-rg \
    --plan jenksy-mcp-plan \
    --name jenksy-mcp-app \
    --deployment-container-image-name your-registry/jenksy-mcp:latest
```

### Google Cloud Platform

**Cloud Run Deployment**
```bash
# Deploy to Cloud Run
gcloud run deploy jenksy-mcp \
    --image gcr.io/project-id/jenksy-mcp:latest \
    --platform managed \
    --region us-central1 \
    --allow-unauthenticated \
    --memory 1Gi \
    --cpu 1 \
    --port 8080 \
    --set-env-vars SPRING_PROFILES_ACTIVE=prod
```

**GKE Deployment**

Kubernetes manifests:

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jenksy-mcp
  labels:
    app: jenksy-mcp
spec:
  replicas: 3
  selector:
    matchLabels:
      app: jenksy-mcp
  template:
    metadata:
      labels:
        app: jenksy-mcp
    spec:
      containers:
      - name: jenksy-mcp
        image: gcr.io/project-id/jenksy-mcp:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10

---
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: jenksy-mcp-service
spec:
  selector:
    app: jenksy-mcp
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP

---
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: jenksy-mcp-ingress
  annotations:
    kubernetes.io/ingress.class: "gce"
spec:
  rules:
  - host: mcp.yourdomain.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: jenksy-mcp-service
            port:
              number: 80
```

## Configuration Management

### Environment Variables

**Core Configuration**
```bash
# Server configuration
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=prod

# JVM tuning
export JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Logging
export LOGGING_LEVEL_ROOT=INFO
export LOGGING_FILE_NAME=/var/log/jenksy-mcp/application.log
```

### External Configuration

**application-prod.yml**
```yaml
server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: ${CONTEXT_PATH:/}

spring:
  application:
    name: jenksy-mcp

logging:
  level:
    root: ${LOGGING_LEVEL_ROOT:INFO}
  file:
    name: ${LOGGING_FILE_NAME:/var/log/jenksy-mcp/application.log}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### Secrets Management

**Using AWS Systems Manager Parameter Store**
```yaml
# application-aws.yml
aws:
  paramstore:
    enabled: true
    prefix: /jenksy-mcp
    profile-separator: _
```

**Using Kubernetes Secrets**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jenksy-mcp-secrets
type: Opaque
data:
  database-password: <base64-encoded-password>
  api-key: <base64-encoded-api-key>
```

## Monitoring and Observability

### Health Checks

**Built-in Health Endpoints**
```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health?show-details=always
```

### Metrics Collection

**Prometheus Integration**

Enable Prometheus metrics in configuration:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**Custom Metrics**
```java
@Component
public class AgentMetrics {
    private final Counter agentInvocations;
    private final Timer agentResponseTime;

    public AgentMetrics(MeterRegistry meterRegistry) {
        this.agentInvocations = Counter.builder("agent_invocations_total")
            .description("Total agent invocations")
            .tag("agent", "unknown")
            .register(meterRegistry);

        this.agentResponseTime = Timer.builder("agent_response_time")
            .description("Agent response time")
            .register(meterRegistry);
    }
}
```

### Log Aggregation

**Structured Logging**
```yaml
logging:
  pattern:
    console: '{"timestamp":"%d{yyyy-MM-dd HH:mm:ss.SSS}","level":"%level","thread":"%thread","logger":"%logger{36}","message":"%msg"}%n'
```

**ELK Stack Integration**
```bash
# Install Filebeat
curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.0.0-linux-x86_64.tar.gz

# Configure filebeat.yml
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/jenksy-mcp/*.log
  fields:
    service: jenksy-mcp
    environment: production
```

### Alerting

**Prometheus Alerting Rules**
```yaml
groups:
- name: jenksy-mcp
  rules:
  - alert: JenksyMCPDown
    expr: up{job="jenksy-mcp"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "Jenksy MCP server is down"

  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High error rate detected"
```

## Security Considerations

### Network Security

**Firewall Configuration**
```bash
# Ubuntu/Debian
sudo ufw allow 8080/tcp
sudo ufw allow from 10.0.0.0/8 to any port 8080

# CentOS/RHEL
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

### Application Security

**Security Headers**
```yaml
# application-security.yml
server:
  servlet:
    session:
      cookie:
        http-only: true
        secure: true

management:
  endpoint:
    health:
      show-details: when-authorized
  endpoints:
    web:
      base-path: /management
      exposure:
        include: health,info,metrics
```

### SSL/TLS Configuration

**Self-Signed Certificate (Development)**
```bash
# Generate keystore
keytool -genkeypair -alias jenksy-mcp \
    -keyalg RSA -keysize 2048 \
    -storetype PKCS12 \
    -keystore keystore.p12 \
    -validity 365
```

**Application SSL Configuration**
```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: jenksy-mcp
  port: 8443
```

## Performance Optimization

### JVM Tuning

**Production JVM Settings**
```bash
#!/bin/bash
java -server \
     -Xms1g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+UseStringDeduplication \
     -XX:+OptimizeStringConcat \
     -XX:+UseCompressedOops \
     -Djava.awt.headless=true \
     -Dfile.encoding=UTF-8 \
     -jar jenksy-mcp.jar
```

### Caching Strategy

**Application-Level Caching**
```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterAccess=300s
```

### Connection Pooling

**Database Connections** (if applicable)
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Backup and Recovery

### Configuration Backup

**Backup Scripts**
```bash
#!/bin/bash
# backup-config.sh

BACKUP_DIR="/backup/jenksy-mcp/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

# Backup configuration files
cp -r /opt/jenksy-mcp/config "$BACKUP_DIR/"
cp /etc/systemd/system/jenksy-mcp.service "$BACKUP_DIR/"

# Backup logs (last 7 days)
find /var/log/jenksy-mcp -name "*.log" -mtime -7 -exec cp {} "$BACKUP_DIR/" \;

# Create archive
tar -czf "${BACKUP_DIR}.tar.gz" -C "$BACKUP_DIR" .
rm -rf "$BACKUP_DIR"

echo "Backup created: ${BACKUP_DIR}.tar.gz"
```

### Disaster Recovery

**Recovery Procedures**
1. **Service Recovery**
   ```bash
   # Stop service
   sudo systemctl stop jenksy-mcp

   # Restore from backup
   tar -xzf backup.tar.gz -C /opt/jenksy-mcp/

   # Restart service
   sudo systemctl start jenksy-mcp
   ```

2. **Configuration Recovery**
   ```bash
   # Restore configuration
   cp backup/config/* /opt/jenksy-mcp/config/
   cp backup/jenksy-mcp.service /etc/systemd/system/

   # Reload systemd
   sudo systemctl daemon-reload
   sudo systemctl restart jenksy-mcp
   ```

## Troubleshooting

### Common Issues

**Application Won't Start**
```bash
# Check Java version
java -version

# Verify JAR integrity
jar -tf jenksy-mcp.jar | head

# Check port availability
netstat -tulpn | grep 8080

# Review logs
journalctl -u jenksy-mcp -f
```

**High Memory Usage**
```bash
# Monitor memory usage
ps aux | grep java
top -p $(pgrep java)

# Generate heap dump
jcmd <pid> GC.run_finalization
jcmd <pid> VM.gc
jcmd <pid> GC.dump /tmp/heapdump.hprof
```

**Performance Issues**
```bash
# Check system resources
iostat -x 1
vmstat 1
free -m

# Monitor application metrics
curl localhost:8080/actuator/metrics
curl localhost:8080/actuator/prometheus
```

### Diagnostic Commands

**Health Diagnostics**
```bash
#!/bin/bash
# health-check.sh

echo "=== System Information ==="
uname -a
free -h
df -h

echo "=== Java Information ==="
java -version
ps aux | grep java

echo "=== Network Information ==="
netstat -tulpn | grep :8080
curl -I http://localhost:8080/actuator/health

echo "=== Service Status ==="
systemctl status jenksy-mcp
journalctl -u jenksy-mcp --no-pager -n 20
```

### Log Analysis

**Common Log Patterns**
```bash
# Find errors in logs
grep -i error /var/log/jenksy-mcp/application.log

# Monitor real-time logs
tail -f /var/log/jenksy-mcp/application.log

# Analyze startup performance
grep "Started JenksyMcpApplication" /var/log/jenksy-mcp/application.log
```

This deployment guide provides comprehensive coverage of production deployment strategies for the Jenksy MCP server. Choose the deployment approach that best fits your infrastructure requirements and operational capabilities.