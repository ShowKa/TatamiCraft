package com.showka.objects.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

/**
 * BlockEntity for tatami parts (shared by full and half tatami).
 * Stores the origin coordinate of the multi-block set.
 */
class TatamiBlockEntity(
    type: BlockEntityType<TatamiBlockEntity>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {

    /** Origin coordinate of the multi-block set */
    var origin: BlockPos? = null

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        origin?.let {
            output.putInt("OriginX", it.x)
            output.putInt("OriginY", it.y)
            output.putInt("OriginZ", it.z)
        }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        val ox = input.getIntOr("OriginX", Int.MIN_VALUE)
        val oy = input.getIntOr("OriginY", Int.MIN_VALUE)
        val oz = input.getIntOr("OriginZ", Int.MIN_VALUE)
        if (ox != Int.MIN_VALUE && oy != Int.MIN_VALUE && oz != Int.MIN_VALUE) {
            origin = BlockPos(ox, oy, oz)
        }
    }
}
