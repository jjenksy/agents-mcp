package com.jenksy.jenksymcp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jenksy.jenksymcp.record.Agent;
import com.jenksy.jenksymcp.record.AgentStats;
import com.jenksy.jenksymcp.record.AgentUsageStats;
import com.jenksy.jenksymcp.record.DashboardData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class AgentStatsService {

    @Value("${jenksy.mcp.stats.persistence.enabled:true}")
    private boolean persistenceEnabled;

    @Value("${jenksy.mcp.stats.persistence.file-path:agent-stats.json}")
    private String statsFilePath;

    private Path getStatsPath() {
        return Paths.get(statsFilePath);
    }

    private final ConcurrentHashMap<String, AgentStats> agentStats = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final AtomicLong totalInvocations = new AtomicLong();

    public AgentStatsService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing AgentStatsService - persistence: {}, file: {}", persistenceEnabled, statsFilePath);

        if (persistenceEnabled) {
            try {
                loadStatsFromFile();
                log.info("AgentStatsService initialized with {} agent statistics", agentStats.size());
            } catch (Exception e) {
                log.warn("Failed to load existing stats, starting fresh: {}", e.getMessage());
            }
        } else {
            log.info("Statistics persistence disabled, starting with empty stats");
        }
    }

    @PreDestroy
    public void shutdown() {
        if (persistenceEnabled) {
            try {
                persistStatsToFile();
                log.info("AgentStatsService shutdown - stats persisted");
            } catch (Exception e) {
                log.error("Failed to persist stats during shutdown", e);
            }
        } else {
            log.info("AgentStatsService shutdown - persistence disabled");
        }
    }

    /**
     * Records an agent invocation with timing and success/failure information.
     * This method is designed for zero-impact performance.
     */
    public void recordInvocation(String agentName, boolean success, long responseTimeMs) {
        if (agentName == null || agentName.trim().isEmpty()) {
            return; // Fail fast without logging for performance
        }

        try {
            // Use compute to ensure thread-safe atomic updates
            agentStats.compute(agentName, (name, existingStats) -> {
                if (existingStats == null) {
                    existingStats = AgentStats.initial(name);
                }
                return existingStats.withInvocation(success, responseTimeMs);
            });

            totalInvocations.incrementAndGet();

        } catch (Exception e) {
            // Silent failure to ensure zero impact on MCP performance
            // Only log at debug level to avoid noise
            log.debug("Failed to record stats for agent {}: {}", agentName, e.getMessage());
        }
    }

    /**
     * Get statistics for a specific agent.
     */
    public AgentStats getAgentStats(String agentName) {
        return agentStats.get(agentName);
    }

    /**
     * Get all agent statistics.
     */
    public Map<String, AgentStats> getAllStats() {
        return Collections.unmodifiableMap(agentStats);
    }

    /**
     * Get total number of invocations across all agents.
     */
    public long getTotalInvocations() {
        return totalInvocations.get();
    }

    /**
     * Get most frequently used agents.
     */
    public List<AgentStats> getMostUsedAgents(int limit) {
        return agentStats.values().stream()
                .sorted((a, b) -> Long.compare(b.invocationCount(), a.invocationCount()))
                .limit(limit)
                .toList();
    }

    /**
     * Get agents with highest success rates.
     */
    public List<AgentStats> getHighestSuccessRateAgents(int limit) {
        return agentStats.values().stream()
                .filter(stats -> stats.invocationCount() > 0)
                .sorted((a, b) -> Double.compare(b.getSuccessRate(), a.getSuccessRate()))
                .limit(limit)
                .toList();
    }

    /**
     * Get recently used agents.
     */
    public List<AgentStats> getRecentlyUsedAgents(int limit) {
        return agentStats.values().stream()
                .filter(stats -> stats.lastUsed() != null)
                .sorted((a, b) -> b.lastUsed().compareTo(a.lastUsed()))
                .limit(limit)
                .toList();
    }

    /**
     * Scheduled method to persist stats every 5 minutes.
     */
    @Scheduled(fixedRateString = "#{${jenksy.mcp.stats.persistence.interval-minutes:5} * 60000}") // Convert minutes to
                                                                                                  // milliseconds
    public void scheduledPersistence() {
        if (persistenceEnabled) {
            try {
                persistStatsToFile();
                log.debug("Scheduled stats persistence completed - {} agents tracked", agentStats.size());
            } catch (Exception e) {
                log.warn("Scheduled stats persistence failed: {}", e.getMessage());
            }
        }
    }

    /**
     * Persists current statistics to JSON file.
     */
    private void persistStatsToFile() throws IOException {
        if (agentStats.isEmpty()) {
            log.debug("No stats to persist");
            return;
        }

        // Create a snapshot to avoid blocking the concurrent map
        Map<String, AgentStats> snapshot = Map.copyOf(agentStats);

        // Write to temporary file first, then atomic move
        Path statsPath = getStatsPath();
        Path tempFile = Paths.get(statsFilePath + ".tmp");
        try {
            objectMapper.writeValue(tempFile.toFile(), snapshot);
            Files.move(tempFile, statsPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            log.debug("Persisted {} agent statistics to {}", snapshot.size(), statsPath);
        } catch (IOException e) {
            // Clean up temp file if it exists
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupEx) {
                log.debug("Failed to cleanup temp file: {}", cleanupEx.getMessage());
            }
            throw e;
        }
    }

    /**
     * Loads statistics from JSON file if it exists.
     */
    private void loadStatsFromFile() throws IOException {
        Path statsPath = getStatsPath();
        if (!Files.exists(statsPath)) {
            log.debug("No existing stats file found at {}", statsPath);
            return;
        }

        try {
            TypeReference<Map<String, AgentStats>> typeRef = new TypeReference<>() {
            };
            Map<String, AgentStats> loadedStats = objectMapper.readValue(statsPath.toFile(), typeRef);

            if (loadedStats != null) {
                agentStats.putAll(loadedStats);

                // Update total invocations counter atomically
                long totalFromFile = loadedStats.values().stream()
                        .mapToLong(AgentStats::invocationCount)
                        .sum();
                totalInvocations.addAndGet(totalFromFile);

                log.info("Loaded {} agent statistics from {}", loadedStats.size(), statsPath);
            }
        } catch (Exception e) {
            log.warn("Failed to parse stats file {}: {}", statsPath, e.getMessage());
            // Continue with empty stats rather than failing
        }
    }

    /**
     * Clears all statistics. Use with caution.
     */
    public void clearAllStats() {
        agentStats.clear();
        totalInvocations.set(0);
        log.info("All agent statistics cleared");
    }

    /**
     * Get statistics summary for monitoring/debugging.
     */
    public String getStatsSummary() {
        if (agentStats.isEmpty()) {
            return "No agent statistics available";
        }

        long totalInvocs = getTotalInvocations();
        long totalSuccess = agentStats.values().stream()
                .mapToLong(AgentStats::successCount)
                .sum();
        double overallSuccessRate = totalInvocs > 0 ? (double) totalSuccess / totalInvocs * 100.0 : 0.0;

        return String.format("Agents: %d, Total Invocations: %d, Overall Success Rate: %.1f%%",
                agentStats.size(), totalInvocs, overallSuccessRate);
    }

    /**
     * Generate dashboard data combining agent info with statistics.
     * Thread-safe implementation with atomic snapshot of data.
     */
    public DashboardData generateDashboardData(List<Agent> agents) {
        // Create a snapshot of the current stats to avoid inconsistencies during
        // computation
        Map<String, AgentStats> statsSnapshot = Map.copyOf(agentStats);

        List<AgentUsageStats> usageStats = agents.stream()
                .map(agent -> {
                    String name = agent.name();
                    AgentStats stats = statsSnapshot.get(name);

                    if (stats == null) {
                        return new AgentUsageStats(
                                name,
                                agent.description(),
                                0,
                                0,
                                0.0,
                                null,
                                null);
                    }

                    return new AgentUsageStats(
                            name,
                            agent.description(),
                            stats.invocationCount(),
                            stats.successCount(),
                            stats.getSuccessRate(),
                            stats.lastUsed(),
                            stats.firstUsed());
                })
                .toList();

        // Use atomic snapshot values for consistency
        long totalInvocationCount = getTotalInvocations();
        long activeAgents = usageStats.stream()
                .filter(stats -> stats.invocationCount() > 0)
                .count();

        double overallSuccessRate = usageStats.stream()
                .filter(stats -> stats.invocationCount() > 0)
                .mapToDouble(AgentUsageStats::successRate)
                .average()
                .orElse(0.0);

        return new DashboardData(
                agents,
                usageStats,
                totalInvocationCount,
                activeAgents,
                overallSuccessRate);
    }
}