package com.showka.objects.items

import com.showka.objects.blocks.AbstractTatamiPartBlock
import com.showka.objects.blocks.TatamiPartBlock
import com.showka.util.TatamiLayout

class TatamiItem(
    properties: Properties,
    partBlockProvider: () -> AbstractTatamiPartBlock,
) : AbstractTatamiItem(
    properties = properties,
    layout = TatamiLayout.TATAMI,
    partBlockProvider = partBlockProvider,
    partProperty = TatamiPartBlock.PART
)
