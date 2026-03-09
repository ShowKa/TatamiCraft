package com.showka.objects.blocks

import com.showka.TatamiCraftModInitializer
import com.showka.objects.TatamiColor
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

    // ── デフォルト畳 ──────────────────────────────────

    // 畳パーツブロック（薄型、カーペット相当）
    // BlockItem は作らない（設置は TatamiItem で行う）
    val TATAMI_PART: Block = registerBlockOnly(
        path = "tatami_part",
        factory = { settings -> TatamiPartBlock(settings) },
        settings = tatamiSettings()
    )

    // 半畳パーツブロック（薄型、カーペット相当、2×2）
    // BlockItem は作らない（設置は TatamiHalfItem で行う）
    val TATAMI_HALF_PART: Block = registerBlockOnly(
        path = "tatami_half_part",
        factory = { settings -> TatamiHalfPartBlock(settings) },
        settings = tatamiSettings()
    )

    // ── カラーバリエーション ──────────────────────────

    /** 色付き畳パーツブロックのマップ（色 → Block） */
    val COLORED_TATAMI_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlockOnly(
            path = "${color.prefix()}tatami_part",
            factory = { settings -> TatamiPartBlock(settings) },
            settings = tatamiSettings()
        )
    }

    /** 色付き半畳パーツブロックのマップ（色 → Block） */
    val COLORED_TATAMI_HALF_PARTS: Map<TatamiColor, Block> = TatamiColor.COLORED.associateWith { color ->
        registerBlockOnly(
            path = "${color.prefix()}tatami_half_part",
            factory = { settings -> TatamiHalfPartBlock(settings) },
            settings = tatamiSettings()
        )
    }

    // ── ヘルパー ─────────────────────────────────────

    /** 畳ブロック共通の Settings */
    private fun tatamiSettings(): AbstractBlock.Settings =
        AbstractBlock.Settings.create()
            .strength(0.1f)
            .sounds(BlockSoundGroup.WOOL)
            .noCollision()

    /** Block + BlockItem の登録 */
    private fun register(
        path: String,
        factory: (AbstractBlock.Settings) -> Block,
        settings: AbstractBlock.Settings
    ): Block {
        val id = Identifier.of(TatamiCraftModInitializer.MOD_ID, path)
        val key: RegistryKey<Block> = RegistryKey.of(RegistryKeys.BLOCK, id)
        val block = Blocks.register(key, factory, settings)
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

    /** 指定色の畳パーツブロックを取得（DEFAULT ならデフォルトを返す） */
    fun getTatamiPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_PART else COLORED_TATAMI_PARTS.getValue(color)

    /** 指定色の半畳パーツブロックを取得（DEFAULT ならデフォルトを返す） */
    fun getTatamiHalfPart(color: TatamiColor): Block =
        if (color == TatamiColor.DEFAULT) TATAMI_HALF_PART else COLORED_TATAMI_HALF_PARTS.getValue(color)

    /** 全畳パーツブロック（デフォルト＋カラー）のリスト */
    fun allTatamiParts(): List<Block> =
        listOf(TATAMI_PART) + COLORED_TATAMI_PARTS.values

    /** 全半畳パーツブロック（デフォルト＋カラー）のリスト */
    fun allTatamiHalfParts(): List<Block> =
        listOf(TATAMI_HALF_PART) + COLORED_TATAMI_HALF_PARTS.values

    // クラスの静的初期化を起こすために呼ぶだけ（中身は空でOK）
    fun init() {
    }
}
