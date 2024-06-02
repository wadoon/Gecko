package org.gecko.io

/**
 * Enumerates the two file types which can be managed in the Gecko Graphic Editor: JSON files as project files and SYS
 * files as automaton files.
 */
enum class FileTypes(val fileDescription: String, val fileExtension: String) {
    JSON("Json Files", "json"),
    SYS("Sys Files", "sys");

    val fileNameGlob: String
        get() = "*.$fileExtension"
}
