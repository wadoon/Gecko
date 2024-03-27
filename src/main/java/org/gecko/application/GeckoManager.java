package org.gecko.application;

import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import lombok.Getter;
import org.gecko.exceptions.ModelException;

/**
 * Represents a manager for the active {@link Gecko}. Additionally, holds a reference to the {@link Stage} of the
 * application.
 */
public class GeckoManager {
    @Getter
    private Gecko gecko;
    private final Stage stage;
    private final JMetro metro = new JMetro(Style.LIGHT);

    private static final int SCENE_WIDTH = 1024;
    private static final int SCENE_HEIGHT = 768;

    public GeckoManager(Stage stage) throws ModelException {
        this.stage = stage;
        setGecko(new Gecko());
    }

    public void setGecko(Gecko gecko) {
        this.gecko = gecko;
        final var scene = new Scene(gecko.getView().getMainPane(), SCENE_WIDTH, SCENE_HEIGHT);
        gecko.getView().mnemonicsProperty().forEach(scene::addMnemonic);
        metro.setScene(scene);
        stage.setScene(scene);
        metro.reApplyTheme();

        gecko.getView().darkModeProperty().addListener((p, o, n) ->
                setStyle(n ? Style.DARK : Style.LIGHT));
    }

    public void setStyle(Style style) {
        metro.setStyle(style);
        metro.reApplyTheme();
    }
}
