package com.jenksy.jenksymcp.integration;

import com.jenksy.jenksymcp.record.Agent;
import com.jenksy.jenksymcp.record.AgentInvocation;
import com.jenksy.jenksymcp.record.AgentResponse;
import com.jenksy.jenksymcp.service.AgentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.com.jenksy.jenksymcp=DEBUG"
})
@DisplayName("MCP Tool Integration Tests")
class McpToolIntegrationTest {

    @Autowired
    private AgentService agentService;

    @Autowired
    private List<ToolCallback> toolCallbacks;

    @Test
    @DisplayName("Should register all MCP tools successfully")
    void shouldRegisterAllMcpToolsSuccessfully() {
        // Then
        assertThat(toolCallbacks).isNotEmpty();
        assertThat(toolCallbacks).hasSize(5); // AgentService provides 5 tool callbacks (one per @Tool method)

        // Verify all expected tool names are present
        List<String> toolNames = toolCallbacks.stream()
            .map(callback -> callback.getToolDefinition().name())
            .toList();
        assertThat(toolNames).containsExactlyInAnyOrder(
            "get_agents", "find_agents", "get_agent_info", "invoke_agent", "get_recommended_agents"
        );
    }

    @Test
    @DisplayName("All MCP tools should be functional end-to-end")
    void allMcpToolsShouldBeFunctionalEndToEnd() {
        // Test get_agents
        List<Agent> allAgents = agentService.getAgents();
        assertThat(allAgents).isNotEmpty();
        assertThat(allAgents.size()).isGreaterThan(0);

        // Test find_agents
        List<Agent> backendAgents = agentService.findAgents("backend");
        assertThat(backendAgents).isNotEmpty();

        // Test get_agent_info
        String testAgentName = allAgents.get(0).name();
        Agent agentInfo = agentService.getAgentInfo(testAgentName);
        assertThat(agentInfo).isNotNull();
        assertThat(agentInfo.name()).isEqualTo(testAgentName);

        // Test invoke_agent
        AgentInvocation invocation = new AgentInvocation(
            testAgentName,
            "Test task for integration testing",
            "Spring Boot MCP server context"
        );
        AgentResponse response = agentService.invokeAgent(invocation);
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("success");
        assertThat(response.model()).isEqualTo("mcp-optimized");
        assertThat(response.response()).isNotEmpty();

        // Test get_recommended_agents
        List<Agent> recommendations = agentService.getRecommendedAgents("build REST API");
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations.size()).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Should load agents from production classpath")
    void shouldLoadAgentsFromProductionClasspath() {
        // When
        List<Agent> agents = agentService.getAgents();

        // Then
        assertThat(agents).hasSizeGreaterThanOrEqualTo(5); // Should have at least default agents

        // Verify some key agents are present (at least default agents)
        List<String> agentNames = agents.stream().map(Agent::name).toList();
        assertThat(agentNames).contains(
            "ai-engineer",
            "backend-architect",
            "frontend-developer",
            "code-reviewer",
            "debugger"
        );

        // Verify all agents have proper model
        agents.forEach(agent -> {
            assertThat(agent.model()).isEqualTo("mcp-optimized");
            assertThat(agent.name()).isNotBlank();
            assertThat(agent.description()).isNotBlank();
            assertThat(agent.systemPrompt()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Should handle concurrent MCP tool calls")
    void shouldHandleConcurrentMcpToolCalls() {
        // Given
        List<Agent> agents = agentService.getAgents();
        String testAgentName = agents.get(0).name();

        // When - Simulate concurrent calls
        List<Thread> threads = List.of(
            new Thread(() -> agentService.getAgents()),
            new Thread(() -> agentService.findAgents("test")),
            new Thread(() -> agentService.getAgentInfo(testAgentName)),
            new Thread(() -> agentService.getRecommendedAgents("build API")),
            new Thread(() -> agentService.invokeAgent(new AgentInvocation(testAgentName, "concurrent test", "test context")))
        );

        threads.forEach(Thread::start);

        // Then - All threads should complete without errors
        assertThatCode(() -> {
            for (Thread thread : threads) {
                thread.join(5000); // 5 second timeout
            }
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should maintain performance under load")
    void shouldMaintainPerformanceUnderLoad() {
        // Given
        String testAgentName = agentService.getAgents().get(0).name();

        // When - Measure performance of cached operations
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            agentService.getAgents(); // Should be cached after first call
            agentService.getAgentInfo(testAgentName); // Should be cached
            agentService.findAgents("backend"); // Should be cached
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Then - Should complete quickly due to caching
        assertThat(totalTime).isLessThan(1000); // Less than 1 second for 300 cached operations
    }

    @Test
    @DisplayName("Should validate MCP tool response formats")
    void shouldValidateMcpToolResponseFormats() {
        // Test get_agents response format
        List<Agent> agents = agentService.getAgents();
        agents.forEach(agent -> {
            assertThat(agent.name()).matches("[a-z][a-z0-9-]*"); // Kebab case naming
            assertThat(agent.description()).isNotEmpty();
            assertThat(agent.model()).isEqualTo("mcp-optimized");
            assertThat(agent.tools()).isNotNull();
            assertThat(agent.systemPrompt()).isNotEmpty();
        });

        // Test invoke_agent response format
        String testAgentName = agents.get(0).name();
        AgentInvocation invocation = new AgentInvocation(testAgentName, "Format validation test", "Integration test context");
        AgentResponse response = agentService.invokeAgent(invocation);

        assertThat(response.agentName()).isEqualTo(testAgentName);
        assertThat(response.model()).isEqualTo("mcp-optimized");
        assertThat(response.status()).isEqualTo("success");
        assertThat(response.response()).contains("## " + testAgentName.toUpperCase() + " SPECIALIST");
        assertThat(response.response()).contains("### Task Analysis");
        assertThat(response.response()).contains("### Recommended Approach");
        assertThat(response.context()).isNotNull();
    }

    @Test
    @DisplayName("Should handle error scenarios gracefully in integration")
    void shouldHandleErrorScenariosGracefullyInIntegration() {
        // Test non-existent agent
        Agent nonExistentAgent = agentService.getAgentInfo("non-existent-agent");
        assertThat(nonExistentAgent).isNull();

        // Test invalid invocation
        AgentResponse errorResponse = agentService.invokeAgent(
            new AgentInvocation("non-existent", "test task", "test context")
        );
        assertThat(errorResponse.status()).isEqualTo("error");
        assertThat(errorResponse.response()).contains("not found");

        // Test empty search
        List<Agent> emptyResults = agentService.findAgents("nonexistentdomain12345");
        assertThat(emptyResults).isEmpty();
    }
}