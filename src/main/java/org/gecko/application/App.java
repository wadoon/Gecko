package org.gecko.application;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize Gecko
        stage.setTitle("Gecko");
        stage.getIcons().add(new Image("file:gecko_logo.png"));
        stage.show();
        GeckoManager geckoManager = new GeckoManager(stage);
        GeckoIOManager.getInstance().setGeckoManager(geckoManager);
        GeckoIOManager.getInstance().setStage(stage);
    }
}
