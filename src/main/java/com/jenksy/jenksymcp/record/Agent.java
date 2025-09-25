package com.jenksy.jenksymcp.record;

import java.util.List;

public record Agent(
    String name,
    String description,
    String model,
    List<String> tools,
    String systemPrompt
) {
}