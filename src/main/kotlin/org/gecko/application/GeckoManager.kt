package org.gecko.application

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.gecko.exceptions.GeckoException
import org.gecko.io.*
import org.gecko.view.GeckoView
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.objectProperty
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue
import java.io.File
import java.io.IOException


/**
 * Represents a manager for the active [Gecko]. Additionally, holds a reference to the [Stage] of the
 * application.
 */
class GeckoManager : Application() {
    lateinit var stage: Stage

    var geckoProperty = objectProperty(GeckoViewModel())
    var gecko by geckoProperty

    var geckoView: GeckoView = GeckoView(this, gecko)

    init {
        geckoProperty.onChange {
            geckoView = GeckoView(this, gecko)
            activateGeckoView()
        }
    }

    @Throws(Exception::class)
    override fun start(stage: Stage) {
        this.stage = stage
        stage.title = "Gecko"
        stage.icons.add(Image("file:gecko_logo.png"))

        stage.onCloseRequest = EventHandler { e: WindowEvent ->
            try {
                launchSaveChangesAlert()
            } catch (ex: GeckoException) {
                e.consume()
            }
        }


        activateGeckoView()
        stage.show()
    }

    private fun activateGeckoView() {
        val scene = Scene(geckoView.root, SCENE_WIDTH, SCENE_HEIGHT)
        geckoView.mnemonicsProperty.forEach { scene.addMnemonic(it) }
        stage.scene = scene

        /*
    gdbus call --session --dest org.freedesktop.portal.Desktop --object-path /org/freedesktop/portal/desktop --method org.freedesktop.portal.Settings.Read org.freedesktop.appearance color-scheme
    */
        setUserAgentStylesheet(PrimerLight().userAgentStylesheet)
        geckoView.darkModeProperty.onChange { n ->
            setUserAgentStylesheet(
                if (!n) PrimerLight().userAgentStylesheet
                else PrimerDark().userAgentStylesheet
            )
        }

    }

    /**
     * Represents a manager for the I/O functionalities of the Gecko Graphic Editor, following the singleton pattern.
     * Provides methods for creating, loading and saving project files or importing and exporting a project to another file
     * format. Uses the IO package for serialization and parsing.
     */
    var latestFile: File? = null

    /**
     * Attempts to create a new project and makes the user choose a file to save it to.
     */
    fun createNewProject() {
        this.gecko = GeckoViewModel()
    }

    /**
     * Attempts to load a project from a file chosen that was either previously chosen or asks the user to choose a
     * file.
     */
    fun loadGeckoProject() {
        getOpenFileChooser(FileTypes.JSON)?.let { fileToLoad ->
            val projectFileParser = ProjectFileParser()
            val gvm: GeckoViewModel =
                try {
                    projectFileParser.parse(fileToLoad)
                } catch (e: IOException) {
                    val alert = Alert(Alert.AlertType.ERROR)
                    alert.contentText = "${ResourceHandler.corrupted_file}${fileToLoad.path}."
                    alert.showAndWait()
                    return
                }
            gecko = gvm
            latestFile = fileToLoad
        }
    }

    /**
     * Imports an automaton from a file chosen by the user.
     *
     * @param file The file to import the automaton from.
     */
    fun importAutomatonFile(file: File) {
        val automatonFileParser = AutomatonFileParser()
        try {
            gecko = automatonFileParser.parse(file)
            this.latestFile = null
        } catch (e: IOException) {
            val message: String =
                ResourceHandler.could_not_read_file + String.format(
                    "%s.%s%s",
                    file.path,
                    System.lineSeparator(), e.message
                )
            val alert = Alert(Alert.AlertType.ERROR)
            alert.contentText = message
            alert.showAndWait()
            return
        }
    }

    /**
     * Saves the current project to a file chosen by the user.
     *
     * @param file The file to save the project to.
     */
    fun saveGeckoProject(file: File) {
        try {
            val projectFileSerializer = ProjectFileSerializer(gecko)
            projectFileSerializer.writeToFile(file)
        } catch (e: IOException) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.contentText = ResourceHandler.could_not_write_file
            alert.showAndWait()
        }
    }

    /**
     * Exports the current automaton to a file chosen by the user.
     *
     * @param file The file to export the automaton to.
     */
    fun exportAutomatonFile(file: File?) {
        try {
            val fileSerializer: FileSerializer = AutomatonFileSerializer(gecko)
            fileSerializer.writeToFile(file!!)
        } catch (e: IOException) {
            val alert = Alert(Alert.AlertType.ERROR, ResourceHandler.could_not_write_file, ButtonType.OK)
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
        val saveChangesAlert = Alert(Alert.AlertType.CONFIRMATION)

        saveChangesAlert.initModality(Modality.WINDOW_MODAL)
        saveChangesAlert.initOwner(stage)

        saveChangesAlert.title = ResourceHandler.CONFIRM_EXIT
        saveChangesAlert.contentText = ResourceHandler.save_changes_prompt
        saveChangesAlert.showAndWait()

        if (saveChangesAlert.result == ButtonType.CANCEL) {
            return
        }

        if (latestFile == null) {
            val fileToSaveTo = getSaveFileChooser(FileTypes.JSON) ?: throw GeckoException("No file chosen.")
            latestFile = fileToSaveTo
        }
        saveGeckoProject(latestFile!!)
    }
}

const val SCENE_WIDTH = 1024.0
const val SCENE_HEIGHT = 768.0
