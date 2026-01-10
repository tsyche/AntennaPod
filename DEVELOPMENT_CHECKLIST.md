# AntennaPod Development Checklist

## Quick Start (Minimal Commands)

```bash
# Set Java version
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS)

# Start emulator
emulator -avd <avd_name>

# Build and install debug APK
./gradlew :app:assembleFreeDebug --no-daemon
adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk

# Launch app
adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity

# Attach debugger
adb forward tcp:5005 jdwp:$(adb shell pidof de.danoeh.antennapod.debug)
jdb -attach localhost:5005

# Run all tests and checks (pre-merge validation - matches GitHub Actions CI)
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew clean build checkstyle :app:lintPlayDebug spotbugsPlayDebug spotbugsDebug --no-daemon

# Run specific tests (faster for development)
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew test --tests "*FeedTest*" --no-daemon
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew test --tests "*LocalFeedUpdaterTest*" --no-daemon
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew :model:test --no-daemon

# Single start dev cycle command
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew :app:assembleFreeDebug --no-daemon && adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk && sleep 2 && adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity && sleep 6 && adb forward tcp:5005 jdwp:$(adb shell pidof de.danoeh.antennapod.debug) && sleep 2 && jdb -attach localhost:5005
```

---

## Environment Setup (one-time)

### Java Version
```bash
java -version  # Should be Java 11+
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS)
```

### Android Emulator
```bash
# List available emulators
emulator -list-avds

# Start emulator
emulator -avd <avd_name>

# Check if emulator is connected
adb devices
```

## Quick Development Workflow

### Build and Install Debug APK
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-22.0.1+8) && ./gradlew :app:assembleFreeDebug --no-daemon && adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk
```

### Launch App
```bash
# Launch with debugger (for debugging)
adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity

# Launch without debugger (for testing)
adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity --ez debug false
```

### Connect Debugger (Command Line)

#### Option 1: Command Line Debugger (jdb)
```bash
# Forward debugger port
adb forward tcp:5005 jdwp:$(adb shell pidof de.danoeh.antennapod.debug)

# Attach debugger
jdb -attach localhost:5005
```

#### Option 2: Android Studio Debug Run (if using Android Studio)
1. Open project in Android Studio
2. Click the **Debug** button (green bug icon) instead of Run
3. Android Studio will build, install, launch, and attach debugger automatically

#### Option 3: Launch Without Debugger (for testing only)
```bash
adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity --ez debug false
```

## Testing

# Unit Tests (matches GitHub Actions CI)
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew testDebugUnitTest --no-daemon
```

# Instrumented Tests (matches GitHub Actions CI)
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew connectedDebugAndroidTest --no-daemon
```

# Lint Check (matches GitHub Actions CI)
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew lintDebug --no-daemon
```

## Pre-merge Checklist

### 1. Clean Build
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew clean build --no-daemon
```

### 2. Run All Tests
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-22.0.1+8) && ./gradlew test --no-daemon
```

### 3. Check Lint Issues
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-22.0.1+8) && ./gradlew lintDebug --no-daemon
```

### 4. Manual Testing
- Build and install debug APK
- Test new functionality
- Test related existing functionality
- Test on different API levels if possible

## Release Builds (for final testing)

### Debug Build (recommended for development)
```bash
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-22.0.1+8) && ./gradlew :app:assembleFreeDebug --no-daemon && adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk
```

### Release Build (requires signing)
```bash
# Build unsigned release APK
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-22.0.1+8) && ./gradlew :app:assembleFreeRelease --no-daemon

# Install unsigned APK (may not work on all devices)
adb install -r app/build/outputs/apk/free/release/app-free-release-unsigned.apk
```

## Troubleshooting

### App Stuck on "Waiting for Debugger"

#### Option 1: Command Line Debugger (jdb) - Recommended
```bash
# Forward debugger port
adb forward tcp:5005 jdwp:$(adb shell pidof de.danoeh.antennapod.debug)

# Attach debugger
jdb -attach localhost:5005
```

#### Option 2: Android Studio Debug Run (if using Android Studio)
1. Open project in Android Studio
2. Click the **Debug** button (green bug icon)
3. Android Studio handles everything automatically

#### Option 3: Launch Without Debugger
```bash
adb shell am force-stop de.danoeh.antennapod.debug
adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity --ez debug false
```

#### Option 4: Use Monkey Launcher
```bash
adb shell monkey -p de.danoeh.antennapod.debug -c android.intent.category.LAUNCHER 1
```

### ADB Issues
```bash
# Restart ADB
adb kill-server && adb start-server

# Check connected devices
adb devices
```

### Build Issues
```bash
# Clean and rebuild
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew clean build --no-daemon

# Clear Gradle cache (if needed)
./gradlew clean --no-daemon
rm -rf .gradle
```

### Clear App Data
```bash
adb shell pm clear de.danoeh.antennapod.debug
```

## Important Notes

- **Always set JAVA_HOME** before running Gradle commands to avoid Java version conflicts
- **Use debug builds** for development and testing
- **Release builds require signing** and are mainly for distribution
- **Lint warnings are currently suppressed** in this branch (warningsAsErrors: false)
- **Test failures** may occur due to Mockito compatibility with Java 22 - this is a known issue

## Quick Commands Reference

```bash
# Build + Install + Launch (debug)
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew :app:assembleFreeDebug --no-daemon && adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk && adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity

# Build + Install + Launch + Debug (with jdb)
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew :app:assembleFreeDebug --no-daemon && adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk && adb shell am start -n de.danoeh.antennapod.debug/de.danoeh.antennapod.activity.MainActivity && adb forward tcp:5005 jdwp:$(adb shell pidof de.danoeh.antennapod.debug) && sleep 6 && adb forward tcp:5005 jdwp:$(adb shell pidof de.danoeh.antennapod.debug) && sleep 6 && jdb -attach localhost:5005

# Run tests
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew test --no-daemon

# Full clean build
export JAVA_HOME=$(/usr/local/bin/asdf where java adoptopenjdk-21.0.0+35.0.LTS) && ./gradlew clean build --no-daemon
