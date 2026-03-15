package com.showka.objects.items

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

/**
 * Fabric 1.20.1 item registration
 */
object ModItems {

    val TATAMI_ITEM: Item = registerItem("tatami") { props ->
        TatamiItem(props, partBlockProvider = { ModBlocks.TATAMI_PART as AbstractTatamiPartBlock })
    }

    val TATAMI_HALF_ITEM: Item = registerItem("tatami_half") { props ->
        TatamiHalfItem(props, partBlockProvider = { ModBlocks.TATAMI_HALF_PART as AbstractTatamiPartBlock })
    }

    val COLORED_TATAMI_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        registerItem("${color.prefix()}tatami") { props ->
            TatamiItem(props, partBlockProvider = { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock })
        }
    }

    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        registerItem("${color.prefix()}tatami_half") { props ->
            TatamiHalfItem(props, partBlockProvider = { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock })
        }
    }

    private fun registerItem(path: String, factory: (Item.Properties) -> Item): Item {
        val id = ResourceLocation(TatamiCraftConstants.MOD_ID, path)
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
