package org.example.landoflustrous.controller;

import javafx.stage.Stage;
import org.example.landoflustrous.view.GameStartScene;
import org.example.landoflustrous.view.MapViewerScene;

import java.io.IOException;

public class LevelSelectionController extends Controller {
    private final Stage stage;

    public LevelSelectionController(Stage stage) {
        this.stage = stage;
    }

    public void openMapPage(String levelIdentifier, String playerName) throws IOException {
        MapViewerScene mapViewer = new MapViewerScene(stage, levelIdentifier, playerName);
        stage.setScene(mapViewer.getScene());
        stage.show();
    }


    public void returnToMainMenu() {
        GameStartScene startScene = new GameStartScene();
        stage.setScene(startScene.createStartScene(stage));
        stage.show();
    }

}



