package org.gecko.io

import org.gecko.viewmodel.GeckoViewModel
import java.io.File
import java.io.IOException

/**
 * Provides methods for the conversion of Gecko-specific data to the JSON format and writing the converted data in a
 * JSON file.
 */
class ProjectFileSerializer(val viewModel: GeckoViewModel) : FileSerializer {
    @Throws(IOException::class)
    override fun writeToFile(file: File) {
        val root = viewModel
        //val saver = ViewModelElementSaver(viewModel)
        //val startStates = saver.startStates
        //val viewModelProperties = saver.getViewModelProperties(root)
        val geckoJsonWrapper = GeckoJsonWrapper(root)

        file.outputStream().use {
            encodeToStream(geckoJsonWrapper, it)
        }
    }
}
