package com.showka.objects.blocks

import com.showka.objects.FusumaSide
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

/**
 * Fusuma (Japanese sliding door) part block.
 *
 * A full fusuma consists of 12 blocks arranged in a 4-wide × 3-tall grid:
 *   - Left panel:  SIDE=LEFT,  PART_X=0..1, PART_Y=0..2
 *   - Right panel: SIDE=RIGHT, PART_X=0..1, PART_Y=0..2
 *
 * Origin = bottom-left corner of the entire fusuma.
 * Width expands in the FACING.getClockWise() direction.
 *
 * When OPEN=false: thin panel collision shape (3/16 thick).
 * When OPEN=true:  no collision (player can pass through).
 *
 * Opening a panel slides it toward the other panel (visually shown via
 * model elements placed outside the 0–16 local coordinate range).
 */
class FusumaPartBlock(
    properties: Properties,
    private val dropItemProvider: () -> Item,
    private val blockEntityTypeProvider: () -> BlockEntityType<FusumaBlockEntity>
) : Block(properties), EntityBlock {

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SIDE, FusumaSide.LEFT)
                .setValue(PART_X, 0)
                .setValue(PART_Y, 0)
                .setValue(OPEN, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, SIDE, PART_X, PART_Y, OPEN)
    }

    companion object {
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
        val SIDE: EnumProperty<FusumaSide> = EnumProperty.create("side", FusumaSide::class.java)
        val PART_X: IntegerProperty = IntegerProperty.create("part_x", 0, 1)
        val PART_Y: IntegerProperty = IntegerProperty.create("part_y", 0, 2)
        val OPEN: BooleanProperty = BlockStateProperties.OPEN

        // Panel is placed on the near face (the face toward the player who placed it).
        // FACING=NORTH: player faces north → stands south → near face = south (Z=13..16)
        private val SHAPE_NORTH = box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0)
        private val SHAPE_SOUTH = box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0)
        private val SHAPE_EAST  = box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0)
        private val SHAPE_WEST  = box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0)

        private val SHAPES_CLOSED: Map<Direction, VoxelShape> = mapOf(
            Direction.NORTH to SHAPE_NORTH,
            Direction.SOUTH to SHAPE_SOUTH,
            Direction.EAST  to SHAPE_EAST,
            Direction.WEST  to SHAPE_WEST,
        )

        private val BREAKING_GROUP: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

        fun getAllPositions(origin: BlockPos, facing: Direction): List<BlockPos> {
            val right = facing.getClockWise()
            val positions = mutableListOf<BlockPos>()
            for (sideOff in listOf(0, 2)) {
                for (px in 0..1) {
                    for (py in 0..2) {
                        positions.add(origin.relative(right, sideOff + px).above(py))
                    }
                }
            }
            return positions
        }

        fun reconstructOrigin(state: BlockState, pos: BlockPos): BlockPos {
            val facing = state.getValue(FACING)
            val side = state.getValue(SIDE)
            val px = state.getValue(PART_X)
            val py = state.getValue(PART_Y)
            val right = facing.getClockWise()
            val sideOffset = if (side == FusumaSide.LEFT) 0 else 2
            return pos.relative(right.opposite, sideOffset + px).below(py)
        }
    }

    // ── Shape ────────────────────────────────────────────────────────────────

    // Always return a shape for hit detection so the block can be clicked when open.
    override fun getShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = SHAPES_CLOSED[state.getValue(FACING)] ?: Shapes.block()

    // Only block passage when the panel is closed.
    override fun getCollisionShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = if (state.getValue(OPEN)) Shapes.empty()
    else SHAPES_CLOSED[state.getValue(FACING)] ?: Shapes.block()

    // ── BlockEntity ───────────────────────────────────────────────────────────

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        FusumaBlockEntity(blockEntityTypeProvider(), pos, state)

    // ── Interaction (open / close) ────────────────────────────────────────────

    @Suppress("OVERRIDE_DEPRECATION")
    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS

        val side = state.getValue(SIDE)
        val isOpen = state.getValue(OPEN)
        val facing = state.getValue(FACING)

        val be = level.getBlockEntity(pos) as? FusumaBlockEntity
        val origin = be?.origin ?: reconstructOrigin(state, pos)

        // Prevent opening when the other panel is already open
        if (!isOpen) {
            val otherSide = if (side == FusumaSide.LEFT) FusumaSide.RIGHT else FusumaSide.LEFT
            if (isSideOpen(level, origin, facing, otherSide)) return InteractionResult.PASS
        }

        val newOpen = !isOpen
        toggleSide(level, origin, facing, side, newOpen)

        val sound = if (newOpen) SoundEvents.WOODEN_DOOR_OPEN else SoundEvents.WOODEN_DOOR_CLOSE
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f)

        return InteractionResult.SUCCESS
    }

    private fun isSideOpen(level: Level, origin: BlockPos, facing: Direction, side: FusumaSide): Boolean {
        val right = facing.getClockWise()
        val sideOff = if (side == FusumaSide.LEFT) 0 else 2
        for (px in 0..1) for (py in 0..2) {
            val s = level.getBlockState(origin.relative(right, sideOff + px).above(py))
            if (s.`is`(this) && s.getValue(OPEN)) return true
        }
        return false
    }

    private fun toggleSide(level: Level, origin: BlockPos, facing: Direction, side: FusumaSide, open: Boolean) {
        val right = facing.getClockWise()
        val sideOff = if (side == FusumaSide.LEFT) 0 else 2
        for (px in 0..1) for (py in 0..2) {
            val p = origin.relative(right, sideOff + px).above(py)
            val s = level.getBlockState(p)
            if (s.`is`(this) && s.getValue(SIDE) == side) {
                level.setBlock(p, s.setValue(OPEN, open), UPDATE_CLIENTS)
            }
        }
    }

    // ── Break handling ────────────────────────────────────────────────────────

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide) {
            if (!player.isCreative) {
                popResource(level, pos, ItemStack(dropItemProvider()))
            }
            removeOtherParts(level, pos, state)
            PLAYER_BREAKING.set(true)
        }
        return super.playerWillDestroy(level, pos, state, player)
    }

    override fun destroy(level: LevelAccessor, pos: BlockPos, state: BlockState) {
        if (!PLAYER_BREAKING.get() && !BREAKING_GROUP.get()) {
            val origin = reconstructOrigin(state, pos)
            val facing = state.getValue(FACING)
            if (level is Level) {
                popResource(level, pos, ItemStack(dropItemProvider()))
            }
            removeAllWithOrigin(level, pos, origin, facing)
        }
        if (PLAYER_BREAKING.get()) PLAYER_BREAKING.set(false)
        super.destroy(level, pos, state)
    }

    private fun removeOtherParts(level: LevelAccessor, brokenPos: BlockPos, state: BlockState) {
        val be = level.getBlockEntity(brokenPos) as? FusumaBlockEntity
        val origin = be?.origin ?: reconstructOrigin(state, brokenPos)
        removeAllWithOrigin(level, brokenPos, origin, state.getValue(FACING))
    }

    private fun removeAllWithOrigin(
        level: LevelAccessor, brokenPos: BlockPos, origin: BlockPos, facing: Direction
    ) {
        BREAKING_GROUP.set(true)
        try {
            for (p in getAllPositions(origin, facing)) {
                if (p != brokenPos) {
                    val s = level.getBlockState(p)
                    if (s.`is`(this)) level.setBlock(p, Blocks.AIR.defaultBlockState(), UPDATE_ALL)
                }
            }
        } finally {
            BREAKING_GROUP.set(false)
        }
    }
}
