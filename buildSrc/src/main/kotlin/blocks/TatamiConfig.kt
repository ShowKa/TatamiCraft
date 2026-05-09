package blocks

private val tatamiColorDefs = listOf(
    ColorDef("red",          "Crimson Red",    "紅色"),
    ColorDef("orange",       "Apricot Orange", "杏色"),
    ColorDef("peach",        "Peach",          "桃色"),
    ColorDef("yellow",       "Mustard Yellow", "芥子色"),
    ColorDef("green",        "Light Green",    "若竹色"),
    ColorDef("sky_blue",     "Sky Blue",       "空色"),
    ColorDef("purple",       "Mauve Purple",   "藤色"),
    ColorDef("grayish_pink", "Grayish Pink",   "灰桜色"),
    ColorDef("milk_white",   "Milk White",     "乳白色"),
    ColorDef("light_brown",  "Light Brown",    "白茶色"),
    ColorDef("walnut",       "Walnut",         "胡桃色"),
)

object DefaultTatamiConfig : TatamiSetConfig() {
    override val prefix    = ""
    override val texSuffix = ""
    override val enPrefix  = ""
    override val jaPrefix  = ""

    val colorVariants: List<TatamiSetConfig> = tatamiColorDefs.map { c ->
        object : TatamiSetConfig() {
            override val prefix    = "${c.id}_"
            override val texSuffix = "_${c.id}"
            override val enPrefix  = "${c.en} "
            override val jaPrefix  = c.ja
        }
    }
}
