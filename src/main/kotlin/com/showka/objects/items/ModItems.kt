package com.showka.objects.items

import com.showka.TatamiCraftModInitializer
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.blocks.TatamiHalfPartBlock
import com.showka.objects.blocks.TatamiPartBlock
import com.showka.util.TatamiLayout
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

/**
 * Mod のアイテム登録
 */
object ModItems {

    // ── デフォルト畳アイテム ──────────────────────────

    val TATAMI_ITEM: Item = register(
        path = "tatami",
        factory = { settings -> TatamiItem(settings) },
        settings = Item.Settings().useItemPrefixedTranslationKey()
    )

    val TATAMI_HALF_ITEM: Item = register(
        path = "tatami_half",
        factory = { settings -> TatamiHalfItem(settings) },
        settings = Item.Settings().useItemPrefixedTranslationKey()
    )

    // ── カラーバリエーション ──────────────────────────

    /** 色付き畳アイテムのマップ（色 → Item） */
    val COLORED_TATAMI_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        register(
            path = "${color.prefix()}tatami",
            factory = { settings ->
                AbstractTatamiItem.create(
                    settings = settings,
                    layout = TatamiLayout.TATAMI,
                    partBlockProvider = { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock },
                    partProperty = TatamiPartBlock.PART
                )
            },
            settings = Item.Settings().useItemPrefixedTranslationKey()
        )
    }

    /** 色付き半畳アイテムのマップ（色 → Item） */
    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        register(
            path = "${color.prefix()}tatami_half",
            factory = { settings ->
                AbstractTatamiItem.create(
                    settings = settings,
                    layout = TatamiLayout.TATAMI_HALF,
                    partBlockProvider = { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock },
                    partProperty = TatamiHalfPartBlock.PART
                )
            },
            settings = Item.Settings().useItemPrefixedTranslationKey()
        )
    }

    // ── ヘルパー ─────────────────────────────────────

    private fun register(
        path: String,
        factory: (Item.Settings) -> Item,
        settings: Item.Settings
    ): Item {
        val id = Identifier.of(TatamiCraftModInitializer.MOD_ID, path)
        val key = RegistryKey.of(RegistryKeys.ITEM, id)
        return Items.register(key, factory, settings)
    }

    /** 指定色の畳アイテムを取得（DEFAULT ならデフォルトを返す） */
    fun getTatamiItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_ITEM else COLORED_TATAMI_ITEMS.getValue(color)

    /** 指定色の半畳アイテムを取得（DEFAULT ならデフォルトを返す） */
    fun getTatamiHalfItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_ITEM else COLORED_TATAMI_HALF_ITEMS.getValue(color)

    fun init() {
        // 静的初期化を起こすだけ
    }
}
