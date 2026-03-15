package com.showka.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.IntegerProperty

/**
 * Tatami multi-block layout definition.
 *
 * rows x cols grid, placed in bed-style (click position = front side).
 *
 * - forward = facing (back direction)
 * - right   = facing.getClockWise() (normal)
 *             facing.getCounterClockWise() (mirrored=true: expand left)
 * - pos(row, col) = origin + forward*row + right*col
 * - PART = row * cols + col
 *
 * @param rows Depth direction block count
 * @param cols Width direction block count
 */
class TatamiLayout(val rows: Int, val cols: Int) {

    val totalParts: Int = rows * cols
    val partProperty: IntegerProperty = IntegerProperty.create("part", 0, totalParts - 1)

    /**
     * Returns all positions in PART order from origin + facing.
     *
     * @param origin   Player click position (front reference point)
     * @param facing   Player's view direction (back direction)
     * @param mirrored false=expand right (default), true=expand left
     */
    fun getAllPartPositions(origin: BlockPos, facing: Direction, mirrored: Boolean = false): List<BlockPos> {
        val safeFacing = if (facing.axis.isHorizontal) facing else Direction.NORTH
        val forward = safeFacing
        val right = if (mirrored) safeFacing.getCounterClockWise() else safeFacing.getClockWise()

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

    /**
     * Returns (row, col) from PART number.
     */
    fun partToRowCol(part: Int): Pair<Int, Int> {
        val row = part / cols
        val col = part % cols
        return Pair(row, col)
    }

    /**
     * Reconstructs origin from a part's position and its block state data.
     * This is the inverse of getAllPartPositions.
     */
    fun getOriginFromPartPosition(partPos: BlockPos, partIndex: Int, facing: Direction, mirrored: Boolean): BlockPos {
        val safeFacing = if (facing.axis.isHorizontal) facing else Direction.NORTH
        val forward = safeFacing
        val right = if (mirrored) safeFacing.getCounterClockWise() else safeFacing.getClockWise()
        val (row, col) = partToRowCol(partIndex)
        // origin = partPos - forward*row - right*col
        return partPos
            .relative(forward, -row)
            .relative(right, -col)
    }

    companion object {
        /** 2x4 tatami (8 parts) */
        val TATAMI = TatamiLayout(rows = 4, cols = 2)

        /** 2x2 half tatami (4 parts) */
        val TATAMI_HALF = TatamiLayout(rows = 2, cols = 2)
    }
}
