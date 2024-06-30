/**
 * Clusters all classes that represent [ViewElements][org.gecko.view.views.viewelement.ViewElement],
 * which are bound to the view model elements of Gecko. These are divided in
 * [BlockViewElements][org.gecko.view.views.viewelement.BlockViewElement] and
 * [ConnectionViewElements][org.gecko.view.views.viewelement.ConnectionViewElement] and can be drawn
 * in the view. There exists a correspondent view element for each of the view model elements,
 * except for [ContractViewModel][org.gecko.viewmodel.ContractViewModel], which is not independently
 * displayed in the view. The [org.gecko.view.views.viewelement.ViewElementVisitor] allows access to
 * each of the view elements.
 */
package org.gecko.view.views.viewelement
