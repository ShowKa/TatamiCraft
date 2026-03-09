package com.showka.objects.blocks

import com.showka.objects.items.ModItems
import com.showka.util.TatamiLayout
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.Direction

/**
 * 畳パーツブロック（2×4 = 8枚）。
 *
 * @param dropItemOverride カラーバリエーション用のドロップアイテム上書き。null ならデフォルト。
 */
class TatamiPartBlock(
    settings: Settings,
    dropItemOverride: (() -> net.minecraft.item.Item)? = null
) : AbstractTatamiPartBlock(
    settings = settings,
    layout = TatamiLayout.TATAMI,
    partProperty = PART,
    dropItemProvider = dropItemOverride ?: { ModItems.TATAMI_ITEM },
    blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
) {
    companion object {
        val PART: IntProperty = IntProperty.of("part", 0, 7)
    }

    init {
        defaultState = stateManager.defaultState
            .with(FACING, Direction.NORTH)
            .with(PART, 0)
            .with(MIRRORED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, PART, MIRRORED)
    }
}
