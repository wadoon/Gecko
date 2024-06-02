/**
 * Groups classes which model tools that can be used in Gecko. They are outlined by a [org.gecko.tools.Tool] with
 * access to the [ActionManager][org.gecko.actions.ActionManager], which provides a tool with the ability to enable
 * operations. Tools are defined by a [org.gecko.tools.ToolType] and fall into the following categories:
 *
 *  *
 * **Creator Tools**: These tools run create-[Actions][org.gecko.actions.Action] in order to create
 * [PositionableViewModelElements][org.gecko.viewmodel.PositionableViewModelElement].
 *
 *
 *  *
 * **Selection Tools**: These tools run selection-, moving- and scaling-[         Actions][org.gecko.actions.Action] in order to arrange one or more displayed elements.
 *
 *
 *  *
 * **View Tools**: These tools run view-switch-, pan- and zoom-[Actions][org.gecko.actions.Action] in
 * order to navigate the view.
 *
 *
 */
package org.gecko.tools

