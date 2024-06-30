/**
 * Clusters classes that manage I/O file content. There are two types of operations provided:
 * 1. **Serialization**: Outlined by the [org.gecko.io.FileSerializer], there are currently two
 *    types of serialization processes supported by Gecko. * **Serialization to JSON**: The
 *    [Root][org.gecko.viewmodel.SystemViewModel] of the model and
 *    [ViewModelProperties][org.gecko.io.ViewModelPropertiesContainer] of all view model elements
 *    are serialized through Jackson, a suite of data-processing tools for Java, by the
 *    [org.gecko.io.ProjectFileSerializer], which uses a [org.gecko.io.ViewModelElementSaver] to
 *    traverse all view model elements. * **Serialization to SYS**: Serializtaion to SYS files is
 *    done by the [AutmatonFileSerializer][org.gecko.io.AutomatonFileSerializer]. When exporting the
 *    model it transforms features unique to Gecko such as [Kinds][org.gecko.model.Kind] or
 *    [Regions][org.gecko.model.Region] to be compatible with the SYS file format.
 * 1. **Parsing**: Outlined by the [org.gecko.io.FileParser], there are currently two types of
 *    parsing processes supported by Gecko. * **Parsing from JSON**: The
 *    [Root][org.gecko.viewmodel.SystemViewModel] of the model and
 *    [ViewModelProperties][org.gecko.io.ViewModelPropertiesContainer] of all model elements are
 *    parsed from a *.json file through Jackson by the [org.gecko.io.ProjectFileParser], which uses
 *    a [org.gecko.io.ViewModelElementCreator] to traverse the model and create the corresponding
 *    view model elements. * **Parsing from SYS**: Parsing from SYS files is done by the
 *    [AutomatonFileParser][org.gecko.io.AutomatonFileParser]. It uses the
 *    [AutomatonFileVisitor][org.gecko.io.AutomatonFileVisitor], which is an ANTLR4 visitor, to
 *    traverse the SYS file and create the corresponding model elements.
 */
package org.gecko.io
