package com.showka

import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import com.showka.util.orderedTatamiItems
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
            orderedTatamiItems(
                ModItems.TATAMI_ITEM.get(),
                ModItems.TATAMI_HALF_ITEM.get(),
                ModItems::getTatamiItem,
                ModItems::getTatamiHalfItem
            ).forEach { event.accept(it) }
        }
    }
}
