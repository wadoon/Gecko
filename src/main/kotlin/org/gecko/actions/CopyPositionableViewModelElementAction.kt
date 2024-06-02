package org.gecko.actions

import org.gecko.exceptions.GeckoException
import org.gecko.viewmodel.GeckoViewModel

class CopyPositionableViewModelElementAction internal constructor(var geckoViewModel: GeckoViewModel) : Action() {
    @Throws(GeckoException::class)
    override fun run(): Boolean {
        val visitor = CopyPositionableViewModelElementVisitor(geckoViewModel)

        var copyQueue = geckoViewModel.currentEditor!!.selectionManager.currentSelection.toMutableSet()
        val elementToCopy = copyQueue.map { it.target }
        for (edge in geckoViewModel.currentEditor!!.currentSystem.target.automaton.edges) {
            if (elementToCopy.contains(edge.source) && elementToCopy.contains(edge.destination)) {
                copyQueue.add(geckoViewModel.getViewModelElement(edge))
            }
        }
        for (connection in geckoViewModel.currentEditor!!.currentSystem.target.connections) {
            val element = geckoViewModel.geckoModel.getSystemWithVariable(connection.source)!!
            val sourceSelected = elementToCopy.contains(element)
                    || elementToCopy.contains(connection.source)
            val destinationSelected = (elementToCopy.contains(
                geckoViewModel.geckoModel.getSystemWithVariable(connection.destination)!!
            )
                    || elementToCopy.contains(connection.destination))

            if (sourceSelected && destinationSelected) {
                copyQueue.add(geckoViewModel.getViewModelElement(connection))
            }
        }
        do {
            visitor.failedCopies.clear()
            copyQueue.forEach { it.accept(visitor) }
            copyQueue = HashSet(visitor.failedCopies)
        } while (copyQueue.isNotEmpty())

        geckoViewModel.actionManager.copyVisitor = visitor
        return true
    }

    override fun getUndoAction(actionFactory: ActionFactory): Action? {
        return null
    }
}
