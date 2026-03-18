package com.showka.objects.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

/**
 * Fusuma block.
 *
 * 最小実装方針:
 * - 2ブロック高の襖
 * - BlockEntityは使わない
 * - 右クリックで開閉する
 * - 上下どちらを壊しても両方消える
 * - ドロップはコード側で1個だけ制御する
 *
 * 注意:
 * - この実装は「引き戸」ではなく、Minecraftのdoor相当の簡易挙動
 * - loot table 側は空にしておく前提（Tatami系と同じくコード側でドロップする）
 */
class FusumaBlock(properties: Properties) : Block(properties) {

    companion object {
        /** 襖の向き */
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        /** 開閉状態 */
        val OPEN: BooleanProperty = BlockStateProperties.OPEN

        /** 上下どちらの半分か */
        val HALF: EnumProperty<FusumaHalf> = EnumProperty.create("half", FusumaHalf::class.java)

        /** ヒンジ LEFT / RIGHT */
        val HINGE = BlockStateProperties.DOOR_HINGE

        /**
         * 下半分 / 上半分
         *
         * Minecraft標準の DOUBLE_BLOCK_HALF を使ってもよいが、
         * JSON上で分かりやすくしたいため独自enumにしている。
         */
        enum class FusumaHalf : StringRepresentable {
            LOWER,
            UPPER;

            override fun getSerializedName(): String = name.lowercase()
        }

        /**
         * 閉じたときの薄い板。
         * north/south向きのときはZ方向に薄い板として扱う。
         */
        private val CLOSED_NORTH_SOUTH: VoxelShape = box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0)

