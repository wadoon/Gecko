package org.gecko.io

import org.gecko.viewmodel.GModel
import org.gecko.viewmodel.Visibility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class ProjectFileSerializerTest {
    val projectFileParser = ProjectFileParser()
    val projectFileSerializerForOneLevel: ProjectFileSerializer
    val projectFileSerializerForTree: ProjectFileSerializer
    val oneLevelGModel = GModel()
    val oneLevelRoot = oneLevelGModel.root
    val oneLevelFactory = oneLevelGModel.viewModelFactory

    val emptyGModel = GModel()
    val treeGModel = GModel()

    var NON_NULL_AUTOMATON_JSON: String = "\"automaton\":{"
    var NON_NULL_START_STATE_JSON: String = "\"startState\":{"
    var NON_NULL_REGIONS_JSON: String = "\"regions\":[{"
    var NON_NULL_REGION_STATES_JSON: String = "},\"states\":[{"
    var NO_CHILDREN: String = "\"children\":[]"
    var PRESENT_CHILDREN: String = "\"children\":[{\"id\":"

    init {
        Assertions.assertDoesNotThrow {
            val port1 = oneLevelFactory.createPort(oneLevelRoot)
            port1.visibility = Visibility.INPUT
            port1.name = "emptyPort1"

            val port2 = oneLevelFactory.createPort(oneLevelRoot)
            port2.visibility = (Visibility.OUTPUT)
            port2.name = "emptyPort2"
        }

        oneLevelFactory.createSystemConnectionViewModelIn(
            oneLevelRoot,
            oneLevelRoot.getVariableByName("emptyPort1")!!,
            oneLevelRoot.getVariableByName("emptyPort2")!!
        )

        oneLevelFactory.createRegion(oneLevelRoot)
        val regionWithStates = oneLevelFactory.createRegion(oneLevelRoot)
        regionWithStates.states.add(oneLevelFactory.createState(oneLevelRoot))

        val state1 = oneLevelFactory.createState(oneLevelRoot)
        val state2 = oneLevelFactory.createState(oneLevelRoot)
        oneLevelFactory.createContractViewModelIn(state2)
        oneLevelFactory.createEdgeViewModelIn(oneLevelRoot, state1, state2)


        val treeRoot = treeGModel.root
        val treeFactory = treeGModel.viewModelFactory

        val child1 = treeFactory.createSystem(treeRoot)
        child1.name = "child1"
        val port1 = treeFactory.createPort(child1)
        port1.visibility = (Visibility.OUTPUT)
        port1.name = "treeVar1"
        val region1 = treeFactory.createRegion(child1)
        region1.states.add(treeFactory.createState(child1))

        val child2 = treeFactory.createSystem(treeRoot)
        child1.name = "child2"
        val port2 = treeFactory.createPort(child2)
        port2.visibility = (Visibility.INPUT)
        port2.name = "treeVar2"
        val region2 = treeFactory.createRegion(child2)
        region2.states.add(treeFactory.createState(child2))

        treeFactory.createSystemConnectionViewModelIn(treeRoot, port1, port2)

        val child3 = treeFactory.createSystem(child2)
        child1.name = "child3"

        projectFileSerializerForOneLevel = ProjectFileSerializer(oneLevelGModel)
        projectFileSerializerForTree = ProjectFileSerializer(treeGModel)
    }


    @Test
    fun writeToFileEmpty() {
        val fileForEmpty = File("build/tmp/emptyGecko.json").absoluteFile
        val projectFileSerializerForEmpty = ProjectFileSerializer(emptyGModel)
        projectFileSerializerForEmpty.writeToFile(fileForEmpty)

        val parsedEmptyGeckoViewModel = projectFileParser.parse(fileForEmpty)

        val serializer = ProjectFileSerializer(parsedEmptyGeckoViewModel)
        val serializedParsedEmpty = File("build/tmp/serializedParsedEmptyGecko.json")
        serializer.writeToFile(serializedParsedEmpty)

        //TODO compare JSON equality
    }




    @Test
    fun parseOneLevel() {
        val fileForOneLevel = File("build/tmp/oneLevelGecko.json")
        projectFileSerializerForOneLevel.writeToFile(fileForOneLevel)

        val serializedParsedOneLevel = File("build/tmp/serializedParsedOneLevelGecko.json")
        val parsedOneLevelGeckoViewModel = projectFileParser.parse(fileForOneLevel)
        Assertions.assertNotNull(parsedOneLevelGeckoViewModel)
        val serializer = ProjectFileSerializer(parsedOneLevelGeckoViewModel)
        serializer.writeToFile(serializedParsedOneLevel)
    }

    @Test
    fun parseTree() {
        val fileForTree = File("build/tmp/treeGecko.json")
        val serializedParsedTree = File("build/tmp/serializedParsedTreeGecko.json")

        projectFileSerializerForTree.writeToFile(fileForTree)
        val parsedTreeGeckoViewModel = projectFileParser.parse(fileForTree)
        val serializer = ProjectFileSerializer(parsedTreeGeckoViewModel)
        serializer.writeToFile(serializedParsedTree)
    }

    @Disabled
    @Test
    fun parseFileThatContainsANonexistentStartState() {
        val fileForNonexistentStartState = File("build/tmp/nonexistentStartState.json")
        projectFileParser.parse(fileForNonexistentStartState)
    }

    @Disabled
    @Test
    fun parseFileWithValidStartStates() {
        val fileForNonexistentStartState = File("build/tmp/existentStartState.json")
        projectFileParser.parse(fileForNonexistentStartState)
    }

    @Disabled
    @Test
    fun parseProjectFileWithNullRoot() {
        val fileForNullRoot = File("build/tmp/nullRoot.json")
        projectFileParser.parse(fileForNullRoot)
    }
}
