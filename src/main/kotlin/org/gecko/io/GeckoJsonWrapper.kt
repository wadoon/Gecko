package org.gecko.io

import org.gecko.viewmodel.GeckoViewModel


/**
 * Wraps two Json Strings: model describes the tree structure of a Gecko Model and viewModelProperties describes
 * ViewModel-specific attributes of PositionableViewModelElements like position coordinates, size coordinates and color
 * values for [RegionViewModels][org.gecko.viewmodel.RegionViewModel].
 */
data class GeckoJsonWrapper(
    val model: GeckoViewModel,
    //val startStates: List<StartStateContainer>,
    //val viewModelProperties: List<ViewModelPropertiesContainer>
)

data class StartStateContainer(var systemId: UInt = 0u, var startStateName: String? = null)


/**
 * Encapsulates [GeckoViewModel][org.gecko.viewmodel.GeckoViewModel]-specific data for a
 * [PositionableViewModelElement][org.gecko.viewmodel.PositionableViewModelElement], useful for the restoration of
 * the [org.gecko.view.GeckoView] after parsing an external file.
 */
class ViewModelPropertiesContainer(
    var elementId: UInt = 0u, var id: Int = 0, var positionX: Double = 0.0,
    var positionY: Double = 0.0, var sizeX: Double = 0.0, var sizeY: Double = 0.0,
    var red: Double = 0.0, var green: Double = 0.0, var blue: Double = 0.0
)
