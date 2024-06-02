package org.gecko.io

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gecko.model.GeckoModel
import org.gecko.model.System
import org.gecko.viewmodel.GeckoViewModel
import java.io.File
import java.io.IOException

/**
 * Provides methods for the conversion of data from a JSON file into Gecko-specific data.
 */

class ProjectFileParser : FileParser {
    @Throws(IOException::class)
    override fun parse(file: File): GeckoViewModel {
        val geckoJsonWrapper =
            file.inputStream().use {
                Json.decodeFromStream<GeckoJsonWrapper>(it)
            }

        val model = GeckoModel(geckoJsonWrapper.model)
        val viewModel = GeckoViewModel(model)

        val creator =
            ViewModelElementCreator(
                viewModel, geckoJsonWrapper.viewModelProperties,
                geckoJsonWrapper.startStates
            )
        creator.traverseModel(model.root)
        updateSystemParents(model.root)

        if (creator.foundNonexistentStartState) {
            throw IOException("Not all start-states belong to the corresponding automaton's states.")
        }

        if (creator.foundNullContainer) {
            throw IOException("Not all elements have view model properties.")
        }
        viewModel.geckoModel.modelFactory.elementId = creator.highestId + 1u
        return viewModel
    }

    fun updateSystemParents(system: System) {
        for (child in system.children) {
            child.parent = system
            updateSystemParents(child)
        }
    }
}
