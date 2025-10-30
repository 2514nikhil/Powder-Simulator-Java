# Powder Simulator - JavaFX Version

This is a JavaFX port of a powder physics simulator, designed to be compatible with JPro for running in web browsers.

## 🚀 Quick Start

### Option 1: Gradle (recommended)
All dependencies (including JavaFX 21) are resolved automatically:

```powershell
./gradlew.bat run
```

### Option 2: Helper scripts (Windows/macOS/Linux)
Prefer the Gradle wrapper above, but a few convenience scripts are included if you need a single command:

```powershell
./quick-run.bat         # Windows quick launcher
./run-with-modules.bat  # Windows launcher passing extra args
./setup.sh              # Bash helper (macOS/Linux/WSL)
```

Each script simply wraps `gradlew run`, so JavaFX SDK downloads are handled automatically.

## 🛠️ VS Code Setup

To fix the JavaFX import errors in VS Code:

1. **Install JavaFX Extension Pack** in VS Code
2. **Configure VS Code settings** - Add to your `.vscode/settings.json`:
```json
{
  "java.project.referencedLibraries": [
    "lib/**/*.jar",
    "${env:JAVA_FX_HOME}/lib/**/*.jar",
    "C:/javafx-sdk-21/lib/**/*.jar",
    "C:/javafx-sdk-25/lib/**/*.jar",
    "${env:USERPROFILE}/javafx-sdk-21/lib/**/*.jar",
    "${env:USERPROFILE}/javafx-sdk-25/lib/**/*.jar"
  ],
    "java.compile.nullAnalysis.mode": "automatic"
}
```

3. **Or use the included VS Code settings** - The project includes `.vscode/settings.json` configured for common JavaFX paths.

> ℹ️ **Note:** JavaFX ships separately from the JDK. If VS Code still shows red squiggles after configuring libraries, reload the window or re-import the Gradle project.

## 🎮 Features

- **Sand**: Falls and forms piles, sinks through water
- **Water**: Flows and pools, floats oil
- **Stone**: Falls with velocity and momentum, can roll
- **Metal**: Solid material that can be created by fire heating sand  
- **Fire**: Burns oil, rises like flames, extinguished by water
- **Oil**: Floats on water, burns when touched by fire, spreads in thin layers

## 🎯 Controls

- **Mouse**: Click and drag to place selected material
- **Number Keys (1-6)**: Select different materials
  - 1: Sand
  - 2: Water  
  - 3: Stone
  - 4: Metal
  - 5: Fire
  - 6: Oil
- **Up/Down Arrow Keys**: Adjust brush size
- **Buttons**: Click to select materials or clear the simulation

## 📋 Requirements

- JDK 21 (Microsoft, Temurin, or Oracle build)
- Gradle wrapper (bundled with the repo; no global Gradle install required)
- Internet access the first time dependencies are downloaded

## 🌐 JPro / Browser Deployment

JPro support is now wired directly into the Gradle build via the `com.sandec.jpro` plugin.

### 1. Configure credentials
Create a `gradle.properties` file *outside version control* (the `.gitignore` already excludes it) and add:

```
jpro.username=YOUR_JPRO_USERNAME
jpro.password=YOUR_JPRO_PASSWORD
```

Alternatively, export environment variables before running tasks:

```powershell
$env:JPRO_USERNAME = "your-user"
$env:JPRO_PASSWORD = "your-secret"
```

On macOS/Linux:

```bash
export JPRO_USERNAME="your-user"
export JPRO_PASSWORD="your-secret"
```

### 2. Run locally in the browser

```powershell
./gradlew.bat jproRun
```

Open the URL shown in the console (default `http://localhost:8080/`).

### 3. Create a release bundle

```powershell
./gradlew.bat jproRelease
```

The ready-to-serve package lives under `build/jpro/`.

### 4. Deploy to JPro Cloud

Set your cloud app ID inside `build.gradle` if required (`jpro { cloud { appId = "..." } }`) and run:

```powershell
./gradlew.bat jproDeploy --info
```

Refer to the official [JPro Gradle guide](https://www.jpro.one/doc/latest/guide_gradle/) for advanced configuration (custom ports, SSL, Docker packaging, etc.).

## 📁 Project Structure

```
powder-simulator/
├── src/main/java/com/powdersimulator/
│   └── PowderSimulator.java    # Main application entry point
├── src/main/java/module-info.java
├── build.gradle                # Gradle build configuration
├── settings.gradle             # Gradle settings (foojay resolver)
├── gradlew / gradlew.bat       # Gradle wrapper scripts
├── quick-run.bat               # Convenience launcher (wraps Gradle)
├── run-with-modules.bat        # Convenience launcher (wraps Gradle)
├── setup.sh                    # Bash helper (wraps Gradle)
└── README.md                   # This file
```

## 🔧 Technical Details

**Migration from Swing to JavaFX:**
- Canvas-based rendering for smooth 60 FPS performance
- JavaFX AnimationTimer for precise timing
- Modern UI with CSS styling support
- Full JPro browser compatibility

**Physics Engine:**
- Cellular automata simulation  
- Particle-based interactions
- Realistic material behaviors
- Velocity and momentum tracking

## 📝 Development Notes

The code has been fully migrated from Java Swing to JavaFX. All import errors you see in VS Code are due to missing JavaFX runtime dependencies, not code issues. The application will compile and run correctly once JavaFX is properly configured.

## 🤝 Contributing

1. Ensure a JDK 21 distribution is installed (or use `sdkman`/`jabba`).
2. Import the Gradle project into your IDE (VS Code, IntelliJ, etc.).
3. Test changes with `./gradlew run` or equivalent.
4. Submit pull requests with a brief summary of the changes.