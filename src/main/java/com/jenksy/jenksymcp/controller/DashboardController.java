package com.jenksy.jenksymcp.controller;

import com.jenksy.jenksymcp.record.Agent;
import com.jenksy.jenksymcp.record.AgentStats;
import com.jenksy.jenksymcp.record.DashboardData;
import com.jenksy.jenksymcp.service.AgentService;
import com.jenksy.jenksymcp.service.AgentStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final AgentService agentService;
    private final AgentStatsService agentStatsService;

    /**
     * Redirect root path to dashboard
     */
    @GetMapping("/")
    public String redirectToDashboard() {
        log.debug("Root path accessed, redirecting to dashboard");
        return "redirect:/dashboard";
    }

    /**
     * Serve the main dashboard page
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("Serving dashboard page");

        try {
            // Add basic data for initial page load
            model.addAttribute("totalAgents", agentService.getAgents().size());
            model.addAttribute("totalInvocations", agentStatsService.getTotalInvocations());
            model.addAttribute("serverTime", LocalDateTime.now());
            model.addAttribute("pageTitle", "Agent Dashboard");

            return "dashboard";
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            model.addAttribute("error", "Failed to load dashboard data");
            return "error";
        }
    }

    /**
     * REST API: Get dashboard summary statistics
     */
    @GetMapping("/api/stats/summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSummaryStats() {
        log.debug("API request for summary statistics");

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("totalInvocations", agentStatsService.getTotalInvocations());
            response.put("uniqueAgents", agentStatsService.getAllStats().size());
            response.put("currentTime", LocalDateTime.now());
            response.put("statsSummary", agentStatsService.getStatsSummary());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting summary statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve summary statistics"));
        }
    }

    /**
     * REST API: Get all agent statistics
     */
    @GetMapping("/api/stats/agents")
    @ResponseBody
    public ResponseEntity<Object> getAllAgentStats() {
        log.debug("API request for all agent statistics");

        try {
            Map<String, AgentStats> allStats = agentStatsService.getAllStats();

            if (allStats.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "No agent statistics available yet",
                    "agents", List.of(),
                    "timestamp", LocalDateTime.now()
                ));
            }

            return ResponseEntity.ok(Map.of(
                "agents", allStats,
                "count", allStats.size(),
                "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Error getting agent statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve agent statistics"));
        }
    }

    /**
     * REST API: Get top N most used agents (default 5)
     */
    @GetMapping("/api/stats/top")
    @ResponseBody
    public ResponseEntity<Object> getTopAgents(@RequestParam(defaultValue = "5") int limit) {
        log.debug("API request for top {} agents", limit);

        // Validate limit parameter
        if (limit < 1 || limit > 50) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Limit must be between 1 and 50"));
        }

        try {
            List<AgentStats> topAgents = agentStatsService.getMostUsedAgents(limit);

            if (topAgents.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "No agent usage data available yet",
                    "topAgents", List.of(),
                    "limit", limit,
                    "timestamp", LocalDateTime.now()
                ));
            }

            return ResponseEntity.ok(Map.of(
                "topAgents", topAgents,
                "limit", limit,
                "actualCount", topAgents.size(),
                "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Error getting top agents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve top agents"));
        }
    }

    /**
     * REST API: Get dashboard data (legacy endpoint for compatibility)
     */
    @GetMapping("/api/dashboard/data")
    @ResponseBody
    public ResponseEntity<DashboardData> getDashboardData() {
        log.debug("Dashboard data API called");

        try {
            DashboardData data = agentStatsService.generateDashboardData(agentService.getAgents());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error getting dashboard data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * REST API: Get statistics for a specific agent
     */
    @GetMapping("/api/stats/agents/{agentName}")
    @ResponseBody
    public ResponseEntity<Object> getAgentStats(@PathVariable String agentName) {
        log.debug("API request for agent statistics: {}", agentName);

        try {
            // Verify agent exists
            Agent agent = agentService.getAgentInfo(agentName);
            if (agent == null) {
                return ResponseEntity.notFound().build();
            }

            AgentStats stats = agentStatsService.getAgentStats(agentName);
            if (stats == null) {
                stats = AgentStats.initial(agentName);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("agentName", stats.agentName());
            response.put("totalInvocations", stats.invocationCount());
            response.put("successCount", stats.successCount());
            response.put("failureCount", stats.failureCount());
            response.put("successRate", stats.getSuccessRate());
            response.put("averageResponseTime", stats.averageResponseTimeMs());
            response.put("lastUsed", stats.lastUsed());
            response.put("firstUsed", stats.firstUsed());
            response.put("agentInfo", agent);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting statistics for agent: {}", agentName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve agent statistics"));
        }
    }

    /**
     * REST API: Get overall system status
     */
    @GetMapping("/api/dashboard/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        log.debug("API request for system status");

        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double usedPercentage = (double) usedMemory / totalMemory * 100;

            Map<String, Object> memory = new HashMap<>();
            memory.put("total", totalMemory);
            memory.put("free", freeMemory);
            memory.put("used", usedMemory);
            memory.put("usedPercentage", Math.round(usedPercentage * 100.0) / 100.0);

            Map<String, Object> status = new HashMap<>();
            status.put("health", "UP");
            status.put("timestamp", LocalDateTime.now());
            status.put("memory", memory);
            status.put("agentCount", agentService.getAgents().size());
            status.put("totalInvocations", agentStatsService.getTotalInvocations());

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error getting system status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve system status"));
        }
    }

    /**
     * REST API: Reset statistics (useful for testing)
     */
    @PostMapping("/api/dashboard/stats/reset")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetStats() {
        log.info("API request to reset statistics");

        try {
            agentStatsService.clearAllStats();

            return ResponseEntity.ok(Map.of(
                "message", "Statistics reset successfully",
                "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Error resetting statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset statistics"));
        }
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("Unexpected error in dashboard controller", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Internal server error",
                    "message", e.getMessage(),
                    "timestamp", LocalDateTime.now()
                ));
    }
}