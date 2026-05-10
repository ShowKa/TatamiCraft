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

private class TatamiBlockConfigImpl(
    override val prefix: String,
    override val texSuffix: String,
    override val blockType: String,
    override val itemType: String,
    override val partCount: Int,
    override val enName: String,
    override val jaName: String,
) : TatamiBlockConfig()

private fun tatamiPair(
    prefix: String,
    texSuffix: String,
    enPrefix: String,
    jaPrefix: String,
): List<TatamiBlockConfig> = listOf(
    TatamiBlockConfigImpl(prefix, texSuffix, "tatami_part",      "tatami",      8, "${enPrefix}Tatami",      "${jaPrefix}畳"),
    TatamiBlockConfigImpl(prefix, texSuffix, "tatami_half_part", "tatami_half", 4, "${enPrefix}Half Tatami", "${jaPrefix}半畳"),
)

internal val defaultTatamiSeries: List<TatamiBlockConfig> = tatamiPair("", "", "", "")
internal val coloredTatamiSeries: List<TatamiBlockConfig> =
    tatamiColorDefs.flatMap { c -> tatamiPair("${c.id}_", "_${c.id}", "${c.en} ", c.ja) }
