package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.ModBlocks
import com.showka.objects.blocks.TatamiPartBlock
import com.showka.util.TatamiLayout

/**
 * 畳アイテム（2×4 = 8枚設置）。
 */
class TatamiItem(settings: Settings) : AbstractTatamiItem(
    settings = settings,
    layout = TatamiLayout.TATAMI,
    partBlockProvider = { ModBlocks.TATAMI_PART as AbstractTatamiPartBlock },
    partProperty = TatamiPartBlock.PART
)
