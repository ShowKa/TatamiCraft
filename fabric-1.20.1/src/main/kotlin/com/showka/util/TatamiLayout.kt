package com.showka.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

/**
 * Tatami multi-block layout definition (1.20.1 compatible).
 */
class TatamiLayout(val rows: Int, val cols: Int) {

    val totalParts: Int = rows * cols

    fun getAllPartPositions(origin: BlockPos, facing: Direction, mirrored: Boolean = false): List<BlockPos> {
        val safeFacing = if (facing.axis.isHorizontal) facing else Direction.NORTH
        val forward = safeFacing
        val right = if (mirrored) safeFacing.counterClockWise else safeFacing.clockWise

        val positions = mutableListOf<BlockPos>()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val p = origin
                    .relative(forward, row)
                    .relative(right, col)
                positions.add(p)
            }
        }
        return positions
    }

    fun partToRowCol(part: Int): Pair<Int, Int> {
        val row = part / cols
        val col = part % cols
        return Pair(row, col)
    }

    fun getOriginFromPartPosition(partPos: BlockPos, partIndex: Int, facing: Direction, mirrored: Boolean): BlockPos {
        val safeFacing = if (facing.axis.isHorizontal) facing else Direction.NORTH
        val forward = safeFacing
        val right = if (mirrored) safeFacing.counterClockWise else safeFacing.clockWise
        val (row, col) = partToRowCol(partIndex)
        return partPos
            .relative(forward, -row)
            .relative(right, -col)
    }

    companion object {
        val TATAMI = TatamiLayout(rows = 4, cols = 2)
        val TATAMI_HALF = TatamiLayout(rows = 2, cols = 2)
    }
}
