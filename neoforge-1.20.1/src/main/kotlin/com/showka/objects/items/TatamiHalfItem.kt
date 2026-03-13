package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiHalfPartBlock
import com.showka.util.TatamiLayout

class TatamiHalfItem(
    properties: Properties,
    partBlockProvider: () -> AbstractTatamiPartBlock,
) : AbstractTatamiItem(
    properties = properties,
    layout = TatamiLayout.TATAMI_HALF,
    partBlockProvider = partBlockProvider,
    partProperty = TatamiHalfPartBlock.PART
)
