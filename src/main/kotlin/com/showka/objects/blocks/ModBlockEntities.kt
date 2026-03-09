package com.showka.objects.blocks

import com.showka.TatamiCraftModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

/**
 * Mod の BlockEntityType 登録
 */
object ModBlockEntities {

    val TATAMI_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(TatamiCraftModInitializer.MOD_ID, "tatami_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY, pos, state) },
            ModBlocks.TATAMI_PART
        ).build()
    )

    val TATAMI_HALF_PART_BLOCK_ENTITY: BlockEntityType<TatamiBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of(TatamiCraftModInitializer.MOD_ID, "tatami_half_part"),
        FabricBlockEntityTypeBuilder.create(
            { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY, pos, state) },
            ModBlocks.TATAMI_HALF_PART
        ).build()
    )

    fun init() {
        // 静的初期化を起こすだけ
    }
}
