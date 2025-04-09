package org.example.landoflustrous.model;

import java.util.Random;

public class Popup {
    private static final String[] ecoTips = {
            "Forests absorb over 2 billion tons of carbon dioxide yearly, playing a vital role in combating climate change.",
            "Recycling one aluminum can save enough energy to run a TV for three hours.",
            "On average, each person in the world throws away about 200 kilograms of packaging each year.",
            "Using public transportation for a month can save about 30% of carbon emissions.",
            "A simple action like turning off the lights can reduce carbon emissions by about 400 pounds a year.",
            "Planting trees is one of the most effective and low-cost ways to combat climate change.",
            "Carpooling not only reduces traffic congestion but can also significantly lower carbon emissions.",
            "The use of emails has greatly reduced the consumption of paper and the felling of trees.",
            "Using solar panels for 10 years can prevent more than a ton of carbon dioxide emissions.",
            "Switching to energy-saving light bulbs is a simple way to reduce household energy consumption."
    };

    public Popup() {
    }

    // 显示数据的方法
    public String displayData(int gemsCollected, int carbonEmission, int score, long timeSpent) {
        String ecoTip = getRandomEcoTip(); // 从内部数据中随机选取环保小贴士
        // 组织信息字符串
        String data = "==============================SCORE BOARD=================================\n" +
                "Gems Collected: " + gemsCollected + "\n" +
                "Carbon Emission: " + carbonEmission + " kg\n" +
                "Score: " + score + "\n" +
                "Time Spent: " + timeSpent + " s\n" +
                "【Eco Tip: " + ecoTip+"】";
        return data;
    }

    // 随机选择一个环保小贴士
    public String getRandomEcoTip() {
        Random random = new Random();
        return ecoTips[random.nextInt(ecoTips.length)];
    }
}
