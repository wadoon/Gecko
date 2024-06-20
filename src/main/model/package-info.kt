/**
 * Contains the [org.gecko.model.GeckoModel] and groups all classes that represent
 * [Elements][org.gecko.model.Element] from the domain model of Gecko, of which some are
 * [org.gecko.model.Renamable]. Gecko's model has a tree-structure made up of
 * [Systems][SytemViewModel]. These have [Variables][org.gecko.model.Variable] which can be connected
 * through [SystemConnections][SytemViewModelConnection] if they are found in systems on the same level,
 * and every system has a corresponding [org.gecko.model.Automaton], which consists of
 * [States][org.gecko.model.State] connected through [Edges][org.gecko.model.Edge] and grouped in
 * [Regions][org.gecko.model.Region]. The [org.gecko.model.ModelFactory] is responsible for creating
 * instances of each of these elements and the [org.gecko.model.ElementVisitor] allows access to them.
 */
package org.gecko.model

