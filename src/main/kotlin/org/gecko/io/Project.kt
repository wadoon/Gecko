package org.gecko.io

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.gecko.viewmodel.GModel

/**
 * Wraps two Json Strings: model describes the tree structure of a Gecko Model and
 * viewModelProperties describes ViewModel-specific attributes of PositionableViewModelElements like
 * position coordinates, size coordinates and color values for
 * [RegionViewModels][org.gecko.viewmodel.Region].
 */
data class Project(
    val model: GModel,
    // val startStates: List<StartStateContainer>,
    // val viewModelProperties: List<ViewModelPropertiesContainer>
) : Mappable {
    override fun asJson(): JsonElement = objectOf("model" to model.asJson())
}

fun objectOf(vararg pair: Pair<String, JsonElement>) =
    JsonObject().also { pair.forEach { (k, v) -> it.add(k, v) } }

data class StartStateContainer(var systemId: UInt = 0u, var startStateName: String? = null)

/**
 * Encapsulates [GeckoViewModel][org.gecko.viewmodel.GModel]-specific data for a
 * [PositionableViewModelElement][org.gecko.viewmodel.PositionableElement], useful for the
 * restoration of the [org.gecko.view.GeckoView] after parsing an external file.
 */
class ViewModelPropertiesContainer(
    var elementId: UInt = 0u,
    var id: Int = 0,
    var positionX: Double = 0.0,
    var positionY: Double = 0.0,
    var sizeX: Double = 0.0,
    var sizeY: Double = 0.0,
    var red: Double = 0.0,
    var green: Double = 0.0,
    var blue: Double = 0.0
)
