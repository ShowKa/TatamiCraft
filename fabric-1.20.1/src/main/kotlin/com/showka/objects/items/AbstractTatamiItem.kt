package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiBlockEntity
import com.showka.util.TatamiLayout
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.core.BlockPos
import org.slf4j.LoggerFactory

/**
 * Abstract base class for tatami items (1.20.1 compatible).
 */
open class AbstractTatamiItem(
    properties: Properties,
    private val layout: TatamiLayout,
    private val partBlockProvider: () -> AbstractTatamiPartBlock,
    private val partProperty: IntegerProperty
) : Item(properties) {

    companion object {
        private val logger = LoggerFactory.getLogger("tatamicraft")
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player ?: return InteractionResult.FAIL
        val clickedPos = context.clickedPos
        val side = context.clickedFace

        val placePos = if (level.getBlockState(clickedPos).canBeReplaced()) {
            clickedPos
        } else {
            clickedPos.relative(side)
        }

        val facing = player.direction
        val origin = placePos

        val rightPositions = layout.getAllPartPositions(origin, facing, mirrored = false)
        val leftPositions = layout.getAllPartPositions(origin, facing, mirrored = true)

        val (positions, mirrored) = when {
            canPlaceAll(level, rightPositions) -> Pair(rightPositions, false)
            canPlaceAll(level, leftPositions)  -> Pair(leftPositions, true)
            else -> return InteractionResult.FAIL
        }

        if (!level.isClientSide) {
            val partBlock = partBlockProvider()
            for ((index, p) in positions.withIndex()) {
                val state = partBlock.defaultBlockState()
                    .setValue(AbstractTatamiPartBlock.FACING, facing)
                    .setValue(partProperty, index)
                    .setValue(AbstractTatamiPartBlock.MIRRORED, mirrored)
                level.setBlock(p, state, Block.UPDATE_ALL)

                val be = level.getBlockEntity(p) as? TatamiBlockEntity
                if (be != null) {
                    be.origin = origin
                    be.setChanged()
                } else {
                    logger.warn("TatamiBlockEntity not found at {}", p)
                }
            }
        }

        if (!player.isCreative) {
            context.itemInHand.shrink(1)
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    private fun canPlaceAll(level: Level, positions: List<BlockPos>): Boolean {
        for (p in positions) {
            if (!level.isInWorldBounds(p)) return false
            if (!level.getBlockState(p).canBeReplaced()) return false
            if (level.getBlockState(p.below()).isAir) return false
        }
        return true
    }
}
