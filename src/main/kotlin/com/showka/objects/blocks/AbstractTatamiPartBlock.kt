package com.showka.objects.blocks

import com.showka.util.TatamiLayout
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import org.slf4j.LoggerFactory

/**
 * 畳パーツブロックの抽象基底クラス（カーペット相当の薄型ブロック、高さ 1/16）。
 *
 * BlockState:
 *   FACING   (NORTH / EAST / SOUTH / WEST)
 *   PART     (0..maxPart)
 *   MIRRORED (true/false) — true なら左方向に展開
 *
 * @param layout     畳レイアウト定義（rows × cols）
 * @param partProperty PART の IntProperty（範囲がサイズにより異なる）
 * @param dropItemProvider ドロップするアイテムを返すラムダ
 * @param blockEntityTypeProvider BlockEntityType を返すラムダ（遅延評価で循環参照を回避）
 */
abstract class AbstractTatamiPartBlock(
    settings: Settings,
    private val layout: TatamiLayout,
    private val partProperty: IntProperty,
    private val dropItemProvider: () -> Item,
    private val blockEntityTypeProvider: () -> BlockEntityType<TatamiBlockEntity>
) : Block(settings), BlockEntityProvider {

    companion object {
        private val logger = LoggerFactory.getLogger("tatamicraft")

        val FACING: EnumProperty<Direction> = Properties.HORIZONTAL_FACING
        val MIRRORED: BooleanProperty = BooleanProperty.of("mirrored")

        /** 高さ 1/16 の VoxelShape */
        val SHAPE: VoxelShape = createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)

        /**
         * まとめ破壊の再帰防止ガード。
         * true の間は onStateReplaced 内のグループ破壊処理をスキップする。
         */
        private val BREAKING_GROUP: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

        /**
         * プレイヤー破壊中フラグ（onBreak → onStateReplaced の二重処理防止）。
         */
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    }

    // ── BlockState プロパティ ──────────────────────────

    // 注意: appendProperties() は Block.<init> 中に呼ばれるため、
    // この時点では partProperty フィールドが未初期化（null）になる。
    // そのため appendProperties() と defaultState の初期化は
    // 各サブクラスで直接行う。

    // ── 形状 ──────────────────────────────────────────

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPE

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPE

    // ── 設置条件（カーペット相当） ─────────────────────

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        return !world.getBlockState(pos.down()).isAir
    }

    // ── 隣接ブロック更新（支え喪失で壊れる） ──────────

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState {
        if (direction == Direction.DOWN && !canPlaceAt(state, world, pos)) {
            return Blocks.AIR.defaultState
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random)
    }

    // ── BlockEntity ───────────────────────────────────

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TatamiBlockEntity(blockEntityTypeProvider(), pos, state)
    }

    // ── 破壊ハンドリング ──────────────────────────────

    /**
     * プレイヤーがブロックを壊したとき（ブロックが実際に除去される前）。
     * - サバイバル: 畳アイテムを1つドロップ
     * - クリエ: ドロップしない
     * - 同一セットの他パーツを除去
     */
    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState {
        if (!world.isClient) {
            // サバイバルでのみドロップ
            if (!player.isCreative) {
                dropStack(world, pos, ItemStack(dropItemProvider()))
            }
            // 他パーツを除去
            removeOtherParts(world, pos, state)
            // フラグを立てて onStateReplaced の二重処理を防ぐ
            PLAYER_BREAKING.set(true)
        }
        return super.onBreak(world, pos, state, player)
    }

    /**
     * ブロック状態が変わった（除去された）とき。
     * プレイヤー操作以外（支え喪失など）ではここでグループ破壊 + ドロップを行う。
     */
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onStateReplaced(
        state: BlockState,
        world: ServerWorld,
        pos: BlockPos,
        moved: Boolean
    ) {
        if (!PLAYER_BREAKING.get() && !BREAKING_GROUP.get()) {
            // プレイヤー以外の要因（支え喪失など）
            // ドロップ: 1個
            dropStack(world, pos, ItemStack(dropItemProvider()))
            // 他パーツを除去
            removeOtherParts(world, pos, state)
        }
        if (PLAYER_BREAKING.get()) {
            PLAYER_BREAKING.set(false)
        }
        super.onStateReplaced(state, world, pos, moved)
    }

    /**
     * 同一セットの他のパーツを除去する。
     * BREAKING_GROUP ガードで再帰を防止。
     */
    private fun removeOtherParts(world: WorldAccess, brokenPos: BlockPos, state: BlockState) {
        val be = world.getBlockEntity(brokenPos) as? TatamiBlockEntity
        val origin = be?.origin
        if (origin == null) {
            logger.warn("TatamiBlockEntity at {} has no origin", brokenPos)
            return
        }
        val facing = state.get(FACING)
        val mirrored = state.get(MIRRORED)
        val positions = layout.getAllPartPositions(origin, facing, mirrored)

        BREAKING_GROUP.set(true)
        try {
            for (p in positions) {
                if (p != brokenPos) {
                    val pState = world.getBlockState(p)
                    if (pState.isOf(this)) {
                        // ドロップなしで除去（BlockEntity は super.onStateReplaced が除去する）
                        world.setBlockState(p, Blocks.AIR.defaultState, NOTIFY_ALL)
                    }
                }
            }
        } finally {
            BREAKING_GROUP.set(false)
        }
    }

    // ── ルートテーブルからのドロップを無効化 ─────────

    // ドロップは onBreak / onStateReplaced で手動処理するため、
    // ルートテーブルは空にしておく（リソース側で空の loot_table を定義）。
}
