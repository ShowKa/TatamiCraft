package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

/**
 * NeoForge BlockEntityType registration using DeferredRegister
 */
object ModBlockEntities {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TatamiCraftConstants.MOD_ID)

    val TATAMI_PART_BLOCK_ENTITY: Supplier<BlockEntityType<TatamiBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("tatami_part") { ->
            BlockEntityType(
                { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY.get(), pos, state) },
                false,
                *ModBlocks.allTatamiParts().map { it.get() }.toTypedArray()
            )
        }

    val TATAMI_HALF_PART_BLOCK_ENTITY: Supplier<BlockEntityType<TatamiBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("tatami_half_part") { ->
            BlockEntityType(
                { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY.get(), pos, state) },
                false,
                *ModBlocks.allTatamiHalfParts().map { it.get() }.toTypedArray()
            )
        }
}
