package blocks

abstract class SlidingPanelConfig : BlockAssetConfig {
    abstract override val blockPartId: String
    abstract override val itemId: String
    abstract override val enName: String
    abstract override val jaName: String
    abstract val texBase: String
    open val texSuffix: String = ""
    open val renderType: String? = null
    open val outerEdgeOnly: Boolean = false
    /** アイテムモデルに使うテクスチャ名 (modId prefix なし)。
     *  融通系は "fusuma_item${texSuffix}" で上書き、引き戸系はデフォルトの "${itemId}_item" を使用。 */
    open val itemTexture: String get() = "${itemId}_item"

    override fun assetEntries(modId: String, isLegacy: Boolean): List<AssetEntry> {
        val lootDir = if (isLegacy) "loot_tables" else "loot_table"
        return buildList {
            add(AssetEntry("assets/$modId/blockstates/$blockPartId.json", blockstateData(modId)))
            addAll(slidingPanelModelEntries(modId))
            add(AssetEntry("assets/$modId/models/item/$itemId.json",       itemModelData(modId)))
            if (!isLegacy) add(AssetEntry("assets/$modId/items/$itemId.json", itemDefinitionData(modId)))
            add(AssetEntry("data/$modId/$lootDir/blocks/$blockPartId.json",
                           linkedMapOf("type" to "minecraft:block", "pools" to emptyList<Any>())))
        }
    }

    private fun blockstateData(modId: String): Map<String, Any> {
        data class Facing(val name: String, val y: Int?)
        val facings = listOf(Facing("north", null), Facing("east", 90), Facing("south", 180), Facing("west", 270))
        val variants = LinkedHashMap<String, Any>()
        for (side in listOf("left", "right")) {
            val s = if (side == "left") "sl" else "sr"
            for (facing in facings) {
                for (flipped in listOf(false, true)) {
                    val flipStr = if (flipped) "_flip" else ""
                    for (px in 0..1) {
                        for (py in 0..2) {
                            for (ds in listOf("closed", "left_open", "right_open")) {
                                val key = "door_state=$ds,facing=${facing.name},flipped_horizontal=$flipped,part_x=$px,part_y=$py,side=$side"
                                val modelPath = when (ds) {
                                    "closed"    -> "$modId:block/${blockPartId}_closed_${s}${flipStr}_x${px}_y${py}"
                                    "left_open" -> if (side == "left")  "$modId:block/fusuma_part_invisible"
                                                  else "$modId:block/${blockPartId}_overlap_sr${flipStr}_x${px}_y${py}"
                                    else        -> if (side == "right") "$modId:block/fusuma_part_invisible"
                                                  else "$modId:block/${blockPartId}_overlap_sl${flipStr}_x${px}_y${py}"
                                }
                                val value = LinkedHashMap<String, Any>()
                                value["model"] = modelPath
                                facing.y?.let { value["y"] = it }
                                variants[key] = value
                            }
                        }
                    }
                }
            }
        }
        return mapOf("variants" to variants)
    }

    private fun slidingPanelModelEntries(modId: String): List<AssetEntry> {
        val entries = mutableListOf<AssetEntry>()
        for (s in listOf("sl", "sr")) {
            for (flip in listOf(false, true)) {
                for (px in 0..1) {
                    for (py in 0..2) {
                        val z       = fusumaZRange(s, flip)
                        val flipStr = if (flip) "_flip" else ""
                        val tex     = tileTexture(modId, s, px, py)
                        val rot     = fusumaRotForTile(s, px, py)
                        val edgeEntry: Map<String, Any> =
                            if (outerEdgeOnly) emptyMap() else mapOf("edge" to "$modId:block/fusuma_edge")

                        // closed モデル
                        val closedTex = LinkedHashMap<String, Any>()
                        closedTex["particle"] = tex; closedTex["front"] = tex; closedTex["back"] = tex
                        closedTex.putAll(edgeEntry)
                        val closedData = LinkedHashMap<String, Any>()
                        closedData["textures"] = closedTex
                        closedData["elements"] = listOf(linkedMapOf(
                            "from"  to listOf(0, 0, z.first),
                            "to"    to listOf(16, 16, z.second),
                            "faces" to buildFaces("front", "back", rot, s, px, py),
                        ))
                        renderType?.let { closedData["render_type"] = it }
                        entries.add(AssetEntry(
                            "assets/$modId/models/block/${blockPartId}_closed_${s}${flipStr}_x${px}_y${py}.json",
                            closedData,
                        ))

                        // overlap モデル
                        val other    = if (s == "sl") "sr" else "sl"
                        val otherZ   = if (z.first == 10) Pair(13, 16) else Pair(10, 13)
                        val xOff     = if (s == "sl") 4 else -4
                        val otherTex = tileTexture(modId, other, px, py)
                        val otherRot = fusumaRotForTile(other, px, py)
                        val overlapTex = LinkedHashMap<String, Any>()
                        overlapTex["particle"] = tex
                        overlapTex["front_l"]  = tex; overlapTex["back_l"] = tex
                        overlapTex["front_r"]  = otherTex; overlapTex["back_r"] = otherTex
                        overlapTex.putAll(edgeEntry)
                        val overlapData = LinkedHashMap<String, Any>()
                        overlapData["textures"] = overlapTex
                        overlapData["elements"] = listOf(
                            linkedMapOf(
                                "from"  to listOf(0, 0, z.first),
                                "to"    to listOf(16, 16, z.second),
                                "faces" to buildFaces("front_l", "back_l", rot, s, px, py),
                            ),
                            linkedMapOf(
                                "from"  to listOf(xOff, 0, otherZ.first),
                                "to"    to listOf(xOff + 16, 16, otherZ.second),
                                "faces" to buildFaces("front_r", "back_r", otherRot, other, px, py),
                            ),
                        )
                        renderType?.let { overlapData["render_type"] = it }
                        entries.add(AssetEntry(
                            "assets/$modId/models/block/${blockPartId}_overlap_${s}${flipStr}_x${px}_y${py}.json",
                            overlapData,
                        ))
                    }
                }
            }
        }
        return entries
    }

