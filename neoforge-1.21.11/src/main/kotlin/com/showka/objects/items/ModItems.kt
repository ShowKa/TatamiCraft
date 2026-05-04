package com.showka.objects.items

import com.showka.TatamiCraftConstants
import com.showka.objects.ModColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.FusumaPartBlock
import com.showka.objects.blocks.ModBlocks
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
        "tatami"
    ) { props: Item.Properties -> AbstractTatamiItem.tatami(props) { ModBlocks.TATAMI_PART.get() as AbstractTatamiPartBlock } }

    val TATAMI_HALF_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "tatami_half"
    ) { props: Item.Properties -> AbstractTatamiItem.tatamiHalf(props) { ModBlocks.TATAMI_HALF_PART.get() as AbstractTatamiPartBlock } }

    // ── Color variations ──────────────────────────

    val COLORED_TATAMI_ITEMS: Map<ModColor, DeferredItem<Item>> =
        ModColor.COLORED.associateWith { color ->
            ITEMS.registerItem(
                color.tatamiId()
            ) { props: Item.Properties -> AbstractTatamiItem.tatami(props) { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock } }
        }

    val COLORED_TATAMI_HALF_ITEMS: Map<ModColor, DeferredItem<Item>> =
        ModColor.COLORED.associateWith { color ->
            ITEMS.registerItem(
                color.tatamiHalfId()
            ) { props: Item.Properties -> AbstractTatamiItem.tatamiHalf(props) { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock } }
        }

    // ── Fusuma ───────────────────────────────────────

    val FUSUMA_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "fusuma"
    ) { props: Item.Properties -> FusumaItem(props) { ModBlocks.FUSUMA_PART.get() as FusumaPartBlock } }

    val COLORED_FUSUMA_ITEMS: Map<ModColor, DeferredItem<Item>> =
        ModColor.FUSUMA_COLORED.associateWith { color ->
            ITEMS.registerItem(color.fusumaId()) { props: Item.Properties ->
                FusumaItem(props) { ModBlocks.getFusumaPart(color) as FusumaPartBlock }
            }
        }

    // ── Sliding Door Variants ─────────────────────────

    val SHOJI_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "shoji"
    ) { props: Item.Properties -> FusumaItem(props) { ModBlocks.SHOJI_PART.get() as FusumaPartBlock } }

    val FROSTED_GLASS_SLIDING_DOOR_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "frosted_glass_sliding_door"
    ) { props: Item.Properties -> FusumaItem(props) { ModBlocks.FROSTED_GLASS_SLIDING_DOOR_PART.get() as FusumaPartBlock } }

    val SLIDING_WINDOW_ITEM: DeferredItem<Item> = ITEMS.registerItem(
        "sliding_window"
    ) { props: Item.Properties -> FusumaItem(props) { ModBlocks.SLIDING_WINDOW_PART.get() as FusumaPartBlock } }

    // ── Helpers ─────────────────────────────────────

    fun getTatamiItem(color: ModColor): Item =
        if (color == ModColor.DEFAULT) TATAMI_ITEM.get() else COLORED_TATAMI_ITEMS.getValue(color).get()

    fun getTatamiHalfItem(color: ModColor): Item =
        if (color == ModColor.DEFAULT) TATAMI_HALF_ITEM.get() else COLORED_TATAMI_HALF_ITEMS.getValue(color).get()

    fun getFusumaItem(color: ModColor): Item =
        if (color == ModColor.DEFAULT) FUSUMA_ITEM.get() else COLORED_FUSUMA_ITEMS.getValue(color).get()
}
