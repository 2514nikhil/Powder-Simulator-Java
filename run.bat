@echo off
echo === JavaFX Powder Simulator Setup ===
echo.

:: Ensure Gradle wrapper is available
if not exist "gradlew.bat" (
    echo ❌ gradlew.bat not found. Please clone the full project or download Gradle manually.
    pause
    exit /b 1
)

echo ✅ Java is installed
echo 🏗️  Building application with Gradle wrapper...

call gradlew.bat --no-daemon run

if errorlevel 1 (
    echo ❌ Gradle failed to build or launch the simulator.
    echo 🛠️  Troubleshooting:
    echo    1. Verify Java 21 is installed and on PATH or set JAVA_HOME.
    echo    2. Ensure internet access for first-time dependency download.
    echo    3. Re-run with --stacktrace for detailed logs.
    pause
    exit /b 1
)

echo.
echo 👋 Application closed.
pause