package blocks

data class AssetEntry(val path: String, val data: Map<String, Any>)
data class LangEntry(val key: String, val en: String, val ja: String)

internal data class ColorDef(val id: String, val en: String, val ja: String)

sealed interface BlockAssetConfig {
    val blockPartId: String
    val itemId: String
    val enName: String
    val jaName: String
    fun assetEntries(modId: String, isLegacy: Boolean): List<AssetEntry>
    fun langEntries(modId: String): List<LangEntry> = listOf(
        LangEntry("block.$modId.$blockPartId", enName, jaName),
        LangEntry("item.$modId.$itemId",       enName, jaName),
    )
}
