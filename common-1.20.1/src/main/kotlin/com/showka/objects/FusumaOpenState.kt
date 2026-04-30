package com.showka.objects

import net.minecraft.util.StringRepresentable

enum class FusumaOpenState : StringRepresentable {
    CLOSED, LEFT_OPEN, RIGHT_OPEN;

    override fun getSerializedName(): String = name.lowercase()
}
