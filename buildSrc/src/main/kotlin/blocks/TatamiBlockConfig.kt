package blocks

abstract class TatamiBlockConfig : BlockAssetConfig {
    abstract val prefix: String
    abstract val texSuffix: String
    abstract val blockType: String
    abstract val itemType: String
    abstract val partCount: Int
    abstract override val enName: String
    abstract override val jaName: String

    override val blockPartId: String get() = "$prefix$blockType"
    override val itemId:      String get() = "$prefix$itemType"

    override fun assetEntries(modId: String, isLegacy: Boolean): List<AssetEntry> {
        val lootDir = if (isLegacy) "loot_tables" else "loot_table"
        return buildList {
            add(AssetEntry("assets/$modId/blockstates/$blockPartId.json",         blockstateData(modId)))
            add(AssetEntry("assets/$modId/models/block/${blockPartId}_left.json",  blockModelData(modId, 270)))
            add(AssetEntry("assets/$modId/models/block/${blockPartId}_right.json", blockModelData(modId, 90)))
            add(AssetEntry("assets/$modId/models/item/$itemId.json",              itemModelData(modId)))
            if (!isLegacy) add(AssetEntry("assets/$modId/items/$itemId.json",    itemDefinitionData(modId)))
            add(AssetEntry("data/$modId/$lootDir/blocks/$blockPartId.json",       lootData()))
        }
    }

    private fun blockstateData(modId: String): Map<String, Any> {
        data class Facing(val name: String, val y: Int?)
        val facings = listOf(Facing("north", null), Facing("east", 90), Facing("south", 180), Facing("west", 270))
        val variants = LinkedHashMap<String, Any>()
        for (facing in facings) {
            for (mirrored in listOf(false, true)) {
                for (part in 0 until partCount) {
                    val side = if (part % 2 == 0) {
                        if (mirrored) "right" else "left"
                    } else {
                        if (mirrored) "left" else "right"
                    }
                    val key = "facing=${facing.name},mirrored=$mirrored,part=$part"
                    val value = LinkedHashMap<String, Any>()
                    value["model"] = "$modId:block/${blockPartId}_$side"
                    facing.y?.let { value["y"] = it }
                    variants[key] = value
                }
            }
        }
        return mapOf("variants" to variants)
    }

    private fun blockModelData(modId: String, rotation: Int): Map<String, Any> {
        val texture = "$modId:block/tatami_block$texSuffix"
        return linkedMapOf(
            "textures" to linkedMapOf(
                "particle" to texture,
                "top"      to texture,
                "bottom"   to texture,
                "side"     to texture,
            ),
            "elements" to listOf(linkedMapOf(
                "from"  to listOf(0, 0, 0),
                "to"    to listOf(16, 1, 16),
                "faces" to linkedMapOf(
                    "down"  to mapOf("texture" to "#bottom", "cullface" to "down"),
                    "up"    to linkedMapOf("texture" to "#top", "rotation" to rotation),
                    "north" to mapOf("texture" to "#side", "cullface" to "north"),
                    "south" to mapOf("texture" to "#side", "cullface" to "south"),
                    "west"  to mapOf("texture" to "#side", "cullface" to "west"),
                    "east"  to mapOf("texture" to "#side", "cullface" to "east"),
                ),
            )),
        )
    }

    private fun itemModelData(modId: String): Map<String, Any> {
        val textureName = if (itemType == "tatami") "tatami_item" else "tatami_half_item"
        return linkedMapOf(
            "parent"   to "minecraft:item/generated",
            "textures" to mapOf("layer0" to "$modId:item/$textureName$texSuffix"),
        )
    }

    private fun itemDefinitionData(modId: String): Map<String, Any> =
        mapOf("model" to mapOf(
            "type"  to "minecraft:model",
            "model" to "$modId:item/$itemId",
        ))

    private fun lootData(): Map<String, Any> = linkedMapOf(
        "type"  to "minecraft:block",
        "pools" to emptyList<Any>(),
    )
}
