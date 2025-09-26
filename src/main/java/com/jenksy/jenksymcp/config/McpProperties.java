package com.jenksy.jenksymcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jenksy.mcp")
public class McpProperties {

    private final Agents agents = new Agents();

    public Agents getAgents() {
        return agents;
    }

    public static class Agents {
        private String location = "agents/";
        private boolean hotReload = false;
        private Duration cacheTtl = Duration.ofMinutes(30);
        private int maxContexts = 1000;
        private Duration contextCleanupInterval = Duration.ofMinutes(5);

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean isHotReload() {
            return hotReload;
        }

        public void setHotReload(boolean hotReload) {
            this.hotReload = hotReload;
        }

        public Duration getCacheTtl() {
            return cacheTtl;
        }

        public void setCacheTtl(Duration cacheTtl) {
            this.cacheTtl = cacheTtl;
        }

        public int getMaxContexts() {
            return maxContexts;
        }

        public void setMaxContexts(int maxContexts) {
            this.maxContexts = maxContexts;
        }

        public Duration getContextCleanupInterval() {
            return contextCleanupInterval;
        }

        public void setContextCleanupInterval(Duration contextCleanupInterval) {
            this.contextCleanupInterval = contextCleanupInterval;
        }
    }
}