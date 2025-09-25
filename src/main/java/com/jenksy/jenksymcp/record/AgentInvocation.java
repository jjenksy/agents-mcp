package com.jenksy.jenksymcp.record;

public record AgentInvocation(
    String agentName,
    String task,
    String context
) {
}