package com.showka.objects.blocks

import com.showka.TatamiCraftModInitializer
import com.showka.objects.ModColor
import com.showka.objects.items.ModItems
import com.showka.util.TatamiLayout
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

    val COLORED_TATAMI_PARTS: Map<ModColor, Block> = ModColor.COLORED.associateWith { color ->
        registerBlock(color.tatamiPartId()) { props ->
            AbstractTatamiPartBlock(
                props, TatamiLayout.TATAMI,
                dropItemProvider = { ModItems.getTatamiItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY }
            )
        }
    }

    val COLORED_TATAMI_HALF_PARTS: Map<ModColor, Block> = ModColor.COLORED.associateWith { color ->
        registerBlock(color.tatamiHalfPartId()) { props ->
            AbstractTatamiPartBlock(
                props, TatamiLayout.TATAMI_HALF,
                dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY }
            )
        }
    }

    // -- Fusuma --

    val FUSUMA_PART: Block = registerFusumaBlock("fusuma_part") { props ->
        FusumaPartBlock(
            props,
            dropItemProvider = { ModItems.FUSUMA_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY }
        )
    }

    val COLORED_FUSUMA_PARTS: Map<ModColor, Block> = ModColor.FUSUMA_COLORED.associateWith { color ->
        registerFusumaBlock(color.fusumaPartId()) { props ->
            FusumaPartBlock(
                props,
                dropItemProvider = { ModItems.getFusumaItem(color) },
                blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY }
            )
        }
    }

    // -- Sliding Door Variants --

    val SHOJI_PART: Block = registerFusumaBlock("shoji_part") { props ->
        FusumaPartBlock(
            props,
            dropItemProvider = { ModItems.SHOJI_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY }
        )
    }

    val FROSTED_GLASS_SLIDING_DOOR_PART: Block = registerFusumaBlock("frosted_glass_sliding_door_part") { props ->
        FusumaPartBlock(
            props,
            dropItemProvider = { ModItems.FROSTED_GLASS_SLIDING_DOOR_ITEM },
            blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY }
        )
    }

    // -- Helpers --

    private fun fusumaSettings(): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of()
            .strength(1.5f)
            .sound(SoundType.WOOD)
            .noOcclusion()

    private fun tatamiSettings(): BlockBehaviour.Properties {
        return BlockBehaviour.Properties.of()
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollission()
    }

    private fun registerFusumaBlock(path: String, factory: (BlockBehaviour.Properties) -> Block): Block {
        val id = ResourceLocation(TatamiCraftModInitializer.MOD_ID, path)
        val block = factory(fusumaSettings())
        return Registry.register(BuiltInRegistries.BLOCK, id, block)
    }

    private fun registerBlock(path: String, factory: (BlockBehaviour.Properties) -> Block): Block {
        val id = ResourceLocation(TatamiCraftModInitializer.MOD_ID, path)
        val block = factory(tatamiSettings())
        return Registry.register(BuiltInRegistries.BLOCK, id, block)
    }

    fun getTatamiPart(color: ModColor): Block =
        if (color == ModColor.DEFAULT) TATAMI_PART else COLORED_TATAMI_PARTS.getValue(color)

    fun getTatamiHalfPart(color: ModColor): Block =
        if (color == ModColor.DEFAULT) TATAMI_HALF_PART else COLORED_TATAMI_HALF_PARTS.getValue(color)

    fun getFusumaPart(color: ModColor): Block =
        if (color == ModColor.DEFAULT) FUSUMA_PART else COLORED_FUSUMA_PARTS.getValue(color)

    fun allTatamiParts(): List<Block> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    fun allTatamiHalfParts(): List<Block> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values

    fun allFusumaParts(): List<Block> =
        listOf(FUSUMA_PART) + COLORED_FUSUMA_PARTS.values

    fun allSlidingDoorVariantParts(): List<Block> =
        listOf(SHOJI_PART, FROSTED_GLASS_SLIDING_DOOR_PART)

    fun allFusumaAndVariantParts(): List<Block> =
        allFusumaParts() + allSlidingDoorVariantParts()

    fun init() {
        // Trigger static initialization
    }
}
