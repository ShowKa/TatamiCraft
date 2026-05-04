package com.showka

import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import com.showka.util.orderedFusumaItems
import com.showka.util.orderedTatamiItems
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(TatamiCraftNeoForge.MOD_ID)
object TatamiCraftNeoForge {

    const val MOD_ID = "tatamicraft"

    init {
        val modBus = MOD_BUS

        // Register deferred registries
        ModBlocks.BLOCKS.register(modBus)
        ModItems.ITEMS.register(modBus)
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus)

        // Creative tab event
        modBus.addListener(::addCreative)
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
            // tatami
            orderedTatamiItems(
                ModItems.TATAMI_ITEM.get(),
                ModItems.TATAMI_HALF_ITEM.get(),
                ModItems::getTatamiItem,
                ModItems::getTatamiHalfItem
            ).forEach { event.accept(it) }
            // fusuma
            orderedFusumaItems(
                ModItems.FUSUMA_ITEM.get(),
                ModItems::getFusumaItem
            ).forEach { event.accept(it) }
            // sliding door variants
            event.accept(ModItems.SHOJI_ITEM.get())
            event.accept(ModItems.FROSTED_GLASS_SLIDING_DOOR_ITEM.get())
        }
    }
}
