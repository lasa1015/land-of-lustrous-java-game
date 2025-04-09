package org.example.landoflustrous.view;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.example.landoflustrous.controller.Controller;
import org.example.landoflustrous.controller.GameOverController;
import org.example.landoflustrous.controller.GamePassController;
import org.example.landoflustrous.model.*;
import org.example.landoflustrous.service.NavigationService;

import java.io.IOException;
import java.util.*;

public class MapViewerScene {
    private GameMap gameMap;
    private VBox root;
    private Pane pane;
    private Stage stage;
    private Scene scene;
    private static final int TILE_SIZE = 20;
    private PlayerCharacter player;
    private ImageView playerImage;
    private Gem gem;
    private ImageView gemImageView;
    private List<Route> routeList;
    private HBox statusBoard;
    private Label nameLabel; // 显示名字
    private Label carbonLabel; // 显示碳足迹
    private Label gemCountLabel; // 显示宝石数量
    private Label timeRemainingLabel;
    private Timeline gameTimer;
    private int gameTimeRemaining = 60;  // 总游戏时间60秒
    private boolean continueGeneratingGems = true;
    NavigationService navigationService;
    String levelIdentifier;
    int routeCost;
    Controller controller;

    private static final Map<String, Map<String, Object>> levelPathMapping = new HashMap<>();

    static {
        levelPathMapping.put("Level 1", Map.of(
                "map", "/maps/map1/level1/map.txt",
                "rail", List.of("/maps/map1/level1/rail.txt"),
                "bus", List.of("/maps/map1/level1/bus1.txt", "/maps/map1/level1/bus2.txt")));
        levelPathMapping.put("Level 2", Map.of(
                "map", "/maps/map1/level2/map.txt",
                "rail", List.of("/maps/map1/level2/rail1.txt", "/maps/map1/level2/rail2.txt"),
                "bus", List.of("/maps/map1/level2/bus1.txt", "/maps/map1/level2/bus2.txt")));
    }


    //此处生成游戏主场景，调用各种方法加载各种模型
    public MapViewerScene(Stage stage, String levelIdentifier, String playerName) throws IOException {
        this.stage = stage;
        this.levelIdentifier = levelIdentifier;

        this.controller = new Controller();
        Map<String, Object> paths = levelPathMapping.get(levelIdentifier);
        String mapPath = (String) paths.get("map");
        List<String> railPaths = (List<String>) paths.get("rail");
        List<String> busPaths = (List<String>) paths.get("bus");

        this.gameMap = new GameMap(mapPath, railPaths, busPaths);
        int gameWidth = gameMap.getWidth();
        int gameHeight = gameMap.getHeight();

        initGameTimer(levelIdentifier);

        //创建VBOX装整个场景
        root = new VBox();

        //创建Pane装地图区域
        pane = new Pane();

        //根据关卡id绘制地图于Pane
        drawMap(pane, levelIdentifier);

        //创建玩家于Pane
        addPlayerCharacter(pane, playerName);

        //随机生成一颗宝石
        createRandomGem(pane, gameWidth, gameHeight);

        //把pane加入vbox上
        root.getChildren().addAll(pane);

        //创建玩家状态栏于vbox
        createStatusBoard(root, player);

        //画地图网格方便观察
//        drawTileBorders(upPart, gameMap.getWidth(), gameMap.getHeight());

        // 引入CSS样式
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        this.scene = new Scene(root);

    }

