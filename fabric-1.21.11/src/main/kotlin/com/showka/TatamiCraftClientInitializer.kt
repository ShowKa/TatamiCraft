package com.showka

import com.showka.objects.blocks.ModBlocks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

@Environment(EnvType.CLIENT)
object TatamiCraftClientInitializer : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.FROSTED_GLASS_SLIDING_DOOR_PART, ChunkSectionLayer.TRANSLUCENT)
        BlockRenderLayerMap.putBlock(ModBlocks.SLIDING_WINDOW_PART, ChunkSectionLayer.TRANSLUCENT)
    }
}
