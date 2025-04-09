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
import org.example.landoflustrous.controller.GamePassController;
import org.example.landoflustrous.model.Popup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePassScene {
    private VBox root; // 使用VBox作为根容器
    private Scene scene;
    private Stage stage;
    private GamePassController controller;

    public GamePassScene(String name, Stage stage, int gemNum, GamePassController controller, String levelIdentifier) throws IOException {
        this.stage = stage;
        this.controller = controller;

        //调用父类controll的方法，保存游戏记录
        controller.saveGameRecord(name, gemNum, levelIdentifier);

        //---------------------一个vbox放分数-------------------------------
        VBox textContainer = new VBox(20); // 元素间距
        textContainer.setAlignment(Pos.CENTER); // 设置VBox居中对齐

        //标题
        Text title = new Text("WELL DONE !!!");
        title.getStyleClass().add("score_title");

        //游戏结果：姓名+宝石数目
        Text playResult = new Text("Name: " + name + "  GEM NUMBER: " + gemNum);
        playResult.getStyleClass().add("score");

        //把读取的game record放入vbox
        VBox fileContentDisplay = createContentDisplay(levelIdentifier);


        //把游戏结果，game record放入vbox
        textContainer.getChildren().addAll(playResult, fileContentDisplay);


        Rectangle rectangle = new Rectangle(600, 230);
        rectangle.getStyleClass().add("rectangle");

        // 使用StackPane来允许背景和文本层叠
        StackPane scores = new StackPane();
        scores.getChildren().addAll(rectangle, textContainer);


        //------------------------------一个hbox放popup-----------------------
        HBox hbox_popup = new HBox(0);
        hbox_popup.setAlignment(Pos.CENTER);

        //创建并调用popup的方法，得到随机的一条eco tip
        Popup popup = new Popup();
        Text ecoTip = new Text(popup.getRandomEcoTip());

        ecoTip.setWrappingWidth(330);
        ecoTip.getStyleClass().add("eco_tip");

        // controller的方法得到随机图片
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

        //再玩一次按钮
        Button btn1 = new Button("");
        btn1.getStyleClass().add("play_again");
        btn1.setOnAction(e -> controller.openMapPage(levelIdentifier, name));


        //下一关按钮,只有第一关时出现
        if (levelIdentifier == "Level 1") {
            Button btn2 = new Button("");
            btn2.getStyleClass().add("next_level");
            btn2.setOnAction(e -> controller.openMapPage("Level 2", name));
            hbox_twobtn.getChildren().add(btn2);
        }


        //退出按钮
        Button btn3 = new Button("");
        btn3.getStyleClass().add("exit");
        btn3.setOnAction(controller::handleExit);


        hbox_twobtn.getChildren().addAll(btn1, btn3);


        // -------------------------VBox组织子元素-------------------
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("scoreboard_root");
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // 引入CSS样式

        root.setStyle("-fx-background-color:rgb(243,243,243);");
        root.getChildren().addAll(title, scores, stackpan_popup, hbox_twobtn);

        this.scene = new Scene(root, 1300, 800);


    }

    public Scene getScene() {
        return this.scene;
    }

    //读取游戏记录并展示
    private VBox createContentDisplay(String levelId) throws IOException {

        //根据关卡id决定要读哪个文件，并读入
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
            validContent.add(parts);
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
}
