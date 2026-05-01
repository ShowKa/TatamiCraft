package com.showka

import com.showka.objects.TatamiColor
import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import com.showka.util.orderedTatamiItems
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.world.item.CreativeModeTabs

object TatamiCraftModInitializer : ModInitializer {

    override fun onInitialize() {
        // Register blocks
        ModBlocks.init()
        // Register block entities
        ModBlockEntities.init()
        // Register items
        ModItems.init()

        // Add tatami items to creative tab (default + all colors, same color tatami & half tatami adjacent)
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register { entries ->
            // tatami
            orderedTatamiItems(
                ModItems.TATAMI_ITEM,
                ModItems.TATAMI_HALF_ITEM,
                ModItems::getTatamiItem,
                ModItems::getTatamiHalfItem
            ).forEach { entries.accept(it) }
            // fusuma (default + colored)
            entries.accept(ModItems.FUSUMA_ITEM)
            TatamiColor.FUSUMA_COLORED.forEach { color -> entries.accept(ModItems.getFusumaItem(color)) }
        }
    }
}
