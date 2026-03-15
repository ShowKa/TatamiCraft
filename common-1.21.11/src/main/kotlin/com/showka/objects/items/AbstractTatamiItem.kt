package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiBlockEntity
import com.showka.util.TatamiLayout
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.core.BlockPos
import org.slf4j.LoggerFactory

/**
 * Tatami item.
 * Right-click places a multi-block structure according to the layout.
 *
 * @param layout     Tatami layout definition (rows x cols, also defines partProperty)
 * @param partBlockProvider Lambda returning the part block to place (lazy)
 */
class AbstractTatamiItem(
    properties: Properties,
    private val layout: TatamiLayout,
    private val partBlockProvider: () -> AbstractTatamiPartBlock
) : Item(properties) {

    private val partProperty = layout.partProperty

    companion object {
        private val logger = LoggerFactory.getLogger("tatamicraft")

        /** Create a full-size tatami item (2x4 layout). */
        fun tatami(props: Properties, partBlockProvider: () -> AbstractTatamiPartBlock): AbstractTatamiItem =
            AbstractTatamiItem(props, TatamiLayout.TATAMI, partBlockProvider)

        /** Create a half-size tatami item (2x2 layout). */
        fun tatamiHalf(props: Properties, partBlockProvider: () -> AbstractTatamiPartBlock): AbstractTatamiItem =
            AbstractTatamiItem(props, TatamiLayout.TATAMI_HALF, partBlockProvider)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player ?: return InteractionResult.FAIL
        val clickedPos = context.clickedPos
        val side = context.clickedFace

        // Determine placement position
        val placePos = if (level.getBlockState(clickedPos).canBeReplaced()) {
            clickedPos
        } else {
            clickedPos.relative(side)
        }

        // Player's horizontal facing
        val facing = player.direction
        val origin = placePos

        // Try right direction (CW) first, then fall back to left (CCW)
        val rightPositions = layout.getAllPartPositions(origin, facing, mirrored = false)
        val leftPositions = layout.getAllPartPositions(origin, facing, mirrored = true)

        val (positions, mirrored) = when {
            canPlaceAll(level, rightPositions) -> Pair(rightPositions, false)
            canPlaceAll(level, leftPositions)  -> Pair(leftPositions, true)
            else -> return InteractionResult.FAIL
        }

        // Place on server side
        if (!level.isClientSide) {
            val partBlock = partBlockProvider()
            for ((index, p) in positions.withIndex()) {
                val state = partBlock.defaultBlockState()
                    .setValue(AbstractTatamiPartBlock.FACING, facing)
                    .setValue(partProperty, index)
                    .setValue(AbstractTatamiPartBlock.MIRRORED, mirrored)
                level.setBlock(p, state, Block.UPDATE_ALL)

                // Set origin on BlockEntity
                val be = level.getBlockEntity(p) as? TatamiBlockEntity
                if (be != null) {
                    be.origin = origin
                    be.setChanged()
                } else {
                    logger.warn("TatamiBlockEntity not found at {}", p)
                }
            }
        }

        // Consume 1 item in survival
        if (!player.isCreative) {
            context.itemInHand.shrink(1)
        }

        return InteractionResult.SUCCESS
    }

    /**
     * Check if all positions are valid for placement.
     */
    private fun canPlaceAll(level: Level, positions: List<BlockPos>): Boolean {
        for (p in positions) {
            if (!level.isInWorldBounds(p)) return false
            if (!level.getBlockState(p).canBeReplaced()) return false
            if (level.getBlockState(p.below()).isAir) return false
        }
        return true
    }
}
