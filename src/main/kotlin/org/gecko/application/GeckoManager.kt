package org.gecko.application

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
class GeckoManager(val stage: Stage) {
    var gecko: Gecko = Gecko()
        set(value) {
            field = value
            val scene = Scene(value.view.mainPane, SCENE_WIDTH, SCENE_HEIGHT)
            value.view.mnemonicsProperty().forEach(Consumer { mnemonic: Mnemonic? -> scene.addMnemonic(mnemonic) })
            stage.scene = scene
            //metro.scene = scene
            //metro.reApplyTheme()

            Application.setUserAgentStylesheet(PrimerLight().userAgentStylesheet)
            //Application.setUserAgentStylesheet(PrimerDark().userAgentStylesheet)

            gecko.view.darkModeProperty().onChange { n -> setStyle(if (n) Style.DARK else Style.LIGHT) }
        }

    val metro = JMetro(Style.LIGHT)

    fun setStyle(style: Style?) {
        metro.style = style
        metro.reApplyTheme()
    }

    companion object {
        const val SCENE_WIDTH = 1024.0
        const val SCENE_HEIGHT = 768.0
    }
}
