package org.gecko.view.views.shortcuts;

import java.util.List;
import org.gecko.actions.ActionManager;
import org.gecko.tools.ToolType;
import org.gecko.view.views.EditorView;

public class AutomatonEditorViewShortcutHandler extends ShortcutHandler {
    public AutomatonEditorViewShortcutHandler(ActionManager actionManager, EditorView editorView) {
        super(actionManager, editorView);

        addCreatorShortcuts();
    }

    private void addCreatorShortcuts() {
        List<ToolType> creatorTools = List.of(ToolType.STATE_CREATOR, ToolType.EDGE_CREATOR, ToolType.REGION_CREATOR);
        creatorTools.forEach(tool -> {
            shortcuts.put(tool.getKeyCodeCombination(),
                () -> actionManager.run(actionFactory.createSelectToolAction(tool)));
        });
    }
}
