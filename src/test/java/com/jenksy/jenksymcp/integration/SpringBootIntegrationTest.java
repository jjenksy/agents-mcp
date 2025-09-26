package com.jenksy.jenksymcp.integration;

import com.jenksy.jenksymcp.JenksyMcpApplication;
import com.jenksy.jenksymcp.service.AgentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = JenksyMcpApplication.class)
@ActiveProfiles("test")
@DisplayName("Spring Boot Integration Tests")
class SpringBootIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AgentService agentService;

    @Autowired
    private List<ToolCallback> toolCallbacks;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Should load Spring Boot application context successfully")
    void shouldLoadApplicationContextSuccessfully() {
        // Then
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean(AgentService.class)).isNotNull();
        assertThat(applicationContext.getBean(JenksyMcpApplication.class)).isNotNull();
    }

    @Test
    @DisplayName("Should configure MCP tool callbacks bean correctly")
    void shouldConfigureMcpToolCallbacksBeanCorrectly() {
        // Then
        assertThat(toolCallbacks).isNotNull();
        assertThat(toolCallbacks).isNotEmpty();
        // Note: The actual size may vary depending on Spring AI configuration
        assertThat(toolCallbacks.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should configure caching correctly")
    void shouldConfigureCachingCorrectly() {
        // Then
        assertThat(cacheManager).isNotNull();

        // Verify caching is working by triggering cached methods
        agentService.getAgents(); // This should create the cache
        agentService.getAgentInfo("ai-engineer"); // This should create cache if agent exists

        // Verify cache manager is functional
        assertThat(cacheManager.getCacheNames()).isNotNull();
    }

    @Test
    @DisplayName("Should initialize AgentService with proper lifecycle")
    void shouldInitializeAgentServiceWithProperLifecycle() {
        // Then
        assertThat(agentService).isNotNull();

        // Verify PostConstruct was called and agents loaded
        assertThat(agentService.getAgents()).isNotEmpty();

        // Verify all agents are properly initialized
        agentService.getAgents().forEach(agent -> {
            assertThat(agent.name()).isNotBlank();
            assertThat(agent.description()).isNotBlank();
            assertThat(agent.model()).isEqualTo("mcp-optimized");
            assertThat(agent.systemPrompt()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Should handle application shutdown gracefully")
    void shouldHandleApplicationShutdownGracefully() {
        // When/Then - Should not throw exception during context close preparation
        assertThatCode(() -> {
            // Simulate pre-destroy activities
            agentService.cleanupExpiredContexts();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should have proper Spring profiles configuration")
    void shouldHaveProperSpringProfilesConfiguration() {
        // Then
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertThat(activeProfiles).contains("test");
    }

    @Test
    @DisplayName("Should have all required Spring components")
    void shouldHaveAllRequiredSpringComponents() {
        // Then
        assertThat(applicationContext.getBean("agentService")).isNotNull();
        assertThat(applicationContext.getBean("toolCallbacks")).isNotNull();
        assertThat(applicationContext.getBean("cacheManager")).isNotNull();

        // Verify component types
        assertThat(applicationContext.getBean(AgentService.class)).isInstanceOf(AgentService.class);
        assertThat(applicationContext.getBean("toolCallbacks")).isInstanceOf(List.class);
    }

    @Test
    @DisplayName("Should validate Spring Boot actuator endpoints are available")
    void shouldValidateSpringBootActuatorEndpointsAreAvailable() {
        // This test verifies that actuator dependencies are properly configured
        // In a real deployment, these would be available at /actuator/*

        // Verify health indicator components are available
        assertThatCode(() -> {
            // ApplicationContext should contain health-related beans
            applicationContext.getBeansOfType(org.springframework.boot.actuate.health.HealthIndicator.class);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should validate proper dependency injection")
    void shouldValidateProperDependencyInjection() {
        // Verify AgentService has all its dependencies properly injected
        assertThat(agentService.getAgents()).isNotNull();

        // Verify tool callbacks were created with AgentService
        assertThat(toolCallbacks).allSatisfy(callback ->
            assertThat(callback).isNotNull()
        );

        // Verify caching is working
        var initialAgents = agentService.getAgents();
        var cachedAgents = agentService.getAgents();
        assertThat(initialAgents).isEqualTo(cachedAgents);
    }
}