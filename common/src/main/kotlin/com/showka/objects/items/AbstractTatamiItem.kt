package com.showka.objects.items

import net.minecraft.world.item.Item

/**
 * Abstract base class for tatami items.
 * Minimal constructor — placement logic will be added back after registration is stable.
 */
open class AbstractTatamiItem(
    properties: Properties
) : Item(properties)
