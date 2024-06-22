package org.gecko.io

import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.SystemViewModel
import org.hildan.fxgson.FxGson
import java.io.*


var gson = FxGson.fullBuilder()
    .setPrettyPrinting()
    .create()

inline fun <reified T : Any> decodeFromStream(it: InputStream) = gson.fromJson(InputStreamReader(it), T::class.java)

inline fun <reified T> encodeToStream(obj: T, it: Writer): Unit =
    gson.toJson(obj, T::class.java, it)


/**
 * Provides methods for the conversion of data from a JSON file into Gecko-specific data.
 */
class ProjectFileParser : FileParser {
    @Throws(IOException::class)
    override fun parse(file: File): GeckoViewModel {
        val wrapper =
            file.inputStream().use {
                decodeFromStream<GeckoJsonWrapper>(it)
            }
        val model = wrapper.model

        //val viewModel = GeckoViewModel(geckoJsonWrapper)

        /*
        val creator =
            ViewModelElementCreator(
                viewModel, geckoJsonWrapper.viewModelProperties,
                geckoJsonWrapper.startStates
            )
        creator.traverseModel(model.root)*/

        updateSystemParents(model.root)

        return model
    }

    fun updateSystemParents(system: SystemViewModel) {
        for (child in system.subSystems) {
            child.parent = system
            updateSystemParents(child)
        }
    }
}
