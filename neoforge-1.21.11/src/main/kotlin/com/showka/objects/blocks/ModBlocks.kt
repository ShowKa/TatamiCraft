package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import com.showka.objects.ModColor
import com.showka.objects.blocks.FusumaPartBlock
import com.showka.objects.items.ModItems
import com.showka.util.TatamiLayout
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

/**
 * NeoForge block registration using DeferredRegister
 */
object ModBlocks {

    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(TatamiCraftConstants.MOD_ID)

    // ── Default tatami ──────────────────────────────

    val TATAMI_PART: DeferredBlock<AbstractTatamiPartBlock> = BLOCKS.registerBlock(
        "tatami_part",
        { props: BlockBehaviour.Properties ->
            AbstractTatamiPartBlock(
                props, TatamiLayout.TATAMI,
                dropItemProvider = { ModItems.TATAMI_ITEM.get() },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY.get() }
            )
        },
        java.util.function.Supplier { tatamiSettings() }
    )

    val TATAMI_HALF_PART: DeferredBlock<AbstractTatamiPartBlock> = BLOCKS.registerBlock(
        "tatami_half_part",
        { props: BlockBehaviour.Properties ->
            AbstractTatamiPartBlock(
                props, TatamiLayout.TATAMI_HALF,
                dropItemProvider = { ModItems.TATAMI_HALF_ITEM.get() },
                blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY.get() }
            )
        },
        java.util.function.Supplier { tatamiSettings() }
    )

    // ── Color variations ──────────────────────────

    val COLORED_TATAMI_PARTS: Map<ModColor, DeferredBlock<AbstractTatamiPartBlock>> =
        ModColor.COLORED.associateWith { color ->
            BLOCKS.registerBlock(
                color.tatamiPartId(),
                { props: BlockBehaviour.Properties ->
                    AbstractTatamiPartBlock(
                        props, TatamiLayout.TATAMI,
                        dropItemProvider = { ModItems.getTatamiItem(color) },
                        blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY.get() }
                    )
                },
                java.util.function.Supplier { tatamiSettings() }
            )
        }

    val COLORED_TATAMI_HALF_PARTS: Map<ModColor, DeferredBlock<AbstractTatamiPartBlock>> =
        ModColor.COLORED.associateWith { color ->
            BLOCKS.registerBlock(
                color.tatamiHalfPartId(),
                { props: BlockBehaviour.Properties ->
                    AbstractTatamiPartBlock(
                        props, TatamiLayout.TATAMI_HALF,
                        dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                        blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY.get() }
                    )
                },
                java.util.function.Supplier { tatamiSettings() }
            )
        }

    // ── Fusuma ───────────────────────────────────────

    val FUSUMA_PART: DeferredBlock<FusumaPartBlock> = BLOCKS.registerBlock(
        "fusuma_part",
        { props: BlockBehaviour.Properties ->
            FusumaPartBlock(
                props,
                dropItemProvider = { ModItems.FUSUMA_ITEM.get() },
                blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY.get() }
            )
        },
        java.util.function.Supplier { fusumaSettings() }
    )

    val COLORED_FUSUMA_PARTS: Map<ModColor, DeferredBlock<FusumaPartBlock>> =
        ModColor.FUSUMA_COLORED.associateWith { color ->
            BLOCKS.registerBlock(
                color.fusumaPartId(),
                { props: BlockBehaviour.Properties ->
                    FusumaPartBlock(
                        props,
                        dropItemProvider = { ModItems.getFusumaItem(color) },
                        blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY.get() }
                    )
                },
                java.util.function.Supplier { fusumaSettings() }
            )
        }

    // ── Sliding Door Variants ────────────────────────

    val SHOJI_PART: DeferredBlock<FusumaPartBlock> = BLOCKS.registerBlock(
        "shoji_part",
        { props: BlockBehaviour.Properties ->
            FusumaPartBlock(
                props,
                dropItemProvider = { ModItems.SHOJI_ITEM.get() },
                blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY.get() }
            )
        },
        java.util.function.Supplier { fusumaSettings() }
    )

    val FROSTED_GLASS_SLIDING_DOOR_PART: DeferredBlock<FusumaPartBlock> = BLOCKS.registerBlock(
        "frosted_glass_sliding_door_part",
        { props: BlockBehaviour.Properties ->
            FusumaPartBlock(
                props,
                dropItemProvider = { ModItems.FROSTED_GLASS_SLIDING_DOOR_ITEM.get() },
                blockEntityTypeProvider = { ModBlockEntities.FUSUMA_PART_BLOCK_ENTITY.get() }
            )
        },
        java.util.function.Supplier { fusumaSettings() }
    )

    // ── Helpers ─────────────────────────────────────

    private fun fusumaSettings(): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of()
            .strength(1.5f)
            .sound(SoundType.WOOD)
            .noOcclusion()

    private fun tatamiSettings(): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of()
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollision()


    fun getTatamiPart(color: ModColor): Block =
        if (color == ModColor.DEFAULT) TATAMI_PART.get() else COLORED_TATAMI_PARTS.getValue(color).get()

    fun getTatamiHalfPart(color: ModColor): Block =
        if (color == ModColor.DEFAULT) TATAMI_HALF_PART.get() else COLORED_TATAMI_HALF_PARTS.getValue(color).get()

    fun getFusumaPart(color: ModColor): Block =
        if (color == ModColor.DEFAULT) FUSUMA_PART.get() else COLORED_FUSUMA_PARTS.getValue(color).get()

    fun allTatamiParts(): List<DeferredBlock<AbstractTatamiPartBlock>> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    fun allTatamiHalfParts(): List<DeferredBlock<AbstractTatamiPartBlock>> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values

    fun allFusumaParts(): List<DeferredBlock<FusumaPartBlock>> =
        listOf(FUSUMA_PART) + COLORED_FUSUMA_PARTS.values

    fun allSlidingDoorVariantParts(): List<DeferredBlock<FusumaPartBlock>> =
        listOf(SHOJI_PART, FROSTED_GLASS_SLIDING_DOOR_PART)

    fun allFusumaAndVariantParts(): List<DeferredBlock<FusumaPartBlock>> =
        allFusumaParts() + allSlidingDoorVariantParts()
}
