package org.gecko.io

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException

import org.gecko.viewmodel.*
import java.io.IOException


/**
 * Performs operations for every [Model-Element][org.gecko.model.Element] from the subtree of a [System],
 * creating for each of them a [ViewModel-Element][org.gecko.viewmodel.Element], depending on the
 * attributes of the corresponding [ViewModelPropertiesContainer].
 */
class ViewModelElementCreator(
    val viewModel: GeckoViewModel,
    viewModelProperties: List<ViewModelPropertiesContainer>,
    startStates: List<StartStateContainer>
) {
    var highestId = 0u
    var foundNullContainer = false
    var foundNonexistentStartState = false
    val viewModelFactory: ViewModelFactory = viewModel.viewModelFactory
    val viewModelProperties = HashMap<UInt, ViewModelPropertiesContainer>()
    val startStates = HashMap<UInt, StartStateContainer?>()

    init {
        for (container in viewModelProperties) {
            this.viewModelProperties[container.elementId] = container
        }
        for (container in startStates) {
            this.startStates[container.systemId] = container
        }
    }

    /**
     * Traverses the model that is beneath the given system and creates the corresponding view model elements.
     *
     * @param system the system to traverse
     * @throws IOException if a view model element cannot be created or no correspondent property container was found
     */
    @Throws(IOException::class)
    fun traverseModel(system: System) {
        for (variable in system.variables) {
            createPortViewModel(variable)
        }

        for (systemConnection in system.connections) {
            createSystemConnectionViewModel(system, systemConnection)
        }

        val automaton = system.automaton
        if (startStates[system.id] != null) {
            val startState = automaton.getStateByName(startStates[system.id]!!.startStateName!!)
            if (automaton.states.isEmpty() || startState == null) {
                foundNonexistentStartState = true
            } else {
                try {
                    automaton.startState = startState
                } catch (e: ModelException) {
                    foundNonexistentStartState = true
                }
            }
        }

        for (state in automaton.states) {
            createStateViewModel(state)
            for (contract in state.contracts) {
                createContractViewModel(contract)
            }
        }

        for (region in automaton.regions) {
            createRegionViewModel(region)
        }

        for (edge in automaton.edges) {
            createEdgeViewModel(edge)
        }

        for (child in system.children) {
            createSystemViewModel(child)
            traverseModel(child)
        }
    }

    @Throws(IOException::class)
    fun createPortViewModel(variable: Variable) {
        val portViewModel = viewModelFactory.createPortViewModelFrom(variable)
        val container = viewModelProperties[variable.id]

        if (container == null) {
            foundNullContainer = true
        }
        setPositionAndSize(portViewModel, container)
        updateHighestId(variable)
    }

    @Throws(IOException::class)
    fun createStateViewModel(state: State) {
        val stateViewModel = viewModelFactory.createStateViewModelFrom(state)
        val container = viewModelProperties[state.id]

        if (container == null) {
            foundNullContainer = true
        }
        setPositionAndSize(stateViewModel, container)
        updateHighestId(state)
    }

    @Throws(IOException::class)
    fun createContractViewModel(contract: ContractViewModel) {
        viewModelFactory.createContractViewModelFrom(contract)
        updateHighestId(contract)
    }

    @Throws(IOException::class)
    fun createRegionViewModel(region: Region) {
        var regionViewModel: RegionViewModel? = null
        try {
            regionViewModel = viewModelFactory.createRegionViewModelFrom(region)
        } catch (e: MissingViewModelElementException) {
            for (state in region.states) {
                val stateViewModel = viewModel.getViewModelElement(state) as StateViewModel
                if (stateViewModel == null) {
                    createStateViewModel(state)
                }
            }
            createRegionViewModel(region)
        }

        if (regionViewModel != null) {
            val container = viewModelProperties[region.id]
            if (container == null) {
                foundNullContainer = true
            } else {
                setPositionAndSize(regionViewModel, container)
                regionViewModel.color = Color.color(container.red, container.green, container.blue)
            }

            updateHighestId(region)
        }
    }

    @Throws(IOException::class)
    fun createEdgeViewModel(edge: Edge) {
        var edgeViewModel: EdgeViewModel? = null
        try {
            edgeViewModel = viewModelFactory.createEdgeViewModelFrom(edge)
        } catch (e: MissingViewModelElementException) {
            val source = viewModel.getViewModelElement(edge.source) as StateViewModel
            if (source == null) {
                viewModelFactory.createStateViewModelFrom(edge.source)
            }
            val destination = viewModel.getViewModelElement(edge.destination) as StateViewModel
            if (destination == null) {
                viewModelFactory.createStateViewModelFrom(edge.destination)
            }
            createEdgeViewModel(edge)
        }

        if (edgeViewModel != null) {
            val container = viewModelProperties[edge.id]

            if (container == null) {
                foundNullContainer = true
            }
            setPositionAndSize(edgeViewModel, container)
            updateHighestId(edge)
        }
    }

    @Throws(IOException::class)
    fun createSystemViewModel(system: System) {
        val systemViewModel = viewModelFactory.createSystemViewModelFrom(system)
        val container = viewModelProperties[system.id]
        if (container == null) {
            foundNullContainer = true
        }
        setPositionAndSize(systemViewModel, container)
        updateHighestId(system)
    }

    @Throws(IOException::class)
    fun createSystemConnectionViewModel(system: System, systemConnection: SystemConnection) {
        var systemConnectionViewModel: SystemConnectionViewModel? = null
        try {
            systemConnectionViewModel = viewModelFactory.createSystemConnectionViewModelFrom(system, systemConnection)
        } catch (e: MissingViewModelElementException) {
            val source = viewModel.getViewModelElement(systemConnection.source) as PortViewModel
            if (source == null) {
                createPortViewModel(systemConnection.source)
            }
            val destination =
                viewModel.getViewModelElement(systemConnection.destination) as PortViewModel
            if (destination == null) {
                createPortViewModel(systemConnection.destination)
            }
            createSystemConnectionViewModel(system, systemConnection)
        }

        if (systemConnectionViewModel != null) {
            val container = viewModelProperties[systemConnection.id]
            if (container == null) {
                foundNullContainer = true
            }
            setPositionAndSize(systemConnectionViewModel, container)
            updateHighestId(systemConnection)
        }
    }

    @Throws(IOException::class)
    fun updateHighestId(element: Element) {
        if (element.id < 0u) {
            throw IOException("Negative IDs are not allowed.")
        }
        if (element.id > highestId) {
            highestId = element.id
        }
    }
}

private fun setPositionAndSize(
    element: PositionableViewModelElement, container: ViewModelPropertiesContainer?
) {
    if (container != null) {
        element.position = Point2D(container.positionX, container.positionY)
        element.size = Point2D(container.sizeX, container.sizeY)
    }
}