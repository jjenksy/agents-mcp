package com.jenksy.jenksymcp;

import com.jenksy.jenksymcp.service.AgentService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class JenksyMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenksyMcpApplication.class, args);
    }

    @Bean
    public List<ToolCallback> toolCallbacks(AgentService agentService) {
        var agentCallbacks = ToolCallbacks.from(agentService);
        return Arrays.asList(agentCallbacks);
    }
}
