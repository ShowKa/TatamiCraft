package com.showka

import com.showka.objects.TatamiColor
import com.showka.objects.blocks.ModBlockEntities
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.ModItems
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(TatamiCraftConstants.MOD_ID)
class TatamiCraftForge {

    init {
        val modBus = FMLJavaModLoadingContext.get().modEventBus
        ModBlocks.BLOCKS.register(modBus)
        ModItems.ITEMS.register(modBus)
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus)

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
