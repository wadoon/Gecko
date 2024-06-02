package org.gecko.application


import org.gecko.model.GeckoModel
import org.gecko.view.GeckoView
import org.gecko.viewmodel.GeckoViewModel

/**
 * Represents a Gecko, integrating the three architectural pattern levels of the graphic editor: a [GeckoModel], a
 * [GeckoView] and a [GeckoViewModel].
 */
data class Gecko(
    val model: GeckoModel = GeckoModel(),
    val viewModel: GeckoViewModel = GeckoViewModel(model),
    val view: GeckoView = GeckoView(viewModel)
) {
    constructor(geckoViewModel: GeckoViewModel)
            : this(geckoViewModel.geckoModel, geckoViewModel, GeckoView(geckoViewModel))
}
