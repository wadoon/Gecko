package org.gecko.application

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage

class App : Application() {
    @Throws(Exception::class)
    override fun start(stage: Stage) {
        // Initialize Gecko
        stage.title = "Gecko"
        stage.icons.add(Image("file:gecko_logo.png"))
        stage.show()
        val geckoManager = GeckoManager(stage)
        GeckoIOManager.geckoManager = geckoManager
        GeckoIOManager.stage = stage
    }
}
