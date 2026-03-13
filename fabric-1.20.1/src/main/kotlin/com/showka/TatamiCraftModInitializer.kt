package com.showka

import com.showka.objects.TatamiColor
import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.world.item.CreativeModeTabs

object TatamiCraftModInitializer : ModInitializer {

    override fun onInitialize() {
        ModBlocks.init()
        ModBlockEntities.init()
        ModItems.init()

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
