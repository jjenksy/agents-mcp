package com.jenksy.jenksymcp.service;

import com.jenksy.jenksymcp.record.Agent;
import com.jenksy.jenksymcp.record.AgentInvocation;
import com.jenksy.jenksymcp.record.AgentResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PreDestroy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AgentService {

    private final List<Agent> agents = new ArrayList<>();
    private final Cache<String, String> agentContexts = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void loadAgents() {
        log.info("Loading agents from classpath and filesystem");

        // First try loading from classpath (for JAR deployments)
        boolean loadedFromClasspath = loadAgentsFromClasspath();

        if (!loadedFromClasspath) {
            // If not found in classpath, try filesystem (for development)
            Path agentsDir = Paths.get("agents");
            if (!Files.exists(agentsDir)) {
                log.warn("Agents not found in classpath or filesystem, loading default agents");
                loadDefaultAgents();
                return;
            }

            try (Stream<Path> paths = Files.walk(agentsDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".md"))
                        .filter(path -> !path.getFileName().toString().equals("README.md"))
                        .forEach(this::loadAgentFromPath);
                log.info("Loaded {} agents from filesystem: {}", agents.size(), agentsDir);
            } catch (IOException e) {
                log.error("Error loading agents from directory", e);
                loadDefaultAgents();
            }
        }
    }

    private boolean loadAgentsFromClasspath() {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:agents/*.md");

            if (resources.length == 0) {
                log.debug("No agents found in classpath");
                return false;
            }

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && !filename.equals("README.md")) {
                    loadAgentFromResource(resource, filename.replace(".md", ""));
                }
            }

            log.info("Loaded {} agents from classpath", agents.size());
            return !agents.isEmpty();

        } catch (IOException e) {
            log.debug("Could not load agents from classpath: {}", e.getMessage());
            return false;
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        agentContexts.invalidateAll();
        log.info("Agent service shutdown completed");
    }

    private void loadAgentFromResource(Resource resource, String agentName) {
        try (InputStream is = resource.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String content = reader.lines().collect(Collectors.joining("\n"));
            Agent agent = parseAgentMarkdown(content, agentName);
            if (agent != null) {
                agents.add(agent);
                log.debug("Loaded agent from classpath: {}", agent.name());
            }
        } catch (IOException e) {
            log.error("Error loading agent from resource: {}", agentName, e);
        }
    }

    private void loadAgentFromPath(Path agentPath) {
        try {
            String content = Files.readString(agentPath);
            Agent agent = parseAgentMarkdown(content, agentPath.getFileName().toString().replace(".md", ""));
            if (agent != null) {
                agents.add(agent);
                log.debug("Loaded agent from filesystem: {}", agent.name());
            }
        } catch (IOException e) {
            log.error("Error loading agent from {}", agentPath, e);
        }
    }

    private Agent parseAgentMarkdown(String content, String filename) {
        // Parse YAML frontmatter
        Pattern frontmatterPattern = Pattern.compile("^---\\s*\\n(.*?)\\n---\\s*\\n(.*)$", Pattern.DOTALL);
        Matcher matcher = frontmatterPattern.matcher(content);

        if (!matcher.find()) {
            log.warn("No frontmatter found in agent file: {}", filename);
            return null;
        }

        String frontmatter = matcher.group(1);
        String systemPrompt = matcher.group(2).trim();

        // Parse YAML frontmatter fields
        String name = extractYamlValue(frontmatter, "name", filename);
        String description = extractYamlValue(frontmatter, "description", "");
        String model = extractYamlValue(frontmatter, "model", "mcp-optimized");
        String toolsStr = extractYamlValue(frontmatter, "tools", "");

        List<String> tools = toolsStr.isEmpty() ? List.of() : Arrays.asList(toolsStr.split(",\\s*"));

        return new Agent(name, description, model, tools, systemPrompt);
    }

    private String extractYamlValue(String yaml, String key, String defaultValue) {
        Pattern pattern = Pattern.compile("^" + key + ":\\s*(.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(yaml);
        return matcher.find() ? matcher.group(1).trim() : defaultValue;
    }

    private void loadDefaultAgents() {
        log.info("Loading default agents");
        agents.addAll(List.of(
                new Agent("ai-engineer",
                        "Build production-ready LLM applications, advanced RAG systems, and intelligent agents",
                        "mcp-optimized", List.of(),
                        "You are an AI engineer specializing in production-grade LLM applications and intelligent agent architectures."),
                new Agent("backend-architect",
                        "Design RESTful APIs, microservice boundaries, and database schemas",
                        "mcp-optimized", List.of(),
                        "You are a backend system architect specializing in scalable API design and microservices."),
                new Agent("frontend-developer",
                        "Build React components, implement responsive layouts, and handle client-side state management",
                        "mcp-optimized", List.of(),
                        "You are a frontend developer specializing in React, modern JavaScript, and responsive design."),
                new Agent("code-reviewer",
                        "Elite code review expert specializing in security, performance, and production reliability",
                        "mcp-optimized", List.of(),
                        "You are a code review expert focusing on security, performance optimization, and production reliability."),
                new Agent("debugger",
                        "Debugging specialist for errors, test failures, and unexpected behavior",
                        "mcp-optimized", List.of(),
                        "You are a debugging specialist expert at resolving errors, test failures, and unexpected behavior.")));
    }

    @Tool(description = "List all available AI agents. Use find_agents to search by domain, or invoke_agent for task-specific guidance.", name = "get_agents")
    @Cacheable("agents")
    public List<Agent> getAgents() {
        log.info("Getting all available agents");
        return agents;
    }

    @Tool(description = "Search agents by domain keywords. Use invoke_agent after finding the right agent for task-specific guidance.", name = "find_agents")
    @Cacheable(value = "agentSearch", key = "#query")
    public List<Agent> findAgents(String query) {
        log.info("Finding agents for query: {}", query);
        String lowerQuery = query.toLowerCase();

        return agents.stream()
                .filter(agent -> agent.name().toLowerCase().contains(lowerQuery) ||
                        agent.description().toLowerCase().contains(lowerQuery) ||
                        agent.systemPrompt().toLowerCase().contains(lowerQuery))
                .toList();
    }

    @Tool(description = "Get agent capabilities and description. Use invoke_agent for actionable task guidance instead of just information.", name = "get_agent_info")
    @Cacheable(value = "agentInfo", key = "#agentName")
    public Agent getAgentInfo(String agentName) {
        log.info("Getting info for agent: {}", agentName);
        return agents.stream()
                .filter(agent -> agent.name().equals(agentName))
                .findFirst()
                .orElse(null);
    }

    @Tool(description = "Get concise, task-specific guidance from a specialized agent. Provides focused expertise and recommendations without redundant system prompts. Use this instead of get_agent_prompt for actionable task guidance.", name = "invoke_agent")
    public AgentResponse invokeAgent(AgentInvocation invocation) {
        // Validate invocation details
        if (!StringUtils.hasText(invocation.agentName())) {
            return new AgentResponse(
                    "unknown",
                    "unknown",
                    "Error: Agent name cannot be blank",
                    "error",
                    invocation.context());
        }

        if (!StringUtils.hasText(invocation.task())) {
            return new AgentResponse(
                    invocation.agentName(),
                    "unknown",
                    "Error: Task description cannot be blank",
                    "error",
                    invocation.context());
        }

        log.info("Invoking agent: {} with task: {}", invocation.agentName(), invocation.task());

        Agent agent = agents.stream()
                .filter(a -> a.name().equals(invocation.agentName()))
                .findFirst()
                .orElse(null);

        if (agent == null) {
            return new AgentResponse(
                    invocation.agentName(),
                    "unknown",
                    "Error: Agent '" + invocation.agentName() + "' not found. Use get_agents to see available agents.",
                    "error",
                    invocation.context());
        }

        // Store context for this agent session with automatic expiration
        String contextKey = invocation.agentName() + "_" + System.currentTimeMillis();
        agentContexts.put(contextKey, invocation.context());

        // Return structured agent guidance for the client tool to use
        String response = buildAgentGuidance(agent, invocation);

        return new AgentResponse(
                agent.name(),
                "mcp-optimized", // Generic identifier instead of specific model
                response,
                "success",
                contextKey);
    }


    @Tool(description = "Get 1-3 best agents for your task with usage guidance. More efficient than browsing all agents.", name = "get_recommended_agents")
    @Cacheable(value = "agentRecommendations", key = "#task")
    public List<Agent> getRecommendedAgents(String task) {
        log.info("Getting recommended agents for task: {}", task);
        String lowerTask = task.toLowerCase();

        // Simple recommendation logic based on task keywords
        List<Agent> recommended = new ArrayList<>();

        if (lowerTask.contains("api") || lowerTask.contains("backend") || lowerTask.contains("database")) {
            agents.stream()
                    .filter(agent -> agent.name().contains("backend") || agent.name().contains("architect"))
                    .findFirst()
                    .ifPresent(recommended::add);
        }

        if (lowerTask.contains("ui") || lowerTask.contains("frontend") || lowerTask.contains("react")) {
            agents.stream()
                    .filter(agent -> agent.name().contains("frontend"))
                    .findFirst()
                    .ifPresent(recommended::add);
        }

        if (lowerTask.contains("ai") || lowerTask.contains("llm") || lowerTask.contains("rag")) {
            agents.stream()
                    .filter(agent -> agent.name().contains("ai-engineer"))
                    .findFirst()
                    .ifPresent(recommended::add);
        }

        if (lowerTask.contains("review") || lowerTask.contains("security") || lowerTask.contains("audit")) {
            agents.stream()
                    .filter(agent -> agent.name().contains("code-reviewer") || agent.name().contains("security"))
                    .findFirst()
                    .ifPresent(recommended::add);
        }

        if (lowerTask.contains("bug") || lowerTask.contains("debug") || lowerTask.contains("error")) {
            agents.stream()
                    .filter(agent -> agent.name().contains("debugger"))
                    .findFirst()
                    .ifPresent(recommended::add);
        }

        if (lowerTask.contains("requirements") || lowerTask.contains("ticket") || lowerTask.contains("story") ||
            lowerTask.contains("planning") || lowerTask.contains("breakdown") || lowerTask.contains("epic") ||
            lowerTask.contains("project management") || lowerTask.contains("acceptance criteria")) {
            agents.stream()
                    .filter(agent -> agent.name().contains("requirements-analyst"))
                    .findFirst()
                    .ifPresent(recommended::add);
        }

        // If no specific recommendations, return top 3 most versatile agents
        if (recommended.isEmpty()) {
            return agents.stream()
                .filter(agent -> agent.name().matches(".*(ai-engineer|backend-architect|code-reviewer).*"))
                .limit(3)
                .toList();
        }

        // Limit to 3 for focused recommendations
        return recommended.stream().limit(3).toList();
    }

    public void cleanupExpiredContexts() {
        agentContexts.cleanUp();
        log.debug("Cleaned up expired agent contexts. Current size: {}", agentContexts.estimatedSize());
    }

    private String buildAgentGuidance(Agent agent, AgentInvocation invocation) {
        // Return concise, actionable guidance optimized for MCP consumers like VS Code Copilot
        StringBuilder guidance = new StringBuilder();

        // Optimized for VS Code Copilot consumption
        guidance.append("## ").append(agent.name().toUpperCase()).append(" SPECIALIST\n");
        guidance.append("> ").append(agent.description()).append("\n\n");

        // Task context
        guidance.append("### Task Analysis\n");
        guidance.append("**Objective**: ").append(invocation.task()).append("\n");
        if (!invocation.context().isEmpty()) {
            guidance.append("**Context**: ").append(invocation.context()).append("\n");
        }
        guidance.append("\n");

        // Actionable recommendations
        guidance.append("### Recommended Approach\n");
        List<String> approaches = getAgentApproaches(agent);
        for (int i = 0; i < approaches.size(); i++) {
            guidance.append(String.format("%d. %s\n", i + 1, approaches.get(i)));
        }

        guidance.append("\n### Expert Context\n");
        guidance.append("```\n");
        guidance.append(agent.systemPrompt());
        guidance.append("\n```");

        return guidance.toString();
    }

    private List<String> getAgentApproaches(Agent agent) {
        String name = agent.name();
        if (name.contains("backend") || name.contains("architect")) {
            return List.of(
                "Design clear API contracts and system boundaries",
                "Plan for scalability with caching and data consistency",
                "Implement security patterns (auth, validation, rate limiting)"
            );
        } else if (name.contains("frontend") || name.contains("ui")) {
            return List.of(
                "Focus on accessibility and responsive design",
                "Optimize state management and component patterns",
                "Ensure Core Web Vitals and comprehensive testing"
            );
        } else if (name.contains("ai") || name.contains("ml")) {
            return List.of(
                "Design for production scalability and reliability",
                "Include monitoring, evaluation metrics, and observability",
                "Optimize for cost efficiency and resource usage"
            );
        } else if (name.contains("security") || name.contains("audit")) {
            return List.of(
                "Apply OWASP principles and threat modeling",
                "Implement defense in depth and least privilege",
                "Include compliance and audit trail considerations"
            );
        } else if (name.contains("requirements") || name.contains("analyst")) {
            return List.of(
                "Break features into actionable user stories with acceptance criteria",
                "Identify dependencies and integration points",
                "Provide estimation guidance and risk assessment"
            );
        }
        return List.of(
            "Apply domain-specific best practices",
            "Focus on maintainability and testability",
            "Consider performance and scalability implications"
        );
    }
}