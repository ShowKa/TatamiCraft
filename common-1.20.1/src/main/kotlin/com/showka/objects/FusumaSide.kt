package com.showka.objects

import net.minecraft.util.StringRepresentable

/**
 * Which panel of the fusuma (sliding door) a part block belongs to.
 *
 * LEFT:  the left panel (occupies part_x=0..1 at side_offset=0)
 * RIGHT: the right panel (occupies part_x=0..1 at side_offset=2)
 */
enum class FusumaSide : StringRepresentable {
    LEFT, RIGHT;

    override fun getSerializedName(): String = name.lowercase()
}
