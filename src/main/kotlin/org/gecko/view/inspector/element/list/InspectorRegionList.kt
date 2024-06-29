package org.gecko.view.inspector.element.list

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import org.gecko.view.inspector.element.label.InspectorLabel
import org.gecko.viewmodel.Region

/**
 * A concrete representation of an [AbstractInspectorList] encapsulating an [InspectorLabel].
 */
class InspectorRegionList(RegionList: ObservableList<Region>) :
    AbstractInspectorList<InspectorLabel>() {
    init {
        for (regionViewModel in RegionList) {
            val regionLabel = InspectorLabel(regionViewModel!!.name)
            items.add(regionLabel)
        }

        RegionList.addListener { c: ListChangeListener.Change<out Region> -> this.updateRegionList(c) }
        minHeight = MIN_HEIGHT
    }

    fun updateRegionList(c: ListChangeListener.Change<out Region>) {
        while (c.next()) {
            if (c.wasAdded()) {
                for (regionViewModel in c.addedSubList) {
                    if (findRegionLabel(regionViewModel!!.name) != null) {
                        continue
                    }
                    val regionLabel = InspectorLabel(regionViewModel.name)
                    items.add(regionLabel)
                }
            } else if (c.wasRemoved()) {
                for (regionViewModel in c.removed) {
                    val regionLabel = findRegionLabel(regionViewModel!!.name)
                    items.remove(regionLabel)
                }
            }
        }
    }

    fun findRegionLabel(name: String): InspectorLabel? {
        return items.firstOrNull { it.text == name }
    }

    companion object {
        const val MIN_HEIGHT = 50.0
    }
}
