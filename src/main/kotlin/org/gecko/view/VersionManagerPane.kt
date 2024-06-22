package org.gecko.view

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import org.gecko.viewmodel.AutoNaming
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.VariantGroup
import org.gecko.viewmodel.onListChange
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.materialdesign2.MaterialDesignB
import tornadofx.*

/**
 * @author Alexander Weigl
 * @version 1 (23.05.24)
 */
class VersionManagerPane(val model: GeckoViewModel) : UIComponent("Versions & Variants") {
    var tree by singleAssign<TreeView<String>>()
    override val root = vbox {
        hbox {
            label("Versions and Variants")
            spacer()
            button("Menu") { }
        }

        tree = treeview {
            isShowRoot = false
            isEditable = true
            contextmenu {
                item("Add") {
                    action { addItem() }
                }
                item("Delete") {
                    action { delItem() }

                }
                item("Toggle Active") {
                    action { toggleActive() }
                }
            }
            vboxConstraints {
                this.vGrow = Priority.ALWAYS
            }
        }


    }

    private fun toggleActive() {
        when (val item = tree.selectionModel.selectedItem as VVTreeItems) {
            is VariantItem -> model.activatedVariants.add(item.value)
            else -> {}
        }
    }

    private fun delItem() {
        when (val item = tree.selectionModel.selectedItem as VVTreeItems) {
            is RootItem -> {}
            is GroupItem -> model.knownVariantGroups.remove(item.group)
            is VariantItem -> item.group.variants.remove(item.value)
        }
    }

    private fun addItem() {
        when (val item = tree.selectionModel.selectedItem as VVTreeItems) {
            is RootItem -> addGroup()
            is GroupItem -> addVariant(item.group)
            is VariantItem -> addVariant(item.group)
        }
    }

    private fun addVariant(group: VariantGroup) {
        group.variants.add(AutoNaming.name("VGroup_"))
    }

    private fun addGroup() {
        model.knownVariantGroups.add(VariantGroup(AutoNaming.name("VGroup_")))
    }

    val populateTreeListener =
        ChangeListener<String> { _: ObservableValue<out Any>, _: String, _: String -> populateTree() }
    val populateTreeListListener = ListChangeListener<String> { _ -> populateTree() }

    init {
        icon = FontIcon(MaterialDesignB.BOOK_VARIANT_MULTIPLE)

        populateTree()
        model.knownVariantGroups.onListChange { group ->
            while (group.next()) {
                group.addedSubList.forEach { g ->
                    g.nameProperty.addListener(populateTreeListener)
                    g.variantsProperty.addListener(populateTreeListListener)
                }

                group.removed.forEach { g ->
                    g.nameProperty.removeListener(populateTreeListener)
                    g.variantsProperty.addListener(populateTreeListListener)
                }
            }
        }
        model.knownVariantGroups.onChange { populateTree() }

        model.knownVariantGroups.add(VariantGroup("Colors", "red", "green", "blue"))
    }

    fun populateTree() {
        tree.root = RootItem(model)
    }

}

sealed class VVTreeItems : TreeItem<String>()

private class RootItem(model: GeckoViewModel) : VVTreeItems() {
    init {
        value = "root"
        for (g in model.knownVariantGroups.sortedBy { it.name }) {
            children.add(GroupItem(g))
        }
    }
}

private class GroupItem(val group: VariantGroup) : VVTreeItems() {
    init {
        value = group.name
        children.setAll(group.variants.sorted().map { VariantItem(group, it) })
    }
}

private class VariantItem(val group: VariantGroup, variant: String) : VVTreeItems() {
    init {
        value = variant
    }
}
