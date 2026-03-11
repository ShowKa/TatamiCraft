package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiBlockEntity
import com.showka.objects.blocks.TatamiPartBlock
import com.showka.util.TatamiLayout
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.entity.BlockEntityType

/**
 * Tatami item (places 2x4 = 8 parts).
 */
class TatamiItem(
    properties: Properties,
    partBlockProvider: () -> AbstractTatamiPartBlock,
) : AbstractTatamiItem(
    properties = properties,
    layout = TatamiLayout.TATAMI,
    partBlockProvider = partBlockProvider,
    partProperty = TatamiPartBlock.PART
)
