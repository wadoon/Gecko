package org.gecko.viewmodel

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import org.gecko.actions.*
import org.gecko.exceptions.ModelException

/**
 * Represents a factory for the view model elements of a Gecko project. Provides methods for the creation of each
 * element. The create[Element]ViewModelIn methods are used to create a new view model element and also new model
 * element. The create[Element]ViewModelFrom methods are used to create a new view model element from an existing model
 * element.
 */
class ViewModelFactory(
    val actionManager: ActionManager,
    val gModel: GModel,
) {
    /**
     * The id of the next view model element to be created.
     */
    var viewModelElementId = 0

    fun createEditorViewModel(
        System: System, parentSystem: System?, isAutomatonEditor: Boolean
    ): EditorViewModel {
        return EditorViewModel(actionManager, System, parentSystem, isAutomatonEditor)
    }

    /**
     * If no start state is set in the parent system, the new state will be set as the start state.
     */
    @Throws(ModelException::class)
    fun createState(parentSystem: System): State =
        createState(parentSystem.automaton)

    fun createState(parentSystem: Automaton): State {
        val result = parentSystem.createState()
        gModel.addViewModelElement(result)
        return result
    }

    /**
     * New ContractViewModels are created for the contracts of the state. If the state is the start state of a system,
     * the system's start state is set to the new state.
     */
//    fun createStateViewModelFrom(): StateViewModel {
//        val result = StateViewModel()
//        for (contract in state.contracts) {
//            val contractViewModel = createContractViewModelFrom(contract)
//            result.addContract(contractViewModel)
//        }
//        geckoViewModel.addViewModelElement(result)
//        updateStartState(state)
//        return result
//    }

    @Throws(ModelException::class)
    fun createEdgeViewModelIn(parentSystem: System, source: State, destination: State)
            : Edge {
        val result = Edge(source, destination)
        parentSystem.automaton.edges.add(result)
        gModel.addViewModelElement(result)
        return result
    }

    /**
     * Expects the source and destination of the edge to be in the view model.
     */
//    @Throws(MissingViewModelElementException::class)
//    fun createEdgeViewModelFrom(edge: Edge): EdgeViewModel {
//        val source = geckoViewModel.getViewModelElement(edge.source!!) as StateViewModel
//        val destination = geckoViewModel.getViewModelElement(edge.destination!!) as StateViewModel
//        if (source == null || destination == null) {
//            throw MissingViewModelElementException("Missing source or destination for edge.")
//        }
//        val result = EdgeViewModel(newViewModelElementId, edge, source, destination)
//        if (edge.contract != null) {
//            //This should never be null because the Edge Model Element has a contract that should be coming
//            //from its source
//            val contract = source.contractsProperty
//                
//                .filter { contractViewModel: ContractViewModel -> contractViewModel.target == edge.contract }
//                .findFirstOrNull()
//                .orElse(null)
//            result.contract = contract
//        }
//        geckoViewModel.addViewModelElement(result)
//        return result
//    }

    @Throws(ModelException::class)
    fun createSystemConnectionViewModelIn(
        parentSystem: System, source: Port, destination: Port
    ): SystemConnection {
        val result = SystemConnection()
        result.source = source
        result.destination = destination
        parentSystem.connections.add(result)
        gModel.addViewModelElement(result)
        setSystemConnectionEdgePoints(parentSystem, source, destination, result)
        return result
    }

    /**
     * Expects the source and destination of the system connection to be in the view model.
     */
//    @Throws(MissingViewModelElementException::class)
//    fun createSystemConnectionViewModelFrom(
//        system: System?, systemConnection: SystemConnection
//    ): SystemConnectionViewModel {
//        val source = geckoViewModel.getViewModelElement(systemConnection.source!!) as PortViewModel
//        val destination =
//            geckoViewModel.getViewModelElement(systemConnection.destination!!) as PortViewModel
//        if (source == null || destination == null) {
//            throw MissingViewModelElementException("Missing source or destination for system connection.")
//        }
//        val result =
//            SystemConnectionViewModel(newViewModelElementId, systemConnection, source, destination)
//        geckoViewModel.addViewModelElement(result)
//        setSystemConnectionEdgePoints(
//            geckoViewModel.getViewModelElement(system!!) as SystemViewModel, source, destination,
//            result
//        )
//        // Since target is already up-to-date and we're building from target, we don't need to call updateTarget
//        return result
//    }

    /**
     * Expects the source and destination of the system connection to be in the view model.
     */
//    @Throws(MissingViewModelElementException::class)
//    fun createSystemConnectionViewModelFrom(systemConnection: SystemConnection): SystemConnectionViewModel {
//        return createSystemConnectionViewModelFrom(
//            geckoViewModel.currentEditor!!.currentSystem.target,
//            systemConnection
//        )
//    }

    fun createSystem(parentSystem: System): System {
        val result = System()
        parentSystem.subSystems.add(result)
        gModel.addViewModelElement(result)
        return result
    }

    /**
     * Missing PortViewModels are created for the variables of the system.
     */
//    fun createSystemViewModelFrom(system: System): SystemViewModel {
//        val result = SystemViewModel()
//        for (variable in system.variables) {
//            var portViewModel = geckoViewModel.getViewModelElement(variable!!) as PortViewModel
//            if (portViewModel == null) {
//                portViewModel = createPortViewModelFrom(variable)
//            }
//            result.addPort(portViewModel)
//        }
//        geckoViewModel.addViewModelElement(result)
//        return result
//    }

//    fun createSystemViewModelForChildren(system: System) {
//        system.children.forEach { child ->
//            if (geckoViewModel.getViewModelElement(child) != null) {
//                return@forEach
//            }
//            val childViewModel = createSystemViewModelFrom(child)
//            geckoViewModel.addViewModelElement(childViewModel)
//            createSystemViewModelForChildren(child)
//        }
//
//        system.automaton!!.states.forEach { state ->
//            if (geckoViewModel.getViewModelElement(state!!) != null) {
//                return@forEach
//            }
//            val stateViewModel = createStateViewModelFrom(state)
//            geckoViewModel.addViewModelElement(stateViewModel)
//        }
//    }

    fun createRegion(parentSystem: System) = createRegion(parentSystem.automaton)

    fun createRegion(automaton: Automaton): Region {
        val result = Region(Contract())
        automaton.addRegion(result)
        // Check for states in the region
        for (state in automaton.states) {
            result.checkStateInRegion(state!!)
        }

        gModel.addViewModelElement(result)
        return result
    }

    fun createPort(System: System): Port {
        val result = Port()
        System.addPort(result)
        gModel.addViewModelElement(result)
        return result
    }

    @Throws(ModelException::class)
    fun createContractViewModelIn(state: State): Contract {
        val result = Contract()
        state.addContract(result)
        return result
    }

    val newViewModelElementId: Int
        get() = viewModelElementId++

    fun updateStartState(state: State) {
        val root = gModel.root
        val parentSystem = findSystemWithState(root, state)
        /*if (parentSystem != null && state == parentSystem.automaton.startState) {
            val parentSystemViewModel = parentSystem
            state.isStartState = true
        }*/
    }

    fun findSystemWithState(parentSystem: System, state: State): System? {
        if (parentSystem.automaton.states.contains(state)) {
            return parentSystem
        }
        if (!parentSystem.subSystems.isEmpty()) {
            for (childSystem in parentSystem.subSystems) {
                val result = findSystemWithState(childSystem, state)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    fun calculateEndPortPosition(
        position: Point2D,
        size: Point2D,
        visibility: Visibility,
        isPort: Boolean
    ): Point2D {
        val sign = if (isPort) 1 else -1
        return position.add(size.multiply(0.5))
            .subtract((if (visibility == Visibility.INPUT) 1 else -1) * sign * size.x / 2, 0.0)
    }

    fun isPort(System: System, Port: Port): Boolean {
        return System.ports.find { Port == it } == null
    }

    fun setSystemConnectionEdgePoints(
        parentSystem: System, source: Port, destination: Port,
        result: SystemConnection
    ) {
        val sourceIsPort = isPort(parentSystem, source)
        val destIsPort = isPort(parentSystem, destination)
        val sourcePosition: Property<Point2D>

        // position the line at the tip of the port
        if (sourceIsPort) {
            sourcePosition = SimpleObjectProperty(
                calculateEndPortPosition(
                    source.systemPortPositionProperty.value,
                    source.systemPortSizeProperty.value, source.visibility, true
                )
            )

            source.systemPortPositionProperty
                .addListener { observable: ObservableValue<out Point2D?>?, oldValue: Point2D?, newValue: Point2D? ->
                    sourcePosition.setValue(
                        calculateEndPortPosition(
                            source.systemPortPositionProperty.value,
                            source.systemPortSizeProperty.value, source.visibility, true
                        )
                    )
                }
        } else {
            sourcePosition = SimpleObjectProperty(
                calculateEndPortPosition(source.position, source.size, source.visibility, false)
            )

            source.positionProperty.addListener { observable: ObservableValue<out Point2D?>?, oldValue: Point2D?, newValue: Point2D? ->
                sourcePosition.setValue(
                    calculateEndPortPosition(source.position, source.size, source.visibility, false)
                )
            }
        }

        val destinationPosition: Property<Point2D>

        if (destIsPort) {
            destinationPosition = SimpleObjectProperty(
                calculateEndPortPosition(
                    destination.systemPortPositionProperty.value,
                    destination.systemPortSizeProperty.value, destination.visibility, true
                )
            )

            destination.systemPortPositionProperty.addListener { observable: ObservableValue<out Point2D?>?, oldValue: Point2D?, newValue: Point2D? ->
                destinationPosition.setValue(
                    calculateEndPortPosition(
                        destination.systemPortPositionProperty.value,
                        destination.systemPortSizeProperty.value, destination.visibility, true
                    )
                )
            }
        } else {
            destinationPosition = SimpleObjectProperty(
                calculateEndPortPosition(
                    destination.position, destination.size, destination.visibility,
                    false
                )
            )

            destination.positionProperty.addListener { observable: ObservableValue<out Point2D?>?, oldValue: Point2D?, newValue: Point2D? ->
                destinationPosition.setValue(
                    calculateEndPortPosition(
                        destination.position, destination.size,
                        destination.visibility, false
                    )
                )
            }
        }

        result.edgePoints.add(sourcePosition.value)
        result.edgePoints.add(destinationPosition.value)
    }
}
