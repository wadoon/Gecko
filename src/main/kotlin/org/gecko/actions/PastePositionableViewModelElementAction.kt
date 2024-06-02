package org.gecko.actions

import javafx.geometry.Point2D
import org.gecko.exceptions.GeckoException
import org.gecko.model.*
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement

class PastePositionableViewModelElementAction internal constructor(
    val geckoViewModel: GeckoViewModel,
    center: Point2D
) : Action() {
    val pastedElements: MutableSet<PositionableViewModelElement<*>> = HashSet()
    val pasteOffset = center

    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val copyVisitor = geckoViewModel.actionManager.copyVisitor
            ?: throw GeckoException("Invalid Clipboard. Nothing to paste.")
        if (geckoViewModel.currentEditor!!.isAutomatonEditor != copyVisitor.isAutomatonCopy) {
            return false
        }

        val pasteVisitor =
            PastePositionableViewModelElementVisitor(geckoViewModel, copyVisitor, pasteOffset)
        for (element in copyVisitor.copiedElements) {
            element.accept(pasteVisitor)
        }
        while (!pasteVisitor.unsuccessfulPastes.isEmpty()) {
            val unsuccessfulPastes: Set<Element> = HashSet(pasteVisitor.unsuccessfulPastes)
            pasteVisitor.unsuccessfulPastes.clear()
            for (element in unsuccessfulPastes) {
                element.accept(pasteVisitor)
            }
        }
        pasteVisitor.updatePositions()
        pastedElements.addAll(pasteVisitor.pastedElements)
        val selectAction: Action =
            geckoViewModel.actionManager.actionFactory.createSelectAction(pastedElements, true)
        geckoViewModel.actionManager.run(selectAction)
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory) =
        actionFactory.createDeletePositionableViewModelElementAction(pastedElements)
}
