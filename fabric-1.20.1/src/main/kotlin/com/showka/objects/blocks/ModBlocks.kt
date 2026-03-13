package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.items.ModItems
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour

/**
 * Fabric 1.20.1 block registration
 */
object ModBlocks {

    val TATAMI_PART: Block = registerBlock("tatami_part") { props ->
        TatamiPartBlock(
            props,
            dropItemProvider = { ModItems.TATAMI_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
        )
    }

    val TATAMI_HALF_PART: Block = registerBlock("tatami_half_part") { props ->
        TatamiHalfPartBlock(
            props,
            dropItemProvider = { ModItems.TATAMI_HALF_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
        )
    }

    val COLORED_TATAMI_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlock("${color.prefix()}tatami_part") { props ->
            TatamiPartBlock(
                props,
                dropItemProvider = { ModItems.getTatamiItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
            )
        }
    }

    val COLORED_TATAMI_HALF_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlock("${color.prefix()}tatami_half_part") { props ->
            TatamiHalfPartBlock(
                props,
                dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
            )
        }
    }

    private fun tatamiSettings(): BlockBehaviour.Properties {
        return BlockBehaviour.Properties.of()
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollission()
    }

    private fun registerBlock(path: String, factory: (BlockBehaviour.Properties) -> Block): Block {
        val id = ResourceLocation(TatamiCraftConstants.MOD_ID, path)
        val block = factory(tatamiSettings())
        return Registry.register(BuiltInRegistries.BLOCK, id, block)
    }

    fun getTatamiPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_PART else COLORED_TATAMI_PARTS.getValue(color)

    fun getTatamiHalfPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_PART else COLORED_TATAMI_HALF_PARTS.getValue(color)

    fun allTatamiParts(): List<Block> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    fun allTatamiHalfParts(): List<Block> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values

    fun init() {
        // Trigger static initialization
    }
}
