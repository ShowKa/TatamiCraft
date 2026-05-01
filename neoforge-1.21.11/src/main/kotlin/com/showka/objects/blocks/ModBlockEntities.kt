package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

/**
 * NeoForge BlockEntityType registration using DeferredRegister
 */
object ModBlockEntities {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TatamiCraftConstants.MOD_ID)

    val TATAMI_PART_BLOCK_ENTITY: Supplier<BlockEntityType<TatamiBlockEntity>> = register(
        "tatami_part",
        { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY.get(), pos, state) },
        { ModBlocks.allTatamiParts().map { it.get() }.toTypedArray() }
    )

    val TATAMI_HALF_PART_BLOCK_ENTITY: Supplier<BlockEntityType<TatamiBlockEntity>> = register(
        "tatami_half_part",
        { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY.get(), pos, state) },
        { ModBlocks.allTatamiHalfParts().map { it.get() }.toTypedArray() }
    )

    val FUSUMA_PART_BLOCK_ENTITY: Supplier<BlockEntityType<FusumaBlockEntity>> = register(
        "fusuma_part",
        { pos, state -> FusumaBlockEntity(FUSUMA_PART_BLOCK_ENTITY.get(), pos, state) },
        { arrayOf(ModBlocks.FUSUMA_PART.get()) }
    )

    private fun <T : BlockEntity> register(
        name: String,
        factory: (BlockPos, BlockState) -> T,
        blocksProvider: () -> Array<out Block>
    ): Supplier<BlockEntityType<T>> = BLOCK_ENTITY_TYPES.register(name) {
        BlockEntityType(factory, false, *blocksProvider())
    }
}
