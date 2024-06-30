package org.gecko.io


import org.gecko.viewmodel.*

/**
 * Performs operations for every [Model-Element][org.gecko.model.Element] from the subtree of a [System],
 * creating for each of them a [ViewModelPropertiesContainer], depending on the attributes of the corresponding
 * [PositionableElement].
 */
class ViewModelElementSaver(val gModel: GModel) {
    val viewModelProperties: MutableList<ViewModelPropertiesContainer> = arrayListOf()
    val startStates: MutableList<StartStateContainer> = arrayListOf()

    fun getViewModelProperties(root: System): List<ViewModelPropertiesContainer> {
        gatherSystemAttributes(root)
        return this.viewModelProperties
    }

    fun gatherSystemAttributes(system: System) {
        for (variable in system.ports) {
            this.savePortViewModelProperties(variable)
        }

        for (systemConnection in system.connections) {
            this.saveSystemConnectionViewModelProperties(systemConnection)
        }

        val automaton = system.automaton

        /*if (automaton.startState != null) {
            val startStateContainer = StartStateContainer()
            //startStateContainer.systemId = system.hashCode()
            startStateContainer.startStateName = automaton.startState!!.name
            startStates.add(startStateContainer)
        }*/

        for (region in automaton.regions) {
            this.saveRegionViewModelProperties(region)
        }

        for (state in automaton.states) {
            this.saveStateViewModelProperties(state)
        }

        for (edge in automaton.edges) {
            this.saveEdgeModelProperties(edge)
        }

        for (child in system.subSystems) {
            this.saveSystemViewModelProperties(child)
            this.gatherSystemAttributes(child)
        }
    }

    fun saveStateViewModelProperties(state: State) {
        val stateViewModelContainer = this.getCoordinateContainer(state)
        viewModelProperties.add(stateViewModelContainer)
    }

    fun saveRegionViewModelProperties(region: Region) {
        val regionViewModelContainer = this.getCoordinateContainer(region)

        val color = region.color
        regionViewModelContainer.red = color.red
        regionViewModelContainer.green = color.green
        regionViewModelContainer.blue = color.blue

        viewModelProperties.add(regionViewModelContainer)
    }

    fun saveSystemViewModelProperties(system: System) {
        val systemViewModelContainer = this.getCoordinateContainer((system))
        viewModelProperties.add(systemViewModelContainer)
    }

    fun saveSystemConnectionViewModelProperties(systemConnection: SystemConnection) {
        val systemConnectionViewModelContainer = this.getCoordinateContainer((systemConnection))
        viewModelProperties.add(systemConnectionViewModelContainer)
    }

    fun saveEdgeModelProperties(edge: Edge) {
        val edgeViewModelContainer = this.getCoordinateContainer(edge)
        viewModelProperties.add(edgeViewModelContainer)
    }

    fun savePortViewModelProperties(variable: Port) {
        val variableViewModelContainer = this.getCoordinateContainer(variable)
        viewModelProperties.add(variableViewModelContainer)
    }

    fun getCoordinateContainer(element: PositionableElement): ViewModelPropertiesContainer {
        val container = ViewModelPropertiesContainer()
        //container.elementId = element.hashCode()
        container.id = element.hashCode()
        container.positionX = element.position.x
        container.positionY = element.position.y
        container.sizeX = element.size.x
        container.sizeY = element.size.y
        return container
    }
}
