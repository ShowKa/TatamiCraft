package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiHalfPartBlock
import com.showka.util.TatamiLayout

/**
 * Half tatami item (places 2x2 = 4 parts) for 1.20.1.
 */
class TatamiHalfItem(
    properties: Properties,
    partBlockProvider: () -> AbstractTatamiPartBlock,
) : AbstractTatamiItem(
    properties = properties,
    layout = TatamiLayout.TATAMI_HALF,
    partBlockProvider = partBlockProvider,
    partProperty = TatamiHalfPartBlock.PART
)
