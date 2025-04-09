package org.example.landoflustrous.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.landoflustrous.controller.LevelSelectionController;

import java.io.IOException;
import java.io.InputStream;

public class LevelSelectionScene {

    private LevelSelectionController controller;

    public Scene createLevelSelectionScene(Stage stage, String playerName) {

        controller = new LevelSelectionController(stage);

        //一个gridpane放两个关卡的按钮
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button button1 = createImageButton("/images/image1.jpg", () -> {
            try {
                controller.openMapPage("Level 1", playerName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button button2 = createImageButton("/images/image2.jpg", () -> {
            try {
                controller.openMapPage("Level 2", playerName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        grid.add(button1, 0, 0);
        grid.add(button2, 1, 0);

        //一个hbox放两个选项的按钮
        Button returnBtn = new Button("");
        returnBtn.getStyleClass().add("return");
        returnBtn.setOnAction(e -> controller.returnToMainMenu());

        Button exitBtn = new Button("");
        exitBtn.getStyleClass().add("exit");
        exitBtn.setOnAction(controller::handleExit);

        HBox hbox_twobtn = new HBox(50);
        hbox_twobtn.setAlignment(Pos.CENTER);
        hbox_twobtn.getChildren().addAll(returnBtn, exitBtn);

        // 全部放到一个Vbox里面
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(grid, hbox_twobtn);

        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // 引入CSS样式

        return new Scene(root);


    }

    private Button createImageButton(String imagePath, Runnable action) {
        ImageView imageView = createImageView(imagePath);
        Button button = new Button();
        button.setGraphic(imageView);
        button.setOnAction(e -> action.run());
        return button;
    }

    private ImageView createImageView(String imagePath) {
        InputStream is = getClass().getResourceAsStream(imagePath);
        if (is == null) {
            throw new RuntimeException("Cannot find resource " + imagePath);
        }
        Image image = new Image(is);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        return imageView;
    }


}
