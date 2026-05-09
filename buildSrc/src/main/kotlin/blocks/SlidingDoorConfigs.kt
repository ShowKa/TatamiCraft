package blocks

object ShojiConfig : SlidingPanelConfig() {
    override val blockPartId = "shoji_part"
    override val itemId      = "shoji"
    override val enName      = "Shoji"
    override val jaName      = "障子"
    override val texBase     = "shoji"
}

object FrostedGlassSlidingDoorConfig : SlidingPanelConfig() {
    override val blockPartId = "frosted_glass_sliding_door_part"
    override val itemId      = "frosted_glass_sliding_door"
    override val enName      = "Frosted Glass Sliding Door"
    override val jaName      = "すりガラスの引き戸"
    override val texBase     = "frosted_glass_sliding_door"
    override val renderType  = "translucent"
}

object SlidingWindowConfig : SlidingPanelConfig() {
    override val blockPartId   = "sliding_window_part"
    override val itemId        = "sliding_window"
    override val enName        = "Sliding Window"
    override val jaName        = "掃き出し窓"
    override val texBase       = "sliding_window"
    override val renderType    = "translucent"
    override val outerEdgeOnly = true
}

object WoodenSlidingDoorConfig : SlidingPanelConfig() {
    override val blockPartId = "wooden_sliding_door_part"
    override val itemId      = "wooden_sliding_door"
    override val enName      = "Wooden Sliding Door"
    override val jaName      = "木製の引き戸"
    override val texBase     = "wooden_sliding_door"
}
