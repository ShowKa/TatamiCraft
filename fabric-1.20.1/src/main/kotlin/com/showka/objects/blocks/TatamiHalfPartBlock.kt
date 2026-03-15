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
 * Half tatami part block (2x2 = 4 parts) for 1.20.1.
 */
class TatamiHalfPartBlock(
    properties: Properties,
    dropItemProvider: () -> Item,
    blockEntityTypeProvider: () -> BlockEntityType<TatamiBlockEntity>
) : AbstractTatamiPartBlock(
    properties = properties,
    layout = TatamiLayout.TATAMI_HALF,
    partProperty = PART,
    dropItemProvider = dropItemProvider,
    blockEntityTypeProvider = blockEntityTypeProvider
) {
    companion object {
        val PART: IntegerProperty = IntegerProperty.create("part", 0, 3)
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
