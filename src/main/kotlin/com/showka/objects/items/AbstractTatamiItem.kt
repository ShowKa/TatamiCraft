package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiBlockEntity
import com.showka.util.TatamiLayout
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.state.property.IntProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.LoggerFactory

/**
 * 畳アイテムの抽象基底クラス。
 * 右クリックで layout に応じたマルチブロックを設置する。
 *
 * @param layout     畳レイアウト定義（rows × cols）
 * @param partBlockProvider 設置するパーツブロックを返すラムダ（遅延評価）
 * @param partProperty PART の IntProperty
 */
abstract class AbstractTatamiItem(
    settings: Settings,
    private val layout: TatamiLayout,
    private val partBlockProvider: () -> AbstractTatamiPartBlock,
    private val partProperty: IntProperty
) : Item(settings) {

    companion object {
        private val logger = LoggerFactory.getLogger("tatamicraft")
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val player = context.player ?: return ActionResult.FAIL
        val clickedPos = context.blockPos
        val side = context.side

        // 設置位置の決定（クリックした面の隣、または replaceable なら同位置）
        val placePos = if (world.getBlockState(clickedPos).isReplaceable) {
            clickedPos
        } else {
            clickedPos.offset(side)
        }

        // プレイヤーの水平向き
        val facing = player.horizontalFacing
        val origin = placePos

        // ── まず右方向（CW）で試し、ダメなら左方向（CCW）にフォールバック ──
        val rightPositions = layout.getAllPartPositions(origin, facing, mirrored = false)
        val leftPositions = layout.getAllPartPositions(origin, facing, mirrored = true)

        val (positions, mirrored) = when {
            canPlaceAll(world, rightPositions) -> Pair(rightPositions, false)
            canPlaceAll(world, leftPositions)  -> Pair(leftPositions, true)
            else -> return ActionResult.FAIL
        }

        // ── サーバ側で配置 ──
        if (!world.isClient) {
            val partBlock = partBlockProvider()
            for ((index, p) in positions.withIndex()) {
                val state = partBlock.defaultState
                    .with(AbstractTatamiPartBlock.FACING, facing)
                    .with(partProperty, index)
                    .with(AbstractTatamiPartBlock.MIRRORED, mirrored)
                world.setBlockState(p, state, Block.NOTIFY_ALL)

                // BlockEntity に origin をセット
                val be = world.getBlockEntity(p) as? TatamiBlockEntity
                if (be != null) {
                    be.origin = origin
                    be.markDirty()
                } else {
                    logger.warn("TatamiBlockEntity not found at {}", p)
                }
            }
        }

        // サバイバルではアイテムを1消費
        if (!player.isCreative) {
            context.stack.decrement(1)
        }

        return ActionResult.SUCCESS
    }

    /**
     * 全座標が配置可能かチェック。
     */
    private fun canPlaceAll(world: World, positions: List<BlockPos>): Boolean {
        for (p in positions) {
            if (!world.isInBuildLimit(p)) return false
            if (!world.getBlockState(p).isReplaceable) return false
            if (world.getBlockState(p.down()).isAir) return false
        }
        return true
    }
}
