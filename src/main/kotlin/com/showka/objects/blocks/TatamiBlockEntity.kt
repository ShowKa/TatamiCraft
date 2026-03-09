package com.showka.objects.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.math.BlockPos

/**
 * 畳パーツ用 BlockEntity（畳・半畳共通）。
 * 同一セットの原点座標 (origin) を保持する。
 */
class TatamiBlockEntity(
    type: BlockEntityType<TatamiBlockEntity>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {

    /** 同一セットの原点座標 */
    var origin: BlockPos? = null

    override fun writeData(view: WriteView) {
        super.writeData(view)
        origin?.let {
            view.putInt("OriginX", it.x)
            view.putInt("OriginY", it.y)
            view.putInt("OriginZ", it.z)
        }
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        val ox = view.getOptionalInt("OriginX")
        val oy = view.getOptionalInt("OriginY")
        val oz = view.getOptionalInt("OriginZ")
        if (ox.isPresent && oy.isPresent && oz.isPresent) {
            origin = BlockPos(ox.get(), oy.get(), oz.get())
        }
    }
}
