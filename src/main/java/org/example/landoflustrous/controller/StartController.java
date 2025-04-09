package org.example.landoflustrous.controller;

import javafx.stage.Stage;
import org.example.landoflustrous.view.LevelSelectionScene;

public class StartController extends Controller {

    private Stage stage;

    public StartController(Stage stage) {
        this.stage = stage;

    }

    //传入玩家姓名
    public void handlePlay(String playerName) {

        //要求玩家必须输入姓名
        if (playerName.isEmpty()) {
            return;
        }

        // 2️⃣此处创建选关场景，并设置给stage
        LevelSelectionScene levelSelection = new LevelSelectionScene();
        stage.setScene(levelSelection.createLevelSelectionScene(stage, playerName));

    }


}