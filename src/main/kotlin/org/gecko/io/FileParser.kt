package org.gecko.io

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.viewmodel.GeckoViewModel
import java.io.File
import java.io.IOException

/**
 * Provides methods for the conversion of data from an external file into Gecko-specific data.
 */
interface FileParser {
    @Throws(IOException::class, MissingViewModelElementException::class)
    fun parse(file: File): GeckoViewModel
}
