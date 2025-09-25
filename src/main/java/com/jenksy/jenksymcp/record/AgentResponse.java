package com.jenksy.jenksymcp.record;

public record AgentResponse(
    String agentName,
    String model,
    String response,
    String status,
    String context
) {
}