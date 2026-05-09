package blocks

object BlockRegistry {
    val fusumaSeries: List<SlidingPanelConfig> =
        listOf(FusumaConfig) + FusumaConfig.colorVariants

    val slidingDoors: List<SlidingPanelConfig> = listOf(
        ShojiConfig,
        FrostedGlassSlidingDoorConfig,
        SlidingWindowConfig,
        WoodenSlidingDoorConfig,
    )

    val tatamiSeries: List<TatamiSetConfig> =
        listOf(DefaultTatamiConfig) + DefaultTatamiConfig.colorVariants
}