        /**
         * 閉じたときの薄い板。
         * east/west向きのときはX方向に薄い板として扱う。
         */
        private val CLOSED_EAST_WEST: VoxelShape = box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0)

        /**
         * ペア破壊中の再帰を防ぐフラグ。
         * 片方を消した結果もう片方の destroy / updateShape が再度走っても、
         * 二重処理しないようにする。
         */
        private val BREAKING_PAIR: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

        /**
         * playerWillDestroy -> destroy の二重処理防止用フラグ。
         */
        private val PLAYER_BREAKING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    }

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HALF, FusumaHalf.LOWER)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, OPEN, HALF, HINGE)
    }

    // ------------------------------------------------------------
    // Shape
    // ------------------------------------------------------------

    /**
     * 見た目と当たり判定を返す。
     *
     * v1ではヒンジ概念を持たないため、
     * OPEN=true のときは単純に板の向きを90度変えるだけにしている。
     */
    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val facing = state.getValue(FACING)
        val open = state.getValue(OPEN)

        return when {
            !open && (facing == Direction.NORTH || facing == Direction.SOUTH) -> CLOSED_NORTH_SOUTH
            !open && (facing == Direction.EAST || facing == Direction.WEST) -> CLOSED_EAST_WEST
            open && (facing == Direction.NORTH || facing == Direction.SOUTH) -> CLOSED_EAST_WEST
            else -> CLOSED_NORTH_SOUTH
        }
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = getShape(state, level, pos, context)

    // ------------------------------------------------------------
    // Placement
    // ------------------------------------------------------------

    /**
     * 設置時のBlockStateを決める。
     *
     * - 常に下半分として設置開始
     * - 上のマスが置換可能でなければ設置不可
     * - 向きはプレイヤーの水平向きに合わせる
     */
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        val pos = context.clickedPos
        val upperPos = pos.above()
        val belowPos = pos.below()

        // ワールド上限チェック
        if (!level.isInWorldBounds(upperPos)) {
            return null
        }

        // 上側に置けないなら設置不可
        if (!level.getBlockState(upperPos).canBeReplaced(context)) {
            return null
        }

        // 下が頑丈でないなら設置不可
        if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
            return null
        }

        // 左右を決める
        val facing = context.horizontalDirection.opposite
        val leftPos = pos.relative(facing.counterClockWise)
        val rightPos = pos.relative(facing.clockWise)
        val hinge = when {
            level.getBlockState(rightPos).block is FusumaBlock -> DoorHingeSide.LEFT
            level.getBlockState(leftPos).block is FusumaBlock -> DoorHingeSide.RIGHT
            else -> DoorHingeSide.LEFT
        }

        // 下半分として設置する
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection)
            .setValue(OPEN, false)
            .setValue(HALF, FusumaHalf.LOWER)
            .setValue(HINGE, hinge)
    }

    /**
     * 下半分が設置された直後に、上半分も自動設置する。
     *
     * BlockItem側で特別な処理をしなくても、通常設置で上下2段になる。
     */
    override fun setPlacedBy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack)

        // サーバー側だけで上半分を置く
        if (level.isClientSide) return

        // すでに上半分なら何もしない（通常は起きないが保険）
        if (state.getValue(HALF) != FusumaHalf.LOWER) return

        val upperPos = pos.above()
        val upperState = state
            .setValue(HALF, FusumaHalf.UPPER)

        level.setBlock(upperPos, upperState, UPDATE_ALL)
    }

    // ------------------------------------------------------------
    // Survival / neighbor update
    // ------------------------------------------------------------

    /**
     * 生存条件。
     *
     * - LOWER: 下に支えが必要、かつ上に自分のUPPERがあること
     * - UPPER: 下に自分のLOWERがあること
     */
    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return when (state.getValue(HALF)) {
            FusumaHalf.LOWER -> {
                val belowPos = pos.below()
                val belowState = level.getBlockState(belowPos)
                // 設置時点では「下にちゃんと支えがあるか」だけ判定する
                belowState.isFaceSturdy(level, belowPos, Direction.UP)
            }

            FusumaHalf.UPPER -> {
                val lowerState = level.getBlockState(pos.below())
                lowerState.`is`(this) && lowerState.getValue(HALF) == FusumaHalf.LOWER
            }
        }
    }

    /**
     * 近傍更新時の崩壊判定。
     *
     * - LOWER は下の支えを失ったら壊れる
     * - LOWER は上半分が消えたら壊れる
     * - UPPER は下半分が消えたら壊れる
     */
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
        val half = state.getValue(HALF)

        if (half == FusumaHalf.LOWER) {
            // 上半分が失われたら下半分も消す
            if (direction == Direction.UP) {
                if (!(neighborState.`is`(this) && neighborState.getValue(HALF) == FusumaHalf.UPPER)) {
                    return Blocks.AIR.defaultBlockState()
                }
            }

            // 支えが失われたら消す
            if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
                return Blocks.AIR.defaultBlockState()
            }
        } else {
            // 上半分は下半分がなければ存在できない
            if (direction == Direction.DOWN) {
                if (!(neighborState.`is`(this) && neighborState.getValue(HALF) == FusumaHalf.LOWER)) {
                    return Blocks.AIR.defaultBlockState()
                }
            }
        }

        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random)
    }

    // ------------------------------------------------------------
    // Interaction
    // ------------------------------------------------------------

    /**
     * 右クリックで開閉する。
     *
     * クリックした側が上下どちらでも、必ず下半分を基準にして上下のOPEN状態を同時に切り替える。
     * 隣にペアとなるブロックがあれば、それも同時にOPEN状態を切り替える。
     */
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {

        // クライアントでも成功を返してアニメーション/手応えを出す
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        // 上半分を右クリックした場合、下半分に処理を委譲する
        if (state.getValue(HALF) == FusumaHalf.UPPER) {
            val below = pos.below()
            return useWithoutItem(level.getBlockState(below), level, below, player, hit)
        }

        // 下半分、上半分ともに開閉
        val newState = state.cycle(OPEN)
        level.setBlock(pos, newState, 10)
        level.setBlock(pos.above(), newState.setValue(HALF, FusumaHalf.UPPER), 10)

        // 隣も同期
        val facing = state.getValue(FACING)
        val hinge = state.getValue(HINGE)

        val neighborPos = if (hinge == DoorHingeSide.LEFT) {
            pos.relative(facing.clockWise)
        } else {
            pos.relative(facing.counterClockWise)
        }

        val neighborState = level.getBlockState(neighborPos)

        if (neighborState.block is FusumaBlock) {
            val neighborNew = neighborState.setValue(OPEN, newState.getValue(OPEN))
            level.setBlock(neighborPos, neighborNew, 10)
            level.setBlock(neighborPos.above(), neighborNew.setValue(HALF, FusumaHalf.UPPER), 10)
        }

        return InteractionResult.SUCCESS
    }

    // ------------------------------------------------------------
    // Rotation / mirror
    // ------------------------------------------------------------

    /**
     * Structure block / world edit / blockstate rotation対応。
     */
    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)))
    }

    /**
     * ミラー時は回転に変換する。
     */
    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(FACING)))
    }

    // ------------------------------------------------------------
    // Break handling
    // ------------------------------------------------------------

    /**
     * プレイヤー破壊時の処理。
     *
     * - survival: 襖アイテムを1個だけドロップ
     * - creative: ドロップしない
     * - 上下のもう片方も同時に削除
     *
     * loot table 側で二重ドロップを避けるため、
     * Fusumaはコード側でドロップを制御する。
     */
    override fun playerWillDestroy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        player: Player
    ): BlockState {
        if (!level.isClientSide) {
            val lowerPos = if (state.getValue(HALF) == FusumaHalf.LOWER) pos else pos.below()

            // サバイバル時のみ1個ドロップ
            if (!player.isCreative) {
                // TODO popResource(level, lowerPos, ItemStack(ModItems.FUSUMA_ITEM))
            }

            // 対になる半分を消す
            removeOtherHalf(level, pos, state)

            // このあと destroy でも呼ばれるため、二重処理防止フラグを立てる
            PLAYER_BREAKING.set(true)
        }

        return super.playerWillDestroy(level, pos, state, player)
    }

    /**
     * 非プレイヤー破壊時の処理。
     *
     * 例:
     * - 支え喪失
     * - コマンドによる削除
     * - 何らかの近傍崩壊
     *
     * playerWillDestroy 経由でない場合だけ、ここでドロップと対消滅を処理する。
     */
    override fun destroy(level: LevelAccessor, pos: BlockPos, state: BlockState) {
        if (!PLAYER_BREAKING.get() && !BREAKING_PAIR.get()) {
            val lowerPos = if (state.getValue(HALF) == FusumaHalf.LOWER) pos else pos.below()

            // 1個だけドロップ
            if (level is Level) {
                // TODO popResource(level, lowerPos, ItemStack(ModItems.FUSUMA_ITEM))
            }

            // 対になる半分も消す
            removeOtherHalf(level, pos, state)
        }

        if (PLAYER_BREAKING.get()) {
            PLAYER_BREAKING.set(false)
        }

        super.destroy(level, pos, state)
    }

    /**
     * もう片方の半分を削除する。
     *
     * BREAKING_PAIRフラグを使って、setBlock(AIR) に伴う再帰的な destroy 呼び出しを防ぐ。
     */
    private fun removeOtherHalf(level: LevelAccessor, brokenPos: BlockPos, state: BlockState) {
        val otherPos = if (state.getValue(HALF) == FusumaHalf.LOWER) brokenPos.above() else brokenPos.below()
        val otherState = level.getBlockState(otherPos)

        if (!otherState.`is`(this)) return

        BREAKING_PAIR.set(true)
        try {
            level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), UPDATE_ALL)
        } finally {
            BREAKING_PAIR.set(false)
        }
    }
}