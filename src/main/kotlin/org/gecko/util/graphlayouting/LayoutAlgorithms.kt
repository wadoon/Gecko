package org.gecko.util.graphlayouting

import org.eclipse.elk.alg.force.ForceLayoutProvider
import org.eclipse.elk.alg.layered.LayeredLayoutProvider
import org.eclipse.elk.core.IGraphLayoutEngine

/**
 * LayoutAlgorithms currently supported by Gecko. This enum is simply a listing of used ELK dependencies.
 */
internal enum class LayoutAlgorithms(val elkId:String) {
    FORCE("force"),
    LAYERED("layered");
}
