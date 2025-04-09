package org.example.landoflustrous.service.strategy;

import org.example.landoflustrous.model.*;

import java.util.*;


public abstract class TrafficStrategy {

    static final int WALK_TOLERANCE = 10;
    GameMap gameMap;

    public TrafficStrategy(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Path findPathRoaming(Coordinated start, Coordinated end, TrafficType trafficType) {
        Map<Tile, Integer> costMap = new HashMap<>();
        Map<Tile, Tile> parentMap = new HashMap<>();
        PriorityQueue<Tile> openList = new PriorityQueue<>(Comparator.comparingInt(costMap::get));
        Set<Tile> closedList = new HashSet<>();

        Tile startTile = gameMap.getTile(start);
        costMap.put(startTile, 0);
        openList.add(startTile);

        while (!openList.isEmpty()) {
            Tile current = openList.poll();

            if (current.x == end.x && current.y == end.y) {
                return reconstructPath(current, parentMap, costMap, trafficType);
            }

            closedList.add(current);

            for (int[] direction : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                if (newX >= 0 && newX < gameMap.getWidth() && newY >= 0 && newY < gameMap.getHeight()) {
                    Tile neighbor = gameMap.getTile(newX, newY);
                    if (closedList.contains(neighbor)) {
                        continue;
                    }

                    int cost = trafficType.getCostPer(current, neighbor);
                    if (cost == Integer.MAX_VALUE) {
                        continue;
                    }

                    int tentativeGScore = costMap.getOrDefault(current, Integer.MAX_VALUE) + cost;
                    if (!costMap.containsKey(neighbor) || tentativeGScore < costMap.get(neighbor)) {
                        costMap.put(neighbor, tentativeGScore);
                        parentMap.put(neighbor, current);

                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Path reconstructPath(Tile end, Map<Tile, Tile> parentMap, Map<Tile, Integer> costMap, TrafficType trafficType) {
        List<Tile> tileList = new ArrayList<>();
        Tile current = end;
        while (current != null) {
            tileList.add(current);
            current = parentMap.get(current);
        }
        Collections.reverse(tileList);

        return new Path(trafficType, tileList);
    }
}
