package org.gecko.io

import org.gecko.viewmodel.GModel
import java.io.IOException
import java.io.Writer

/**
 * Provides methods for the conversion of Gecko-specific data to the JSON format and writing the converted data in a
 * JSON file.
 */
class ProjectFileSerializer(val viewModel: GModel) : FileSerializer {
    @Throws(IOException::class)
    override fun writeToStream(w: Writer) {
        val root = viewModel
        val project = Project(root).asJson()
        gson.toJson(project, w)
    }
}
