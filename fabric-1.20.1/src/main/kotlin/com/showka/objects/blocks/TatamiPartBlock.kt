package com.showka.objects.blocks

import com.showka.util.TatamiLayout
import net.minecraft.core.Direction
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty

/**
 * Tatami part block (2x4 = 8 parts) for 1.20.1.
 */
class TatamiPartBlock(
    properties: Properties,
    dropItemProvider: () -> Item,
    blockEntityTypeProvider: () -> BlockEntityType<TatamiBlockEntity>
) : AbstractTatamiPartBlock(
    properties = properties,
    layout = TatamiLayout.TATAMI,
    partProperty = PART,
    dropItemProvider = dropItemProvider,
    blockEntityTypeProvider = blockEntityTypeProvider
) {
    companion object {
        val PART: IntegerProperty = IntegerProperty.create("part", 0, 7)
    }

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, 0)
                .setValue(MIRRORED, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, PART, MIRRORED)
    }
}
