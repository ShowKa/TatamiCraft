package com.showka.objects.blocks

import com.showka.util.TatamiLayout
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
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
 * Abstract base class for tatami part blocks (carpet-like thin blocks, height 1/16).
 *
 * BlockState:
 *   FACING   (NORTH / EAST / SOUTH / WEST)
 *   PART     (0..maxPart)
 *   MIRRORED (true/false) - true = expand left
 *
 * @param layout     Tatami layout definition (rows x cols)
 * @param partProperty PART IntegerProperty (range varies by size)
 * @param dropItemProvider Lambda returning the item to drop
 * @param blockEntityTypeProvider Lambda returning BlockEntityType (lazy to avoid circular refs)
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

        /** 1/16 height VoxelShape */
        val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)

        /**
         * Guard to prevent recursive group breaking.
         * While true, destroy skips group break processing.
         */
        private val BREAKING_GROUP: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

        /**
         * Player breaking flag (prevents double processing between playerWillDestroy -> destroy).
         */
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    }

    // -- Shape --

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

    // -- Placement condition (carpet-like) --

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return !level.getBlockState(pos.below()).isAir
    }

    // -- Neighbor update (breaks when support is lost) --

    @Suppress("OVERRIDE_DEPRECATION")
    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        tickAccess: ScheduledTickAccess,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: RandomSource
    ): BlockState {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState()
        }
        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random)
    }

    // -- BlockEntity --

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TatamiBlockEntity(blockEntityTypeProvider(), pos, state)
    }

    // -- Break handling --

    /**
     * When player breaks the block (before block is actually removed).
     * - Survival: drop 1 tatami item
     * - Creative: no drop
     * - Remove other parts of the same set
     */
    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide) {
            // Only drop in survival
            if (!player.isCreative) {
                popResource(level, pos, ItemStack(dropItemProvider()))
            }
            // Remove other parts (BlockEntity still available here)
            removeOtherParts(level, pos, state)
            // Set flag to prevent double processing in destroy
            PLAYER_BREAKING.set(true)
        }
        return super.playerWillDestroy(level, pos, state, player)
    }

    /**
     * Called after the block has been removed from the world.
     * For non-player removal (support loss etc.), handle group break + drop here.
     * Note: BlockEntity is gone at this point, so we reconstruct origin from block state.
     */
    override fun destroy(level: LevelAccessor, pos: BlockPos, state: BlockState) {
        if (!PLAYER_BREAKING.get() && !BREAKING_GROUP.get()) {
            // Non-player removal (e.g., support loss)
            // Reconstruct origin from block state data
            val partIndex = state.getValue(partProperty)
            val facing = state.getValue(FACING)
            val mirrored = state.getValue(MIRRORED)
            val origin = layout.getOriginFromPartPosition(pos, partIndex, facing, mirrored)

            // Drop 1 item
            if (level is Level) {
                popResource(level, pos, ItemStack(dropItemProvider()))
            }

            // Remove other parts
            removeOtherPartsWithOrigin(level, pos, origin, facing, mirrored)
        }
        if (PLAYER_BREAKING.get()) {
            PLAYER_BREAKING.set(false)
        }
        super.destroy(level, pos, state)
    }

    /**
     * Remove other parts using BlockEntity origin data.
     * Used from playerWillDestroy where BE is still available.
     */
    private fun removeOtherParts(level: LevelAccessor, brokenPos: BlockPos, state: BlockState) {
        val be = level.getBlockEntity(brokenPos) as? TatamiBlockEntity
        val origin = be?.origin
        if (origin == null) {
            // Fallback: reconstruct from block state
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

    /**
     * Remove other parts given the origin, facing and mirrored state.
     * Uses BREAKING_GROUP guard to prevent recursion.
     */
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

    // Drops are manually handled in playerWillDestroy / destroy,
    // so loot tables should be empty (defined in resources).
}
