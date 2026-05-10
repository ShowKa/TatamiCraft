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

### Asset Generation

The `generateTatamiAssets` task generates 600+ JSON files at build time
(blockstates, block/item models, item definitions, loot tables, lang files).
It runs automatically as part of `processResources`.

All generation logic lives in `buildSrc/src/main/kotlin/blocks/`.
`gradle/tatami-assets.gradle` is a thin runner (~65 lines) that only handles
file I/O (`writeJson`) and the Gradle task wiring.

MC version detection (`isLegacy = mcMajor < 1.21`) switches the loot table
directory between `loot_table/` (1.21+) and `loot_tables/` (1.20.1).

#### Adding a completely new block type

"New block type" means a geometry or behavior distinct from flat tatami or sliding panels
(e.g. a door that opens upward, a multi-block structure, etc.).

**Step 1 — Create a new abstract config class** in `buildSrc/src/main/kotlin/blocks/`
implementing `BlockAssetConfig`. Define `assetEntries()` to return every JSON file
the block needs.

```kotlin
// buildSrc/src/main/kotlin/blocks/MyNewBlockConfig.kt
abstract class MyNewBlockConfig : BlockAssetConfig {
    abstract override val blockPartId: String
    abstract override val itemId: String
    abstract override val enName: String
    abstract override val jaName: String
    // ...type-specific properties...

    override fun assetEntries(modId: String, isLegacy: Boolean): List<AssetEntry> {
        val lootDir = if (isLegacy) "loot_tables" else "loot_table"
        return buildList {
            add(AssetEntry("assets/$modId/blockstates/$blockPartId.json", blockstateData(modId)))
            // ...add model, item model, item definition, loot table entries...
            add(AssetEntry("data/$modId/$lootDir/blocks/$blockPartId.json",
                           linkedMapOf("type" to "minecraft:block", "pools" to emptyList<Any>())))
        }
    }
    private fun blockstateData(modId: String): Map<String, Any> { /* ... */ }
}
```

**Step 2 — Create a concrete config object** for each variant:

```kotlin
object MyNewBlock : MyNewBlockConfig() {
    override val blockPartId = "my_block_part"
    override val itemId      = "my_block"
    override val enName      = "My Block"
    override val jaName      = "マイブロック"
}
```

**Step 3 — Register in `BlockRegistry`** — add to `allConfigs` in `BlockRegistry.kt`.

**Step 4 — Add game-side code** in `common-*/src/main/kotlin/com/showka/`:
register the block and item (refer to existing blocks for the pattern).

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
