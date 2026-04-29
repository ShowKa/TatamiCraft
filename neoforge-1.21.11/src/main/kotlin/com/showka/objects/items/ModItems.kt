package com.showka.objects.items

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.FusumaPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.FusumaItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

/**
 * NeoForge item registration using DeferredRegister
 */
object ModItems {

    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(TatamiCraftConstants.MOD_ID)

    // ── Fusuma ───────────────────────────────────────

    val FUSUMA_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "fusuma"
    ) { props: Item.Properties -> FusumaItem(props) { ModBlocks.FUSUMA_PART.get() as FusumaPartBlock } }

    // ── Default tatami items ──────────────────────────

    val TATAMI_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "tatami"
    ) { props: Item.Properties -> AbstractTatamiItem.tatami(props) { ModBlocks.TATAMI_PART.get() as AbstractTatamiPartBlock } }

    val TATAMI_HALF_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "tatami_half"
    ) { props: Item.Properties -> AbstractTatamiItem.tatamiHalf(props) { ModBlocks.TATAMI_HALF_PART.get() as AbstractTatamiPartBlock } }

    // ── Color variations ──────────────────────────

    val COLORED_TATAMI_ITEMS: Map<TatamiColor, DeferredItem<Item>> =
        TatamiColor.COLORED.associateWith { color ->
            ITEMS.registerItem(
                color.tatamiId()
            ) { props: Item.Properties -> AbstractTatamiItem.tatami(props) { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock } }
        }

    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, DeferredItem<Item>> =
        TatamiColor.COLORED.associateWith { color ->
            ITEMS.registerItem(
                color.tatamiHalfId()
            ) { props: Item.Properties -> AbstractTatamiItem.tatamiHalf(props) { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock } }
        }

    // ── Helpers ─────────────────────────────────────

    fun getTatamiItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_ITEM.get() else COLORED_TATAMI_ITEMS.getValue(color).get()

    fun getTatamiHalfItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_ITEM.get() else COLORED_TATAMI_HALF_ITEMS.getValue(color).get()
}
