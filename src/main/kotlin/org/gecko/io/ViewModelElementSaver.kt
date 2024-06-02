package org.gecko.io


import org.gecko.model.*
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.PositionableViewModelElement
import org.gecko.viewmodel.RegionViewModel

/**
 * Performs operations for every [Model-Element][org.gecko.model.Element] from the subtree of a [System],
 * creating for each of them a [ViewModelPropertiesContainer], depending on the attributes of the corresponding
 * [PositionableViewModelElement].
 */
class ViewModelElementSaver internal constructor(val geckoViewModel: GeckoViewModel) {
    val viewModelProperties: MutableList<ViewModelPropertiesContainer> = arrayListOf()
    val startStates: MutableList<StartStateContainer> = arrayListOf()

    fun getViewModelProperties(root: System): List<ViewModelPropertiesContainer> {
        gatherSystemAttributes(root)
        return this.viewModelProperties
    }

    fun gatherSystemAttributes(system: System) {
        for (variable in system.variables) {
            this.savePortViewModelProperties(variable)
        }

        for (systemConnection in system.connections) {
            this.saveSystemConnectionViewModelProperties(systemConnection)
        }

        val automaton = system.automaton

        if (automaton.startState != null) {
            val startStateContainer = StartStateContainer()
            startStateContainer.systemId = system.id
            startStateContainer.startStateName = automaton.startState!!.name
            startStates.add(startStateContainer)
        }

        for (region in automaton.regions) {
            this.saveRegionViewModelProperties(region)
        }

        for (state in automaton.states) {
            this.saveStateViewModelProperties(state)
        }

        for (edge in automaton.edges) {
            this.saveEdgeModelProperties(edge)
        }

        for (child in system.children) {
            this.saveSystemViewModelProperties(child)
            this.gatherSystemAttributes(child)
        }
    }

    fun saveStateViewModelProperties(state: State) {
        val stateViewModelContainer =
            this.getCoordinateContainer(geckoViewModel.getViewModelElement(state))
        viewModelProperties.add(stateViewModelContainer)
    }

    fun saveRegionViewModelProperties(region: Region) {
        val regionViewModelContainer =
            this.getCoordinateContainer(geckoViewModel.getViewModelElement(region))

        val color = (geckoViewModel.getViewModelElement(region) as RegionViewModel).color
        regionViewModelContainer.red = color.red
        regionViewModelContainer.green = color.green
        regionViewModelContainer.blue = color.blue

        viewModelProperties.add(regionViewModelContainer)
    }

    fun saveSystemViewModelProperties(system: System) {
        val systemViewModelContainer =
            this.getCoordinateContainer(geckoViewModel.getViewModelElement(system))
        viewModelProperties.add(systemViewModelContainer)
    }

    fun saveSystemConnectionViewModelProperties(systemConnection: SystemConnection) {
        val systemConnectionViewModelContainer =
            this.getCoordinateContainer(geckoViewModel.getViewModelElement(systemConnection))
        viewModelProperties.add(systemConnectionViewModelContainer)
    }

    fun saveEdgeModelProperties(edge: Edge) {
        val edgeViewModelContainer =
            this.getCoordinateContainer(geckoViewModel.getViewModelElement(edge))
        viewModelProperties.add(edgeViewModelContainer)
    }

    fun savePortViewModelProperties(variable: Variable) {
        val variableViewModelContainer =
            this.getCoordinateContainer(geckoViewModel.getViewModelElement(variable))
        viewModelProperties.add(variableViewModelContainer)
    }

    fun getCoordinateContainer(element: PositionableViewModelElement<*>): ViewModelPropertiesContainer {
        val container = ViewModelPropertiesContainer()
        container.elementId = element.target.id
        container.id = element.id
        container.positionX = element.position.x
        container.positionY = element.position.y
        container.sizeX = element.size.x
        container.sizeY = element.size.y
        return container
    }
}
