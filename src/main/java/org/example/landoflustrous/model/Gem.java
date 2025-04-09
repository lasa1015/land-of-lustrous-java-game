package org.example.landoflustrous.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Gem extends Coordinated {
    private String gemType; // Stores the type of the gem (Ruby, Sapphire, Emerald)
    private Integer[] gemDetails; // Stores details like score and live time
    private int liveTime; // Defines how long the gem appears

    // Example gem types, scores, and live times
    private static Map<String, Integer[]> availableGemTypes = new HashMap<>();
    private static Random random = new Random();

    static {
        availableGemTypes.put("Ruby", new Integer[]{100, 4}); // Score: 100, Live time: 3 seconds
        availableGemTypes.put("Sapphire", new Integer[]{200, 3}); // Score: 200, Live time: 5 seconds
        availableGemTypes.put("Emerald", new Integer[]{300, 2}); // Score: 300, Live time: 7 seconds
    }

    // Constructor that takes coordinates and assigns a random type
    public Gem(int x, int y) {
        super(x, y);
        assignRandomType();
    }

    // Method to randomly assign a type to the gem
    private void assignRandomType() {
        Object[] types = availableGemTypes.keySet().toArray();
        gemType = (String) types[random.nextInt(types.length)];
        gemDetails = availableGemTypes.get(gemType);
        liveTime = gemDetails[1];
    }

    // Method to get the type of the gem (e.g., "Ruby")
    public String getType() {
        return gemType;
    }

    // Method to get the type details including score and live time
    public Map<String, Integer[]> getTypeDetails() {
        Map<String, Integer[]> details = new HashMap<>();
        details.put(gemType, gemDetails);
        return details;
    }

    // Method to get the live time of the gem
    public int getLiveTime() {
        return liveTime;
    }

    public int getScore() {
        return gemDetails[0]; // 返回分数
    }

}
