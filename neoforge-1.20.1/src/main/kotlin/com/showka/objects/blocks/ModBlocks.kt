package com.showka.objects.blocks

import com.showka.TatamiCraftNeoForge
import com.showka.objects.TatamiColor
import com.showka.objects.items.ModItems
import com.showka.util.TatamiLayout
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

/**
 * Forge 1.20.1 block registration using DeferredRegister
 */
object ModBlocks {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, TatamiCraftNeoForge.MOD_ID)

    // -- Default tatami --

    val TATAMI_PART: RegistryObject<AbstractTatamiPartBlock> = BLOCKS.register("tatami_part") {
        AbstractTatamiPartBlock(
            tatamiSettings(), TatamiLayout.TATAMI,
            dropItemProvider = { ModItems.TATAMI_ITEM.get() },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY.get() }
        )
    }

    val TATAMI_HALF_PART: RegistryObject<AbstractTatamiPartBlock> = BLOCKS.register("tatami_half_part") {
        AbstractTatamiPartBlock(
            tatamiSettings(), TatamiLayout.TATAMI_HALF,
            dropItemProvider = { ModItems.TATAMI_HALF_ITEM.get() },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY.get() }
        )
    }

    // -- Color variations --

    val COLORED_TATAMI_PARTS: Map<TatamiColor, RegistryObject<AbstractTatamiPartBlock>> =
        TatamiColor.COLORED.associateWith { color ->
            BLOCKS.register(color.tatamiPartId()) {
                AbstractTatamiPartBlock(
                    tatamiSettings(), TatamiLayout.TATAMI,
                    dropItemProvider = { ModItems.getTatamiItem(color) },
                    blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY.get() }
                )
            }
        }

    val COLORED_TATAMI_HALF_PARTS: Map<TatamiColor, RegistryObject<AbstractTatamiPartBlock>> =
        TatamiColor.COLORED.associateWith { color ->
            BLOCKS.register(color.tatamiHalfPartId()) {
                AbstractTatamiPartBlock(
                    tatamiSettings(), TatamiLayout.TATAMI_HALF,
                    dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                    blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY.get() }
                )
            }
        }

    // -- Helpers --

    private fun tatamiSettings(): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of()
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollission()

    fun getTatamiPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_PART.get() else COLORED_TATAMI_PARTS.getValue(color).get()

    fun getTatamiHalfPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_PART.get() else COLORED_TATAMI_HALF_PARTS.getValue(color).get()

    fun allTatamiParts(): List<RegistryObject<AbstractTatamiPartBlock>> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    fun allTatamiHalfParts(): List<RegistryObject<AbstractTatamiPartBlock>> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values
}
