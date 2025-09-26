package com.jenksy.jenksymcp.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Record Classes Tests")
class RecordTests {

    @Test
    @DisplayName("Agent record should work correctly")
    void agentRecordShouldWorkCorrectly() {
        // Given
        String name = "test-agent";
        String description = "Test agent description";
        String model = "mcp-optimized";
        List<String> tools = List.of("tool1", "tool2");
        String systemPrompt = "You are a test agent.";

        // When
        Agent agent = new Agent(name, description, model, tools, systemPrompt);

        // Then
        assertThat(agent.name()).isEqualTo(name);
        assertThat(agent.description()).isEqualTo(description);
        assertThat(agent.model()).isEqualTo(model);
        assertThat(agent.tools()).isEqualTo(tools);
        assertThat(agent.systemPrompt()).isEqualTo(systemPrompt);
    }

    @Test
    @DisplayName("Agent record should support equality and hashCode")
    void agentRecordShouldSupportEqualityAndHashCode() {
        // Given
        Agent agent1 = new Agent("test", "desc", "model", List.of(), "prompt");
        Agent agent2 = new Agent("test", "desc", "model", List.of(), "prompt");
        Agent agent3 = new Agent("different", "desc", "model", List.of(), "prompt");

        // Then
        assertThat(agent1).isEqualTo(agent2);
        assertThat(agent1).isNotEqualTo(agent3);
        assertThat(agent1.hashCode()).isEqualTo(agent2.hashCode());
        assertThat(agent1.hashCode()).isNotEqualTo(agent3.hashCode());
    }

    @Test
    @DisplayName("Agent record should have proper toString")
    void agentRecordShouldHaveProperToString() {
        // Given
        Agent agent = new Agent("test", "desc", "model", List.of("tool1"), "prompt");

        // When
        String toString = agent.toString();

        // Then
        assertThat(toString).contains("test", "desc", "model", "tool1", "prompt");
    }

    @Test
    @DisplayName("AgentInvocation record should work correctly")
    void agentInvocationRecordShouldWorkCorrectly() {
        // Given
        String agentName = "test-agent";
        String task = "Test task";
        String context = "Test context";

        // When
        AgentInvocation invocation = new AgentInvocation(agentName, task, context);

        // Then
        assertThat(invocation.agentName()).isEqualTo(agentName);
        assertThat(invocation.task()).isEqualTo(task);
        assertThat(invocation.context()).isEqualTo(context);
    }

    @Test
    @DisplayName("AgentResponse record should work correctly")
    void agentResponseRecordShouldWorkCorrectly() {
        // Given
        String agentName = "test-agent";
        String model = "mcp-optimized";
        String content = "Test response content";
        String status = "success";
        String contextKey = "context123";

        // When
        AgentResponse response = new AgentResponse(agentName, model, content, status, contextKey);

        // Then
        assertThat(response.agentName()).isEqualTo(agentName);
        assertThat(response.model()).isEqualTo(model);
        assertThat(response.response()).isEqualTo(content);
        assertThat(response.status()).isEqualTo(status);
        assertThat(response.context()).isEqualTo(contextKey);
    }

    @ParameterizedTest
    @MethodSource("recordEqualityTestCases")
    @DisplayName("Records should handle equality correctly")
    void recordsShouldHandleEqualityCorrectly(Object record1, Object record2, boolean shouldBeEqual) {
        // Then
        if (shouldBeEqual) {
            assertThat(record1).isEqualTo(record2);
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        } else {
            assertThat(record1).isNotEqualTo(record2);
        }
    }

    private static Stream<Arguments> recordEqualityTestCases() {
        return Stream.of(
            // Agent equality cases
            Arguments.of(
                new Agent("same", "desc", "model", List.of(), "prompt"),
                new Agent("same", "desc", "model", List.of(), "prompt"),
                true
            ),
            Arguments.of(
                new Agent("different", "desc", "model", List.of(), "prompt"),
                new Agent("other", "desc", "model", List.of(), "prompt"),
                false
            ),

            // AgentInvocation equality cases
            Arguments.of(
                new AgentInvocation("agent", "task", "context"),
                new AgentInvocation("agent", "task", "context"),
                true
            ),
            Arguments.of(
                new AgentInvocation("agent1", "task", "context"),
                new AgentInvocation("agent2", "task", "context"),
                false
            ),

            // AgentResponse equality cases
            Arguments.of(
                new AgentResponse("agent", "model", "content", "status", "key"),
                new AgentResponse("agent", "model", "content", "status", "key"),
                true
            ),
            Arguments.of(
                new AgentResponse("agent", "model", "content1", "status", "key"),
                new AgentResponse("agent", "model", "content2", "status", "key"),
                false
            )
        );
    }

    @Test
    @DisplayName("Records should handle null values appropriately")
    void recordsShouldHandleNullValuesAppropriately() {
        // Test Agent with nulls
        Agent agentWithNulls = new Agent(null, null, null, null, null);
        assertThat(agentWithNulls.name()).isNull();
        assertThat(agentWithNulls.description()).isNull();
        assertThat(agentWithNulls.model()).isNull();
        assertThat(agentWithNulls.tools()).isNull();
        assertThat(agentWithNulls.systemPrompt()).isNull();

        // Test AgentInvocation with nulls
        AgentInvocation invocationWithNulls = new AgentInvocation(null, null, null);
        assertThat(invocationWithNulls.agentName()).isNull();
        assertThat(invocationWithNulls.task()).isNull();
        assertThat(invocationWithNulls.context()).isNull();

        // Test AgentResponse with nulls
        AgentResponse responseWithNulls = new AgentResponse(null, null, null, null, null);
        assertThat(responseWithNulls.agentName()).isNull();
        assertThat(responseWithNulls.model()).isNull();
        assertThat(responseWithNulls.response()).isNull();
        assertThat(responseWithNulls.status()).isNull();
        assertThat(responseWithNulls.context()).isNull();
    }

    @Test
    @DisplayName("Records should be immutable")
    void recordsShouldBeImmutable() {
        // Given
        List<String> originalTools = List.of("tool1", "tool2");
        Agent agent = new Agent("test", "desc", "model", originalTools, "prompt");

        // When - Try to modify the tools list
        List<String> retrievedTools = agent.tools();

        // Then - Should not be able to modify the original
        assertThatThrownBy(() -> retrievedTools.add("new-tool"))
            .isInstanceOf(UnsupportedOperationException.class);

        // Original agent should remain unchanged
        assertThat(agent.tools()).hasSize(2);
    }
}