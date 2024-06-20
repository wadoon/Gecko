package org.gecko.actions

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import javafx.geometry.Point2D

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.SystemViewModel
import kotlin.math.max
import kotlin.math.min

class PastePositionableViewModelElementVisitor internal constructor(
    val geckoViewModel: GeckoViewModel,
    val copyVisitor: CopyPositionableViewModelElementVisitor,
    val pasteOffset: Point2D
) : ElementVisitor {

    val pastedElements: MutableSet<PositionableViewModelElement> = HashSet()
    val clipboardToPasted: BiMap<Element, Element> =
        HashBiMap.create()


    val unsuccessfulPastes: MutableSet<Element> = HashSet()

    @Throws(ModelException::class)
    override fun visit(stateFromClipboard: State) {
        val copyResult =
            geckoViewModel.geckoModel.modelFactory.copyState(stateFromClipboard)
        val stateToPaste = copyResult.key
        clipboardToPasted.putAll(copyResult.value)
        val automaton = geckoViewModel.currentEditor!!.currentSystem.automaton
        automaton.addState(stateToPaste)
        if (automaton.startState == null) {
            automaton.startState = stateToPaste
        }
        val stateViewModel = geckoViewModel.viewModelFactory.createStateViewModelFrom(stateToPaste)
        stateViewModel.position = (copyVisitor.elementToPosAndSize[stateFromClipboard]!!.key)
        stateViewModel.size = (copyVisitor.elementToPosAndSize[stateFromClipboard]!!.value)
        clipboardToPasted[stateFromClipboard] = stateToPaste
        pastedElements.add(stateViewModel)
    }

    override fun visit(contract: Contract?) {
    }

    @Throws(MissingViewModelElementException::class)
    override fun visit(connectionFromClipboard: SystemConnection) {
        val connectionToPaste =
            geckoViewModel.geckoModel.modelFactory.copySystemConnection(connectionFromClipboard)
        val pastedSource = clipboardToPasted[connectionFromClipboard.source] as Variable?
        val pastedDestination = clipboardToPasted[connectionFromClipboard.destination] as Variable?
        if (pastedSource == null || pastedDestination == null) {
            unsuccessfulPastes.add(connectionFromClipboard)
            return
        }
        try {
            connectionToPaste.source = (pastedSource)
            connectionToPaste.destination = (pastedDestination)
        } catch (e: ModelException) {
            throw RuntimeException(e)
        }
        geckoViewModel.currentEditor!!.currentSystem.addConnection(connectionToPaste)
        val systemConnectionViewModel =
            geckoViewModel.viewModelFactory.createSystemConnectionViewModelFrom(connectionToPaste)
        pastedElements.add(systemConnectionViewModel)
    }

    @Throws(ModelException::class)
    override fun visit(variableFromClipboard: Variable) {
        val variableToPaste = geckoViewModel.geckoModel.modelFactory.copyVariable(variableFromClipboard)
        geckoViewModel.currentEditor!!.currentSystem.addVariable(variableToPaste)
        val portViewModel = geckoViewModel.viewModelFactory.createPortViewModelFrom(variableToPaste)
        clipboardToPasted[variableFromClipboard] = variableToPaste
        pastedElements.add(portViewModel)

        if (!geckoViewModel.currentEditor!!
                .currentSystem
                .target
                .variables
                .contains(variableFromClipboard)
        ) {
            val systemViewModel = geckoViewModel.getViewModelElement(
                geckoViewModel.currentEditor!!.currentSystem.target
            ) as SystemViewModel
            systemViewModel.addPort(portViewModel)

            portViewModel.position = (copyVisitor.elementToPosAndSize[variableFromClipboard]!!.key)
            portViewModel.size = (copyVisitor.elementToPosAndSize[variableFromClipboard]!!.value)
        }
    }

    @Throws(ModelException::class)
    override fun visit(systemFromClipboard: System) {
        if (systemFromClipboard.parent != null) {
            return
        }
        val copyResult =
            geckoViewModel.geckoModel.modelFactory.copySystem(systemFromClipboard)
        val systemToPaste = copyResult.key
        clipboardToPasted.putAll(copyResult.value)
        geckoViewModel.currentEditor!!.currentSystem.addChild(systemToPaste)
        systemToPaste.parent = geckoViewModel.currentEditor!!.currentSystem.target
        createRecursiveSystemViewModels(systemToPaste)
        clipboardToPasted[systemFromClipboard] = systemToPaste
    }

    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun visit(regionFromClipboard: Region) {
        val regionToPaste = geckoViewModel.geckoModel.modelFactory.copyRegion(regionFromClipboard)
        clipboardToPasted[regionFromClipboard] = regionToPaste
        geckoViewModel.currentEditor!!.currentSystem.automaton.addRegion(regionToPaste)
        val regionViewModel = geckoViewModel.viewModelFactory.createRegionViewModelFrom(regionToPaste)
        regionViewModel.position = (copyVisitor.elementToPosAndSize[regionFromClipboard]!!.key)
        regionViewModel.size = (copyVisitor.elementToPosAndSize[regionFromClipboard]!!.value)
        pastedElements.add(regionViewModel)
    }

    @Throws(ModelException::class, MissingViewModelElementException::class)
    override fun visit(edge: Edge) {
        val copy = geckoViewModel.geckoModel.modelFactory.copyEdge(edge)
        val pastedSource = clipboardToPasted[edge.source] as State?
        val pastedDestination = clipboardToPasted[edge.destination] as State?
        val pastedContract = clipboardToPasted[edge.contract] as Contract?
        if (pastedSource == null || pastedDestination == null) {
            unsuccessfulPastes.add(edge)
            return
        }
        geckoViewModel.currentEditor!!.currentSystem.automaton.addEdge(copy)
        copy.source = (pastedSource)
        copy.destination = (pastedDestination)
        copy.contract = pastedContract!!
        val edgeViewModel = geckoViewModel.viewModelFactory.createEdgeViewModelFrom(copy)
        pastedElements.add(edgeViewModel)
    }

    fun createRecursiveSystemViewModels(systemToPaste: System) {
        val systemFromClipboard = clipboardToPasted.inverse()[systemToPaste] as System?
        val systemViewModel = geckoViewModel.viewModelFactory.createSystemViewModelFrom(systemToPaste)
        systemViewModel.position = (copyVisitor.elementToPosAndSize[systemFromClipboard]!!.getKey())
        pastedElements.add(systemViewModel)
        for (variable in systemToPaste.variables) {
            geckoViewModel.viewModelFactory.createPortViewModelFrom(variable)
        }
        for (state in systemToPaste.automaton.states) {
            val stateViewModel = geckoViewModel.viewModelFactory.createStateViewModelFrom(state)
            stateViewModel.position = (
                    copyVisitor.elementToPosAndSize[clipboardToPasted.inverse()[state]]!!.getKey()
                    )
        }
        for (region in systemToPaste.automaton.regions) {
            try {
                val regionViewModel =
                    geckoViewModel.viewModelFactory.createRegionViewModelFrom(region)
                regionViewModel.position = (
                        copyVisitor.elementToPosAndSize[clipboardToPasted.inverse()[region]]!!.getKey()
                        )
                regionViewModel.size = (
                        copyVisitor.elementToPosAndSize[clipboardToPasted.inverse()[region]]!!.getValue()
                        )
            } catch (e: MissingViewModelElementException) {
                throw RuntimeException(e)
            }
        }
        for (edge in systemToPaste.automaton.edges) {
            try {
                geckoViewModel.viewModelFactory.createEdgeViewModelFrom(edge)
            } catch (e: MissingViewModelElementException) {
                throw RuntimeException(e)
            }
        }
        for (child in systemToPaste.children) {
            createRecursiveSystemViewModels(child)
        }
        for (connection in systemToPaste.connections) {
            try {
                geckoViewModel.viewModelFactory.createSystemConnectionViewModelFrom(connection)
            } catch (e: MissingViewModelElementException) {
                throw RuntimeException(e)
            }
        }
    }

    fun updatePositions() {
        var minPos = Point2D(Double.MAX_VALUE, Double.MAX_VALUE)
        var maxPos = Point2D(-Double.MAX_VALUE, -Double.MAX_VALUE)
        for (element in pastedElements) {
            if (element.size == Point2D.ZERO) {
                continue
            }
            val x = element.position.x
            val y = element.position.y
            minPos = Point2D(min(minPos.x, x), min(minPos.y, y))
            maxPos = Point2D(
                max(maxPos.x, x + element.size.x),
                max(maxPos.y, y + element.size.y)
            )
        }
        val center = minPos.midpoint(maxPos)
        for (element in pastedElements) {
            val pos = element.center
            element.center = pos!!.subtract(center).add(pasteOffset)
        }
    }
}
