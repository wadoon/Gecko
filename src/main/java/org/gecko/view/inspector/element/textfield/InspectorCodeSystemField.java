package org.gecko.view.inspector.element.textfield;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.gecko.actions.ActionManager;
import org.gecko.view.inspector.element.InspectorElement;
import org.gecko.viewmodel.SystemViewModel;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InspectorCodeSystemField implements InspectorElement<CodeArea> {
    private static final int MAX_HEIGHT = 40;

    private final CodeArea managedTextArea = new CodeArea();


    private static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private final ExecutorService executor;

    public InspectorCodeSystemField(ActionManager actionManager, SystemViewModel systemViewModel) {
        managedTextArea.setParagraphGraphicFactory(LineNumberFactory.get(managedTextArea));
        managedTextArea.setContextMenu(new DefaultContextMenu());

        executor = Executors.newSingleThreadExecutor();
        Subscription cleanupWhenDone = managedTextArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .retainLatestUntilLater(executor)
                .supplyTask(this::computeHighlighting)
                .awaitLatest(managedTextArea.multiPlainChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(it -> managedTextArea.setStyleSpans(0, it));

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile("^\\s+");
        managedTextArea.addEventHandler(KeyEvent.KEY_PRESSED, it -> {
            if (it.getCode() == KeyCode.ENTER) {
                int caretPosition = managedTextArea.getCaretPosition();
                int currentParagraph = managedTextArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(managedTextArea.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find()) Platform.runLater(() -> managedTextArea.insertText(caretPosition, m0.group()));
            }
        });

        managedTextArea.clear();
        final var stringProperty = systemViewModel.getCodeProperty();

        Supplier<String> getTextFromModel = () -> {
            var s = stringProperty.get();
            return s == null ? "" : s;
        };

        managedTextArea.appendText(getTextFromModel.get());

        stringProperty.addListener((observable, oldValue, newValue) -> setText(newValue));
        managedTextArea.setPrefHeight(MAX_HEIGHT);
        managedTextArea.setWrapText(true);
        managedTextArea.focusedProperty().addListener(event -> {
            var text = managedTextArea.getText();
            if ((text == null && getTextFromModel.get() == null)
                    || (text != null && text.equals(getTextFromModel.get()))
                    || (getTextFromModel.get() != null && getTextFromModel.get().equals(text))) {
                return;
            }

            if ((text == null || text.isEmpty())) {
                setText(getTextFromModel.get());
            }

            actionManager.run(
                    actionManager.getActionFactory().createChangeCodeSystemViewModelAction(systemViewModel,
                            managedTextArea.getText()));
        });

        /* managedTextArea.prefHeightProperty().bind(
            Bindings.createDoubleBinding(() ->
                            managedTextArea.getFont().getSize() *
                                    managedTextArea.getParagraphs().size() + HEIGHT_THRESHOLD,
                    managedTextArea.fontProperty(),
                    managedTextArea.textProperty()));
         */
    }

    private void setText(String s) {
        managedTextArea.clear();
        managedTextArea.appendText(s);
    }

    /*public void toggleExpand() {
        setPrefHeight(getPrefHeight() == MAX_HEIGHT ? EXPANDED_MAX_HEIGHT : MAX_HEIGHT);
    }*/

    @Override
    public CodeArea getControl() {
        return managedTextArea;
    }

    private Task<StyleSpans<Collection<String>>> computeHighlighting() {
        var text = managedTextArea.getText();
        var t=  new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                Matcher matcher = PATTERN.matcher(text);
                int lastKwEnd = 0;
                StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
                while (matcher.find()) {
                    String styleClass =
                            matcher.group("KEYWORD") != null ? "keyword" :
                                    matcher.group("PAREN") != null ? "paren" :
                                            matcher.group("BRACE") != null ? "brace" :
                                                    matcher.group("BRACKET") != null ? "bracket" :
                                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                                    matcher.group("STRING") != null ? "string" :
                                                                            matcher.group("COMMENT") != null ? "comment" :
                                                                                    null; /* never happens */
                    assert styleClass != null;
                    spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                    spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                    lastKwEnd = matcher.end();
                }
                spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
                return spansBuilder.create();
            }
        };
        executor.execute(t);
        return t;
    }

    public DoubleProperty prefWidthProperty() {
        return managedTextArea.prefHeightProperty();
    }

    private class DefaultContextMenu extends ContextMenu {
        private final MenuItem fold;
        private final MenuItem unfold;
        private final MenuItem print;

        public DefaultContextMenu() {
            fold = new MenuItem("Fold selected text");
            fold.setOnAction(AE -> {
                hide();
                fold();
            });

            unfold = new MenuItem("Unfold from cursor");
            unfold.setOnAction(AE -> {
                hide();
                unfold();
            });

            print = new MenuItem("Print");
            print.setOnAction(AE -> {
                hide();
                print();
            });

            getItems().addAll(fold, unfold, print);
        }

        /**
         * Folds multiple lines of selected text, only showing the first line and hiding the rest.
         */
        private void fold() {
            ((CodeArea) getOwnerNode()).foldSelectedParagraphs();
        }

        /**
         * Unfold the CURRENT line/paragraph if it has a fold.
         */
        private void unfold() {
            CodeArea area = (CodeArea) getOwnerNode();
            area.unfoldParagraphs(area.getCurrentParagraph());
        }

        private void print() {
            System.out.println(((CodeArea) getOwnerNode()).getText());
        }
    }
}
