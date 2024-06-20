package org.gecko.view

import javafx.scene.control.DialogPane
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

/**
 * @author Alexander Weigl
 * @version 1 (23.05.24)
 */
class VersionManagerPane : DialogPane() {
    var tree: TreeView<String> = TreeView()

    init {
        content = tree
        tree.isEditable = true
        tree.isShowRoot = false

        val pseudoRoot = TreeItem<String>()

        tree.root = pseudoRoot
    }
}
