package com.showka

import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.item.ItemGroups
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

object TatamiCraftModInitializer : ModInitializer {
    private val logger = LoggerFactory.getLogger("tatamicraft")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ServerPlayConnectionEvents.JOIN.register{ handler, _, _->
			val player = handler.player
			player.sendMessage(
				Text.literal("こんにちは！"),
				false
			)
		}
		// mod block 登録
		ModBlocks.init()
		// mod block entity 登録
		ModBlockEntities.init()
		// mod item 登録
		ModItems.init()

		// クリエイティブタブに畳アイテムを追加（デフォルト＋全カラー）
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register { entries ->
			entries.add(ModItems.TATAMI_ITEM)
			entries.add(ModItems.TATAMI_HALF_ITEM)
			for (item in ModItems.COLORED_TATAMI_ITEMS.values) {
				entries.add(item)
			}
			for (item in ModItems.COLORED_TATAMI_HALF_ITEMS.values) {
				entries.add(item)
			}
		}
	}

	const val MOD_ID = "tatamicraft"
}
