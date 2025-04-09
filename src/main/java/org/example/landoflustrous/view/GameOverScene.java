package org.example.landoflustrous.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.landoflustrous.controller.GameOverController;
import org.example.landoflustrous.model.Popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//All styling matching that of scoreboard scene
public class GameOverScene {
    private VBox root; // 使用VBox作为根容器
    private Scene scene;
    private Stage stage;
    private GameOverController controller;

    private String levelIdentifier;

    public GameOverScene(String name, Stage stage, int carbon, int gemNum, int gemScore, GameOverController controller, String levelIdentifier) throws IOException {
        this.stage = stage;
        this.controller = controller;


        //保存游戏记录
        controller.saveGameRecord(name, gemNum, levelIdentifier);

        //---------------------一个vbox放各种分数-------------------------------
        VBox textContainer = new VBox(20); // 10是元素之间的间距
        textContainer.setAlignment(Pos.CENTER); // 设置VBox居中对齐
        textContainer.getStyleClass().add("text-container"); // CSS类名

        Text title = new Text("GAME OVER");
        title.getStyleClass().add("gameover_title");

        Text playName = new Text("Name: " + name + "  GEM NUMBER: " + gemNum);
        playName.getStyleClass().add("score");


        //排行榜
        VBox fileContentDisplay = createContentDisplay(levelIdentifier);

        textContainer.getChildren().addAll(playName, fileContentDisplay);

        Rectangle rectangle = new Rectangle(600, 230);
        rectangle.getStyleClass().add("rectangle");

        // 使用StackPane来允许背景和文本层叠
        StackPane scores = new StackPane();
        scores.getChildren().addAll(rectangle, textContainer);

        //------------------------------一个hbox放popup-----------------------
        HBox hbox_popup = new HBox(0);
        hbox_popup.setAlignment(Pos.CENTER);

        Popup popup = new Popup();
        Text ecoTip = new Text(popup.getRandomEcoTip());

        ecoTip.setWrappingWidth(330);
        ecoTip.getStyleClass().add("eco_tip");

        // 随机图片
        ImageView imageView = controller.getRandomImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);

        // 将ImageView和Text添加到HBox
        hbox_popup.getChildren().addAll(imageView, ecoTip);
        Rectangle rectangle2 = new Rectangle(600, 200);
        rectangle2.getStyleClass().add("rectangle");

        StackPane stackpan_popup = new StackPane();
        stackpan_popup.getChildren().addAll(rectangle2, hbox_popup);

        //--------------------------一个hbox放两个按钮----------------------------
        HBox hbox_twobtn = new HBox(50);
        hbox_twobtn.setAlignment(Pos.CENTER);

        Button btn1 = new Button("");
        btn1.getStyleClass().add("play_again");
        btn1.setOnAction(e -> controller.openMapPage(levelIdentifier, name));


        Button btn3 = new Button("");
        btn3.getStyleClass().add("exit");
        btn3.setOnAction(controller::handleExit);


        hbox_twobtn.getChildren().addAll(btn1, btn3);


        // -------------------------VBox组织子元素-------------------
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("gameover_root");
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // 引入CSS样式

        root.setStyle("-fx-background-color:rgb(243,243,243);");

        root.getChildren().addAll(title, scores, stackpan_popup, hbox_twobtn);

        this.scene = new Scene(root, 1300, 800);

    }

    private VBox createContentDisplay(String levelId) throws IOException {
        List<String[]> fileContent = controller.readFileByLevelId(levelId);
        VBox contentBox = new VBox(10); // 设置元素之间的垂直间距为10
        contentBox.setAlignment(Pos.CENTER);

        // 添加标题
        Text header = new Text("Rank\t\tLevel\t    Name\t     Gem");
        header.getStyleClass().add("header-text"); // 添加 CSS 类名，以便于设置样式
        contentBox.getChildren().add(header);

        // 解析文件内容，并尝试转换宝石数为整数，忽略无法转换的行
        List<String[]> validContent = new ArrayList<>();
        for (String[] parts : fileContent) {
            try {
                parts[1] = Integer.toString(Math.abs(Integer.parseInt(parts[1]))); // 确保宝石数量是个正整数
                validContent.add(parts); // 只有转换成功的才添加到列表
            } catch (NumberFormatException e) {
                // 处理错误，例如可以打印日志或者忽略这行数据
            }
        }

        // 按宝石数降序排序，并只取前五条
        validContent.sort((a, b) -> Integer.compare(Integer.parseInt(b[1]), Integer.parseInt(a[1])));
        List<String[]> topFive = validContent.stream().limit(5).toList();

        // 使用制表符对齐数据并添加到VBox
        int rank = 1;
        for (String[] lineParts : topFive) {
            // 使用制表符来分隔数据
            String line = rank++ + ".\t\t" + lineParts[0] + "\t\t" + lineParts[2] + "\t\t" + lineParts[1];
            Text contentText = new Text(line);
            contentText.getStyleClass().add("content-text");
            contentBox.getChildren().add(contentText);
        }

        return contentBox;
    }


    public Scene getScene() {
        return this.scene;
    }


}