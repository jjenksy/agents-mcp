package com.jenksy.jenksymcp.service;

import com.jenksy.jenksymcp.record.Agent;
import com.jenksy.jenksymcp.record.AgentInvocation;
import com.jenksy.jenksymcp.record.AgentResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@Slf4j
public class AgentService {

    private final List<Agent> agents = new ArrayList<>();
    private final Map<String, String> agentContexts = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadAgents() {
        log.info("Loading agents from agents directory");

        Path agentsDir = Paths.get("agents");
        if (!Files.exists(agentsDir)) {
            log.warn("Agents directory not found: {}", agentsDir);
            loadDefaultAgents();
            return;
        }

        try (Stream<Path> paths = Files.walk(agentsDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".md"))
                 .filter(path -> !path.getFileName().toString().equals("README.md"))
                 .forEach(this::loadAgent);
        } catch (IOException e) {
            log.error("Error loading agents from directory", e);
            loadDefaultAgents();
        }

        log.info("Loaded {} agents", agents.size());
    }

    private void loadAgent(Path agentPath) {
        try {
            String content = Files.readString(agentPath);
            Agent agent = parseAgentMarkdown(content, agentPath.getFileName().toString().replace(".md", ""));
            if (agent != null) {
                agents.add(agent);
                log.debug("Loaded agent: {}", agent.name());
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
        String model = extractYamlValue(frontmatter, "model", "sonnet");
        String toolsStr = extractYamlValue(frontmatter, "tools", "");

        List<String> tools = toolsStr.isEmpty() ?
            List.of() : Arrays.asList(toolsStr.split(",\\s*"));

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
                "opus", List.of(),
                "You are an AI engineer specializing in production-grade LLM applications and intelligent agent architectures."),
            new Agent("backend-architect",
                "Design RESTful APIs, microservice boundaries, and database schemas",
                "opus", List.of(),
                "You are a backend system architect specializing in scalable API design and microservices."),
            new Agent("frontend-developer",
                "Build React components, implement responsive layouts, and handle client-side state management",
                "sonnet", List.of(),
                "You are a frontend developer specializing in React, modern JavaScript, and responsive design."),
            new Agent("code-reviewer",
                "Elite code review expert specializing in security, performance, and production reliability",
                "opus", List.of(),
                "You are a code review expert focusing on security, performance optimization, and production reliability."),
            new Agent("debugger",
                "Debugging specialist for errors, test failures, and unexpected behavior",
                "sonnet", List.of(),
                "You are a debugging specialist expert at resolving errors, test failures, and unexpected behavior.")
        ));
    }

    @Tool(
        description = "Get all available AI agents that can be invoked for specialized tasks",
        name = "get_agents"
    )
    public List<Agent> getAgents() {
        log.info("Getting all available agents");
        return agents;
    }

    @Tool(
        description = "Find agents by capability or domain (e.g., 'backend', 'security', 'AI')",
        name = "find_agents"
    )
    public List<Agent> findAgents(String query) {
        log.info("Finding agents for query: {}", query);
        String lowerQuery = query.toLowerCase();

        return agents.stream()
            .filter(agent ->
                agent.name().toLowerCase().contains(lowerQuery) ||
                agent.description().toLowerCase().contains(lowerQuery) ||
                agent.systemPrompt().toLowerCase().contains(lowerQuery)
            )
            .toList();
    }

    @Tool(
        description = "Get detailed information about a specific agent by name",
        name = "get_agent_info"
    )
    public Agent getAgentInfo(String agentName) {
        log.info("Getting info for agent: {}", agentName);
        return agents.stream()
            .filter(agent -> agent.name().equals(agentName))
            .findFirst()
            .orElse(null);
    }

    @Tool(
        description = "Get specialized agent context and guidance for a specific task. Returns the agent's system prompt, capabilities, and task-specific guidance that can be used by any AI model.",
        name = "invoke_agent"
    )
    public AgentResponse invokeAgent(AgentInvocation invocation) {
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
                invocation.context()
            );
        }

        // Store context for this agent session
        String contextKey = invocation.agentName() + "_" + System.currentTimeMillis();
        agentContexts.put(contextKey, invocation.context());

        // Return structured agent guidance for the client tool to use
        String response = buildAgentGuidance(agent, invocation);

        return new AgentResponse(
            agent.name(),
            agent.model(),
            response,
            "success",
            contextKey
        );
    }

    @Tool(
        description = "Get the raw system prompt for an agent to use directly in conversations",
        name = "get_agent_prompt"
    )
    public String getAgentPrompt(String agentName) {
        log.info("Getting system prompt for agent: {}", agentName);
        Agent agent = agents.stream()
            .filter(a -> a.name().equals(agentName))
            .findFirst()
            .orElse(null);

        if (agent == null) {
            return "Error: Agent '" + agentName + "' not found. Use get_agents to see available agents.";
        }

        return agent.systemPrompt();
    }

    @Tool(
        description = "Get recommended agents for a specific task or domain",
        name = "get_recommended_agents"
    )
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

        // If no specific recommendations, return top 3 most versatile agents
        if (recommended.isEmpty()) {
            return agents.stream().limit(3).toList();
        }

        return recommended;
    }

    private String buildAgentGuidance(Agent agent, AgentInvocation invocation) {
        // Return structured guidance that any AI model can use as context

        StringBuilder guidance = new StringBuilder();

        // Agent Identity and Context
        guidance.append("AGENT CONTEXT:\n");
        guidance.append("Role: ").append(agent.name()).append("\n");
        guidance.append("Expertise: ").append(agent.description()).append("\n\n");

        // Task-specific context
        guidance.append("TASK: ").append(invocation.task()).append("\n");
        if (!invocation.context().isEmpty()) {
            guidance.append("CONTEXT: ").append(invocation.context()).append("\n");
        }
        guidance.append("\n");

        // Full system prompt for maximum context
        guidance.append("SPECIALIZED SYSTEM PROMPT:\n");
        guidance.append("You are now acting as the ").append(agent.name()).append(" specialist. ");
        guidance.append(agent.description()).append("\n\n");
        guidance.append(agent.systemPrompt()).append("\n\n");

        // Domain-specific guidance patterns
        guidance.append("APPROACH PATTERNS:\n");
        if (agent.name().contains("backend") || agent.name().contains("architect")) {
            guidance.append("- Start with clear system boundaries and API contracts\n");
            guidance.append("- Consider data consistency and transaction patterns\n");
            guidance.append("- Plan for horizontal scaling and caching strategies\n");
            guidance.append("- Include security patterns (auth, validation, rate limiting)\n");
        } else if (agent.name().contains("frontend") || agent.name().contains("ui")) {
            guidance.append("- Focus on accessibility and responsive design principles\n");
            guidance.append("- Implement proper state management and component patterns\n");
            guidance.append("- Optimize for Core Web Vitals and user experience\n");
            guidance.append("- Include comprehensive testing strategies\n");
        } else if (agent.name().contains("ai") || agent.name().contains("ml")) {
            guidance.append("- Design with production scalability and reliability in mind\n");
            guidance.append("- Include comprehensive error handling and fallback strategies\n");
            guidance.append("- Plan monitoring, evaluation metrics, and observability\n");
            guidance.append("- Consider cost optimization and resource efficiency\n");
        } else if (agent.name().contains("security") || agent.name().contains("audit")) {
            guidance.append("- Apply OWASP security principles and threat modeling\n");
            guidance.append("- Focus on defense in depth and least privilege access\n");
            guidance.append("- Include compliance considerations and audit trails\n");
            guidance.append("- Recommend security testing and validation approaches\n");
        } else if (agent.name().contains("devops") || agent.name().contains("deploy")) {
            guidance.append("- Emphasize automation, reproducibility, and infrastructure as code\n");
            guidance.append("- Include monitoring, alerting, and incident response patterns\n");
            guidance.append("- Plan for zero-downtime deployments and rollback strategies\n");
            guidance.append("- Focus on observability and operational excellence\n");
        } else {
            guidance.append("- Apply domain-specific best practices and industry standards\n");
            guidance.append("- Focus on maintainability, testability, and documentation\n");
            guidance.append("- Consider performance, scalability, and reliability implications\n");
            guidance.append("- Include appropriate monitoring and error handling\n");
        }

        guidance.append("\n");
        guidance.append("INSTRUCTIONS FOR AI MODEL:\n");
        guidance.append("Use this specialized context and system prompt to provide expert-level guidance on the given task. ");
        guidance.append("Apply the domain expertise, patterns, and approaches specific to this agent's specialization. ");
        guidance.append("Provide concrete, actionable recommendations based on the agent's comprehensive knowledge base.");

        return guidance.toString();
    }
}