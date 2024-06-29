package org.gecko.util

import org.gecko.exceptions.ModelException


import org.gecko.viewmodel.GModel

object TestHelper {
    @Throws(ModelException::class)
    fun createGeckoViewModel(): GModel {
        return GModel()
    }
}
