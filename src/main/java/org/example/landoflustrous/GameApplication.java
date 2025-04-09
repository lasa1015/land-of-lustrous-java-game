package org.example.landoflustrous;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.landoflustrous.model.AudioPlayer;
import org.example.landoflustrous.view.GameStartScene;

public class GameApplication extends Application {

    @Override
    public void start(Stage primaryStage) {

        // load font file
        Font.loadFont(getClass().getResourceAsStream("/fonts/Oswald-VariableFont_wght.ttf"), 16);

        //  1️⃣此处创建每个场景共用的stage，给stage设置为GameStartScene
        GameStartScene gameStartScene = new GameStartScene();

        primaryStage.setScene(gameStartScene.createStartScene(primaryStage));
        primaryStage.setTitle("LAND OF LUSTROUS");
        primaryStage.setResizable(false);
        primaryStage.setWidth(1010);
        primaryStage.setHeight(680);

        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.startBackgroundMusic(); // 开始播放背景音乐

        primaryStage.setOnCloseRequest(event -> {
            audioPlayer.stopMusic(); // 确保关闭窗口时停止音乐
        });

        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}