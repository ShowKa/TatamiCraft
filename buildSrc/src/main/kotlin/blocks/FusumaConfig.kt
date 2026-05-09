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

    val colorVariants: List<SlidingPanelConfig> = fusumaColorDefs.map { c ->
        object : SlidingPanelConfig() {
            override val blockPartId = "${c.id}_fusuma_part"
            override val itemId      = "${c.id}_fusuma"
            override val enName      = "${c.en} Fusuma"
            override val jaName      = "${c.ja}襖"
            override val texBase     = "fusuma"
            override val texSuffix   = "_${c.id}"
        }
    }
}
