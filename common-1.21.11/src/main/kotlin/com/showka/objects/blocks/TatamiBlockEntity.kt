package com.showka.objects.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class TatamiBlockEntity(
    type: BlockEntityType<TatamiBlockEntity>,
    pos: BlockPos,
    state: BlockState
) : OriginBlockEntity(type, pos, state)
