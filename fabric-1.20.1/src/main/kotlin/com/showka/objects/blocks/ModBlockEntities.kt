package com.showka.objects.blocks

import com.showka.TatamiCraftModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType

/**
 * Fabric 1.20.1 BlockEntityType registration
 */
object ModBlockEntities {

    val FUSUMA_PART_BLOCK_ENTITY: BlockEntityType<FusumaBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation(TatamiCraftModInitializer.MOD_ID, "fusuma_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> FusumaBlockEntity(FUSUMA_PART_BLOCK_ENTITY, pos, state) },
            ModBlocks.FUSUMA_PART
        ).build()
    )

    val TATAMI_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation(TatamiCraftModInitializer.MOD_ID, "tatami_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY, pos, state) },
            *ModBlocks.allTatamiParts().toTypedArray<Block>()
        ).build()
    )

    val TATAMI_HALF_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation(TatamiCraftModInitializer.MOD_ID, "tatami_half_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY, pos, state) },
            *ModBlocks.allTatamiHalfParts().toTypedArray<Block>()
        ).build()
    )

    fun init() {
        // Trigger static initialization
    }
}
