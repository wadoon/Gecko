package org.gecko.view.inspector.element.textfield

import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import java.util.regex.Pattern
import javafx.application.Platform
import javafx.beans.Observable
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.gecko.actions.Action
import org.gecko.actions.ActionManager
import org.gecko.view.inspector.element.InspectorElement
import org.gecko.viewmodel.*

abstract class InspectorAreaField(
    actionManager: ActionManager,
    stringProperty: StringProperty,
    isEmptyAllowed: Boolean
) : TextArea(), InspectorElement<TextArea> {
    init {
        text = stringProperty.get()
        stringProperty.addListener { _: ObservableValue<out String?>?, _: String?, newValue: String?
            ->
            text = newValue
        }
        prefHeight = MAX_HEIGHT.toDouble()
        isWrapText = true

        focusedProperty().addListener { _: Observable? ->
            if (
                (text == null && stringProperty.get() == null) ||
                    (text != null && text == stringProperty.get()) ||
                    (stringProperty.get() != null && stringProperty.get() == text)
            ) {
                return@addListener
            }
            if ((text == null || text.isEmpty()) && !isEmptyAllowed) {
                text = stringProperty.get()
            }
            action?.let { actionManager.run(it) }
        }
    }

    protected abstract val action: Action?

    override val control
        get() = this

    fun toggleExpand() {
        prefHeight =
            (if (prefHeight == MAX_HEIGHT.toDouble()) EXPANDED_MAX_HEIGHT else MAX_HEIGHT)
                .toDouble()
    }

    companion object {
        const val MAX_HEIGHT = 40
        const val EXPANDED_MAX_HEIGHT = 90
    }
}

