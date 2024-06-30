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

