# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Kotlin Android application using Jetpack Compose and MVVM. Sources live in `app/src/main/java/com/example/`: `domain/` contains financial calculations, `data/` contains models and persistence, `ui/` contains screens and components, and `util/localization/` contains six language implementations. Android assets are in `app/src/main/res/`.

JVM and Robolectric tests are in `app/src/test/`; device tests are in `app/src/androidTest/`. Store descriptions, screenshots, and changelogs live in `fastlane/metadata/android/`. Dependency versions are centralized in `gradle/libs.versions.toml`. F-Droid preparation is documented in `FDROID_SETUP.md`.

## Build, Test, and Development Commands

Use JDK 17, Android SDK platform 36.1, and the checked-in Gradle Wrapper. Configure your SDK through untracked `local.properties`.

- `bash ./gradlew assembleDebug`: build the debug APK.
- `bash ./gradlew installDebug`: install on a connected device or emulator.
- `bash ./gradlew testDebugUnitTest`: run JVM and Robolectric tests.
- `bash ./gradlew testDebugUnitTest --tests com.example.LocalizationFormattersTest`: run one suite.
- `bash ./gradlew lintRelease assembleRelease`: check and build the release variant; signing requires environment variables.
- `bash ./gradlew connectedDebugAndroidTest`: run device instrumentation tests.

## Coding Style & Naming Conventions

Follow official Kotlin style: four-space indentation, `UpperCamelCase` classes and composables, `lowerCamelCase` functions and properties, and `UPPER_SNAKE_CASE` constants. Gradle scripts currently use two-space indentation; preserve surrounding formatting. Keep calculations in `domain/`, persistence in `data/`, and presentation in `ui/`. Extend `AppStrings` and all six translations together. Use Android Lint; no standalone Kotlin formatter is configured.

## Testing Guidelines

Tests use JUnit 4, Robolectric, Compose testing, and coroutine test utilities. Name suites `*Test.kt` with descriptive test methods. Cover changed financial formulas, boundary values, localization, and persistence behavior. No numeric coverage threshold is configured. Robolectric defaults to API 35; formatter tests explicitly use API 28. Record physical-device verification for release fixes.

## Commit & Pull Request Guidelines

Follow existing messages such as `fix: handle yen amounts` and `refactor(ui): simplify metric cards`. Keep commits focused. PRs should explain the problem, resulting behavior, related issues, and validation; include screenshots for visual changes.

## Configuration & Agent Instructions

Communicate with the user in Russian. Keep keystores, passwords, tokens, and SDK paths out of Git. Release signing uses `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, and `KEY_ALIAS`. Preserve the signing key and published tags. Pushing `v*` tags triggers GitHub release publication. Follow `FDROID_SETUP.md` for reproducibility checks and retain FLOSS dependencies and offline operation.
