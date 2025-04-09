package org.example.landoflustrous.util;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class SimpleMapCreator extends Application {

    public static final int TILE_SIZE = 28;  // 瓷砖大小

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new GridPane();
        List<String> mapData = readMapFile("src/main/resources/maps/map1/level2/map.txt");  // 读取地图数据
        generateMap(root, mapData);  // 生成地图
        savePaneAsImage(root, "simple_map_level2.png");  // 保存地图为PNG

        Scene scene = new Scene(root);
        primaryStage.setTitle("Simple Map Creator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<String> readMapFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    private void generateMap(Pane root, List<String> mapData) {
        int y = 0;
        for (String row : mapData) {
            for (int x = 0; x < row.length(); x++) {
                char tileType = row.charAt(x);
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
                rect.setFill(getTilePattern(tileType));  // 直接使用颜色填充
                GridPane.setConstraints(rect, x, y);
                root.getChildren().add(rect);
            }
            y++;
        }
    }

    private Paint getTilePattern(char tileType) {
        switch (tileType) {
            case '9':
                return Color.web("#735e50"); // 铁路站
            case '2':
                return Color.web("#b09b84");  // 铁路
            case '1':
                return Color.LIGHTGRAY;  // 使用灰色表示道路
            case '0':
                return Color.web("#6ebbc6"); // 使用蓝色表示水域
            case '4':
                return Color.web("#98b584"); // 使用绿色表示草地
            case '7':
                return Color.DARKGREEN;//自行车站
            case '8':
                return Color.GRAY; //公交站
            case '3':
                return Color.web("#bf7203");  //公路且铁路，即交叉点

            default:
                return Color.web("#98b584");
        }
    }


    private void savePaneAsImage(Pane pane, String filename) {
        // 构造保存路径
        String directoryPath = "src/main/resources/images_output";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // 如果目录不存在，创建目录
        }

        WritableImage writableImage = pane.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        // 指定文件保存的完整路径
        File outFile = new File(directory, filename);
        try {
            ImageIO.write(bufferedImage, "png", outFile);
            System.out.println("Map saved to " + outFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save map to " + outFile.getAbsolutePath());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
