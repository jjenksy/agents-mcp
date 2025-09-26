package com.jenksy.jenksymcp;

import com.jenksy.jenksymcp.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class JenksyMcpApplication {

    public static void main(String[] args) {
        log.info("Starting Jenksy MCP Server...");
        SpringApplication.run(JenksyMcpApplication.class, args);
    }

    @Bean
    public List<ToolCallback> toolCallbacks(AgentService agentService) {
        log.info("Initializing MCP tool callbacks");
        var agentCallbacks = ToolCallbacks.from(agentService);
        return Arrays.asList(agentCallbacks);
    }
}
