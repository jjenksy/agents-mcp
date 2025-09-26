#!/bin/bash

# Jenksy MCP Agents Installation Script
# This script downloads and installs the Jenksy MCP Agents server

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
GITHUB_REPO="jenksy/jenksy-mcp"
INSTALL_DIR="$HOME/.jenksy-mcp"
JAR_NAME="jenksy-mcp.jar"
JAR_PATH="$INSTALL_DIR/$JAR_NAME"

echo "🚀 Jenksy MCP Agents Installer"
echo "==============================="
echo ""

# Check for Java
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    echo "Please install Java 21 or later from: https://adoptium.net/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo -e "${YELLOW}⚠️  Java 21 or later is recommended (found Java $JAVA_VERSION)${NC}"
fi
echo -e "${GREEN}✅ Java is installed${NC}"

# Check for VS Code (optional)
if command -v code &> /dev/null; then
    echo -e "${GREEN}✅ VS Code CLI detected${NC}"
    VSCODE_AVAILABLE=true
else
    echo -e "${YELLOW}ℹ️  VS Code CLI not found (optional)${NC}"
    VSCODE_AVAILABLE=false
fi

# Create installation directory
echo ""
echo "Creating installation directory..."
mkdir -p "$INSTALL_DIR"

# Download latest release
echo "Fetching latest release information..."
LATEST_RELEASE=$(curl -s "https://api.github.com/repos/$GITHUB_REPO/releases/latest")

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Failed to fetch release information${NC}"
    exit 1
fi

DOWNLOAD_URL=$(echo "$LATEST_RELEASE" | grep -o '"browser_download_url": *"[^"]*jenksy-mcp-latest.jar"' | head -1 | cut -d'"' -f4)

if [ -z "$DOWNLOAD_URL" ]; then
    # Fallback to versioned JAR
    DOWNLOAD_URL=$(echo "$LATEST_RELEASE" | grep -o '"browser_download_url": *"[^"]*\.jar"' | head -1 | cut -d'"' -f4)
fi

if [ -z "$DOWNLOAD_URL" ]; then
    echo -e "${RED}❌ Could not find JAR download URL${NC}"
    echo "Please visit: https://github.com/$GITHUB_REPO/releases"
    exit 1
fi

echo "Downloading JAR from: $DOWNLOAD_URL"
curl -L "$DOWNLOAD_URL" -o "$JAR_PATH" --progress-bar

if [ ! -f "$JAR_PATH" ]; then
    echo -e "${RED}❌ Download failed${NC}"
    exit 1
fi

# Make JAR executable
chmod +x "$JAR_PATH"

echo -e "${GREEN}✅ JAR downloaded successfully${NC}"

# Configure VS Code if available
if [ "$VSCODE_AVAILABLE" = true ]; then
    echo ""
    echo "Would you like to configure VS Code automatically? (y/n)"
    read -r CONFIGURE_VSCODE

    if [ "$CONFIGURE_VSCODE" = "y" ] || [ "$CONFIGURE_VSCODE" = "Y" ]; then
        echo "Configuring VS Code..."
        code --add-mcp "{\"name\":\"jenksy-agents\",\"command\":\"java\",\"args\":[\"-jar\",\"$JAR_PATH\"]}"
        echo -e "${GREEN}✅ VS Code configured${NC}"
    fi
fi

echo ""
echo "=========================================="
echo -e "${GREEN}🎉 Installation complete!${NC}"
echo ""
echo "JAR installed to: $JAR_PATH"
echo ""

if [ "$VSCODE_AVAILABLE" = true ] && [ "$CONFIGURE_VSCODE" != "y" ]; then
    echo "To configure VS Code manually, run:"
    echo "  code --add-mcp '{\"name\":\"jenksy-agents\",\"command\":\"java\",\"args\":[\"-jar\",\"$JAR_PATH\"]}'"
    echo ""
fi

echo "To test the installation, run:"
echo "  java -jar $JAR_PATH"
echo ""
echo "For more information, visit:"
echo "  https://github.com/$GITHUB_REPO"