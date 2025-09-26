package com.jenksy.jenksymcp.record;

import java.time.LocalDateTime;

public record AgentUsageStats(
        String agentName,
        String description,
        long invocationCount,
        long successCount,
        double successRate,
        LocalDateTime lastUsed,
        LocalDateTime firstUsed
) {
}