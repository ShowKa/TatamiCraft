package com.showka.objects.blocks

import com.showka.TatamiCraftModInitializer
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

/**
 * Modで作るブロック
 */
object ModBlocks {

    // 畳パーツブロック（薄型、カーペット相当）
    // BlockItem は作らない（設置は TatamiItem で行う）
    val TATAMI_PART: Block = registerBlockOnly(
        path = "tatami_part",
        factory = { settings -> TatamiPartBlock(settings) },
        settings = AbstractBlock.Settings.create()
            .strength(0.1f)
            .sounds(BlockSoundGroup.WOOL)
            .noCollision()
    )

    // 半畳パーツブロック（薄型、カーペット相当、2×2）
    // BlockItem は作らない（設置は TatamiHalfItem で行う）
    val TATAMI_HALF_PART: Block = registerBlockOnly(
        path = "tatami_half_part",
        factory = { settings -> TatamiHalfPartBlock(settings) },
        settings = AbstractBlock.Settings.create()
            .strength(0.1f)
            .sounds(BlockSoundGroup.WOOL)
            .noCollision()
    )

    /** Block + BlockItem の登録 */
    private fun register(
        path: String,
        factory: (AbstractBlock.Settings) -> Block,
        settings: AbstractBlock.Settings
    ): Block {
        val id = Identifier.of(TatamiCraftModInitializer.MOD_ID, path)
        val key: RegistryKey<Block> = RegistryKey.of(RegistryKeys.BLOCK, id)
        // ★ 1.21.2+ ではこれが Settings に registry key を書き込んでからブロック生成＆登録してくれる
        val block = Blocks.register(key, factory, settings)
        // ★ ついでに同じIDの BlockItem を作って登録してくれる
        Items.register(block)
        return block
    }

    /** Block のみ登録（BlockItem は別途登録する場合） */
    private fun registerBlockOnly(
        path: String,
        factory: (AbstractBlock.Settings) -> Block,
        settings: AbstractBlock.Settings
    ): Block {
        val id = Identifier.of(TatamiCraftModInitializer.MOD_ID, path)
        val key: RegistryKey<Block> = RegistryKey.of(RegistryKeys.BLOCK, id)
        return Blocks.register(key, factory, settings)
    }

    // クラスの静的初期化を起こすために呼ぶだけ（中身は空でOK）
    fun init() {
    }
}
