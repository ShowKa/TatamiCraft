package com.showka.objects.blocks

import com.showka.objects.FusumaOpenState
import com.showka.objects.FusumaSide
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
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
                .setValue(FLIPPED_HORIZONTAL, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, SIDE, PART_X, PART_Y, DOOR_STATE, FLIPPED_HORIZONTAL)
    }

    companion object {
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
        val SIDE: EnumProperty<FusumaSide> = EnumProperty.create("side", FusumaSide::class.java)
        val PART_X: IntegerProperty = IntegerProperty.create("part_x", 0, 1)
        val PART_Y: IntegerProperty = IntegerProperty.create("part_y", 0, 2)
        val DOOR_STATE: EnumProperty<FusumaOpenState> = EnumProperty.create("door_state", FusumaOpenState::class.java)
        val FLIPPED_HORIZONTAL: BooleanProperty = BooleanProperty.create("flipped_horizontal")

        private val SHAPES_CLOSED: Map<Triple<Direction, FusumaSide, Boolean>, VoxelShape> = mapOf(
            Triple(Direction.NORTH, FusumaSide.RIGHT, false) to box( 0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
            Triple(Direction.NORTH, FusumaSide.LEFT,  false) to box( 0.0, 0.0, 10.0, 16.0, 16.0, 13.0),
            Triple(Direction.SOUTH, FusumaSide.RIGHT, false) to box( 0.0, 0.0,  0.0, 16.0, 16.0,  3.0),
            Triple(Direction.SOUTH, FusumaSide.LEFT,  false) to box( 0.0, 0.0,  3.0, 16.0, 16.0,  6.0),
            Triple(Direction.EAST,  FusumaSide.RIGHT, false) to box( 0.0, 0.0,  0.0,  3.0, 16.0, 16.0),
            Triple(Direction.EAST,  FusumaSide.LEFT,  false) to box( 3.0, 0.0,  0.0,  6.0, 16.0, 16.0),
            Triple(Direction.WEST,  FusumaSide.RIGHT, false) to box(13.0, 0.0,  0.0, 16.0, 16.0, 16.0),
            Triple(Direction.WEST,  FusumaSide.LEFT,  false) to box(10.0, 0.0,  0.0, 13.0, 16.0, 16.0),
            Triple(Direction.NORTH, FusumaSide.LEFT,  true)  to box( 0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
            Triple(Direction.NORTH, FusumaSide.RIGHT, true)  to box( 0.0, 0.0, 10.0, 16.0, 16.0, 13.0),
            Triple(Direction.SOUTH, FusumaSide.LEFT,  true)  to box( 0.0, 0.0,  0.0, 16.0, 16.0,  3.0),
            Triple(Direction.SOUTH, FusumaSide.RIGHT, true)  to box( 0.0, 0.0,  3.0, 16.0, 16.0,  6.0),
            Triple(Direction.EAST,  FusumaSide.LEFT,  true)  to box( 0.0, 0.0,  0.0,  3.0, 16.0, 16.0),
            Triple(Direction.EAST,  FusumaSide.RIGHT, true)  to box( 3.0, 0.0,  0.0,  6.0, 16.0, 16.0),
            Triple(Direction.WEST,  FusumaSide.LEFT,  true)  to box(13.0, 0.0,  0.0, 16.0, 16.0, 16.0),
            Triple(Direction.WEST,  FusumaSide.RIGHT, true)  to box(10.0, 0.0,  0.0, 13.0, 16.0, 16.0),
        )

        private val BREAKING_GROUP: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

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

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape {
        val side = state.getValue(SIDE)
        val doorState = state.getValue(DOOR_STATE)
        if (isBlockInvisible(side, doorState)) return Shapes.empty()
        return SHAPES_CLOSED[Triple(state.getValue(FACING), side, state.getValue(FLIPPED_HORIZONTAL))]
            ?: Shapes.block()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getCollisionShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape {
        val side = state.getValue(SIDE)
        val doorState = state.getValue(DOOR_STATE)
        if (isBlockInvisible(side, doorState)) return Shapes.empty()
        return SHAPES_CLOSED[Triple(state.getValue(FACING), side, state.getValue(FLIPPED_HORIZONTAL))]
            ?: Shapes.block()
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        FusumaBlockEntity(blockEntityTypeProvider(), pos, state)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun use(
        state: BlockState, level: Level, pos: BlockPos, player: Player,
        hand: InteractionHand, hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS

        val facing = state.getValue(FACING)
        val be = level.getBlockEntity(pos) as? FusumaBlockEntity
        val origin = be?.origin ?: reconstructOrigin(state, pos)

        if (player.isCrouching) {
            val newFlipped = !state.getValue(FLIPPED_HORIZONTAL)
            setFlipped(level, origin, facing, newFlipped)
            return InteractionResult.SUCCESS
        }

        val side = state.getValue(SIDE)
        val doorState = state.getValue(DOOR_STATE)
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

    private fun updateAllBlocks(level: Level, origin: BlockPos, facing: Direction, transform: (BlockState) -> BlockState) {
        for (p in getAllPositions(origin, facing)) {
            val s = level.getBlockState(p)
            if (s.`is`(this)) level.setBlock(p, transform(s), UPDATE_CLIENTS)
        }
    }

    private fun setDoorState(level: Level, origin: BlockPos, facing: Direction, newState: FusumaOpenState) =
        updateAllBlocks(level, origin, facing) { it.setValue(DOOR_STATE, newState) }

    private fun setFlipped(level: Level, origin: BlockPos, facing: Direction, flipped: Boolean) =
        updateAllBlocks(level, origin, facing) { it.setValue(FLIPPED_HORIZONTAL, flipped) }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player) {
        if (!level.isClientSide) {
            if (!player.isCreative) {
                popResource(level, pos, ItemStack(dropItemProvider()))
            }
            removeOtherParts(level, pos, state)
            PLAYER_BREAKING.set(true)
        }
        super.playerWillDestroy(level, pos, state, player)
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
