package com.showka.objects.blocks

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.items.ModItems
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

    val TATAMI_PART: DeferredBlock<TatamiPartBlock> = BLOCKS.register("tatami_part") { _ ->
        TatamiPartBlock(
            tatamiSettings(),
            dropItemProvider = { ModItems.TATAMI_ITEM.get() },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY.get() }
        )
    }

    val TATAMI_HALF_PART: DeferredBlock<TatamiHalfPartBlock> = BLOCKS.register("tatami_half_part") { _ ->
        TatamiHalfPartBlock(
            tatamiSettings(),
            dropItemProvider = { ModItems.TATAMI_HALF_ITEM.get() },
            blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY.get() }
        )
    }

    // ── Color variations ──────────────────────────

    val COLORED_TATAMI_PARTS: Map<TatamiColor, DeferredBlock<TatamiPartBlock>> =
        TatamiColor.COLORED.associateWith { color ->
            BLOCKS.register("${color.prefix()}tatami_part") { _ ->
                TatamiPartBlock(
                    tatamiSettings(),
                    dropItemProvider = { ModItems.getTatamiItem(color) },
                    blockEntityTypeProvider = { ModBlockEntities.TATAMI_PART_BLOCK_ENTITY.get() }
                )
            }
        }

    val COLORED_TATAMI_HALF_PARTS: Map<TatamiColor, DeferredBlock<TatamiHalfPartBlock>> =
        TatamiColor.COLORED.associateWith { color ->
            BLOCKS.register("${color.prefix()}tatami_half_part") { _ ->
                TatamiHalfPartBlock(
                    tatamiSettings(),
                    dropItemProvider = { ModItems.getTatamiHalfItem(color) },
                    blockEntityTypeProvider = { ModBlockEntities.TATAMI_HALF_PART_BLOCK_ENTITY.get() }
                )
            }
        }

    // ── Helpers ─────────────────────────────────────

    private fun tatamiSettings(): BlockBehaviour.Properties =
        BlockBehaviour.Properties.of()
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .noCollision()

    fun getTatamiPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_PART.get() else COLORED_TATAMI_PARTS.getValue(color).get()

    fun getTatamiHalfPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_PART.get() else COLORED_TATAMI_HALF_PARTS.getValue(color).get()

    fun allTatamiParts(): List<DeferredBlock<TatamiPartBlock>> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    fun allTatamiHalfParts(): List<DeferredBlock<TatamiHalfPartBlock>> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values
}
