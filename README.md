# Powder Simulator

A JavaFX powder simulation built around a small cellular-automata engine. The project runs as a desktop app with Gradle and is also wired for JPro browser deployment.

## Features

- Sand, water, stone, metal, fire, oil, lava, wood, steam, acid, and TNT behaviors
- Mouse painting with adjustable brush size
- Keyboard shortcuts for quick material selection
- JavaFX rendering with an animation loop
- Optional JPro support for running in a browser

## Requirements

- JDK 21
- Gradle wrapper, included in the repository
- Internet access the first time Gradle resolves dependencies

## Run Locally

Use the Gradle wrapper from the project root:

```powershell
./gradlew.bat run
```

If you prefer the bundled helper scripts, these wrap the same Gradle task:

```powershell
./quick-run.bat
./run-with-modules.bat
```

## Controls

- Mouse drag: paint the selected material
- Number keys `1` to `0`: select materials
  - `1` Sand
  - `2` Water
  - `3` Stone
  - `4` Metal
  - `5` Fire
  - `6` Oil
  - `7` Lava
  - `8` Wood
  - `9` Acid
  - `0` TNT
- `-`: Steam
- Up / Down: change brush size
- Buttons: select a material, erase, or clear the simulation

## Browser Mode

The build includes the JPro Gradle plugin. To run in a browser, configure your JPro credentials first:

```powershell
$env:JPRO_USERNAME = "your-user"
$env:JPRO_PASSWORD = "your-password"
```

Then run:

```powershell
./gradlew.bat jproRun
```

For release packaging:

```powershell
./gradlew.bat jproRelease
```

## Project Layout

```text
src/main/java/
  module-info.java
  com/powdersimulator/
    PowderSimulator.java
    PowderEngine.java
    CellType.java
    *Behavior.java
build.gradle
settings.gradle
gradlew
gradlew.bat
quick-run.bat
run-with-modules.bat
```

## Notes

- The app is configured for JavaFX 21 in `build.gradle`.
- The module declaration opens the application package so JavaFX can launch it correctly.
- If VS Code shows JavaFX import warnings, reload the Java project after Gradle sync completes.