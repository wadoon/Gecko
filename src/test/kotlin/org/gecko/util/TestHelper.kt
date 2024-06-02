package org.gecko.util

import org.gecko.exceptions.ModelException
import org.gecko.model.GeckoModel

import org.gecko.viewmodel.GeckoViewModel

object TestHelper {
    @Throws(ModelException::class)
    fun createGeckoViewModel(): GeckoViewModel {
        val geckoModel = GeckoModel()
        return GeckoViewModel(geckoModel)
    }
}
