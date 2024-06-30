package org.gecko.io

import java.io.*

/**
 * Provides methods for the conversion of Gecko-specific data to a different format and writing the
 * converted data in a desired file format.
 */
interface FileSerializer {
    @Throws(IOException::class) fun writeToStream(w: Writer)

    fun writeToFile(file: File) {
        file.bufferedWriter().use { it -> writeToStream(it) }
    }

    fun writeToString(): String {
        val sw = StringWriter()
        writeToStream(sw)
        return sw.toString()
    }
}
