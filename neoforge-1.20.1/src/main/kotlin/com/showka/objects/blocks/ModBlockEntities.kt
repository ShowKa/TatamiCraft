package com.showka.objects.blocks

import com.showka.TatamiCraftNeoForge
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

/**
 * Forge 1.20.1 BlockEntityType registration using DeferredRegister
 */
object ModBlockEntities {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TatamiCraftNeoForge.MOD_ID)

    val TATAMI_PART_BLOCK_ENTITY: RegistryObject<BlockEntityType<TatamiBlockEntity>> = register(
        "tatami_part",
        { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY.get(), pos, state) },
        { ModBlocks.allTatamiParts().map { it.get() }.toTypedArray() }
    )

    val TATAMI_HALF_PART_BLOCK_ENTITY: RegistryObject<BlockEntityType<TatamiBlockEntity>> = register(
        "tatami_half_part",
        { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY.get(), pos, state) },
        { ModBlocks.allTatamiHalfParts().map { it.get() }.toTypedArray() }
    )

    val FUSUMA_PART_BLOCK_ENTITY: RegistryObject<BlockEntityType<FusumaBlockEntity>> = register(
        "fusuma_part",
        { pos, state -> FusumaBlockEntity(FUSUMA_PART_BLOCK_ENTITY.get(), pos, state) },
        { ModBlocks.allFusumaParts().map { it.get() }.toTypedArray() }
    )

    private fun <T : BlockEntity> register(
        name: String,
        factory: (BlockPos, BlockState) -> T,
        blocksProvider: () -> Array<out Block>
    ): RegistryObject<BlockEntityType<T>> = BLOCK_ENTITY_TYPES.register(name) {
        BlockEntityType.Builder.of(factory, *blocksProvider()).build(null)
    }
}
