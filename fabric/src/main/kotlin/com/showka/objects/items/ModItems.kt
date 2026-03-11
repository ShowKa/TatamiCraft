package com.showka.objects.items

import com.showka.TatamiCraftConstants
import com.showka.objects.TatamiColor
import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.blocks.TatamiHalfPartBlock
import com.showka.objects.blocks.TatamiPartBlock
import com.showka.util.TatamiLayout
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

/**
 * Fabric item registration
 */
object ModItems {

    // -- Default tatami items --

    val TATAMI_ITEM: Item = registerItem("tatami") { props ->
        TatamiItem(
            props,
            partBlockProvider = { ModBlocks.TATAMI_PART as AbstractTatamiPartBlock }
        )
    }

    val TATAMI_HALF_ITEM: Item = registerItem("tatami_half") { props ->
        TatamiHalfItem(
            props,
            partBlockProvider = { ModBlocks.TATAMI_HALF_PART as AbstractTatamiPartBlock }
        )
    }

    // -- Color variations --

    val COLORED_TATAMI_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        registerItem("${color.prefix()}tatami") { props ->
            AbstractTatamiItem.create(
                properties = props,
                layout = TatamiLayout.TATAMI,
                partBlockProvider = { ModBlocks.getTatamiPart(color) as AbstractTatamiPartBlock },
                partProperty = TatamiPartBlock.PART
            )
        }
    }

    val COLORED_TATAMI_HALF_ITEMS: Map<TatamiColor, Item> = TatamiColor.COLORED.associateWith { color ->
        registerItem("${color.prefix()}tatami_half") { props ->
            AbstractTatamiItem.create(
                properties = props,
                layout = TatamiLayout.TATAMI_HALF,
                partBlockProvider = { ModBlocks.getTatamiHalfPart(color) as AbstractTatamiPartBlock },
                partProperty = TatamiHalfPartBlock.PART
            )
        }
    }

    // -- Helpers --

    private fun registerItem(path: String, factory: (Item.Properties) -> Item): Item {
        val id = Identifier.fromNamespaceAndPath(TatamiCraftConstants.MOD_ID, path)
        val key = ResourceKey.create(Registries.ITEM, id)
        val props = Item.Properties().useItemDescriptionPrefix().setId(key)
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
