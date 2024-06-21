package org.gecko.actions

import javafx.geometry.Point2D
import javafx.util.Pair
import org.gecko.exceptions.ModelException

import org.gecko.viewmodel.*

class CopyPositionableViewModelElementVisitor(val geckoViewModel: GeckoViewModel) :
    PositionableViewModelElementVisitor<Void?> {
    val isAutomatonCopy: Boolean = geckoViewModel.currentEditor!!.isAutomatonEditor
    val originalToClipboard = HashMap<Element, Element>()
    val elementToPosAndSize = HashMap<Element?, Pair<Point2D, Point2D>>()


    val failedCopies: MutableSet<PositionableViewModelElement> = HashSet()
    val copiedElements: MutableSet<Element> = HashSet()

    override fun visit(systemViewModel: SystemViewModel): Void? {
        val original = systemViewModel
        val copyResult: Pair<System, Map<Element, Element>>
        try {
            copyResult = geckoViewModel.copySystem(systemViewModel)
        } catch (e: ModelException) {
            failedCopies.add(systemViewModel)
            return null
        }
        val copy = copyResult.key
        originalToClipboard.putAll(copyResult.value)
        originalToClipboard[original] = copy
        savePositionRecursively(original)
        copiedElements.add(copy)
        return null
    }

    override fun visit(regionViewModel: RegionViewModel): Void? {
        val original = regionViewModel
        val copy = geckoViewModel.copyRegion(original)
        originalToClipboard[original] = copy
        savePositionAndSize(copy, regionViewModel)
        copiedElements.add(copy)
        return null
    }

    override fun visit(edgeViewModel: EdgeViewModel): Void? {
        val selection =
            geckoViewModel.currentEditor!!.selectionManager.currentSelection
        if (selection.contains(edgeViewModel.source) && selection.contains(edgeViewModel.destination)) {
            val original = edgeViewModel
            val copy = geckoViewModel.copyEdge(original)
            val sourceOnClipboard = originalToClipboard[original.source] as State?
            val destinationOnClipboard = originalToClipboard[original.destination] as State?
            val contractOnClipboard = originalToClipboard[original.contract] as Contract?
            if (sourceOnClipboard == null || destinationOnClipboard == null) {
                failedCopies.add(edgeViewModel)
                return null
            }
            copy.source = (sourceOnClipboard)
            copy.destination = (destinationOnClipboard)
            copy.contract = contractOnClipboard!!
            originalToClipboard[original] = copy
            copiedElements.add(copy)
        }
        return null
    }

    override fun visit(stateViewModel: StateViewModel): Void? {
        val original = stateViewModel
        val copyResult =
            geckoViewModel.copyState(original)
        val copy = copyResult.key
        originalToClipboard.putAll(copyResult.value)
        originalToClipboard[original] = copy
        savePositionAndSize(copy, stateViewModel)
        copiedElements.add(copy)
        return null
    }

    override fun visit(portViewModel: PortViewModel): Void? {
        val original = portViewModel
        val copy = geckoViewModel.copyVariable(original)
        originalToClipboard[original] = copy
        savePositionAndSize(copy, portViewModel)
        copiedElements.add(copy)
        return null
    }

    override fun visit(automatonViewModel: AutomatonViewModel): Void? {
        TODO("Not yet implemented")
    }

    override fun visit(systemConnectionViewModel: SystemConnectionViewModel): Void? {
        val selection =
            geckoViewModel.currentEditor!!.selectionManager.currentSelection
        val sourceSystemViewModel = (
                geckoViewModel.currentEditor!!.currentSystem
                    .getChildSystemWithVariable(systemConnectionViewModel.source)!!
                ) as SystemViewModel

        val destinationSystemViewModel = (
                geckoViewModel.currentEditor!!.currentSystem
                    .getChildSystemWithVariable(systemConnectionViewModel.destination)!!
                ) as SystemViewModel

        val original = systemConnectionViewModel

        val sourceSelected = selection.contains(sourceSystemViewModel) || selection.contains(
            (original.source)
        )
        val destinationSelected = selection.contains(destinationSystemViewModel) || selection.contains(
            (original.destination)
        )

        if (sourceSelected && destinationSelected) {
            val copy = geckoViewModel.copySystemConnection(original)
            val sourceOnClipboard = originalToClipboard[original.source] as Variable?
            val destinationOnClipboard = originalToClipboard[original.destination] as Variable?
            if (sourceOnClipboard == null || destinationOnClipboard == null) {
                failedCopies.add(systemConnectionViewModel)
                return null
            }
            try {
                copy.source = (sourceOnClipboard)
                copy.destination = (destinationOnClipboard)
            } catch (e: ModelException) {
                throw RuntimeException(e)
            }
            originalToClipboard[original] = copy
            copiedElements.add(copy)
        }
        return null
    }

    fun savePositionRecursively(original: System) {
        val copy = originalToClipboard[original] as System?
        savePositionAndSize(copy, (original))
        for (state in original.automaton.states) {
            savePositionAndSize(originalToClipboard[state], (state))
        }
        for (region in original.automaton.regions) {
            savePositionAndSize(originalToClipboard[region], (region))
        }
        for (child in original.children) {
            savePositionRecursively(child)
        }
    }

    fun savePositionAndSize(key: Element?, positionSource: PositionableViewModelElement) {
        elementToPosAndSize[key] = Pair(positionSource.position, positionSource.size)
    }
}
