package com.showka.objects

/**
 * 畳のカラーバリエーション。
 *
 * @param id           レジストリパス接頭辞（例: "red"）。デフォルトは空文字。
 * @param englishName  英語表示名（例: "Red"）
 * @param japaneseName 日本語表示名（例: "赤色"）
 */
enum class TatamiColor(
    val id: String,
    val englishName: String,
    val japaneseName: String
) {
    DEFAULT("", "", ""),
    RED("red", "Crimson Red", "紅色"),
    ORANGE("orange", "Apricot Orange", "杏色"),
    YELLOW("yellow", "Mustard Yellow", "芥子色"),
    SKY_BLUE("sky_blue", "Sky Blue", "水色"),
    GREEN("green", "Light Green", "若竹色"),
    BLUE("blue", "Blue", "青色"),
    PEACH("peach", "Peach", "桃色"),
    PURPLE("purple", "Mauve Purple", "藤色"),
    BROWN("brown", "Brown", "茶色"),
    WHITE("white", "White", "白色"),
    GRAY("gray", "Gray", "灰色"),
    BLACK("black", "Black", "黒色"),
    WALNUT("walnut", "Walnut", "胡桃色"),
    LIGHT_BROWN("light_brown", "Light Brown", "白茶色"),
    GRAYISH_PINK("grayish_pink", "Grayish Pink", "灰桜色"),
    MILK_WHITE("milk_white", "Milk White", "乳白色"),
    SILVER_WHITE("silver_white", "Silver White", "銀白色");

    /** ブロック/アイテムパスの接頭辞。デフォルトは空文字。 */
    fun prefix(): String = if (id.isEmpty()) "" else "${id}_"

    /** テクスチャファイル名のサフィックス。デフォルトは空文字。 */
    fun suffix(): String = if (id.isEmpty()) "" else "_${id}"

    companion object {
        /** デフォルト以外のすべてのカラー */
        val COLORED = entries.filter { it != DEFAULT }
    }
}
