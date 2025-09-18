# Jenksy MCP Server

A Model Context Protocol (MCP) server built with Spring Boot that provides course-related tools for Claude Desktop integration.

## Overview

This MCP server exposes programming course data and management tools that Claude can use to help users discover and manage educational content. The server comes pre-loaded with 20 programming courses covering various technologies.

## Prerequisites

- **Java 21** or higher
- **Gradle** (or use the included Gradle wrapper)

## Building the Project

### 1. Clone and Navigate
```bash
cd /path/to/jenksy-mcp
```

### 2. Build the JAR
```bash
./gradlew clean build
```

This creates two JAR files in `build/libs/`:
- `jenksy-mcp-0.0.1-SNAPSHOT.jar` - Fat JAR with all dependencies (~22MB)
- `jenksy-mcp-0.0.1-SNAPSHOT-plain.jar` - Plain JAR without dependencies

### 3. Test the Build
```bash
java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar
```

## Claude Desktop Configuration

### 1. Locate Your Claude Desktop Config
The configuration file location varies by OS:

- **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
- **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
- **Linux**: `~/.config/Claude/claude_desktop_config.json`

### 2. Add MCP Server Configuration
Add this configuration to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "jenksy-courses": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/johnjenkins/java-projects/jenksy.me/jenksy-mcp/build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Important**: Update the JAR path to match your actual project location.

### 3. Restart Claude Desktop
Close and restart Claude Desktop for the configuration to take effect.

## Available Tools

Once configured, Claude will have access to these tools:

### `get_courses`
Returns all available programming courses.

### `add_course`
Adds a new course to the collection.
- **Parameters**: Course object with `title` and `url`

### `get_course_by_title`
Finds a course by its title (case-insensitive search).
- **Parameters**: `title` (String)

## Pre-loaded Courses

The server initializes with courses covering:

**Backend Technologies:**
- Java, Spring Boot
- Python, Django, Flask
- C#, .NET Core
- Ruby on Rails
- PHP, Laravel
- Go

**Frontend Technologies:**
- JavaScript, TypeScript
- Angular, React
- Node.js

**Mobile Development:**
- Kotlin, Swift
- iOS Development
- Android Development

## Development

### Running in Development Mode
```bash
./gradlew bootRun
```

### Running Tests
```bash
./gradlew test
```

### Rebuilding After Changes
```bash
./gradlew clean build
```

**Note**: After rebuilding, restart Claude Desktop to use the updated JAR.

## Troubleshooting

### Claude Desktop Not Recognizing Server
1. Verify the JAR path in your configuration is correct and absolute
2. Ensure the JAR file exists and is executable
3. Check Claude Desktop logs for error messages
4. Restart Claude Desktop after configuration changes

### Build Issues
- Ensure Java 21+ is installed: `java -version`
- Clean build directory: `./gradlew clean`
- Check for network issues if dependencies fail to download

### Server Not Starting
- Verify Java can execute the JAR: `java -jar build/libs/jenksy-mcp-0.0.1-SNAPSHOT.jar`
- Check for port conflicts if running multiple instances
- Review application logs for startup errors

## Project Structure

```
src/
├── main/java/com/jenksy/jenksymcp/
│   ├── JenksyMcpApplication.java      # Main Spring Boot application
│   ├── service/CourseService.java     # MCP tool implementations
│   └── record/Course.java             # Course data model
└── test/
    └── java/com/jenksy/jenksymcp/
        └── JenksyMcpApplicationTests.java
```

## Dependencies

- **Spring Boot 3.5.5** - Application framework
- **Spring AI 1.0.2** - MCP server support
- **Lombok** - Code generation
- **JUnit 5** - Testing framework

## License

This project is part of the Jenksy educational platform.