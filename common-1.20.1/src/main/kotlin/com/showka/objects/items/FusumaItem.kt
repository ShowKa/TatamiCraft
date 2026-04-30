package com.showka.objects.items

import com.showka.objects.FusumaOpenState
import com.showka.objects.FusumaSide
import com.showka.objects.blocks.FusumaBlockEntity
import com.showka.objects.blocks.FusumaPartBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class FusumaItem(
    properties: Properties,
    private val partBlockProvider: () -> FusumaPartBlock
) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player ?: return InteractionResult.FAIL
        val clickedPos = context.clickedPos
        val clickedFace = context.clickedFace

        val placePos = if (level.getBlockState(clickedPos).canBeReplaced()) {
            clickedPos
        } else {
            clickedPos.relative(clickedFace)
        }

        val facing = player.direction
        val ccw = facing.getCounterClockWise()
        val origin = (0..3)
            .map { offset -> placePos.relative(ccw, offset) }
            .firstOrNull { canPlaceAll(level, it, facing) }
            ?: return InteractionResult.FAIL

        if (!level.isClientSide) {
            val partBlock = partBlockProvider()
            val right = facing.getClockWise()
            val flipped = player.isCrouching
            for (side in FusumaSide.entries) {
                val sideOff = if (side == FusumaSide.LEFT) 0 else 2
                for (px in 0..1) {
                    for (py in 0..2) {
                        val pos = origin.relative(right, sideOff + px).above(py)
                        val state = partBlock.defaultBlockState()
                            .setValue(FusumaPartBlock.FACING, facing)
                            .setValue(FusumaPartBlock.SIDE, side)
                            .setValue(FusumaPartBlock.PART_X, px)
                            .setValue(FusumaPartBlock.PART_Y, py)
                            .setValue(FusumaPartBlock.DOOR_STATE, FusumaOpenState.CLOSED)
                            .setValue(FusumaPartBlock.FLIPPED_HORIZONTAL, flipped)
                        level.setBlock(pos, state, Block.UPDATE_ALL)

                        val be = level.getBlockEntity(pos) as? FusumaBlockEntity
                        if (be != null) {
                            be.origin = origin
                            be.setChanged()
                        }
                    }
                }
            }
        }

        if (!player.isCreative) context.itemInHand.shrink(1)
        return InteractionResult.SUCCESS
    }

    private fun canPlaceAll(level: Level, origin: BlockPos, facing: Direction): Boolean {
        for (pos in FusumaPartBlock.getAllPositions(origin, facing)) {
            if (!level.isInWorldBounds(pos)) return false
            if (!level.getBlockState(pos).canBeReplaced()) return false
            if (pos.y == origin.y && level.getBlockState(pos.below()).isAir) return false
        }
        return true
    }
}