    //计时器，同时判断结束时是 game over 还是 good job
    private void initGameTimer(String levelIdentifier) {

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            gameTimeRemaining--;
            timeRemainingLabel.setText("Time: " + gameTimeRemaining + "s"); // Update the timer display

            if (gameTimeRemaining == 0 & player.getGemNumber() >= 5) {
                try {

                    switchToScoreBoard(levelIdentifier);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (gameTimeRemaining == 0 & player.getGemNumber() < 5) {
                try {
                    handleGameOver(levelIdentifier);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }


    private void switchToScoreBoard(String levelIdentifier) throws IOException {

        int gemCount = player.getGemNumber();

        GamePassController gamePassController = new GamePassController(stage);
        GamePassScene gamePassScene = new GamePassScene(player.getName(), stage, gemCount, gamePassController, levelIdentifier);
        stage.setScene(gamePassScene.getScene());
        stage.show();
    }

    private void handleGameOver(String levelIdentifier) throws IOException {
        int currentCarbon = player.getCarbonHP();
        int gemScore = player.getGemScore();
        int gemCount = player.getGemNumber();

        GameOverController gameOverController = new GameOverController(stage);
        GameOverScene gameOverScene = new GameOverScene(player.getName(), stage, currentCarbon, gemCount, gemScore, gameOverController, levelIdentifier);
        stage.setScene(gameOverScene.getScene());
        stage.show();

    }


    //绘制地图
    private void drawMap(Pane root, String levelIdentifier) {
        int tempLevel = Integer.parseInt(levelIdentifier.replaceAll("[^0-9]", ""));
        String pathToMap = "/images/map_level" + tempLevel + ".png";
        Image mapImage = new Image(getClass().getResourceAsStream(pathToMap));
        ImageView mapView = new ImageView(mapImage);
        mapView.setFitWidth(1000);
        mapView.setFitHeight(600);
        root.getChildren().add(mapView);
    }

    //增加人物
    public void addPlayerCharacter(Pane root, String playerName) {

        Image image = new Image(getClass().getResourceAsStream("/images/player.png"));
        if (image.isError()) {
            throw new RuntimeException("Error loading player image.");
        }

        this.player = new PlayerCharacter(0, 0, playerName);

        // 创建玩家的 ImageView，并设置图像。
        this.playerImage = new ImageView(image);
        playerImage.setFitWidth(20); // 设置图像视图的宽度以适应地图格子的尺寸。
        playerImage.setFitHeight(28);

        // 将玩家图像添加到根面板中。
        root.getChildren().add(playerImage);
    }

    //状态栏
    private void createStatusBoard(VBox root, PlayerCharacter player) {
        this.statusBoard = new HBox(50); // 间距
        statusBoard.setStyle("-fx-background-color: #FFFFFF;");
        statusBoard.setPrefHeight(50); // 设置HBox的预设高度为50像素

        nameLabel = new Label("Name: " + player.getName());

        carbonLabel = new Label("Carbon HP: " + player.getCarbonHP());

        gemCountLabel = new Label("Gems Num: " + player.getGemNumber());
        timeRemainingLabel = new Label("Time: " + gameTimeRemaining + "s"); // 显示剩余时间

        nameLabel.getStyleClass().add("label_map");
        carbonLabel.getStyleClass().add("label_carbon");
        gemCountLabel.getStyleClass().add("label_map");
        timeRemainingLabel.getStyleClass().add("label_time");

        Button btn3 = new Button("");
        btn3.getStyleClass().add("exit_small");
        btn3.setOnAction(controller::handleExit);

        // 创建推挤组件
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // spacer会推挤其他元素到右侧

        // 将组件添加到HBox
        statusBoard.getChildren().addAll(timeRemainingLabel, nameLabel, carbonLabel, gemCountLabel, spacer, btn3);
        root.getChildren().add(statusBoard);
    }


    //随机宝石
    private void createRandomGem(Pane root, int gameWidth, int gameHeight) {

        //时间>1才生成宝石
        if (gameTimeRemaining > 1) {

            Random random = new Random();

            int gem_x;
            int gem_y;

            //宝石不能在水里、rail和玩家位置生成
            do {
                gem_x = random.nextInt(gameWidth);
                gem_y = random.nextInt(gameHeight);
            }
            while (gameMap.getTile(gem_x, gem_y).isForbidden || gameMap.getTile(gem_x, gem_y).isRail || (gem_x == player.x && gem_y == player.y));


            this.gem = new Gem(gem_x, gem_y);
            String gemType = gem.getType();
            int liveTime = gem.getLiveTime();

            Image gemImage = new Image(getClass().getResourceAsStream("/images/" + gemType + ".gif"));
            this.gemImageView = new ImageView(gemImage);
            gemImageView.setX(gem_x * TILE_SIZE - 7);
            gemImageView.setY(gem_y * TILE_SIZE - 3);
            gemImageView.setFitWidth(30);
            gemImageView.setFitHeight(30);

            // 宝石根据自身的timelive时间来消失
            Timeline disappearTimeline = new Timeline(new KeyFrame(Duration.seconds(liveTime), e -> {
                pane.getChildren().remove(gemImageView);
                //消失后生成下一颗宝石

                continueGeneratingGems = true;
                scheduleNextGem(root, gameWidth, gameHeight);
            }));


            // 宝石点击事件

            gemImageView.setOnMouseClicked(event -> {
                //点击后不再继续生成下一颗宝石
                continueGeneratingGems = false;

                handleGemClick();

                //点击后宝石不再根据自身时间消失
                disappearTimeline.stop();

                scheduleNextGem(root, gameWidth, gameHeight);

                //确保每颗宝石只能被点击一次
                gemImageView.setMouseTransparent(true);

            });


            root.getChildren().add(gemImageView);
            disappearTimeline.play();

        }
        //获得路线选项
        this.navigationService = new NavigationService(gameMap);
        this.routeList = navigationService.navigate(player, gem);
        System.out.println(routeList);
    }


    private void scheduleNextGem(Pane root, int gameWidth, int gameHeight) {
        if (continueGeneratingGems) {
            Random random = new Random();
            int delay = 1 + random.nextInt(2);  // 生成宝石前的随机延迟

            new Timeline(new KeyFrame(Duration.seconds(delay), e -> createRandomGem(root, gameWidth, gameHeight))).play();

        }
        continueGeneratingGems = false;
    }


    // 宝石点击事件的方法
    private void handleGemClick() {

        //路线选项卡
        VBox optionBoard = createOptionBoard(routeList);
        //控制选项版的位置
        optionBoard.layoutXProperty().bind(pane.widthProperty().subtract(optionBoard.widthProperty()).divide(2));
        optionBoard.layoutYProperty().bind(pane.heightProperty().subtract(optionBoard.heightProperty()).divide(2));

        pane.getChildren().add(optionBoard);

    }


    //绘制tile边框，方便观察
    private void drawTileBorders(Pane root, int gameWidth, int gameHeight) {
        for (int x = 0; x < gameWidth; x++) {
            for (int y = 0; y < gameHeight; y++) {
                Rectangle border = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                border.setFill(null); // 不填充颜色，只显示边框
                border.setStroke(Color.web("#00ffff", 0.1)); // 设置边框颜色为白色，半透明
                border.setStrokeWidth(1); // 设置边框宽度
                root.getChildren().add(border);
            }
        }
    }

    //选项板
    private VBox createOptionBoard(List<Route> routeList) {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("CHOOSE A ROUTE");
        titleLabel.getStyleClass().add("route-title");
        root.getChildren().add(titleLabel);

        for (int i = 0; i < routeList.size(); i++) {
            Route route = routeList.get(i);

            String routeDetails = "Route " + (i + 1) + ": " + route.getTrafficType() +
                    " Carbon HP Cost: " + route.getTotalCarbon();

            Button routeButton = new Button(routeDetails);
            routeButton.getStyleClass().add("route-button");
            routeButton.setId("Route" + (i + 1)); // 设置按钮ID


            int finalI = i;

            routeButton.setOnAction(event -> {

                System.out.println("Route " + (finalI + 1) + " selected");


                this.routeCost = (int) route.getTotalCarbon();

                //只有在Carbon HP大于路线的消耗时，选项才能被点击
                if (player.getCarbonHP() >= routeCost) {
                    movePlayerToGem(route);  // 绑定动画触发方法
                    System.out.println("route cost is " + routeCost);
                    //点击后关闭选项卡
                    pane.getChildren().removeAll(root);


                } else {

                    Label notice = new Label("                                         You don't have enough Carbon HP!");
                    // 设置标签的尺寸
                    notice.setMinSize(350, 250); // 根据需要设置尺寸
                    // 设置背景颜色和文字大小
                    notice.setStyle("-fx-background-color: transparent; -fx-font-size: 26px; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 5, 0.6, 0, 0);");

                    pane.getChildren().add(notice);

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event2 -> pane.getChildren().remove(notice));
                    pause.play();


                }


            });
            root.getChildren().add(routeButton);


        }

        //SKIP按钮
        Label skip = new Label("SKIP THIS GEM");
        skip.getStyleClass().add("skip");
        root.getChildren().add(skip);
        skip.setOnMouseClicked(event -> {
            // 移除选项板
            pane.getChildren().remove(root);
            pane.getChildren().remove(gemImageView);
            // 立即触发下一颗宝石的生成
            continueGeneratingGems = true;
            scheduleNextGem(pane, gameMap.getWidth(), gameMap.getHeight());
        });

        return root;
    }


    private void movePlayerToGem(Route route) {
        List<Path> paths = route.getPathList();
        Queue<Pair<Path, PathTransition>> transitions = new LinkedList<>();

        Tile lastTile = null;

        for (Path path : paths) {
            javafx.scene.shape.Path segmentPath = new javafx.scene.shape.Path();
            List<Tile> tiles = path.getTileList();
            if (tiles.isEmpty()) continue;

            Tile startTile = tiles.get(0);
            lastTile = tiles.get(tiles.size() - 1);
            segmentPath.getElements().add(new MoveTo(startTile.getX() * TILE_SIZE + 9, startTile.getY() * TILE_SIZE + 8));

            for (int i = 1; i < tiles.size(); i++) {
                Tile tile = tiles.get(i);
                segmentPath.getElements().add(new LineTo(tile.getX() * TILE_SIZE + 9, tile.getY() * TILE_SIZE + 8));
            }

            PathTransition transition = new PathTransition();
            transition.setPath(segmentPath);
            transition.setNode(playerImage);
            transition.setInterpolator(Interpolator.LINEAR);

            //时间：路线长度*交通类型
            transition.setDuration(Duration.seconds(0.3 * tiles.size() * calculateSpeedFactor(path.getTrafficType())));

            transitions.add(new Pair<>(path, transition));  // Store Path with its transition
        }

        // Play all transitions in sequence
        playTransitionsSequentially(transitions, lastTile);
    }

    //
    private void playTransitionsSequentially(Queue<Pair<Path, PathTransition>> transitions, Tile lastTile) {
        if (transitions.isEmpty()) {
            return;
        }

        Pair<Path, PathTransition> currentPair = transitions.poll();
        PathTransition currentTransition = currentPair.getValue();

        currentTransition.setOnFinished(event -> {
            System.out.println("Segment completed.");
            playTransitionsSequentially(transitions, lastTile);
        });

        if (transitions.isEmpty()) {
            currentTransition.setOnFinished(event -> {

                System.out.println("Animation completed.");
                updatePlayerSpriteImage(TrafficType.WALK); // Reset to default walking image

                collectGem(gem, lastTile);
                createRandomGem(pane, gameMap.getWidth(), gameMap.getHeight());
            });
        }

        updatePlayerSpriteImage(currentPair.getKey().getTrafficType());
        System.out.println("Starting animation...");
        currentTransition.play();
    }


    private double calculateSpeedFactor(TrafficType trafficType) {
        // 根据交通类型返回不同的速度因子。
        switch (trafficType) {
            case WALK:
                return 1.5;
            case BIKE:
                return 0.5;
            case BUS:
                return 0.4;
            case CAR:
                return 0.3;
            case TRAIN:
                return 0.2;
            default:
                return 0.6;
        }
    }


    private void collectGem(Gem gem, Tile lastTile) {
        // 更新玩家位置
        player.setX(lastTile.getX());
        player.setY(lastTile.getY());

        // 从界面上移除宝石的视图
        pane.getChildren().remove(gemImageView);

        // 给玩家各种属性记分
        player.addGemNumber(1);
        player.addGemScore(gem.getScore());
        player.substractCarbonHP(routeCost);

        //计分后更新状态栏
        updateStatusBoard();

        if (continueGeneratingGems) {
            scheduleNextGem(pane, gameMap.getWidth(), gameMap.getHeight()); // 继续生成新的宝石
        }

    }


    private void updateStatusBoard() {
        nameLabel.setText("Name: " + player.getName());
        carbonLabel.setText("Carbon HP: " + player.getCarbonHP());
        gemCountLabel.setText("Gems Num: " + player.getGemNumber());
    }

    // 更新玩家角色的图像以反映当前的移动方式，例如自行车或公交车。
    private void updatePlayerSpriteImage(TrafficType trafficType) {
        String imagePath;
        double width = 20;  // 默认宽度
        double height = 28;  // 默认高度

        switch (trafficType) {
            case BIKE:
                imagePath = "/images/bike.png";
                width = 35;   // 更宽的图像
                height = 30;  // 更高的图像
                break;
            case BUS:
                imagePath = "/images/bus.png";
                width = 35;
                height = 30;
                break;
            case TRAIN:
                imagePath = "/images/rail.png";
                width = 35;
                height = 30;
                break;
            case CAR:
                imagePath = "/images/texi.png";
                width = 38;
                height = 28;
                break;
            case WALK:
                imagePath = "/images/side.png";
                // 使用默认尺寸
                break;
            default:
                imagePath = "/images/player.png";
                // 使用默认尺寸
                break;
        }

        Image newImage = new Image(getClass().getResourceAsStream(imagePath));
        playerImage.setImage(newImage);
        playerImage.setFitWidth(width);
        playerImage.setFitHeight(height);
    }


    public Scene getScene() {
        return this.scene;
    }
}