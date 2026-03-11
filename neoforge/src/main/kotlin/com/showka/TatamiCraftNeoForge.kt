package com.showka

import com.showka.objects.TatamiColor
import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent

@Mod(TatamiCraftConstants.MOD_ID)
class TatamiCraftNeoForge(modBus: IEventBus) {

    init {
        // Register deferred registries
        ModBlocks.BLOCKS.register(modBus)
        ModItems.ITEMS.register(modBus)
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus)

        // Creative tab event
        modBus.addListener(::addCreative)

        // Player join event
        NeoForge.EVENT_BUS.addListener(::onPlayerJoin)
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.TATAMI_ITEM)
            event.accept(ModItems.TATAMI_HALF_ITEM)
            for (color in TatamiColor.COLORED) {
                event.accept(ModItems.getTatamiItem(color))
                event.accept(ModItems.getTatamiHalfItem(color))
            }
        }
    }

    private fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        event.entity.displayClientMessage(Component.literal("こんにちは！"), false)
    }
}
