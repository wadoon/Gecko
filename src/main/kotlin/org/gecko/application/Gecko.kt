package org.gecko.application


import org.gecko.view.GeckoView
import org.gecko.viewmodel.GeckoViewModel

/**
 * Represents a Gecko, integrating the three architectural pattern levels of the graphic editor: a [GeckoModel], a
 * [GeckoView] and a [GeckoViewModel].
 */
data class Gecko(
    val viewModel: GeckoViewModel = GeckoViewModel(),
    val view: GeckoView = GeckoView(viewModel)
) {
    constructor(geckoViewModel: GeckoViewModel)
            : this(geckoViewModel, GeckoView(geckoViewModel))
}
