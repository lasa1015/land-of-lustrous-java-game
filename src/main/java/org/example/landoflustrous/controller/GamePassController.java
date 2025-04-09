package org.example.landoflustrous.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.landoflustrous.view.MapViewerScene;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GamePassController extends Controller {
    private Stage stage;

    public GamePassController(Stage stage) {
        super();
        this.stage = stage;
    }

    //根据关卡id参数，再次进入游戏
    public void openMapPage(String LevelIdentifier, String playerName) {
        try {
            MapViewerScene mapViewer = new MapViewerScene(stage, LevelIdentifier, playerName);
            stage.setScene(mapViewer.getScene());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //创建随机图片
    public ImageView getRandomImageView() {
        List<String> imagePaths = Arrays.asList("/images/earth.png", "/images/tree2.png", "/images/water.png", "/images/dophine.png");

        //随机选择路径
        Random random = new Random();
        String path = imagePaths.get(random.nextInt(imagePaths.size()));

        //加载图像文件
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            throw new RuntimeException("Image not found at path: " + path);
        }
        Image image = new Image(stream);
        return new ImageView(image);
    }

}
