# MCP Server Troubleshooting Guide

> **🚀 NEW**: The MCP server has been optimized for VS Code Copilot with 75% smaller responses and improved workflow patterns. See the optimization benefits below.

## Common Issues & Solutions

### VS Code Not Using MCP Server

The MCP server is working (as confirmed in Claude Desktop), but VS Code Copilot isn't connecting to it.

## Possible Causes & Solutions

### 1. VS Code Version Requirements

**Check VS Code Version**: You need VS Code 1.99+ for MCP support

```bash
code --version
```

**Update VS Code**: Ensure you have the latest version
- VS Code 1.102+ has full MCP support generally available
- Earlier versions might have limited or experimental support

### 2. GitHub Copilot Extension Version

**Update Extensions**:
1. Open VS Code Extensions (`Ctrl+Shift+X`)
2. Check for updates to:
   - GitHub Copilot
   - GitHub Copilot Chat
3. Restart VS Code after updates

### 3. Enable Agent Mode

**In VS Code Copilot Chat**:
1. Open Copilot Chat (`Ctrl+Shift+I` or `Cmd+Shift+I`)
2. Look for an "Agent Mode" dropdown or toggle
3. Enable Agent Mode if available
4. Look for a "Tools" button to see MCP tools

### 4. MCP Feature Flags

**Enable Experimental Features** (if needed):

Add to your User Settings (`Ctrl+,` → Open Settings JSON):
```json
{
  "github.copilot.chat.experimental.mcp.enabled": true,
  "github.copilot.chat.experimental.agents.enabled": true
}
```

### 5. Organization/Enterprise Policies

**If you're using Copilot Business/Enterprise**:
- Your organization admin needs to enable "MCP servers in Copilot" policy
- This policy is disabled by default
- Contact your admin to enable it

**For Copilot Free/Pro users**: No policy restrictions

### 6. Alternative: Global MCP Configuration

Instead of workspace-specific configuration, try global configuration:

**Add to User Settings JSON** (`~/.vscode/settings.json` or through VS Code settings):
```json
{
  "github.copilot.chat.mcp.servers": {
    "ai-agents": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/johnjenkins/java-projects/jenksy.me/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Verification Steps

### 1. Check MCP Server Status
```bash
# Test that server starts properly
java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
# Should show Spring Boot startup logs
```

### 2. Look for MCP Indicators in VS Code
- Open Copilot Chat
- Look for "Agent Mode" or "Tools" options
- Check for MCP server status in VS Code status bar
- Look for MCP-related notifications

### 3. Alternative Testing

**Test with Claude Desktop** (which works):
```json
// In claude_desktop_config.json
{
  "mcpServers": {
    "ai-agents": {
      "command": "java",
      "args": ["-jar", "/Users/johnjenkins/java-projects/jenksy.me/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"]
    }
  }
}
```

## Current Status

**MCP Server**: Working (tested with Claude Desktop)
**Agent Loading**: 10 agents loaded from `agents/` directory
**Spring Boot**: Server starts successfully
**VS Code Integration**: Not connecting to MCP server

## Next Steps

1. **Check VS Code Version**: Ensure 1.99+
2. **Update Extensions**: Get latest Copilot extensions
3. **Look for Agent Mode**: In Copilot Chat interface
4. **Try Global Config**: Add to user settings instead of workspace
5. **Check Enterprise Policy**: If using Copilot Business/Enterprise

## Optimized MCP Usage (When Working)

Once MCP integration is working, use these **optimized patterns** for best results:

### ✅ Recommended: Single-Call Workflow
```javascript
// Get everything you need in one efficient call
@workspace Use invoke_agent({
  "agentName": "security-auditor",
  "task": "Review authentication implementation for vulnerabilities",
  "context": "Spring Security 6, JWT tokens, microservices architecture"
})
```

**Benefits:**
- 75% smaller response size (~1.5KB vs 6KB+)
- Structured markdown format for better readability
- Context-aware recommendations
- All guidance in single response

### ❌ Avoid: Legacy Multiple-Call Pattern
```javascript
// Less efficient (still works but not optimized)
@workspace Use get_agent_info("security-auditor")
@workspace Use get_agent_prompt("security-auditor")
// Then manually construct request
```

## Fallback: Manual Agent Usage

If MCP integration doesn't work immediately, you can manually use agents by:

1. **Get Optimized Guidance**: Use Claude Desktop with optimized workflow
2. **Copy Structured Results**: Use the markdown-formatted responses
3. **Example**:
   ```
   # In Claude Desktop (optimized):
   invoke_agent({
     "agentName": "security-auditor",
     "task": "Review authentication code",
     "context": "Spring Security JWT implementation"
   })

   # Copy the structured response to VS Code
   # Use the organized recommendations directly
   ```

## Report Issue

If MCP still doesn't work after these steps:
1. Report to GitHub Copilot support
2. Include VS Code version, Copilot extension versions
3. Include MCP configuration details
4. Mention that MCP server works with Claude Desktop

The MCP protocol is still relatively new in VS Code, so there might be compatibility issues or missing features being rolled out gradually.