package com.jenksy.jenksymcp.record;

import java.time.LocalDateTime;

public record AgentStats(
    String agentName,
    long invocationCount,
    long successCount,
    long failureCount,
    double averageResponseTimeMs,
    long totalResponseTimeMs,
    LocalDateTime lastUsed,
    LocalDateTime firstUsed
) {
    public static AgentStats initial(String agentName) {
        LocalDateTime now = LocalDateTime.now();
        return new AgentStats(agentName, 0, 0, 0, 0.0, 0, now, now);
    }

    public AgentStats withInvocation(boolean success, long responseTimeMs) {
        LocalDateTime now = LocalDateTime.now();
        long newInvocationCount = invocationCount + 1;
        long newSuccessCount = success ? successCount + 1 : successCount;
        long newFailureCount = success ? failureCount : failureCount + 1;
        long newTotalResponseTime = totalResponseTimeMs + responseTimeMs;
        double newAverageResponseTime = (double) newTotalResponseTime / newInvocationCount;

        return new AgentStats(
            agentName,
            newInvocationCount,
            newSuccessCount,
            newFailureCount,
            newAverageResponseTime,
            newTotalResponseTime,
            now,
            firstUsed
        );
    }

    public double getSuccessRate() {
        if (invocationCount == 0) return 0.0;
        return (double) successCount / invocationCount * 100.0;
    }

    public double getFailureRate() {
        if (invocationCount == 0) return 0.0;
        return (double) failureCount / invocationCount * 100.0;
    }
}