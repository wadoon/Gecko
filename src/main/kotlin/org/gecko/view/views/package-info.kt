/**
 * Clusters classes which manage the [org.gecko.view.views.EditorView], respectively what is
 * displayed in the current view of Gecko. The [org.gecko.view.views.ViewFactory] and the
 * [org.gecko.view.views.ViewElementCreatorVisitor] are responsible for creating
 * [ViewElements][org.gecko.view.views.viewelement.ViewElement], while the
 * [org.gecko.view.views.ViewElementSearchVisitor] collects view model element matches to a search
 * based on the name of the elements. The [org.gecko.view.views.FloatingUIBuilder] builds floating
 * UI elements (buttons and labels) displayed in the view.
 */
package org.gecko.view.views
