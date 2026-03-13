package com.showka.objects.blocks

import com.showka.util.TatamiLayout
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.slf4j.LoggerFactory

/**
 * Abstract base class for tatami part blocks (1.20.1 Forge compatible).
 */
abstract class AbstractTatamiPartBlock(
    properties: Properties,
    private val layout: TatamiLayout,
    private val partProperty: IntegerProperty,
    private val dropItemProvider: () -> Item,
    private val blockEntityTypeProvider: () -> BlockEntityType<TatamiBlockEntity>
) : Block(properties), EntityBlock {

    companion object {
        private val logger = LoggerFactory.getLogger("tatamicraft")

        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
        val MIRRORED: BooleanProperty = BooleanProperty.create("mirrored")

        val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)

        private val BREAKING_GROUP: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return !level.getBlockState(pos.below()).isAir
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState()
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TatamiBlockEntity(blockEntityTypeProvider(), pos, state)
    }

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
            val partIndex = state.getValue(partProperty)
            val facing = state.getValue(FACING)
            val mirrored = state.getValue(MIRRORED)
            val origin = layout.getOriginFromPartPosition(pos, partIndex, facing, mirrored)

            if (level is Level) {
                popResource(level, pos, ItemStack(dropItemProvider()))
            }

            removeOtherPartsWithOrigin(level, pos, origin, facing, mirrored)
        }
        if (PLAYER_BREAKING.get()) {
            PLAYER_BREAKING.set(false)
        }
        super.destroy(level, pos, state)
    }

    private fun removeOtherParts(level: LevelAccessor, brokenPos: BlockPos, state: BlockState) {
        val be = level.getBlockEntity(brokenPos) as? TatamiBlockEntity
        val origin = be?.origin
        if (origin == null) {
            val partIndex = state.getValue(partProperty)
            val facing = state.getValue(FACING)
            val mirrored = state.getValue(MIRRORED)
            val reconstructedOrigin = layout.getOriginFromPartPosition(brokenPos, partIndex, facing, mirrored)
            removeOtherPartsWithOrigin(level, brokenPos, reconstructedOrigin, facing, mirrored)
            return
        }
        val facing = state.getValue(FACING)
        val mirrored = state.getValue(MIRRORED)
        removeOtherPartsWithOrigin(level, brokenPos, origin, facing, mirrored)
    }

    private fun removeOtherPartsWithOrigin(
        level: LevelAccessor,
        brokenPos: BlockPos,
        origin: BlockPos,
        facing: Direction,
        mirrored: Boolean
    ) {
        val positions = layout.getAllPartPositions(origin, facing, mirrored)

        BREAKING_GROUP.set(true)
        try {
            for (p in positions) {
                if (p != brokenPos) {
                    val pState = level.getBlockState(p)
                    if (pState.`is`(this)) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), UPDATE_ALL)
                    }
                }
            }
        } finally {
            BREAKING_GROUP.set(false)
        }
    }
}