class InspectorCodeSystemField(actionManager: ActionManager, System: System) :
    InspectorElement<CodeArea> {
    /*public void toggleExpand() {
        setPrefHeight(getPrefHeight() == MAX_HEIGHT ? EXPANDED_MAX_HEIGHT : MAX_HEIGHT);
    }*/
    override val control: CodeArea = CodeArea()

    val executor: ExecutorService

    init {
        control.paragraphGraphicFactory = LineNumberFactory.get(control)
        control.contextMenu = DefaultContextMenu()

        executor = Executors.newSingleThreadExecutor()
        val cleanupWhenDone =
            control
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .retainLatestUntilLater(executor)
                .supplyTask { this.computeHighlighting() }
                .awaitLatest(control.multiPlainChanges())
                .filterMap { t ->
                    if (t.isSuccess) {
                        return@filterMap Optional.of(t.get()!!)
                    } else {
                        t.failure.printStackTrace()
                        return@filterMap Optional.empty()
                    }
                }
                .subscribe { control.setStyleSpans(0, it) }

        // auto-indent: insert previous line's indents on enter
        val whiteSpace = Pattern.compile("^\\s+")
        control.addEventHandler(KeyEvent.KEY_PRESSED) { it: KeyEvent ->
            if (it.code == KeyCode.ENTER) {
                val caretPosition = control.caretPosition
                val currentParagraph = control.currentParagraph
                val m0 = whiteSpace.matcher(control.getParagraph(currentParagraph - 1).segments[0])
                if (m0.find()) Platform.runLater { control.insertText(caretPosition, m0.group()) }
            }
        }

        control.clear()
        val stringProperty = System.codeProperty

        val getTextFromModel = Supplier {
            val s = stringProperty.get()
            s ?: ""
        }

        control.appendText(getTextFromModel.get())

        stringProperty.addListener {
            observable: ObservableValue<out String?>?,
            oldValue: String?,
            newValue: String? ->
            setText(newValue)
        }
        control.prefHeight = MAX_HEIGHT.toDouble()
        control.isWrapText = true
        control.focusedProperty().addListener { event: Observable? ->
            val text = control.text
            if (
                (text == null && getTextFromModel.get() == null) ||
                    (text != null && text == getTextFromModel.get()) ||
                    (getTextFromModel.get() != null && getTextFromModel.get() == text)
            ) {
                return@addListener
            }

            if ((text == null || text.isEmpty())) {
                setText(getTextFromModel.get())
            }
            actionManager.run(
                actionManager.actionFactory.createChangeCodeSystemViewModelAction(
                    System,
                    control.text
                )
            )
        }

        /* managedTextArea.prefHeightProperty().bind(
           Bindings.createDoubleBinding(() ->
                           managedTextArea.getFont().getSize() *
                                   managedTextArea.getParagraphs().size() + HEIGHT_THRESHOLD,
                   managedTextArea.fontProperty(),
                   managedTextArea.textProperty()));
        */
    }

    fun setText(s: String?) {
        control.clear()
        control.appendText(s)
    }

    fun computeHighlighting(): Task<StyleSpans<Collection<String>>> {
        val text = control.text
        val t: Task<StyleSpans<Collection<String>>> =
            object : Task<StyleSpans<Collection<String>>>() {
                override fun call(): StyleSpans<Collection<String>> {
                    val matcher = PATTERN.matcher(text)
                    var lastKwEnd = 0
                    val spansBuilder = StyleSpansBuilder<Collection<String>>()
                    while (matcher.find()) {
                        val styleClass =
                            checkNotNull(
                                if (matcher.group("KEYWORD") != null) "keyword"
                                else if (matcher.group("PAREN") != null) "paren"
                                else if (matcher.group("BRACE") != null) "brace"
                                else if (matcher.group("BRACKET") != null) "bracket"
                                else if (matcher.group("SEMICOLON") != null) "semicolon"
                                else if (matcher.group("STRING") != null) "string"
                                else if (matcher.group("COMMENT") != null) "comment" else null
                            ) /* never happens */
                        spansBuilder.add(emptyList(), matcher.start() - lastKwEnd)
                        spansBuilder.add(setOf(styleClass), matcher.end() - matcher.start())
                        lastKwEnd = matcher.end()
                    }
                    spansBuilder.add(emptyList(), text.length - lastKwEnd)
                    return spansBuilder.create()
                }
            }
        executor.execute(t)
        return t
    }

    fun prefWidthProperty(): DoubleProperty {
        return control.prefHeightProperty()
    }

    inner class DefaultContextMenu : ContextMenu() {
        val fold = MenuItem("Fold selected text")
        val unfold: MenuItem
        val print: MenuItem

        init {
            fold.onAction = EventHandler { AE: ActionEvent? ->
                hide()
                fold()
            }

            unfold = MenuItem("Unfold from cursor")
            unfold.onAction = EventHandler { AE: ActionEvent? ->
                hide()
                unfold()
            }

            print = MenuItem("Print")
            print.onAction = EventHandler { AE: ActionEvent? ->
                hide()
                print()
            }

            items.addAll(fold, unfold, print)
        }

        /**
         * Folds multiple lines of selected text, only showing the first line and hiding the rest.
         */
        fun fold() {
            (ownerNode as CodeArea).foldSelectedParagraphs()
        }

        /** Unfold the CURRENT line/paragraph if it has a fold. */
        fun unfold() {
            val area = ownerNode as CodeArea
            area.unfoldParagraphs(area.currentParagraph)
        }

        fun print() {
            println((ownerNode as CodeArea).text)
        }
    }

    companion object {
        const val MAX_HEIGHT = 40

        val KEYWORDS =
            arrayOf(
                "abstract",
                "assert",
                "boolean",
                "break",
                "byte",
                "case",
                "catch",
                "char",
                "class",
                "const",
                "continue",
                "default",
                "do",
                "double",
                "else",
                "enum",
                "extends",
                "final",
                "finally",
                "float",
                "for",
                "goto",
                "if",
                "implements",
                "import",
                "instanceof",
                "int",
                "interface",
                "long",
                "native",
                "new",
                "package",
                "private",
                "protected",
                "public",
                "return",
                "short",
                "static",
                "strictfp",
                "super",
                "switch",
                "synchronized",
                "this",
                "throw",
                "throws",
                "transient",
                "try",
                "void",
                "volatile",
                "while"
            )

        val KEYWORD_PATTERN = "\\b(" + java.lang.String.join("|", *KEYWORDS) + ")\\b"
        const val PAREN_PATTERN = "\\(|\\)"
        const val BRACE_PATTERN = "\\{|\\}"
        const val BRACKET_PATTERN = "\\[|\\]"
        const val SEMICOLON_PATTERN = "\\;"
        const val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
        const val COMMENT_PATTERN =
            ("//[^\n]*" +
                "|" +
                "/\\*(.|\\R)*?\\*/" // for whole text processing (text blocks)
                +
                "|" +
                "/\\*[^\\v]*" +
                "|" +
                "^\\h*\\*([^\\v]*|/)") // for visible paragraph processing (line by line)

        val PATTERN: Pattern =
            Pattern.compile(
                "(?<KEYWORD>" +
                    KEYWORD_PATTERN +
                    ")" +
                    "|(?<PAREN>" +
                    PAREN_PATTERN +
                    ")" +
                    "|(?<BRACE>" +
                    BRACE_PATTERN +
                    ")" +
                    "|(?<BRACKET>" +
                    BRACKET_PATTERN +
                    ")" +
                    "|(?<SEMICOLON>" +
                    SEMICOLON_PATTERN +
                    ")" +
                    "|(?<STRING>" +
                    STRING_PATTERN +
                    ")" +
                    "|(?<COMMENT>" +
                    COMMENT_PATTERN +
                    ")"
            )
    }
}

/**
 * A concrete representation of an [InspectorAreaField] for a [Region], through which the invariant
 * of the region can be changed.
 */
class InspectorInvariantField(val actionManager: ActionManager, val Region: Region) :
    InspectorAreaField(actionManager, Region.invariant.valueProperty, false) {
    override val action: Action
        get() =
            actionManager.actionFactory.createChangeInvariantViewModelElementAction(Region, text)
}

