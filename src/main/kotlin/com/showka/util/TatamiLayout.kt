package com.showka.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * 畳マルチブロックのレイアウト定義。
 *
 * rows × cols のグリッドを、ベッド方式（クリック位置を手前側）で配置する。
 *
 * - forward = facing（奥方向）
 * - right   = facing.rotateYClockwise()（通常）
 *             facing.rotateYCounterclockwise()（mirrored=true: 左に広げる）
 * - pos(row, col) = origin + forward*row + right*col
 * - PART = row * cols + col
 *
 * @param rows 奥行き方向のマス数
 * @param cols 横方向のマス数
 */
class TatamiLayout(val rows: Int, val cols: Int) {

    val totalParts: Int = rows * cols

    /**
     * origin + facing から全座標を PART 順で返す。
     *
     * @param origin   プレイヤーがクリックした位置（手前の基準点）
     * @param facing   プレイヤーの視線方向（奥方向）
     * @param mirrored false=右に広げる（デフォルト）、true=左に広げる
     */
    fun getAllPartPositions(origin: BlockPos, facing: Direction, mirrored: Boolean = false): List<BlockPos> {
        val safeFacing = if (facing.axis.isHorizontal) facing else Direction.NORTH
        val forward = safeFacing
        val right = if (mirrored) safeFacing.rotateYCounterclockwise() else safeFacing.rotateYClockwise()

        val positions = mutableListOf<BlockPos>()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val p = origin
                    .offset(forward, row)
                    .offset(right, col)
                positions.add(p)
            }
        }
        return positions
    }

    /**
     * PART 番号から (row, col) を返す。
     */
    fun partToRowCol(part: Int): Pair<Int, Int> {
        val row = part / cols
        val col = part % cols
        return Pair(row, col)
    }

    companion object {
        /** 2×4 畳（8枚） */
        val TATAMI = TatamiLayout(rows = 4, cols = 2)

        /** 2×2 半畳（4枚） */
        val TATAMI_HALF = TatamiLayout(rows = 2, cols = 2)
    }
}
