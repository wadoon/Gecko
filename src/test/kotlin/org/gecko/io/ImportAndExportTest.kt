package org.gecko.io

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class AutomatonFileSerializerTest {
    var automatonFileParser: AutomatonFileParser = AutomatonFileParser()

    val geckoViewModel by lazy {
        val aebFile = File("src/test/kotlin/org/gecko/io/files/AEB.sys")
        automatonFileParser.parse(aebFile)
        //Assertions.fail<Any>("View model could not be extracted from basic automaton file with one root.")
    }

    @Test
    fun writeToFile() {
        val serializedParsedAEBFile = File("src/test/kotlin/org/gecko/io/files/serializedParsedAEB.sys")
        val automatonFileSerializer = AutomatonFileSerializer(geckoViewModel)
        Assertions.assertDoesNotThrow { automatonFileSerializer.writeToFile(serializedParsedAEBFile) }
    }

    @Disabled("outdated format")
    @Test
    fun parseComplexGecko2() {
        val projectFileParser = ProjectFileParser()
        val input = File("src/test/kotlin/org/gecko/io/files/complexGecko.json")
        val complexViewModel = projectFileParser.parse(input)

        val f = File("src/test/kotlin/org/gecko/io/files/exportedComplexGecko.sys")
        val af = AutomatonFileSerializer(complexViewModel)
        af.writeToFile(f)

        af.writeToString()
        automatonFileParser.parse(f)
    }


    @Test
    fun parseOneRoot() {
        val aebFile = File("src/test/kotlin/org/gecko/io/files/AEB.sys")
        automatonFileParser.parse(aebFile)
    }

    /*@Test
    fun parseComplexGecko() {
        val serializedExportedComplexFile = File("src/test/kotlin/org/gecko/io/files/exportedComplexGecko.sys")
        automatonFileParser.parse(serializedExportedComplexFile)
    }*/
}

