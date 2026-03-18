package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.items.ModItems
import com.showka.util.TatamiLayout
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour

/**
 * Fabric block registration
 */
object ModBlocks {

    fun init() {
        // Trigger static initialization
    }

    private fun registerBlock(
        path: String,
        settingsFactory: (String) -> BlockBehaviour.Properties = ::tatamiSettings,
        factory: (BlockBehaviour.Properties) -> Block
    ): Block {
        val id = Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, path)
        val block = factory(settingsFactory(path))
        return Registry.register(BuiltInRegistries.BLOCK, id, block)
    }

    // -- Default tatami --

    val TATAMI_PART: Block = registerBlock("tatami_part") { props ->
        AbstractTatamiPartBlock(
            props, TatamiLayout.TATAMI,
            dropItemProvider = { ModItems.TATAMI_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
        )
    }

    val TATAMI_HALF_PART: Block = registerBlock("tatami_half_part") { props ->
        AbstractTatamiPartBlock(
            props, TatamiLayout.TATAMI_HALF,
            dropItemProvider = { ModItems.TATAMI_HALF_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
        )
    }

    // -- Color variations --

    val COLORED_TATAMI_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlock(color.tatamiPartId()) { props ->
            AbstractTatamiPartBlock(
                props, TatamiLayout.TATAMI,
                dropItemProvider = { ModItems.getTatamiItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
            )
        }
    }

    val COLORED_TATAMI_HALF_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlock(color.tatamiHalfPartId()) { props ->
            AbstractTatamiPartBlock(
                props, TatamiLayout.TATAMI_HALF,
                dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
            )
        }
    }

    // -- Helpers --

    private fun tatamiSettings(path: String): BlockBehaviour.Properties {
        val key = ResourceKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, path)
        )
        return BlockBehaviour.Properties.of()
            .setId(key)
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollision()
    }

    fun getTatamiPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_PART else COLORED_TATAMI_PARTS.getValue(color)

    fun getTatamiHalfPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_PART else COLORED_TATAMI_HALF_PARTS.getValue(color)

    fun allTatamiParts(): List<Block> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    fun allTatamiHalfParts(): List<Block> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values

    // –------
    // FUSUMA
    // –------

    val FUSUMA: Block = registerBlock("fusuma", ::fusumaSettings) { props ->
        FusumaBlock(props)
    }

    private fun fusumaSettings(path: String): BlockBehaviour.Properties {
        val key = ResourceKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, path)
        )

        return BlockBehaviour.Properties.of()
            .setId(key)
            .strength(0.5f)
            .sound(SoundType.WOOD)
            .noOcclusion()
    }
}
