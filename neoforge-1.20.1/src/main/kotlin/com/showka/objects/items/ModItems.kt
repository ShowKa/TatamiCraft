package com.showka.objects.items

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

/**
 * Forge 1.20.1 item registration using DeferredRegister
 */
object ModItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, TatamiCraftConstants.MOD_ID)

    val TATAMI_ITEM: RegistryObject<Item> = ITEMS.register("tatami") {
        TatamiItem(Item.Properties(), partBlockProvider = { ModBlocks.TATAMI_PART.get() as AbstractTatamiPartBlock })
    }

    val TATAMI_HALF_ITEM: RegistryObject<Item> = ITEMS.register("tatami_half") {
        TatamiHalfItem(Item.Properties(), partBlockProvider = { ModBlocks.TATAMI_HALF_PART.get() as AbstractTatamiPartBlock })
    }

    val COLORED_TATAMI_ITEMS: Map<TatamiColor, RegistryObject<Item>> =
        TatamiColor.COLORED.associateWith { color ->
            ITEMS.register("${color.prefix()}tatami") {
                TatamiItem(Item.Properties(), partBlockProvider = { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock })
            }
        }

    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, RegistryObject<Item>> =
        TatamiColor.COLORED.associateWith { color ->
            ITEMS.register("${color.prefix()}tatami_half") {
                TatamiHalfItem(Item.Properties(), partBlockProvider = { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock })
            }
        }

    fun getTatamiItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_ITEM.get() else COLORED_TATAMI_ITEMS.getValue(color).get()

    fun getTatamiHalfItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_ITEM.get() else COLORED_TATAMI_HALF_ITEMS.getValue(color).get()
}
