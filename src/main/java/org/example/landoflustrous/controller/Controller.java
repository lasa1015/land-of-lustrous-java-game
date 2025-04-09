package org.example.landoflustrous.controller;

import javafx.event.ActionEvent;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    public Controller() {
    }

    //各个场景通用的结束游戏方法
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    //场景通用的保存游戏记录到txt文件
    public void saveGameRecord(String playerName, int gemNum, String levelId) throws IOException {
        // 根据 LevelId 选择文件名
        String fileName;
        if ("Level 1".equals(levelId)) {
            fileName = "game_records1.txt";
        } else if ("Level 2".equals(levelId)) {
            fileName = "game_records2.txt";
        } else {
            // 如果 LevelId 不是 Level 1 或 Level 2，可以选择抛出异常或者保存到默认文件
            fileName = "game_records_other.txt";
        }

        // 构建路径
        Path path = Paths.get("src/main/resources", fileName);

        // 使用 try-with-resources 语句确保 PrintWriter 能正确关闭
        try (PrintWriter out = new PrintWriter(new FileWriter(path.toString(), true))) {
            System.out.println("保存记录到文件：" + fileName);
            // 将玩家名字、宝石数量和等级ID存储到文件
            out.println(levelId + "," + gemNum + "," + playerName);
        }
    }

    //场景通用的读取游戏排行榜记录的txt文件
    public List<String[]> readFileByLevelId(String levelId) throws IOException {
        String fileName = null;
        if (levelId == "Level 1") {
            fileName = "game_records1.txt";
        }
        if (levelId == "Level 2") {
            fileName = "game_records2.txt";
        }

        Path path = Paths.get("src/main/resources", fileName);
        List<String[]> content = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.add(line.split(","));
            }
        }

        return content;
    }


}