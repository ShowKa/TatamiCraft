package com.showka.objects.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

/** BlockEntity for fusuma part blocks. Inherits origin storage from [OriginBlockEntity]. */
class FusumaBlockEntity(
    type: BlockEntityType<FusumaBlockEntity>,
    pos: BlockPos,
    state: BlockState
) : OriginBlockEntity(type, pos, state)
