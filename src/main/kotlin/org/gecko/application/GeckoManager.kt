package org.gecko.application

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.Mnemonic
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import tornadofx.onChange
import java.util.function.Consumer


/**
 * Represents a manager for the active [Gecko]. Additionally, holds a reference to the [Stage] of the
 * application.
 */
class GeckoManager(val stage: Stage, gecko: Gecko = Gecko()) {
    var gecko: Gecko = Gecko()

    init {
        val scene = Scene(gecko.view.mainPane, SCENE_WIDTH, SCENE_HEIGHT)
        gecko.view.mnemonicsProperty().forEach(Consumer { mnemonic: Mnemonic? -> scene.addMnemonic(mnemonic) })
        stage.scene = scene

        gecko.view.darkModeProperty().onChange { n ->
            Application.setUserAgentStylesheet(
                if (!n) PrimerLight().userAgentStylesheet
                else PrimerDark().userAgentStylesheet
            )
        }
    }

    companion object {
        const val SCENE_WIDTH = 1024.0
        const val SCENE_HEIGHT = 768.0
    }
}
