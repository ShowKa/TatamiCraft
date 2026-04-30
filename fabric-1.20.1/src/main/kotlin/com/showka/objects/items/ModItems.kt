package com.showka.objects.items

import com.showka.TatamiCraftModInitializer
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.FusumaPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.items.FusumaItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

/**
 * Fabric 1.20.1 item registration
 */
object ModItems {

    // -- Fusuma --

    val FUSUMA_ITEM: Item = registerItem("fusuma") { props ->
        FusumaItem(props) { ModBlocks.FUSUMA_PART as FusumaPartBlock }
    }

    // -- Default tatami items --

    val TATAMI_ITEM: Item = registerItem("tatami") { props ->
        AbstractTatamiItem.tatami(props) { ModBlocks.TATAMI_PART as AbstractTatamiPartBlock }
    }

    val TATAMI_HALF_ITEM: Item = registerItem("tatami_half") { props ->
        AbstractTatamiItem.tatamiHalf(props) { ModBlocks.TATAMI_HALF_PART as AbstractTatamiPartBlock }
    }

    // -- Color variations --

    val COLORED_TATAMI_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        registerItem(color.tatamiId()) { props ->
            AbstractTatamiItem.tatami(props) { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock }
        }
    }

    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        registerItem(color.tatamiHalfId()) { props ->
            AbstractTatamiItem.tatamiHalf(props) { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock }
        }
    }

    // -- Helpers --

    private fun registerItem(path: String, factory: (Item.Properties) -> Item): Item {
        val id = ResourceLocation(TatamiCraftModInitializer.MOD_ID, path)
        val props = Item.Properties()
        return Registry.register(BuiltInRegistries.ITEM, id, factory(props))
    }

    fun getTatamiItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_ITEM else COLORED_TATAMI_ITEMS.getValue(color)

    fun getTatamiHalfItem(color: TatamiColor): Item =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_ITEM else COLORED_TATAMI_HALF_ITEMS.getValue(color)

    fun init() {
        // Trigger static initialization
    }
}
