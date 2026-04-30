package com.showka.objects.blocks

import com.showka.TatamiCraftNeoForge
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

/**
 * Forge 1.20.1 BlockEntityType registration using DeferredRegister
 */
object ModBlockEntities {

    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TatamiCraftNeoForge.MOD_ID)

    val FUSUMA_PART_BLOCK_ENTITY: RegistryObject<BlockEntityType<FusumaBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("fusuma_part") {
            BlockEntityType.Builder.of(
                { pos, state -> FusumaBlockEntity(FUSUMA_PART_BLOCK_ENTITY.get(), pos, state) },
                ModBlocks.FUSUMA_PART.get()
            ).build(null)
        }

    val TATAMI_PART_BLOCK_ENTITY: RegistryObject<BlockEntityType<TatamiBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("tatami_part") {
            BlockEntityType.Builder.of(
                { pos, state -> TatamiBlockEntity(TATAMI_PART_BLOCK_ENTITY.get(), pos, state) },
                *ModBlocks.allTatamiParts().map { it.get() }.toTypedArray()
            ).build(null)
        }

    val TATAMI_HALF_PART_BLOCK_ENTITY: RegistryObject<BlockEntityType<TatamiBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("tatami_half_part") {
            BlockEntityType.Builder.of(
                { pos, state -> TatamiBlockEntity(TATAMI_HALF_PART_BLOCK_ENTITY.get(), pos, state) },
                *ModBlocks.allTatamiHalfParts().map { it.get() }.toTypedArray()
            ).build(null)
        }
}
