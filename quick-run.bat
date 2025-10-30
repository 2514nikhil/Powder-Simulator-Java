@echo off
echo =======================================
echo    JavaFX Powder Simulator - Quick Run
echo    Using JavaFX SDK (auto-detect)
echo =======================================
echo.

:: Ensure Gradle wrapper is available
if not exist "gradlew.bat" (
    echo ❌ gradlew.bat not found. Please clone the full project or download Gradle manually.
    pause
    exit /b 1
)

echo 🏗️  Building and running with Gradle wrapper...
call gradlew.bat --no-daemon run

if errorlevel 1 (
    echo ❌ Gradle failed to build or launch the simulator.
    echo 🔍 Check the earlier output for details (Java installation, internet access, etc.).
    pause
    exit /b 1
)

echo.
echo 👋 Powder Simulator closed.
pause