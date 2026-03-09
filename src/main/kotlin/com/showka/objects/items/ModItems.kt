package com.showka.objects.items

import com.showka.TatamiCraftModInitializer
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

/**
 * Mod のアイテム登録
 */
object ModItems {

    val TATAMI_ITEM: Item = register(
        path = "tatami",
        factory = { settings -> TatamiItem(settings) },
        settings = Item.Settings()
    )

    val TATAMI_HALF_ITEM: Item = register(
        path = "tatami_half",
        factory = { settings -> TatamiHalfItem(settings) },
        settings = Item.Settings()
    )

    private fun register(
        path: String,
        factory: (Item.Settings) -> Item,
        settings: Item.Settings
    ): Item {
        val id = Identifier.of(TatamiCraftModInitializer.MOD_ID, path)
        val key = RegistryKey.of(RegistryKeys.ITEM, id)
        return Items.register(key, factory, settings)
    }

    fun init() {
        // 静的初期化を起こすだけ
    }
}
