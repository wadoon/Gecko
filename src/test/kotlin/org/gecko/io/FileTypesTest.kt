package org.gecko.io

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileTypesTest {
    @Test
    fun fileNameRegex() {
        Assertions.assertEquals("*.json", FileTypes.JSON.fileNameGlob)
        Assertions.assertEquals("*.sys", FileTypes.SYS.fileNameGlob)
    }
}
