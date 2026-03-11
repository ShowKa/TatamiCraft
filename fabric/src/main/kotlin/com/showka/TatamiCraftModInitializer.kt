package com.showka

import com.showka.objects.TatamiColor
import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTabs
import org.slf4j.LoggerFactory

object TatamiCraftModInitializer : ModInitializer {
    private val logger = LoggerFactory.getLogger("tatamicraft")

    override fun onInitialize() {
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            val player = handler.player
            player.displayClientMessage(
                Component.literal("こんにちは！"), false
            )
        }
        // Register blocks
        ModBlocks.init()
        // Register block entities
        ModBlockEntities.init()
        // Register items
        ModItems.init()

        // Add tatami items to creative tab (default + all colors, same color tatami & half tatami adjacent)
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register { entries ->
            entries.accept(ModItems.TATAMI_ITEM)
            entries.accept(ModItems.TATAMI_HALF_ITEM)
            for (color in TatamiColor.COLORED) {
                entries.accept(ModItems.getTatamiItem(color))
                entries.accept(ModItems.getTatamiHalfItem(color))
            }
        }
    }
}
