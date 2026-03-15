package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType

/**
 * Fabric BlockEntityType registration
 */
object ModBlockEntities {

    val TATAMI_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, "tatami_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY, pos, state) },
            *ModBlocks.allTatamiParts().toTypedArray<Block>()
        ).build()
    )

    val TATAMI_HALF_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, "tatami_half_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY, pos, state) },
            *ModBlocks.allTatamiHalfParts().toTypedArray<Block>()
        ).build()
    )

    fun init() {
        // Trigger static initialization
    }
}
