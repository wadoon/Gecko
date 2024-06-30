package org.gecko.io

import gecko.parser.SystemDefLexer
import gecko.parser.SystemDefParser
import java.io.File
import java.io.IOException
import javafx.scene.control.*
import org.antlr.v4.runtime.*
import org.gecko.util.graphlayouting.Graphlayouter
import org.gecko.view.ResourceHandler
import org.gecko.viewmodel.GModel

/**
 * The AutomatonFileParser is used to import a project from a sys file. It is responsible for
 * parsing a sys file and creating a [GModel] from it. It uses the [AutomatonFileVisitor] the file
 * into a [GModel]. And then uses the [ViewModelElementCreator] to create the view model from the
 * model.
 */
class AutomatonFileParser : FileParser {
    @Throws(IOException::class)
    override fun parse(file: File): GModel {
        val stream = CharStreams.fromPath(file.toPath())
        val parser = SystemDefParser(CommonTokenStream(SystemDefLexer(stream)))
        val listener = SyntaxErrorListener()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)

        val visitor = AutomatonFileVisitor()
        val gvm = visitor.visitModel(parser.model()).let { visitor.model }

        /*
        if (visitor.warnings.isNotEmpty()) {
            showWarnings(visitor.warnings)
        }
        */

        Graphlayouter(gvm).layout()
        return gvm
    }

    fun showWarnings(warnings: Set<String>) {
        val warningAlert = Alert(Alert.AlertType.WARNING)
        warningAlert.title = ResourceHandler.title
        warningAlert.headerText = ResourceHandler.parse_header
        warningAlert.contentText = java.lang.String.join(System.lineSeparator(), warnings)
        warningAlert.showAndWait()
    }

    class SyntaxErrorListener : BaseErrorListener() {
        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException
        ) {
            val errorMessage = "$msg at line $line:$charPositionInLine"
            throw ParseException(errorMessage)
        }
    }
}

data class ParseException(val errorMessage: String) : RuntimeException(errorMessage)
