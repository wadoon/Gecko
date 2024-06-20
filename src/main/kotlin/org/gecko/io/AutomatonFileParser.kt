package org.gecko.io

import gecko.parser.SystemDefLexer
import gecko.parser.SystemDefParser
import javafx.scene.control.*
import org.antlr.v4.runtime.*
import org.gecko.exceptions.ModelException
import org.gecko.util.graphlayouting.Graphlayouter
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.GeckoViewModel
import java.io.File
import java.io.IOException

/**
 * The AutomatonFileParser is used to import a project from a sys file. It is responsible for parsing a sys file and
 * creating a [GeckoViewModel] from it. It uses the [AutomatonFileVisitor] the file into a
 * [GeckoModel]. And then uses the [ViewModelElementCreator] to create the view model from the model.
 */
class AutomatonFileParser : FileParser {
    @Throws(IOException::class)
    override fun parse(file: File): GeckoViewModel {
        val stream = CharStreams.fromFileName(file.absolutePath)
        val parser = SystemDefParser(CommonTokenStream(SystemDefLexer(stream)))
        val listener = SyntaxErrorListener()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)

        val visitor: AutomatonFileVisitor

        try {
            visitor = AutomatonFileVisitor()
        } catch (e: ModelException) {
            throw RuntimeException("Failed to create an instance of an empty model")
        }

        try {
            visitor.visitModel(parser.model())
        } catch (e: RuntimeException) {
            throw IOException(e.message)
        }
        if (listener.syntaxError) {
            throw IOException(listener.errorMessage)
        }
        if (!visitor.warnings.isEmpty()) {
            showWarnings(visitor.warnings)
        }

        val gvm = GeckoViewModel()
        //val vmVisitor = ViewModelElementCreator(gvm, listOf(), listOf())
        //vmVisitor.traverseModel(gvm.root)
        val graphlayouter = Graphlayouter(gvm)
        graphlayouter.layout()
        return gvm
    }

    fun showWarnings(warnings: Set<String>) {
        val warningAlert = Alert(Alert.AlertType.WARNING)
        warningAlert.title = ResourceHandler.Companion.title
        warningAlert.headerText = ResourceHandler.Companion.parse_header
        warningAlert.contentText = java.lang.String.join(System.lineSeparator(), warnings)
        warningAlert.showAndWait()
    }

    class SyntaxErrorListener : BaseErrorListener() {
        var syntaxError: Boolean = false
        var errorMessage: String? = null

        override fun syntaxError(
            recognizer: Recognizer<*, *>?, offendingSymbol: Any, line: Int, charPositionInLine: Int,
            msg: String, e: RecognitionException
        ) {
            if (errorMessage == null) {
                errorMessage = "$msg at line $line:$charPositionInLine"
            }
            syntaxError = true
        }
    }
}
