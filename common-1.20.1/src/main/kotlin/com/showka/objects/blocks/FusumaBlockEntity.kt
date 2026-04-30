package com.showka.objects.blocks

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class FusumaBlockEntity(
    type: BlockEntityType<FusumaBlockEntity>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {

    var origin: BlockPos? = null

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        origin?.let {
            tag.putInt("OriginX", it.x)
            tag.putInt("OriginY", it.y)
            tag.putInt("OriginZ", it.z)
        }
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("OriginX") && tag.contains("OriginY") && tag.contains("OriginZ")) {
            origin = BlockPos(
                tag.getInt("OriginX"),
                tag.getInt("OriginY"),
                tag.getInt("OriginZ")
            )
        }
    }
}
