package org.gecko.util

import org.gecko.exceptions.ModelException


import org.gecko.viewmodel.GeckoViewModel

object TestHelper {
    @Throws(ModelException::class)
    fun createGeckoViewModel(): GeckoViewModel {
        return GeckoViewModel()
    }
}
