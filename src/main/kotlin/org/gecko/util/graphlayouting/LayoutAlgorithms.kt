package org.gecko.util.graphlayouting

/**
 * LayoutAlgorithms currently supported by Gecko. This enum is simply a listing of used ELK
 * dependencies.
 */
internal enum class LayoutAlgorithms(val elkId: String) {
    FORCE("force"),
    LAYERED("layered")
}
