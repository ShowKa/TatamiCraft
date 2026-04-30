package com.showka.objects

import net.minecraft.util.StringRepresentable

enum class FusumaSide : StringRepresentable {
    LEFT, RIGHT;

    override fun getSerializedName(): String = name.lowercase()
}
