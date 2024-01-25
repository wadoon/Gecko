package org.gecko.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.gecko.io.FileTypes;
import org.gecko.io.ProjectFileParser;
import org.gecko.io.ProjectFileSerializer;
import org.gecko.model.GeckoModel;
import org.gecko.viewmodel.GeckoViewModel;
import org.gecko.viewmodel.PositionableViewModelElement;

public class GeckoIOManager {
    private static GeckoIOManager instance;
    @Getter
    @Setter
    private GeckoManager geckoManager;
    @Getter
    @Setter
    private Stage stage;

    @Getter
    @Setter
    private static File file;

    public static GeckoIOManager getInstance() {
        if (instance == null) {
            instance = new GeckoIOManager();
            file = new File("json/untitled_gecko_project.json");
        }
        return instance;
    }

    public void createNewProject() {
        File newFile = saveFileChooser(FileTypes.JSON);
        if (newFile != null) {
            file = newFile;
            Gecko newGecko = new Gecko();
            geckoManager.setGecko(newGecko);
            stage.setScene(new Scene(geckoManager.getGecko().getView().getMainPane()));
            saveGeckoProject(file);
        }
    }

    public void loadGeckoProject() {
        File fileToLoad = openFileChooser(FileTypes.JSON);
        if (fileToLoad != null) {
            ProjectFileParser projectFileParser = new ProjectFileParser();
            Pair<GeckoModel, GeckoViewModel> geckoPair = null;
            try {
                geckoPair = projectFileParser.parse(fileToLoad);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Corrupted file. Could not read project from " + fileToLoad.getPath() + ".", ButtonType.OK);
                alert.showAndWait();
            }

            if (geckoPair != null) {
                Gecko newGecko = new Gecko(geckoPair.getKey(), geckoPair.getValue());
                geckoManager.setGecko(newGecko);
                stage.setScene(new Scene(geckoManager.getGecko().getView().getMainPane()));
                file = fileToLoad;

                List<PositionableViewModelElement<?>> generatedViewModelElements =
                    projectFileParser.getGeneratedViewModelElements();
                if (generatedViewModelElements != null && !generatedViewModelElements.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Loaded file contains elements with missing attributes. "
                            + "Do you want to keep those elements with default attributes?", ButtonType.YES,
                        ButtonType.NO);
                    alert.showAndWait();
                    if (alert.getResult().equals(ButtonType.NO)) {
                        for (PositionableViewModelElement<?> viewModelElement : generatedViewModelElements) {
                            geckoManager.getGecko().getViewModel().deleteViewModelElement(viewModelElement);
                        }
                    }
                }
            }
        }
    }

    public void importAutomatonFile(File file) {

    }

    public void saveGeckoProject(File file) {
        ProjectFileSerializer projectFileSerializer = new ProjectFileSerializer();
        try {
            projectFileSerializer.createFile(geckoManager.getGecko().getModel(), geckoManager.getGecko().getViewModel(),
                file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Designated file could not be created.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void exportAutomatonFile(File file) {

    }

    public File openFileChooser(FileTypes fileType) {
        FileChooser fileChooser = getNewFileChooser(fileType);
        return fileChooser.showOpenDialog(stage);
    }

    public File saveFileChooser(FileTypes fileType) {
        FileChooser fileChooser = getNewFileChooser(fileType);
        return fileChooser.showSaveDialog(stage);
    }

    private FileChooser getNewFileChooser(FileTypes fileType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
            .addAll(new FileChooser.ExtensionFilter(fileType.getFileDescription(), fileType.getFileNameRegex()));
        return fileChooser;
    }
}
