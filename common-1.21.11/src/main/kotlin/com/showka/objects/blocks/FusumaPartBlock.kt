package com.showka.objects.blocks

import com.showka.objects.FusumaOpenState
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
 * DOOR_STATE tracks the open/close state of the whole fusuma:
 *   - CLOSED:     both panels visible and solid
 *   - LEFT_OPEN:  left panel invisible/passable; right side shows 2-panel overlap
 *   - RIGHT_OPEN: right panel invisible/passable; left side shows 2-panel overlap
 *
 * RIGHT panel = front channel (Z=13..16 for FACING=NORTH).
 * LEFT panel  = back channel  (Z=10..13 for FACING=NORTH).
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
                .setValue(DOOR_STATE, FusumaOpenState.CLOSED)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, SIDE, PART_X, PART_Y, DOOR_STATE)
    }

    companion object {
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
        val SIDE: EnumProperty<FusumaSide> = EnumProperty.create("side", FusumaSide::class.java)
        val PART_X: IntegerProperty = IntegerProperty.create("part_x", 0, 1)
        val PART_Y: IntegerProperty = IntegerProperty.create("part_y", 0, 2)
        val DOOR_STATE: EnumProperty<FusumaOpenState> = EnumProperty.create("door_state", FusumaOpenState::class.java)

        // RIGHT panel = front channel; LEFT panel = back channel (3 px behind).
        private val SHAPES_CLOSED: Map<Pair<Direction, FusumaSide>, VoxelShape> = mapOf(
            Pair(Direction.NORTH, FusumaSide.RIGHT) to box( 0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
            Pair(Direction.NORTH, FusumaSide.LEFT)  to box( 0.0, 0.0, 10.0, 16.0, 16.0, 13.0),
            Pair(Direction.SOUTH, FusumaSide.RIGHT) to box( 0.0, 0.0,  0.0, 16.0, 16.0,  3.0),
            Pair(Direction.SOUTH, FusumaSide.LEFT)  to box( 0.0, 0.0,  3.0, 16.0, 16.0,  6.0),
            Pair(Direction.EAST,  FusumaSide.RIGHT) to box( 0.0, 0.0,  0.0,  3.0, 16.0, 16.0),
            Pair(Direction.EAST,  FusumaSide.LEFT)  to box( 3.0, 0.0,  0.0,  6.0, 16.0, 16.0),
            Pair(Direction.WEST,  FusumaSide.RIGHT) to box(13.0, 0.0,  0.0, 16.0, 16.0, 16.0),
            Pair(Direction.WEST,  FusumaSide.LEFT)  to box(10.0, 0.0,  0.0, 13.0, 16.0, 16.0),
        )

        private val BREAKING_GROUP: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

        /** Returns true when this block position has no visible panel (the open-side original location). */
        private fun isBlockInvisible(side: FusumaSide, doorState: FusumaOpenState): Boolean =
            (side == FusumaSide.LEFT  && doorState == FusumaOpenState.LEFT_OPEN) ||
            (side == FusumaSide.RIGHT && doorState == FusumaOpenState.RIGHT_OPEN)

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

    override fun getShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape {
        val side = state.getValue(SIDE)
        val doorState = state.getValue(DOOR_STATE)
        if (isBlockInvisible(side, doorState)) return Shapes.empty()
        return SHAPES_CLOSED[Pair(state.getValue(FACING), side)] ?: Shapes.block()
    }

    override fun getCollisionShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape {
        val side = state.getValue(SIDE)
        val doorState = state.getValue(DOOR_STATE)
        if (isBlockInvisible(side, doorState)) return Shapes.empty()
        return SHAPES_CLOSED[Pair(state.getValue(FACING), side)] ?: Shapes.block()
    }

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
        val doorState = state.getValue(DOOR_STATE)
        val facing = state.getValue(FACING)

        val be = level.getBlockEntity(pos) as? FusumaBlockEntity
        val origin = be?.origin ?: reconstructOrigin(state, pos)

        val newDoorState = when (doorState) {
            FusumaOpenState.CLOSED ->
                if (side == FusumaSide.LEFT) FusumaOpenState.LEFT_OPEN else FusumaOpenState.RIGHT_OPEN
            else -> FusumaOpenState.CLOSED
        }

        setDoorState(level, origin, facing, newDoorState)

        val sound = if (newDoorState != FusumaOpenState.CLOSED) SoundEvents.WOODEN_DOOR_OPEN else SoundEvents.WOODEN_DOOR_CLOSE
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f)

        return InteractionResult.SUCCESS
    }

    private fun setDoorState(level: Level, origin: BlockPos, facing: Direction, newState: FusumaOpenState) {
        val right = facing.getClockWise()
        for (sideOff in listOf(0, 2)) {
            for (px in 0..1) {
                for (py in 0..2) {
                    val p = origin.relative(right, sideOff + px).above(py)
                    val s = level.getBlockState(p)
                    if (s.`is`(this)) {
                        level.setBlock(p, s.setValue(DOOR_STATE, newState), UPDATE_CLIENTS)
                    }
                }
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
