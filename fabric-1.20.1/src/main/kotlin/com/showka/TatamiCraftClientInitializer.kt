package com.showka

import com.showka.objects.blocks.ModBlocks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType

@Environment(EnvType.CLIENT)
object TatamiCraftClientInitializer : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FROSTED_GLASS_SLIDING_DOOR_PART, RenderType.translucent())
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIDING_WINDOW_PART, RenderType.translucent())
    }
}
