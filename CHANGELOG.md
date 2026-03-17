<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Basic Compose Activity Plugin Changelog

## [Unreleased]

## [1.0.1] - 2026-03-17

### Fixed

- Prevent plugin from triggering on non-Android projects (e.g., IntelliJ "Empty Project")

### Added

- GitHub Actions build workflow with plugin verification and draft release
- IntelliJ IDEA run configuration for testing
- CHANGELOG.md

## [1.0.0]

### Added

- Basic Compose Activity template in the New Project wizard under Phone and Tablet
- Multi-module project generation: `app`, `build-logic/convention`, `core:common`, `core:domain`, `core:ui`, `feature:sample`
- Jetpack Compose with Material 3 and dynamic color
- Navigation 3 for type-safe compose navigation
- Hilt dependency injection with KSP
- Convention plugins via `build-logic` for shared build configuration
- Detekt static code analysis with Compose rules
- Gradle Version Catalog (`libs.versions.toml`) for centralized dependency management
- Sample two-screen app with list and add screens sharing a single ViewModel
- Dependencies included: Coil, Lottie, LeakCanary, kotlinx-serialization, and more
- Plugin signing and JetBrains Marketplace publishing support
- GitHub Actions workflows for CI build and automated release

[Unreleased]: https://github.com/Drjacky/BasicComposeActivityPlugin/compare/v1.0.1...HEAD
[1.0.1]: https://github.com/Drjacky/BasicComposeActivityPlugin/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Drjacky/BasicComposeActivityPlugin/commits/v1.0.0
