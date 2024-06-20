package org.gecko.viewmodel

import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import tornadofx.asObservable
import java.util.*

/**
 * Represents a manager for the selection history of an editor view, allowing the navigation through the history of
 * selected elements.
 */
data class SelectionManager(
    val undoSelectionStack: Deque<MutableSet<PositionableViewModelElement>> = ArrayDeque(),
    val redoSelectionStack: Deque<Set<PositionableViewModelElement>> = ArrayDeque(),
    val currentSelectionProperty: SetProperty<PositionableViewModelElement> =
        SimpleSetProperty(FXCollections.observableSet())
) {
    /**
     * Goes back to the previous selection.
     */
    fun goBack() {
        if (undoSelectionStack.isEmpty()) {
            return
        }
        redoSelectionStack.push(HashSet(currentSelectionProperty.get()))
        currentSelectionProperty.set(undoSelectionStack.pop().asObservable())
    }

    /**
     * Goes forward to the next selection.
     */
    fun goForward() {
        if (redoSelectionStack.isEmpty()) {
            return
        }
        undoSelectionStack.push(HashSet(currentSelectionProperty.get()))
        currentSelectionProperty.set(redoSelectionStack.pop().asObservable())
    }

    fun select(element: PositionableViewModelElement) {
        select(setOf(element))
    }

    /**
     * Selects the given elements. If the given elements are already selected, nothing happens. A new selection is made
     * with the given elements. It works by setting the current selection to the given elements and pushing the current
     * selection to the undo stack. The redo stack is cleared because a new selection was made.
     *
     * @param elements the elements to be selected
     */
    fun select(elements: Set<PositionableViewModelElement>) {
        if (elements.isEmpty() || elements == currentSelectionProperty.get()) {
            return
        }
        redoSelectionStack.clear()
        addSelectionToUndoStack(HashSet(currentSelectionProperty.get()))
        currentSelectionProperty.set(elements.asObservable())
    }

    fun deselect(element: PositionableViewModelElement) {
        deselect(setOf(element))
    }

    /**
     * Deselects the given elements. A new selection is made with the remaining elements. If the given elements are not
     * selected, nothing happens.
     *
     * @param elements the elements to be deselected
     */
    fun deselect(elements: Set<PositionableViewModelElement>) {
        if (elements.isEmpty() || elements.stream()
                .noneMatch { o: PositionableViewModelElement -> currentSelectionProperty.get().contains(o) }
        ) {
            return
        }
        redoSelectionStack.clear()
        addSelectionToUndoStack(HashSet(currentSelectionProperty.get()))
        val newSelection: MutableSet<PositionableViewModelElement> = HashSet(currentSelectionProperty.get())
        newSelection.removeAll(elements)
        currentSelectionProperty.set(newSelection.asObservable())
    }

    fun deselectAll() {
        deselect(currentSelectionProperty.get())
    }

    val currentSelection: Set<PositionableViewModelElement>
        get() = HashSet(currentSelectionProperty.get())

    /**
     * Updates all selections by removing the given elements from them. This method is used when elements are removed
     * from the view model. It keeps the selections consistent.
     *
     * @param removedElements the elements that are removed from the view model
     */
    fun updateSelections(removedElements: Set<PositionableViewModelElement>) {
        val selectionToBeRemoved = ArrayDeque<MutableSet<PositionableViewModelElement>>()
        if (removedElements.stream()
                .anyMatch { o: PositionableViewModelElement -> currentSelectionProperty.value.contains(o) }
        ) {
            val newSelection: MutableSet<PositionableViewModelElement> = HashSet(currentSelectionProperty.value)
            newSelection.removeAll(removedElements)
            currentSelectionProperty.set(newSelection.asObservable())
            redoSelectionStack.clear()
        }
        var lastSelection = currentSelection
        for (selection in undoSelectionStack) {
            if (selection.isEmpty()) {
                continue
            }
            if (selection.removeAll(removedElements)) {
                redoSelectionStack.clear()
            }
            if (selection.isEmpty() || selection == lastSelection) {
                selectionToBeRemoved.push(selection)
            }
            lastSelection = selection
        }
        undoSelectionStack.removeAll(selectionToBeRemoved)
    }

    fun updateSelections(removedElement: PositionableViewModelElement) {
        updateSelections(setOf(removedElement))
    }

    fun addSelectionToUndoStack(selection: MutableSet<PositionableViewModelElement>) {
        if (undoSelectionStack.size >= MAX_STACK_SIZE) {
            undoSelectionStack.removeLast()
        }
        undoSelectionStack.push(selection)
    }

    companion object {
        const val MAX_STACK_SIZE = 2000
    }
}
