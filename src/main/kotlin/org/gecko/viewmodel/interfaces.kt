package org.gecko.viewmodel

import javafx.beans.property.StringProperty
import org.gecko.actions.ActionManager
import org.gecko.view.GeckoView
import org.gecko.view.inspector.builder.AbstractInspectorBuilder
import org.gecko.view.views.EditorView
import org.gecko.view.views.viewelement.decorator.ViewElementDecorator

/**
 *
 * @author Alexander Weigl
 * @version 1 (21.06.24)
 */
interface Inspectable {
    fun inspector(actionManager: ActionManager): AbstractInspectorBuilder<*>
}

interface Viewable {
    fun view(actionManager: ActionManager, geckoView: GeckoView): ViewElementDecorator
}


interface Openable {
    fun editor(actionManager: ActionManager, geckoView: GeckoView): EditorView
}


/**
 * Provides methods for renamable objects, that is view model elements with a name property. These include retrieving
 * and modifying the name of the view model element.
 */
interface Renamable {
    var name: String
    val nameProperty: StringProperty
}
