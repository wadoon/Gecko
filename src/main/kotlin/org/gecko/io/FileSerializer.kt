package org.gecko.io

import java.io.File
import java.io.IOException

/**
 * Provides methods for the conversion of Gecko-specific data to a different format and writing the converted data in a
 * desired file format.
 */
interface FileSerializer {
    @Throws(IOException::class)
    fun writeToFile(file: File)
}
