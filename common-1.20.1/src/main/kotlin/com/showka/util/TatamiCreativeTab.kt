package com.showka.util

import com.showka.objects.ModColor
import net.minecraft.world.item.Item

/**
 * Provides the ordered list of tatami items for creative tab registration.
 * Used by both Fabric and NeoForge initializers.
 */
fun orderedTatamiItems(
    defaultTatami: Item,
    defaultTatamiHalf: Item,
    getColoredTatami: (ModColor) -> Item,
    getColoredTatamiHalf: (ModColor) -> Item
): List<Item> = buildList {
    add(defaultTatami)
    add(defaultTatamiHalf)
    for (color in ModColor.COLORED) {
        add(getColoredTatami(color))
        add(getColoredTatamiHalf(color))
    }
}

fun orderedFusumaItems(
    defaultFusuma: Item,
    getColoredFusuma: (ModColor) -> Item
): List<Item> = buildList {
    add(defaultFusuma)
    for (color in ModColor.FUSUMA_COLORED) {
        add(getColoredFusuma(color))
    }
}
