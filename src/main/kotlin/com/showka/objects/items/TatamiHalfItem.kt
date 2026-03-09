package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.blocks.TatamiHalfPartBlock
import com.showka.util.TatamiLayout

/**
 * 半畳アイテム（2×2 = 4枚設置）。
 */
class TatamiHalfItem(settings: Settings) : AbstractTatamiItem(
    settings = settings,
    layout = TatamiLayout.TATAMI_HALF,
    partBlockProvider = { ModBlocks.TATAMI_HALF_PART as AbstractTatamiPartBlock },
    partProperty = TatamiHalfPartBlock.PART
)
