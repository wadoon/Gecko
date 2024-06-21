package org.gecko.application

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.gecko.exceptions.GeckoException
import org.gecko.exceptions.ModelException
import org.gecko.io.*
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.GeckoViewModel
import java.io.File
import java.io.IOException

/**
 * Represents a manager for the I/O functionalities of the Gecko Graphic Editor, following the singleton pattern.
 * Provides methods for creating, loading and saving project files or importing and exporting a project to another file
 * format. Uses the IO package for serialization and parsing.
 */
object GeckoIOManager {
    var geckoManager: GeckoManager? = null
    var file: File? = null
    var stage: Stage? = null
        set(value) {
            field = value
            field?.onCloseRequest = EventHandler { e: WindowEvent ->
                try {
                    launchSaveChangesAlert()
                } catch (ex: GeckoException) {
                    e.consume()
                }
            }
        }


    /**
     * Attempts to create a new project and makes the user choose a file to save it to.
     */
    fun createNewProject() {
        val newFile = getSaveFileChooser(FileTypes.JSON)
        if (newFile != null) {
            file = newFile
            val newGecko: Gecko
            try {
                newGecko = Gecko()
            } catch (e: ModelException) {
                geckoManager!!.gecko.viewModel.actionManager.showExceptionAlert(e.message)
                return
            }
            geckoManager!!.gecko = newGecko
            saveGeckoProject(file!!)
        }
    }

    /**
     * Attempts to load a project from a file chosen that was either previously chosen or asks the user to choose a
     * file.
     */
    fun loadGeckoProject() {
        val fileToLoad = getOpenFileChooser(FileTypes.JSON)!!
        val projectFileParser = ProjectFileParser()
        val gvm: GeckoViewModel =
            try {
                projectFileParser.parse(fileToLoad)
            } catch (e: IOException) {
                val alert = Alert(
                    Alert.AlertType.ERROR,
                    ResourceHandler.corrupted_file + fileToLoad.path + ".", ButtonType.OK
                )
                alert.showAndWait()
                return
            }

        val newGecko = Gecko(gvm)
        geckoManager!!.gecko = newGecko
        file = fileToLoad
    }

    /**
     * Imports an automaton from a file chosen by the user.
     *
     * @param file The file to import the automaton from.
     */
    fun importAutomatonFile(file: File) {
        val automatonFileParser = AutomatonFileParser()
        val gvm: GeckoViewModel?
        try {
            gvm = automatonFileParser.parse(file)
        } catch (e: IOException) {
            val message: String =
                ResourceHandler.could_not_read_file + String.format(
                    "%s.%s%s",
                    file.path,
                    System.lineSeparator(), e.message
                )
            val alert = Alert(Alert.AlertType.ERROR, message, ButtonType.OK)
            alert.showAndWait()
            return
        }
        val newGecko = Gecko(gvm)
        geckoManager!!.gecko = newGecko
        this.file = null
    }

    /**
     * Saves the current project to a file chosen by the user.
     *
     * @param file The file to save the project to.
     */
    fun saveGeckoProject(file: File) {
        val projectFileSerializer = ProjectFileSerializer(geckoManager!!.gecko.viewModel)
        try {
            projectFileSerializer.writeToFile(file)
        } catch (e: IOException) {
            val alert =
                Alert(
                    Alert.AlertType.ERROR, ResourceHandler.could_not_write_file,
                    ButtonType.OK
                )
            alert.showAndWait()
        }
    }

    /**
     * Exports the current automaton to a file chosen by the user.
     *
     * @param file The file to export the automaton to.
     */
    fun exportAutomatonFile(file: File?) {
        val fileSerializer: FileSerializer = AutomatonFileSerializer(geckoManager!!.gecko.viewModel)
        try {
            fileSerializer.writeToFile(file!!)
        } catch (e: IOException) {
            val alert =
                Alert(
                    Alert.AlertType.ERROR, ResourceHandler.could_not_write_file,
                    ButtonType.OK
                )
            alert.showAndWait()
        }
    }

    fun getOpenFileChooser(fileType: FileTypes): File? {
        val fileChooser = getNewFileChooser(fileType)
        return fileChooser.showOpenDialog(stage)
    }

    fun getSaveFileChooser(fileType: FileTypes): File? {
        val fileChooser = getNewFileChooser(fileType)
        var result = fileChooser.showSaveDialog(stage) ?: return null
        if (!result.name.endsWith("." + fileType.fileExtension)) {
            result = File(result.path + "." + fileType.fileExtension)
        }
        return result
    }

    fun getNewFileChooser(fileType: FileTypes): FileChooser {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters
            .addAll(FileChooser.ExtensionFilter(fileType.fileDescription, fileType.fileNameGlob))
        return fileChooser
    }

    @Throws(GeckoException::class)
    fun launchSaveChangesAlert() {
        val saveChangesAlert =
            Alert(
                Alert.AlertType.NONE, ResourceHandler.save_changes_prompt, ButtonType.YES,
                ButtonType.NO
            )
        saveChangesAlert.title = ResourceHandler.confirm_exit
        saveChangesAlert.showAndWait()

        if (saveChangesAlert.result == ButtonType.NO) {
            return
        }

        if (file == null) {
            val fileToSaveTo = getSaveFileChooser(FileTypes.JSON) ?: throw GeckoException("No file chosen.")
            file = fileToSaveTo
        }
        saveGeckoProject(file!!)
    }
}