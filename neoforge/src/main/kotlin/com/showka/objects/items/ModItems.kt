package com.showka.objects.items

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.blocks.TatamiHalfPartBlock
import com.showka.objects.blocks.TatamiPartBlock
import com.showka.util.TatamiLayout
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

/**
 * NeoForge item registration using DeferredRegister
 */
object ModItems {

    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(TatamiCraftConstants.MOD_ID)

    // ── Default tatami items ──────────────────────────

    val TATAMI_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "tatami",
        { props: Item.Properties ->
            TatamiItem(
                props,
                partBlockProvider = { ModBlocks.TATAMI_PART.get() as AbstractTatamiPartBlock }
            )
        },
        Item.Properties().useItemDescriptionPrefix()
    )

    val TATAMI_HALF_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "tatami_half",
        { props: Item.Properties ->
            TatamiHalfItem(
                props,
                partBlockProvider = { ModBlocks.TATAMI_HALF_PART.get() as AbstractTatamiPartBlock }
            )
        },
        Item.Properties().useItemDescriptionPrefix()
    )

    // ── Color variations ──────────────────────────

    val COLORED_TATAMI_ITEMS: Map<TatamiColor, DeferredItem<Item>> =
        TatamiColor.COLORED.associateWith { color ->
            ITEMS.registerItem(
                "${color.prefix()}tatami",
                { props: Item.Properties ->
                    AbstractTatamiItem.create(
                        properties = props,
                        layout = TatamiLayout.TATAMI,
                        partBlockProvider = { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock },
                        partProperty = TatamiPartBlock.PART
                    )
                },
                Item.Properties().useItemDescriptionPrefix()
            )
        }

    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, DeferredItem<Item>> =
        TatamiColor.COLORED.associateWith { color ->
            ITEMS.registerItem(
                "${color.prefix()}tatami_half",
                { props: Item.Properties ->
                    AbstractTatamiItem.create(
                        properties = props,
                        layout = TatamiLayout.TATAMI_HALF,
                        partBlockProvider = { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock },
                        partProperty = TatamiHalfPartBlock.PART
                    )
                },
                Item.Properties().useItemDescriptionPrefix()
            )
        }

    // ── Helpers ─────────────────────────────────────

    fun getTatamiItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_ITEM.get() else COLORED_TATAMI_ITEMS.getValue(color).get()

    fun getTatamiHalfItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_ITEM.get() else COLORED_TATAMI_HALF_ITEMS.getValue(color).get()
}
