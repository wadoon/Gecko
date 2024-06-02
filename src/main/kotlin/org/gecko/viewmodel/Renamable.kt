package org.gecko.viewmodel

import javafx.beans.property.StringProperty

/**
 * Provides methods for renamable objects, that is view model elements with a name property. These include retrieving
 * and modifying the name of the view model element.
 */
interface Renamable {
    var name: String
    val nameProperty: StringProperty
}
