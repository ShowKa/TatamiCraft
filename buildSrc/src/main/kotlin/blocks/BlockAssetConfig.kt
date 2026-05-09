package blocks

internal data class ColorDef(val id: String, val en: String, val ja: String)

sealed interface BlockAssetConfig

abstract class SlidingPanelConfig : BlockAssetConfig {
    abstract val blockPartId: String
    abstract val itemId: String
    abstract val enName: String
    abstract val jaName: String
    abstract val texBase: String
    open val texSuffix: String = ""
    open val renderType: String? = null
    open val outerEdgeOnly: Boolean = false
}

abstract class TatamiBlockConfig : BlockAssetConfig {
    abstract val prefix: String
    abstract val texSuffix: String
    abstract val blockType: String
    abstract val itemType: String
    abstract val partCount: Int
    abstract val enName: String
    abstract val jaName: String
    val blockPartId: String get() = "$prefix$blockType"
    val itemId: String get() = "$prefix$itemType"
}
