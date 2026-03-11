package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.items.ModItems
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour

/**
 * Fabric block registration
 */
object ModBlocks {

    // -- Default tatami --

    val TATAMI_PART: Block = registerBlock(
        path = "tatami_part",
        block = TatamiPartBlock(
            tatamiSettings(),
            dropItemProvider = { ModItems.TATAMI_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
        )
    )

    val TATAMI_HALF_PART: Block = registerBlock(
        path = "tatami_half_part",
        block = TatamiHalfPartBlock(
            tatamiSettings(),
            dropItemProvider = { ModItems.TATAMI_HALF_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
        )
    )

    // -- Color variations --

    val COLORED_TATAMI_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlock(
            path = "${color.prefix()}tatami_part",
            block = TatamiPartBlock(
                tatamiSettings(),
                dropItemProvider = { ModItems.getTatamiItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
            )
        )
    }

    val COLORED_TATAMI_HALF_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlock(
            path = "${color.prefix()}tatami_half_part",
            block = TatamiHalfPartBlock(
                tatamiSettings(),
                dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
            )
        )
    }

    // -- Helpers --

    private fun tatamiSettings(): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of()
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollision()

    private fun registerBlock(path: String, block: Block): Block {
        val id = Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, path)
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
