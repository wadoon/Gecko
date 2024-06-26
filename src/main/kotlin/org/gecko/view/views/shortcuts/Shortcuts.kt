package org.gecko.view.views.shortcuts

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

/**
 * Enumerates the types of keyboard shortcuts used in the Gecko Graphic Editor. Each
 * [Shortcut][Shortcuts] is described by a [KeyCode] and a [Modifier][KeyCombination.Modifier].
 */
enum class Shortcuts(keyCode: KeyCode, vararg modifiers: KeyCombination.Modifier) {
    NEW(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
    OPEN(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
    SAVE(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
    SAVE_AS(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
    IMPORT(KeyCode.I, KeyCombination.SHORTCUT_DOWN),
    EXPORT(KeyCode.E, KeyCombination.SHORTCUT_DOWN),
    UNDO(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
    REDO(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
    CUT(KeyCode.X, KeyCombination.SHORTCUT_DOWN),
    COPY(KeyCode.C, KeyCombination.SHORTCUT_DOWN),
    PASTE(KeyCode.V, KeyCombination.SHORTCUT_DOWN),
    DELETE(KeyCode.BACK_SPACE, KeyCombination.SHORTCUT_DOWN),
    SELECT_ALL(KeyCode.A, KeyCombination.SHORTCUT_DOWN),
    DESELECT_ALL(KeyCode.A, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
    SELECTION_BACK(KeyCode.U, KeyCombination.SHORTCUT_DOWN),
    SELECTION_FORWARD(KeyCode.U, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
    ZOOM_IN(KeyCode.PLUS, KeyCombination.SHORTCUT_DOWN),
    ZOOM_OUT(KeyCode.MINUS, KeyCombination.SHORTCUT_DOWN),
    SWITCH_EDITOR(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN),
    OPEN_CHILD_SYSTEM_EDITOR(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN),
    OPEN_PARENT_SYSTEM_EDITOR(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN),
    CURSOR_TOOL(KeyCode.A),
    MARQUEE_TOOL(KeyCode.M),
    PAN_TOOL(KeyCode.H),
    ZOOM_TOOL(KeyCode.Z),
    SYSTEM_CREATOR(KeyCode.S),
    CONNECTION_CREATOR(KeyCode.E),
    VARIABLE_BLOCK_CREATOR(KeyCode.V),
    STATE_CREATOR(KeyCode.S),
    EDGE_CREATOR(KeyCode.E),
    REGION_CREATOR(KeyCode.R),
    TOGGLE_APPEARANCE(KeyCode.T, KeyCombination.SHORTCUT_DOWN),
    TOGGLE_SEARCH(KeyCode.F, KeyCombination.SHORTCUT_DOWN),
    FOCUS_SELECTED_ELEMENT(KeyCode.F, KeyCombination.SHIFT_DOWN);

    val keyCodeCombination = KeyCodeCombination(keyCode, *modifiers)

    fun get(): KeyCodeCombination {
        return keyCodeCombination
    }
}
