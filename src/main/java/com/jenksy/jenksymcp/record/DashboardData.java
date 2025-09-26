package com.jenksy.jenksymcp.record;

import java.util.List;

public record DashboardData(
        List<Agent> agents,
        List<AgentUsageStats> usageStats,
        long totalInvocations,
        long activeAgents,
        double overallSuccessRate
) {
}