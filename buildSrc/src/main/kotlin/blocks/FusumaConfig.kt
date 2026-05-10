package blocks

private val fusumaColorDefs = listOf(
    ColorDef("grayish_pink", "Grayish Pink", "灰桜色"),
    ColorDef("milk_white",   "Milk White",   "乳白色"),
    ColorDef("light_brown",  "Light Brown",  "白茶色"),
    ColorDef("walnut",       "Walnut",       "胡桃色"),
)

object FusumaConfig : SlidingPanelConfig() {
    override val blockPartId = "fusuma_part"
    override val itemId      = "fusuma"
    override val enName      = "Fusuma"
    override val jaName      = "襖"
    override val texBase     = "fusuma"
    override val itemTexture get() = "fusuma_item$texSuffix"

    // fusuma_part_invisible は全スライドパネルのブロックステートから参照される共有モデル。
    // fusumaSeries の先頭に位置する FusumaConfig が代表して生成する。
    override fun assetEntries(modId: String, isLegacy: Boolean): List<AssetEntry> =
        listOf(AssetEntry("assets/$modId/models/block/fusuma_part_invisible.json",
                          mapOf("elements" to emptyList<Any>()))) +
        super.assetEntries(modId, isLegacy)

    val colorVariants: List<SlidingPanelConfig> = fusumaColorDefs.map { c ->
        object : SlidingPanelConfig() {
            override val blockPartId = "${c.id}_fusuma_part"
            override val itemId      = "${c.id}_fusuma"
            override val enName      = "${c.en} Fusuma"
            override val jaName      = "${c.ja}襖"
            override val texBase     = "fusuma"
            override val texSuffix   = "_${c.id}"
            override val itemTexture get() = "fusuma_item$texSuffix"
        }
    }
}
