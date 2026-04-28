# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build          # Build all modules
./gradlew clean build    # Clean build

# Run in-game (per module)
./gradlew :fabric-1.21.11:runClient
./gradlew :neoforge-1.21.11:runClient

# Asset generation (runs automatically on build)
./gradlew generateTatamiAssets

# 1.20.1 data format conversion (converts 1.21.x recipe/advancement formats)
./gradlew :fabric-1.20.1:convertDataJson
```

There are no automated tests in this project.

## Architecture

This is a multi-loader, multi-version Minecraft mod (Fabric + NeoForge, for 1.21.11 and 1.20.1). The mod adds tatami blocks in 11 color variants.

### Module Structure

Six Gradle subprojects:

```
common-1.21.11   ← shared Kotlin/Java sources, compiled as artifacts
fabric-1.21.11   ← consumes common, implements Fabric-specific registration
neoforge-1.21.11 ← consumes common, implements NeoForge-specific registration
common-1.20.1    ← same pattern, Java 17, uses LegacyForge MCP
fabric-1.20.1    ← also runs convertDataJson for format differences
neoforge-1.20.1
```

Shared textures/icons/recipes live in `common-resources/` (not a Gradle project, just files).

### Code Sharing via buildSrc Plugins

Two custom Groovy Gradle plugins in `buildSrc/src/main/groovy/`:

- **`multiloader-common.gradle`** — applied to `common-*` projects; publishes source sets as configurations (`commonJava`, `commonKotlin`, `commonResources`) so loader modules can consume them.
- **`multiloader-loader.gradle`** — applied to `fabric-*` and `neoforge-*` projects; injects common sources via `source()`, sets Gradle attribute `io.github.mcgradleconventions.loader` for dependency resolution.

This means the common module is compiled into the loader build directly (not as a dependency jar), so common code can reference loader APIs without circular dependency issues.

### Asset Generation (`gradle/tatami-assets.gradle`)

The `generateTatamiAssets` task programmatically generates 200+ JSON files at build time:
- Blockstates, block/item models, loot tables, language entries — one set per color variant
- Detects MC version to emit correct path formats (e.g., `loot_table` vs `loot_tables` for 1.20.1)

### 1.20.1 Differences

- Java 17 instead of 21
- Uses `net.neoforged.moddev.legacyforge` (Forge MCP) instead of NeoForge NMM
- `convertDataJson` task rewrites data files to 1.20.1 format:
  - Renames `recipe/` → `recipes/`, `loot_table/` → `loot_tables/`
  - Converts result `{id: "x"}` → `{item: "x"}` in recipes
  - Removes `sends_telemetry_event` from advancements

### Key Source Locations

- `common-1.21.11/src/main/kotlin/com/showka/` — block/item abstractions, `TatamiColor.kt` (11 variants), utilities
- `fabric-1.21.11/src/main/kotlin/com/showka/` — `ModInitializer` entrypoint, deferred block/item/entity registries
- `neoforge-1.21.11/src/main/kotlin/com/showka/` — `@Mod` class with event bus, `DeferredRegistries`-based registration

### Release

Releases are triggered by pushing a `v*` tag. GitHub Actions publishes all 4 variants to CurseForge (project `1483308`) and Modrinth (`8L1DTZf2`).
