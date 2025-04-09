package org.example.landoflustrous.service.strategy;

import org.example.landoflustrous.model.*;
import org.example.landoflustrous.util.Pair;

import java.util.List;

import java.util.*;

public class TaxiStrategy extends TrafficStrategy {

    public TaxiStrategy(GameMap gameMap) {
        super(gameMap);
    }

    public Route navigate(Coordinated start, Coordinated end) {
        return navigate(start, end, findPathRoaming(start, end, TrafficType.WALK));
    }

    public Route navigate(Coordinated start, Coordinated end, Path walkPath) {

        if (walkPath == null) {
            return null;
        }

        List<Tile> startEdgeTiles, endEdgeTiles;
        Map<Tile, Integer> startBlockCosts = null, endBlockCosts = null;
        if (gameMap.getTile(start).isRoad) startEdgeTiles = List.of(gameMap.getTile(start));
        else {
            startBlockCosts = calculateCosts(start);
            startEdgeTiles = selectNearestTiles(start, startBlockCosts);
        }
        if (gameMap.getTile(end).isRoad) endEdgeTiles = List.of(gameMap.getTile(end));
        else {
            endBlockCosts = calculateCosts(end);
            endEdgeTiles = selectNearestTiles(end, endBlockCosts);
        }

        List<Pair<Tile, Tile>> edgeTilePairs = Pair.generateFirstNPairs(startEdgeTiles, endEdgeTiles, 15);
        int bestCost = Integer.MAX_VALUE;
        Pair<Tile, Tile> bestPair = null;
        for (Pair<Tile, Tile> edgeTilePair : edgeTilePairs) {
            int startWalkCost = (startBlockCosts == null)? 0 : startBlockCosts.get(edgeTilePair.getLeft());
            int endWalkCost = (endBlockCosts == null)? 0 : endBlockCosts.get(edgeTilePair.getRight());
            Path carPath = findPathRoaming(edgeTilePair.getLeft(), edgeTilePair.getRight(), TrafficType.CAR);
            if (carPath == null) break;
            int currentCost = startWalkCost + endWalkCost + carPath.getCost();
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestPair = edgeTilePair;
            }
        }

        if (bestCost >= walkPath.getCost()) return null;

        return new Route(List.of(
                findPathRoaming(start, bestPair.getLeft(), TrafficType.WALK),
                findPathRoaming(bestPair.getLeft(), bestPair.getRight(), TrafficType.CAR),
                findPathRoaming(bestPair.getRight(), end, TrafficType.WALK)
        ));
    }

    public List<Tile> selectNearestTiles(Coordinated point, Map<Tile, Integer> costs) {
        Block block = extractBlock(point);
        List<List<Tile>> edges = block.extractEdges();

        List<Tile> ret = new ArrayList<>();
        for (List<Tile> edge : edges) {
            int minCost = Integer.MAX_VALUE;
            Tile nearest = null;
            for (Tile tile : edge) {
                Integer tileCost = costs.get(tile);
                if (tileCost != null && tileCost < minCost) {
                    minCost = tileCost;
                    nearest = tile;
                }
            }
            if (nearest != null) {
                ret.add(nearest);
            }
        }
        ret.sort(Comparator.comparingInt(costs::get));
        return ret;
    }

    public Map<Tile, Integer> calculateCosts(Coordinated coord) {
        Block block = extractBlock(coord);

        Map<Tile, Integer> distances = new HashMap<>();
        for (List<Tile> col : block.tiles) {
            for (Tile tile : col) {
                distances.put(tile, Integer.MAX_VALUE);
            }
        }

        Tile startTile = gameMap.getTile(coord.x, coord.y);
        distances.put(startTile, 0);

        Queue<Tile> queue = new LinkedList<>();
        queue.offer(startTile);

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            int currentDist = distances.get(current);

            for (Tile neighbor : block.getNeighbors(current)) {
                int dist = TrafficType.WALK.getCostPer(current, neighbor);
                int newDist = currentDist;
                if (dist == Integer.MAX_VALUE) newDist = Integer.MAX_VALUE;
                else newDist += dist;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    queue.offer(neighbor);
                }
            }
        }

        return distances;
    }

    public Block extractBlock(Coordinated coord) {
        if (gameMap.getTile(coord).isRoad) {
            return null;
        }

        Block block = new Block();
        int x = coord.x;
        int y = coord.y;
        while (!gameMap.getTile(x, y).isRoad) x++;
        int maxX = x--;
        while (!gameMap.getTile(x, y).isRoad) x--;
        int minX = x++;
        while (!gameMap.getTile(x, y).isRoad) y++;
        int maxY = y--;
        while (!gameMap.getTile(x, y).isRoad) y--;
        int minY = y++;
        for (x = minX;x <= maxX;x++) {
            List<Tile> col = new ArrayList<>();
            for (y = minY;y <= maxY;y++) {
                col.add(gameMap.getTile(x, y));
            }
            block.tiles.add(col);
        }

        return block;
    }

    private boolean notAtEdgeOfMap(Coordinated coord) {
        return coord.x != 0 && coord.y != 0 && coord.x != gameMap.getWidth() && coord.y != gameMap.getHeight();
    }
}

class Block {
    List<List<Tile>> tiles = new ArrayList<>();

    Tile getBaseTile() {
        return tiles.getFirst().getFirst();
    }

    Tile getTile(int x, int y) {
        return tiles.get(x).get(y);
    }

    int getWidth() {
        return tiles.size();
    }

    int getHeight() {
        return tiles.getFirst().size();
    }

    public List<Tile> getNeighbors(Tile tile) {
        int x = tile.x - getBaseTile().x;
        int y = tile.y - getBaseTile().y;

        List<Tile> ret = new ArrayList<>();
        if (x > 0) ret.add(getTile(x - 1, y));
        if (x < getWidth() - 1) ret.add(getTile(x + 1, y));
        if (y > 0) ret.add(getTile(x, y - 1));
        if (y < getHeight() - 1) ret.add(getTile(x, y + 1));

        return ret;
    }

    public List<List<Tile>> extractEdges() {
        List<Tile> up = new ArrayList<>();
        List<Tile> low = new ArrayList<>();
        List<Tile> left = new ArrayList<>();
        List<Tile> right = new ArrayList<>();

        for (int x = 0;x < getWidth();x++) {
            up.add(getTile(x, 0));
            low.add(getTile(x, getHeight() - 1));
        }

        for (int y = 0;y < getHeight();y++) {
            left.add(getTile(0, y));
            right.add(getTile(getWidth() - 1, y));
        }

        return List.of(up, low, left, right);
    }
}


