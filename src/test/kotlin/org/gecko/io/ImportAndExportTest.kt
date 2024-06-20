package org.gecko.io

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException

class AutomatonFileSerializerTest {
    var automatonFileParser: AutomatonFileParser = AutomatonFileParser()

    val geckoViewModel by lazy {
        val aebFile = File("src/test/java/org/gecko/io/files/AEB.sys")
        automatonFileParser.parse(aebFile)
        //Assertions.fail<Any>("View model could not be extracted from basic automaton file with one root.")
    }

    @Test
    fun writeToFile() {
        val serializedParsedAEBFile = File("src/test/java/org/gecko/io/files/serializedParsedAEB.sys")
        val automatonFileSerializer = AutomatonFileSerializer(geckoViewModel)
        Assertions.assertDoesNotThrow { automatonFileSerializer.writeToFile(serializedParsedAEBFile) }
    }

    @Test
    fun parseComplexGecko2() {
        val projectFileParser = ProjectFileParser()
        val complexGeckoFile = File("src/test/java/org/gecko/io/files/complexGecko.json")
        val complexViewModel = projectFileParser.parse(complexGeckoFile)

        val serializedExportedComplexFile = File("src/test/java/org/gecko/io/files/exportedComplexGecko.sys")
        val automatonFileSerializer =
            AutomatonFileSerializer(complexViewModel)
        Assertions.assertDoesNotThrow { automatonFileSerializer.writeToFile(serializedExportedComplexFile) }
    }


    @Test
    fun parseOneRoot() {
        // Importing a file with multiple top level systems can only be resolved in the view.
        val aebFile = File("src/test/java/org/gecko/io/files/AEB.sys")
        try {
            automatonFileParser.parse(aebFile)
        } catch (e: IOException) {
            Assertions.fail<Any>("View model could not be extracted from automaton file with one root.")
        }
    }

    @Test
    fun parseComplexGecko() {
        val serializedExportedComplexFile = File("src/test/java/org/gecko/io/files/exportedComplexGecko.sys")
        try {
            automatonFileParser.parse(serializedExportedComplexFile)
        } catch (e: IOException) {
            Assertions.fail<Any>("View model could not be extracted from automaton file.")
        }
    }
}

