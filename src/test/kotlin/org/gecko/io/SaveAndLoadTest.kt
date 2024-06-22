package org.gecko.io

import org.gecko.exceptions.ModelException
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class ProjectFileSerializerTest {
    val projectFileParser = ProjectFileParser()
    val projectFileSerializerForEmpty: ProjectFileSerializer
    val projectFileSerializerForOneLevel: ProjectFileSerializer
    val projectFileSerializerForTree: ProjectFileSerializer
    val oneLevelGeckoViewModel = GeckoViewModel()
    val oneLevelRoot = oneLevelGeckoViewModel.root
    val oneLevelFactory = oneLevelGeckoViewModel.viewModelFactory

    val emptyGeckoViewModel = GeckoViewModel()
    val treeGeckoViewModel = GeckoViewModel()

    var EMPTY_GECKO_JSON: String =
        ("{\"model\":{\"id\":0,\"name\":\"Element_0\",\"code\":null,\"automaton\":{\"startState\":null,\"regions\""
                + ":[],\"states\":[],\"edges\":[]},\"children\":[],\"connections\":[],\"variables\":[]},\"startStates"
                + "\":[],\"viewModelProperties\":[]}")

    var NON_NULL_AUTOMATON_JSON: String = "\"automaton\":{"
    var NON_NULL_START_STATE_JSON: String = "\"startState\":{"
    var NON_NULL_REGIONS_JSON: String = "\"regions\":[{"
    var NON_NULL_REGION_STATES_JSON: String = "},\"states\":[{"
    var NO_CHILDREN: String = "\"children\":[]"
    var PRESENT_CHILDREN: String = "\"children\":[{\"id\":"

    init {
        Assertions.assertDoesNotThrow {
            val port1 = oneLevelFactory.createPortViewModelIn(oneLevelRoot)
            port1.visibility = Visibility.INPUT
            port1.name = "emptyPort1"

            val port2 = oneLevelFactory.createPortViewModelIn(oneLevelRoot)
            port2.visibility = (Visibility.OUTPUT)
            port2.name = "emptyPort2"
        }

        Assertions.assertThrows<ModelException>(ModelException::class.java) {
            oneLevelFactory.createSystemConnectionViewModelIn(
                oneLevelRoot,
                oneLevelRoot.getVariableByName("emptyPort1")!!,
                oneLevelRoot.getVariableByName("emptyPort2")!!
            )
        }

        Assertions.assertDoesNotThrow {
            oneLevelFactory.createRegionViewModelIn(oneLevelRoot)
            val regionWithStates = oneLevelFactory.createRegionViewModelIn(oneLevelRoot)
            regionWithStates.addState(oneLevelFactory.createStateViewModelIn(oneLevelRoot))
        }

        Assertions.assertDoesNotThrow {
            val state1 = oneLevelFactory.createStateViewModelIn(oneLevelRoot)
            val state2 = oneLevelFactory.createStateViewModelIn(oneLevelRoot)
            oneLevelFactory.createContractViewModelIn(state2)

            oneLevelFactory.createEdgeViewModelIn(oneLevelRoot, state1, state2)
        }


        val treeRoot = treeGeckoViewModel.root
        val treeFactory = treeGeckoViewModel.viewModelFactory

        Assertions.assertDoesNotThrow {
            val child1 = treeFactory.createSystemViewModelIn(treeRoot)
            child1.name = "child1"
            val port1 = treeFactory.createPortViewModelIn(child1)
            port1.visibility = (Visibility.OUTPUT)
            port1.name = "treeVar1"
            val region1 = treeFactory.createRegionViewModelIn(child1)
            region1.addState(treeFactory.createStateViewModelIn(child1))

            val child2 = treeFactory.createSystemViewModelIn(treeRoot)
            child1.name = "child2"
            val port2 = treeFactory.createPortViewModelIn(child2)
            port2.visibility = (Visibility.INPUT)
            port2.name = "treeVar2"
            val region2 = treeFactory.createRegionViewModelIn(child2)
            region2.addState(treeFactory.createStateViewModelIn(child2))

            treeFactory.createSystemConnectionViewModelIn(treeRoot, port1, port2)

            val child3 = treeFactory.createSystemViewModelIn(child2)
            child1.name = "child3"
        }

        projectFileSerializerForEmpty = ProjectFileSerializer(emptyGeckoViewModel)
        projectFileSerializerForOneLevel = ProjectFileSerializer(oneLevelGeckoViewModel)
        projectFileSerializerForTree = ProjectFileSerializer(treeGeckoViewModel)
    }


    @Test
    fun writeToFileEmpty() {
        val fileForEmpty = File("src/test/java/org/gecko/io/files/emptyGecko.json")
        projectFileSerializerForEmpty.writeToFile(fileForEmpty)
    }

    /*

        @Test
        fun writeToFileOneLevel() {
            val fileForOneLevel = File("src/test/java/org/gecko/io/files/oneLevelGecko.json")
            Assertions.assertDoesNotThrow { projectFileSerializerForOneLevel!!.writeToFile(fileForOneLevel) }
            var oneLevel: JsonNode
            try {
                oneLevel = Companion.mapper.readTree(fileForOneLevel)
            } catch (e: IOException) {
                Assertions.fail<Any>("File for one-level Gecko does not contain a JSON valid string.")
            }

            Assertions.assertTrue(
                oneLevel.toString().contains(NON_NULL_AUTOMATON_JSON) && oneLevel.toString()
                    .contains(NON_NULL_START_STATE_JSON) && oneLevel.toString()
                    .contains(NON_NULL_REGIONS_JSON)
                        && oneLevel.toString().contains(NON_NULL_REGION_STATES_JSON) && oneLevel.toString()
                    .contains(NO_CHILDREN)
            )
        }

        @Test
        fun writeToFileTree() {
            val fileForTree = File("src/test/java/org/gecko/io/files/treeGecko.json")
            Assertions.assertDoesNotThrow { projectFileSerializerForTree!!.writeToFile(fileForTree) }

            var tree: JsonNode
            try {
                tree = Companion.mapper.readTree(fileForTree)
            } catch (e: IOException) {
                Assertions.fail<Any>("File for tree-structured Gecko does not contain a JSON valid string.")
            }

            Assertions.assertTrue(
                tree.toString().contains(NON_NULL_AUTOMATON_JSON) && tree.toString()
                    .contains(NON_NULL_START_STATE_JSON)
                        && tree.toString().contains(NON_NULL_REGIONS_JSON) && tree.toString()
                    .contains(NON_NULL_REGION_STATES_JSON) && tree.toString()
                    .contains(PRESENT_CHILDREN)
            )
        }



        @Test
        fun parse() {
            var parsedEmptyGeckoViewModel: GeckoViewModel
            val fileForEmpty = File("src/test/java/org/gecko/io/files/emptyGecko.json")
            val serializedParsedEmpty = File("src/test/java/org/gecko/io/files/serializedParsedEmptyGecko.json")

            try {
                parsedEmptyGeckoViewModel = projectFileParser!!.parse(fileForEmpty)
            } catch (e: IOException) {
                Assertions.fail<Any>("Empty Gecko could not be parsed from file.")
            }
            Assertions.assertNotNull(parsedEmptyGeckoViewModel)

            val serializer = ProjectFileSerializer(parsedEmptyGeckoViewModel)
            Assertions.assertDoesNotThrow { serializer.writeToFile(serializedParsedEmpty) }
            Assertions.assertDoesNotThrow {
                Assertions.assertEquals(
                    readTree(fileForEmpty),
                    readTree(serializedParsedEmpty)
                )
            }
        }

        @Test
        fun parseOneLevel() {
            var parsedOneLevelGeckoViewModel: GeckoViewModel
            val fileForOneLevel = File("src/test/java/org/gecko/io/files/oneLevelGecko.json")
            val serializedParsedOneLevel =
                File("src/test/java/org/gecko/io/files/serializedParsedOneLevelGecko.json")
            try {
                parsedOneLevelGeckoViewModel = projectFileParser!!.parse(fileForOneLevel)
            } catch (e: IOException) {
                Assertions.fail<Any>("One-level Gecko could not be parsed from file.")
            }
            Assertions.assertNotNull(parsedOneLevelGeckoViewModel)

            val serializer = ProjectFileSerializer(parsedOneLevelGeckoViewModel)
            Assertions.assertDoesNotThrow { serializer.writeToFile(serializedParsedOneLevel) }

            var scanner1: Scanner
            var scanner2: Scanner
            try {
                scanner1 = Scanner(fileForOneLevel)
                scanner1.useDelimiter("\"viewModelProperties\"")

                scanner2 = Scanner(serializedParsedOneLevel)
                scanner2.useDelimiter("\"viewModelProperties\"")
            } catch (e: FileNotFoundException) {
                Assertions.fail<Any>("File to scan was not found.")
            }

            val parsedModel = scanner1!!.next()
            val serializedParsedModel = scanner2!!.next()
            Assertions.assertEquals(parsedModel, serializedParsedModel)
        }

        @Test
        fun parseTree() {
            var parsedTreeGeckoViewModel: GeckoViewModel
            val fileForTree = File("src/test/java/org/gecko/io/files/treeGecko.json")
            val serializedParsedTree = File("src/test/java/org/gecko/io/files/serializedParsedTreeGecko.json")
            try {
                parsedTreeGeckoViewModel = projectFileParser!!.parse(fileForTree)
            } catch (e: IOException) {
                Assertions.fail<Any>("Tree-structured Gecko could not be parsed from file.")
            }
            Assertions.assertNotNull(parsedTreeGeckoViewModel)

            val serializer = ProjectFileSerializer(parsedTreeGeckoViewModel)
            Assertions.assertDoesNotThrow { serializer.writeToFile(serializedParsedTree) }

            var scanner1: Scanner
            var scanner2: Scanner
            try {
                scanner1 = Scanner(fileForTree)
                scanner1.useDelimiter("\"viewModelProperties\"")

                scanner2 = Scanner(serializedParsedTree)
                scanner2.useDelimiter("\"viewModelProperties\"")
            } catch (e: FileNotFoundException) {
                Assertions.fail<Any>("File to scan was not found.")
            }

            val parsedModel = scanner1!!.next()
            val serializedParsedModel = scanner2!!.next()
            Assertions.assertEquals(parsedModel, serializedParsedModel)
        }

    @Test
    fun parseFileThatContainsANonexistentStartState() {
        val fileForNonexistentStartState = File("src/test/java/org/gecko/io/files/nonexistentStartState.json")
        Assertions.assertThrows<IOException>(
            IOException::class.java,
            Executable { projectFileParser!!.parse(fileForNonexistentStartState) })
    }

    @Test
    fun parseFileWithValidStartStates() {
        val fileForNonexistentStartState = File("src/test/java/org/gecko/io/files/existentStartState.json")
        Assertions.assertDoesNotThrow<GeckoViewModel>(ThrowingSupplier<GeckoViewModel> {
            projectFileParser!!.parse(
                fileForNonexistentStartState
            )
        })
    }

    @Test
    fun parseProjectFileWithNullRoot() {
        val fileForNullRoot = File("src/test/java/org/gecko/io/files/nullRoot.json")
        Assertions.assertThrows<IOException>(
            IOException::class.java
        ) { projectFileParser!!.parse(fileForNullRoot) }
    }
        */
}
