package com.showka.objects

/**
 * Tatami color variations.
 *
 * @param id           Registry path prefix (e.g., "red"). Default is empty string.
 * @param englishName  English display name (e.g., "Red")
 * @param japaneseName Japanese display name (e.g., "紅色")
 */
enum class TatamiColor(
    val id: String,
    val englishName: String,
    val japaneseName: String
) {
    DEFAULT("", "", ""),
    RED("red", "Crimson Red", "紅色"),
    ORANGE("orange", "Apricot Orange", "杏色"),
    PEACH("peach", "Peach", "桃色"),
    YELLOW("yellow", "Mustard Yellow", "芥子色"),
    GREEN("green", "Light Green", "若竹色"),
    SKY_BLUE("sky_blue", "Sky Blue", "空色"),
    PURPLE("purple", "Mauve Purple", "藤色"),
    GRAYISH_PINK("grayish_pink", "Grayish Pink", "灰桜色"),
    MILK_WHITE("milk_white", "Milk White", "乳白色"),
    LIGHT_BROWN("light_brown", "Light Brown", "白茶色"),
    WALNUT("walnut", "Walnut", "胡桃色");

    /** Block/item path prefix. Empty string for default. */
    fun prefix(): String = if (id.isEmpty()) "" else "${id}_"

    /** Texture file name suffix. Empty string for default. */
    fun suffix(): String = if (id.isEmpty()) "" else "_${id}"

    // -- Registry ID helpers --

    fun tatamiPartId(): String = "${prefix()}tatami_part"
    fun tatamiHalfPartId(): String = "${prefix()}tatami_half_part"
    fun tatamiId(): String = "${prefix()}tatami"
    fun tatamiHalfId(): String = "${prefix()}tatami_half"

    fun fusumaPartId(): String = "${prefix()}fusuma_part"
    fun fusumaId(): String = "${prefix()}fusuma"

    companion object {
        /** All colors except DEFAULT */
        val COLORED = entries.filter { it != DEFAULT }

        /** Colors available for Fusuma variants */
        val FUSUMA_COLORED = listOf(GRAYISH_PINK, MILK_WHITE, LIGHT_BROWN, WALNUT)
    }
}
