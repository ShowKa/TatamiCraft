package com.showka.util

import com.showka.objects.TatamiColor
import net.minecraft.world.item.Item

/**
 * Provides the ordered list of tatami items for creative tab registration.
 * Used by both Fabric and NeoForge initializers.
 */
fun orderedTatamiItems(
    defaultTatami: Item,
    defaultTatamiHalf: Item,
    getColoredTatami: (TatamiColor) -> Item,
    getColoredTatamiHalf: (TatamiColor) -> Item
): List<Item> = buildList {
    add(defaultTatami)
    add(defaultTatamiHalf)
    for (color in TatamiColor.COLORED) {
        add(getColoredTatami(color))
        add(getColoredTatamiHalf(color))
    }
}
