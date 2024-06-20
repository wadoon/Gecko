package org.gecko.view.inspector


import org.gecko.viewmodel.*

/**
 * Follows the visitor pattern, implementing the [PositionableViewModelElementVisitor] interface. Holds a
 * reference to the [InspectorFactory] and to the built [Inspector]. It uses the factory when visiting each
 * type of [PositionableViewModelElement][org.gecko.viewmodel.PositionableViewModelElement] in order to create
 * inspectors for concrete [PositionableViewModelElement][org.gecko.viewmodel.PositionableViewModelElement]s.
 */
class InspectorFactoryVisitor internal constructor(private val inspectorFactory: InspectorFactory) :
    PositionableViewModelElementVisitor<Void?> {

    var inspector: Inspector? = null

    override fun visit(systemViewModel: SystemViewModel): Void? {
        inspector = inspectorFactory.createSystemInspector(systemViewModel)
        return null
    }

    override fun visit(regionViewModel: RegionViewModel): Void? {
        inspector = inspectorFactory.createRegionInspector(regionViewModel)
        return null
    }

    override fun visit(systemConnectionViewModel: SystemConnectionViewModel): Void? {
        return null
    }

    override fun visit(edgeViewModel: EdgeViewModel): Void? {
        inspector = inspectorFactory.createEdgeInspector(edgeViewModel)
        return null
    }

    override fun visit(stateViewModel: StateViewModel): Void? {
        inspector = inspectorFactory.createStateInspector(stateViewModel)
        return null
    }

    override fun visit(portViewModel: PortViewModel): Void? {
        inspector = inspectorFactory.createVariableBlockInspector(portViewModel)
        return null
    }

    override fun visit(automatonViewModel: AutomatonViewModel): Void? {
        TODO("Not yet implemented")
    }
}
