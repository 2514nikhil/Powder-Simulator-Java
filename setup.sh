#!/bin/bash

echo "=== JavaFX Powder Simulator Setup ==="
echo ""

# Ensure Java is available
if ! command -v java > /dev/null; then
    echo "❌ Java is not installed. Please install a JDK 21 (or compatible) first."
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Ensure Gradle wrapper exists
if [ ! -f "gradlew" ]; then
    echo "❌ gradlew not found. Please clone the repository completely or install Gradle manually."
    exit 1
fi

echo "🏗️  Building and running with Gradle wrapper..."
./gradlew --no-daemon run

STATUS=$?
if [ $STATUS -ne 0 ]; then
    echo "❌ Gradle failed to build or launch the simulator."
    echo "🛠️  Troubleshooting:"
    echo "   1. Ensure JAVA_HOME points to a JDK 21 installation."
    echo "   2. Check network access for the first dependency download."
    echo "   3. Re-run with --stacktrace for more details."
    exit $STATUS
fi

echo ""
echo "👋 Powder Simulator closed."
exit 0