/**
 * Contains the [org.gecko.viewmodel.GeckoViewModel] and groups all classes that represent
 * [AbstractViewModelElements][org.gecko.viewmodel.AbstractViewModelElement] corresponding to the elements in the
 * domain model of Gecko. The tree-structure of the model is not preserved here. All elements except
 * [ContractViewModels][org.gecko.viewmodel.ContractViewModel] are
 * [PositionableViewModelElements][org.gecko.viewmodel.PositionableViewModelElement], that is elements with a
 * position and a size in the view. Among these are distinguished
 * [BlockViewModelElements][org.gecko.viewmodel.BlockViewModelElement], which are
 * [org.gecko.viewmodel.Renamable], and [ConnectionViewModels][org.gecko.viewmodel.ConnectionViewModel]. The
 * [org.gecko.viewmodel.ViewModelFactory] is responsible for creating instances of each of these elements and the
 * [org.gecko.viewmodel.PositionableViewModelElementVisitor] allows access to them. Additionally, the
 * [org.gecko.viewmodel.EditorViewModel] models the currently displayed editor view and the
 * [org.gecko.viewmodel.SelectionManager] takes account of the selection history.
 */
package org.gecko.viewmodel


