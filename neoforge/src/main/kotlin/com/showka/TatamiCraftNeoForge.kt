package com.showka

import com.showka.objects.TatamiColor
import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

@Mod(TatamiCraftConstants.MOD_ID)
class TatamiCraftNeoForge(modBus: IEventBus) {

    init {
        // Register deferred registries
        ModBlocks.BLOCKS.register(modBus)
        ModItems.ITEMS.register(modBus)
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus)

        // Creative tab event
        modBus.addListener(::addCreative)
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
}
