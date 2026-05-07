package com.showka.objects.blocks

import com.showka.TatamiCraftModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

/**
 * Fabric 1.20.1 BlockEntityType registration
 */
object ModBlockEntities {

    val TATAMI_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = register(
        "tatami_part",
        { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY, pos, state) },
        *ModBlocks.allTatamiParts().toTypedArray<Block>()
    )

    val TATAMI_HALF_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = register(
        "tatami_half_part",
        { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY, pos, state) },
        *ModBlocks.allTatamiHalfParts().toTypedArray<Block>()
    )

    val FUSUMA_PART_BLOCK_ENTITY: BlockEntityType<FusumaBlockEntity> = register(
        "fusuma_part",
        { pos, state -> FusumaBlockEntity(FUSUMA_PART_BLOCK_ENTITY, pos, state) },
        *ModBlocks.allFusumaAndVariantParts().toTypedArray<Block>()
    )

    fun init() {
        // Trigger static initialization
    }

    private fun <T : BlockEntity> register(
        name: String,
        factory: (BlockPos, BlockState) -> T,
        vararg blocks: Block
    ): BlockEntityType<T> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation(TatamiCraftModInitializer.MOD_ID, name),
        FabricBlockEntityTypeBuilder.create(factory, *blocks).build()
    )
}
