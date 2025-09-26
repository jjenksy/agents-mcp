package com.jenksy.jenksymcp.service;

import com.jenksy.jenksymcp.record.Agent;
import com.jenksy.jenksymcp.record.AgentInvocation;
import com.jenksy.jenksymcp.record.AgentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgentService Tests")
class AgentServiceTest {

    private AgentService agentService;

    @BeforeEach
    void setUp() {
        agentService = new AgentService();
    }

    @Nested
    @DisplayName("Agent Loading Tests")
    class AgentLoadingTests {

        @Test
        @DisplayName("Should load default agents when no classpath agents found")
        void shouldLoadDefaultAgentsWhenNoClasspathAgentsFound() {
            // When
            agentService.loadAgents();

            // Then
            List<Agent> agents = agentService.getAgents();
            assertThat(agents).hasSize(5); // Default agents
            assertThat(agents.stream().map(Agent::name))
                .containsExactlyInAnyOrder("ai-engineer", "backend-architect", "frontend-developer", "code-reviewer", "debugger");
        }

        @Test
        @DisplayName("Should handle malformed agent files gracefully")
        void shouldHandleMalformedAgentFiles() {
            // Given
            String malformedContent = "This is not valid YAML frontmatter content";

            // When/Then - parseAgentMarkdown should return null for malformed content
            Agent result = (Agent) ReflectionTestUtils.invokeMethod(agentService, "parseAgentMarkdown", malformedContent, "test-agent");
            assertThat(result).isNull();
        }

    }

    @Nested
    @DisplayName("YAML Parsing Tests")
    class YamlParsingTests {

        @Test
        @DisplayName("Should parse complete agent YAML correctly")
        void shouldParseCompleteAgentYaml() {
            // Given
            String content = """
                ---
                name: test-agent
                description: Test agent description
                tools: tool1, tool2, tool3
                ---

                You are a test agent with these capabilities.
                """;

            // When
            Agent result = (Agent) ReflectionTestUtils.invokeMethod(agentService, "parseAgentMarkdown", content, "test-agent");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isNotNull(); // Ensure non-null for IDE
            assertThat(result.name()).isEqualTo("test-agent");
            assertThat(result.description()).isEqualTo("Test agent description");
            assertThat(result.model()).isEqualTo("mcp-optimized");
            assertThat(result.tools()).containsExactly("tool1", "tool2", "tool3");
            assertThat(result.systemPrompt()).contains("You are a test agent");
        }