/**
 * A concrete representation of an [InspectorTextField] for a [Port], through which the value of the
 * variable can be changed.
 */
class InspectorVariableValueField(actionManager: ActionManager, val Port: Port) :
    InspectorTextField(Port.valueProperty, actionManager) {
    override val action: Action
        get() = actionManager.actionFactory.createChangeVariableValuePortViewModelAction(Port, text)

    override fun updateText() {
        parent.requestFocus()
        if (text != null && text == Port.value) {
            return
        }
        actionManager.run(action)
        text = Port.value
    }
}

/**
 * A concrete representation of an [InspectorTextField] for a [Renamable], through which the name of
 * the element can be changed.
 */
class InspectorRenameField(actionManager: ActionManager, val renamable: Renamable) :
    InspectorTextField(renamable.nameProperty, actionManager) {
    override val action: Action
        get() = actionManager.actionFactory.createRenameViewModelElementAction(renamable, text)
}

/**
 * Represents a type of [Spinner] encapsulating an [Integer] and implementing the [InspectorElement]
 * interface. Used to change the priority of an [Edge].
 */
class InspectorPriorityField(val actionManager: ActionManager, val Edge: Edge) :
    Spinner<Int>(MIN_PRIORITY, MAX_PRIORITY, Edge.priority), InspectorElement<Spinner<Int>> {
    init {
        isEditable = true
        editor.textProperty().addListener { _, oldValue, newValue ->
            if (!newValue.matches("-?\\d*".toRegex())) {
                editor.text = oldValue
            }
        }
        Edge.priorityProperty.addListener { event: Observable? ->
            valueFactory.setValue(Edge.priority)
        }

        onKeyPressed =
            EventHandler<KeyEvent> { event: KeyEvent ->
                if (event.code != KeyCode.ENTER) {
                    return@EventHandler
                }
                parent.requestFocus()
                if (editor.text.isEmpty()) {
                    editor.text = Edge.priority.toString()
                    commitValue()
                    return@EventHandler
                }
                if (value == Edge.priority) {
                    return@EventHandler
                }
                actionManager.run(
                    actionManager.actionFactory.createModifyEdgeViewModelPriorityAction(
                        Edge,
                        value!!
                    )
                )
            }
    }

    override fun decrement(steps: Int) {
        super.decrement(steps)
        actionManager.run(
            actionManager.actionFactory.createModifyEdgeViewModelPriorityAction(Edge, value!!)
        )
    }

    override fun increment(steps: Int) {
        super.increment(steps)
        actionManager.run(
            actionManager.actionFactory.createModifyEdgeViewModelPriorityAction(Edge, value!!)
        )
    }

    override val control
        get() = this

    companion object {
        const val MAX_PRIORITY = 100
        const val MIN_PRIORITY = 0
    }
}

/**
 * A concrete representation of an [TextField] implementing the [InspectorElement] interface, which
 * encapsulates a [TextField].
 */
abstract class InspectorTextField
protected constructor(val stringProperty: StringProperty, val actionManager: ActionManager) :
    TextField(), InspectorElement<TextField> {
    init {
        text = stringProperty.get()
        stringProperty.addListener {
            observable: ObservableValue<out String?>?,
            oldValue: String?,
            newValue: String? ->
            text = newValue
        }
        onAction = EventHandler { event: ActionEvent? -> updateText() }
        focusedProperty().addListener {
            observable: ObservableValue<out Boolean?>?,
            oldValue: Boolean?,
            newValue: Boolean? ->
            if (!newValue!!) {
                updateText()
            }
        }
    }

    protected open fun updateText() {
        if (text.isEmpty()) {
            text = stringProperty.get()
        }
        parent.requestFocus()
        if (text == stringProperty.get()) {
            return
        }
        action?.let { actionManager.run(it) }
        text = stringProperty.get()
    }

    protected abstract val action: Action?

    override val control
        get() = this
}

/**
 * A concrete representation of an [InspectorAreaField] for a [Contract], through which the
 * precondition of the contract can be changed.
 */
class InspectorPreconditionField(val actionManager: ActionManager, val Contract: Contract) :
    InspectorAreaField(actionManager, Contract.preCondition.valueProperty, false) {
    override val action: Action
        get() =
            actionManager.actionFactory.createChangePreconditionViewModelElementAction(
                Contract,
                text
            )
}

/**
 * A concrete representation of an [InspectorAreaField] for a [Contract], through which the
 * postcondition of the contract can be changed.
 */
class InspectorPostconditionField(val actionManager: ActionManager, val Contract: Contract) :
    InspectorAreaField(actionManager, Contract.postCondition.valueProperty, false) {
    override val action: Action
        get() =
            actionManager.actionFactory.createChangePostconditionViewModelElementAction(
                Contract,
                text
            )
}
