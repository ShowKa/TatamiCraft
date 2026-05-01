package com.showka.objects

import net.minecraft.util.StringRepresentable

/**
 * Open/closed state of the fusuma (sliding door).
 *
 * CLOSED:     both panels are in their default closed position
 * LEFT_OPEN:  left panel has slid open
 * RIGHT_OPEN: right panel has slid open
 */
enum class FusumaOpenState : StringRepresentable {
    CLOSED, LEFT_OPEN, RIGHT_OPEN;

    override fun getSerializedName(): String = name.lowercase()
}