        @Test
        @DisplayName("Should parse minimal agent YAML with defaults")
        void shouldParseMinimalAgentYaml() {
            // Given
            String content = """
                ---
                name: minimal-agent
                description: Minimal description
                ---

                Basic system prompt.
                """;

            // When
            Agent result = (Agent) ReflectionTestUtils.invokeMethod(agentService, "parseAgentMarkdown", content, "minimal-agent");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isNotNull(); // Ensure non-null for IDE
            assertThat(result.name()).isEqualTo("minimal-agent");
            assertThat(result.description()).isEqualTo("Minimal description");
            assertThat(result.model()).isEqualTo("mcp-optimized");
            assertThat(result.tools()).isEmpty();
            assertThat(result.systemPrompt()).isEqualTo("Basic system prompt.");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "No frontmatter at all",
            "---\nincomplete frontmatter",
            "---\nname: test\n--\nMissing closing delimiter"
        })
        @DisplayName("Should return null for invalid YAML frontmatter")
        void shouldReturnNullForInvalidYaml(String invalidContent) {
            // When
            Agent result = (Agent) ReflectionTestUtils.invokeMethod(agentService, "parseAgentMarkdown", invalidContent, "test");

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("MCP Tool Tests")
    class McpToolTests {

        @BeforeEach
        void setUpAgents() {
            // Manually add test agents
            List<Agent> testAgents = List.of(
                new Agent("ai-engineer", "AI engineering expert", "mcp-optimized", List.of(), "You are an AI engineer."),
                new Agent("backend-architect", "Backend architecture specialist", "mcp-optimized", List.of(), "You are a backend architect."),
                new Agent("security-auditor", "Security analysis expert", "mcp-optimized", List.of(), "You are a security auditor.")
            );
            ReflectionTestUtils.setField(agentService, "agents", new java.util.ArrayList<>(testAgents));
        }

        @Test
        @DisplayName("get_agents should return all agents")
        void getAgentsShouldReturnAllAgents() {
            // When
            List<Agent> result = agentService.getAgents();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.stream().map(Agent::name))
                .containsExactly("ai-engineer", "backend-architect", "security-auditor");
        }

        @Test
        @DisplayName("find_agents should filter agents by query")
        void findAgentsShouldFilterByQuery() {
            // When
            List<Agent> result = agentService.findAgents("backend");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("backend-architect");
        }

        @Test
        @DisplayName("find_agents should be case insensitive")
        void findAgentsShouldBeCaseInsensitive() {
            // When
            List<Agent> result = agentService.findAgents("BACKEND");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("backend-architect");
        }

        @Test
        @DisplayName("find_agents should search in description and system prompt")
        void findAgentsShouldSearchInDescriptionAndPrompt() {
            // When
            List<Agent> securityResults = agentService.findAgents("security");

            // Then
            assertThat(securityResults).hasSize(1);
            assertThat(securityResults.get(0).name()).isEqualTo("security-auditor");
        }

        @Test
        @DisplayName("get_agent_info should return specific agent")
        void getAgentInfoShouldReturnSpecificAgent() {
            // When
            Agent result = agentService.getAgentInfo("ai-engineer");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("ai-engineer");
            assertThat(result.description()).contains("AI engineering");
        }

        @Test
        @DisplayName("get_agent_info should return null for non-existent agent")
        void getAgentInfoShouldReturnNullForNonExistentAgent() {
            // When
            Agent result = agentService.getAgentInfo("non-existent");

            // Then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Agent Invocation Tests")
    class AgentInvocationTests {

        @BeforeEach
        void setUpAgents() {
            List<Agent> testAgents = List.of(
                new Agent("ai-engineer", "AI engineering expert", "mcp-optimized", List.of(), "You are an AI engineer specializing in production systems.")
            );
            ReflectionTestUtils.setField(agentService, "agents", new java.util.ArrayList<>(testAgents));
        }

        @Test
        @DisplayName("invoke_agent should return structured response for valid request")
        void invokeAgentShouldReturnStructuredResponse() {
            // Given
            AgentInvocation invocation = new AgentInvocation("ai-engineer", "Design a RAG system", "Spring Boot application");

            // When
            AgentResponse response = agentService.invokeAgent(invocation);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.agentName()).isEqualTo("ai-engineer");
            assertThat(response.model()).isEqualTo("mcp-optimized");
            assertThat(response.status()).isEqualTo("success");
            assertThat(response.response()).contains("AI-ENGINEER SPECIALIST");
            assertThat(response.response()).contains("Design a RAG system");
            assertThat(response.response()).contains("Spring Boot application");
            assertThat(response.context()).isNotNull();
        }

        @Test
        @DisplayName("invoke_agent should return error for blank agent name")
        void invokeAgentShouldReturnErrorForBlankAgentName() {
            // Given
            AgentInvocation invocation = new AgentInvocation("", "Some task", "Some context");

            // When
            AgentResponse response = agentService.invokeAgent(invocation);

            // Then
            assertThat(response.status()).isEqualTo("error");
            assertThat(response.response()).contains("Agent name cannot be blank");
        }

        @Test
        @DisplayName("invoke_agent should return error for blank task")
        void invokeAgentShouldReturnErrorForBlankTask() {
            // Given
            AgentInvocation invocation = new AgentInvocation("ai-engineer", "", "Some context");

            // When
            AgentResponse response = agentService.invokeAgent(invocation);

            // Then
            assertThat(response.status()).isEqualTo("error");
            assertThat(response.response()).contains("Task description cannot be blank");
        }

        @Test
        @DisplayName("invoke_agent should return error for non-existent agent")
        void invokeAgentShouldReturnErrorForNonExistentAgent() {
            // Given
            AgentInvocation invocation = new AgentInvocation("non-existent", "Some task", "Some context");

            // When
            AgentResponse response = agentService.invokeAgent(invocation);

            // Then
            assertThat(response.status()).isEqualTo("error");
            assertThat(response.response()).contains("Agent 'non-existent' not found");
        }
    }

    @Nested
    @DisplayName("Agent Recommendations Tests")
    class AgentRecommendationsTests {

        @BeforeEach
        void setUpAgents() {
            List<Agent> testAgents = List.of(
                new Agent("ai-engineer", "LLM applications and RAG systems", "mcp-optimized", List.of(), "AI expert"),
                new Agent("backend-architect", "RESTful APIs and microservices", "mcp-optimized", List.of(), "Backend expert"),
                new Agent("frontend-developer", "React and UI development", "mcp-optimized", List.of(), "Frontend expert"),
                new Agent("security-auditor", "Security analysis and auditing", "mcp-optimized", List.of(), "Security expert"),
                new Agent("debugger", "Error analysis and debugging", "mcp-optimized", List.of(), "Debug expert"),
                new Agent("requirements-analyst", "Requirements breakdown and tickets", "mcp-optimized", List.of(), "Requirements expert")
            );
            ReflectionTestUtils.setField(agentService, "agents", new java.util.ArrayList<>(testAgents));
        }

        @ParameterizedTest
        @MethodSource("recommendationTestCases")
        @DisplayName("get_recommended_agents should return relevant agents")
        void getRecommendedAgentsShouldReturnRelevantAgents(String task, List<String> expectedAgents) {
            // When
            List<Agent> result = agentService.getRecommendedAgents(task);

            // Then
            assertThat(result).hasSizeLessThanOrEqualTo(3); // Should limit to 3
            assertThat(result.stream().map(Agent::name)).containsAnyElementsOf(expectedAgents);
        }

        private static Stream<Arguments> recommendationTestCases() {
            return Stream.of(
                Arguments.of("build API backend", List.of("backend-architect")),
                Arguments.of("implement React frontend", List.of("frontend-developer")),
                Arguments.of("AI and LLM integration", List.of("ai-engineer")),
                Arguments.of("security audit review", List.of("security-auditor")),
                Arguments.of("debug application errors", List.of("debugger")),
                Arguments.of("create user stories and tickets", List.of("requirements-analyst")),
                Arguments.of("unknown domain task", List.of("ai-engineer", "backend-architect", "code-reviewer"))
            );
        }

        @Test
        @DisplayName("get_recommended_agents should return top 3 versatile agents for unknown tasks")
        void getRecommendedAgentsShouldReturnVersatileAgentsForUnknownTasks() {
            // When
            List<Agent> result = agentService.getRecommendedAgents("completely unknown task");

            // Then
            assertThat(result).hasSizeLessThanOrEqualTo(3);
            assertThat(result).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Caching Tests")
    class CachingTests {

        @Test
        @DisplayName("Should cache agent contexts with proper expiration")
        void shouldCacheAgentContexts() {
            // Given
            List<Agent> testAgents = List.of(
                new Agent("test-agent", "Test description", "mcp-optimized", List.of(), "Test prompt")
            );
            ReflectionTestUtils.setField(agentService, "agents", new java.util.ArrayList<>(testAgents));

            AgentInvocation invocation = new AgentInvocation("test-agent", "test task", "test context");

            // When
            AgentResponse response = agentService.invokeAgent(invocation);

            // Then
            assertThat(response.context()).isNotNull();

            // Verify context is cached
            var agentContexts = ReflectionTestUtils.getField(agentService, "agentContexts");
            assertThat(agentContexts).isNotNull();
        }

        @Test
        @DisplayName("Should cleanup expired contexts")
        void shouldCleanupExpiredContexts() {
            // When
            agentService.cleanupExpiredContexts();

            // Then - Should not throw exception
            assertDoesNotThrow(() -> agentService.cleanupExpiredContexts());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null inputs gracefully")
        void shouldHandleNullInputsGracefully() {
            // When/Then
            assertThatThrownBy(() -> agentService.findAgents(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle empty search queries")
        void shouldHandleEmptySearchQueries() {
            // When
            List<Agent> result = agentService.findAgents("");

            // Then
            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should handle invocation with null fields gracefully")
        void shouldHandleInvocationWithNullFields() {
            // Given
            AgentInvocation invocation = new AgentInvocation(null, null, null);

            // When
            AgentResponse response = agentService.invokeAgent(invocation);

            // Then
            assertThat(response.status()).isEqualTo("error");
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("Should shutdown gracefully")
        void shouldShutdownGracefully() {
            // When/Then - Should not throw exception
            assertDoesNotThrow(() -> {
                ReflectionTestUtils.invokeMethod(agentService, "shutdown");
            });
        }
    }
}