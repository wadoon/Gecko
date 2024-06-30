package org.gecko.io

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

class AutomatonFileSerializerTest {
    val gmodel by lazy {
        val aebFile = File("src/test/kotlin/org/gecko/io/files/AEB.sys")
        val (g, w) = IOFacade.parse(aebFile)
        assertTrue { w.isEmpty() }
        g
    }

    @Test
    fun writeToFile() {
        val serializedParsedAEBFile = File("src/test/kotlin/org/gecko/io/files/serializedParsedAEB.sys")
        val automatonFileSerializer = AutomatonFileSerializer(gmodel)
        Assertions.assertDoesNotThrow { automatonFileSerializer.writeToFile(serializedParsedAEBFile) }
    }

    @Disabled("outdated format")
    @Test
    fun parseComplexGecko2() {
        val input = File("src/test/kotlin/org/gecko/io/files/complexGecko.json")
        val (complexViewModel, _) = IOFacade.parse(input)

        val f = File("src/test/kotlin/org/gecko/io/files/exportedComplexGecko.sys")
        val af = AutomatonFileSerializer(complexViewModel)
        af.writeToFile(f)

        af.writeToString()
        IOFacade.parse(f)
    }


    @Test
    fun parseOneRoot() {
        val aebFile = File("src/test/kotlin/org/gecko/io/files/AEB.sys")
        IOFacade.parse(aebFile)
    }

    @Test
    @Disabled
    fun parseComplexGecko() {
        val serializedExportedComplexFile = File("src/test/kotlin/org/gecko/io/files/exportedComplexGecko.sys")
        IOFacade.parse(serializedExportedComplexFile)
    }
}

