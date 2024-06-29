package org.gecko.viewmodel

import javafx.scene.paint.Color

/**
 * Enumerates the three types of [Variable]s.
 */
enum class Visibility(val color: Color) {
    INPUT(Color.LIGHTGREEN), OUTPUT(Color.LIGHTGOLDENRODYELLOW), STATE(Color.LIGHTSEAGREEN)
}

/**
 * Enumerates the three ways in which an [Edge] can handle its [Contract].
 */
enum class Kind {
    HIT, MISS, FAIL
}