    private fun fusumaZRange(s: String, flip: Boolean): Pair<Int, Int> {
        val outer = (s == "sr" && !flip) || (s == "sl" && flip)
        return if (outer) Pair(13, 16) else Pair(10, 13)
    }

    private fun tileTexture(modId: String, s: String, px: Int, py: Int): String {
        if (py != 1) return "$modId:block/${texBase}_tile_corner$texSuffix"
        val outer = (s == "sl" && px == 0) || (s == "sr" && px == 1)
        return if (outer) "$modId:block/${texBase}_tile_edge$texSuffix"
               else       "$modId:block/${texBase}_tile_edge_plain$texSuffix"
    }

    private fun fusumaRotForTile(s: String, px: Int, py: Int): Int = when {
        py == 2              -> if (px == 0) 0 else 90
        py == 0              -> if (px == 0) 270 else 180
        s == "sl" && px == 0 -> 0
        s == "sl" && px == 1 -> 180
        s == "sr" && px == 0 -> 0
        else                 -> 180
    }

    private fun fusumaFaces(frontKey: String, backKey: String, southRot: Int): LinkedHashMap<String, Any> {
        val northRot = (360 - southRot) % 360
        val southFace = linkedMapOf<String, Any>("texture" to "#$frontKey", "uv" to listOf(0, 0, 16, 16))
        if (southRot != 0) southFace["rotation"] = southRot
        val northFace = linkedMapOf<String, Any>("texture" to "#$backKey", "uv" to listOf(16, 0, 0, 16))
        if (northRot != 0) northFace["rotation"] = northRot
        return linkedMapOf(
            "north" to northFace,
            "south" to southFace,
            "east"  to mapOf("texture" to "#edge", "uv" to listOf(0, 0, 3, 16)),
            "west"  to mapOf("texture" to "#edge", "uv" to listOf(0, 0, 3, 16)),
            "up"    to mapOf("texture" to "#edge", "uv" to listOf(0, 0, 16, 3)),
            "down"  to mapOf("texture" to "#edge", "uv" to listOf(0, 0, 16, 3)),
        )
    }

    private fun buildFaces(
        fk: String, bk: String, frot: Int, fs: String, fpx: Int, fpy: Int,
    ): LinkedHashMap<String, Any> {
        val allF = fusumaFaces(fk, bk, frot)
        if (!outerEdgeOnly) return allF
        val f = linkedMapOf<String, Any>("north" to allF["north"]!!, "south" to allF["south"]!!)
        if (fs == "sl" && fpx == 0) f["west"] = mapOf("texture" to "#$fk", "uv" to listOf(0, 0, 2, 16))
        if (fs == "sl" && fpx == 1) f["east"] = mapOf("texture" to "#$fk", "uv" to listOf(0, 0, 2, 16))
        if (fs == "sr" && fpx == 1) f["east"] = mapOf("texture" to "#$fk", "uv" to listOf(0, 0, 2, 16))
        if (fs == "sr" && fpx == 0) f["west"] = mapOf("texture" to "#$fk", "uv" to listOf(0, 0, 2, 16))
        if (fpy == 2) f["up"]   = mapOf("texture" to "#$fk", "uv" to listOf(0, 0, 16, 2))
        if (fpy == 0) f["down"] = mapOf("texture" to "#$fk", "uv" to listOf(0, 0, 16, 2))
        return f
    }

    private fun itemModelData(modId: String): Map<String, Any> = linkedMapOf(
        "parent"   to "minecraft:item/generated",
        "textures" to mapOf("layer0" to "$modId:item/$itemTexture"),
    )

    private fun itemDefinitionData(modId: String): Map<String, Any> =
        mapOf("model" to mapOf(
            "type"  to "minecraft:model",
            "model" to "$modId:item/$itemId",
        ))
}
