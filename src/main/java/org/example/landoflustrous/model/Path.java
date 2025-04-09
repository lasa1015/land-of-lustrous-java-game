package org.example.landoflustrous.model;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private TrafficType trafficType;
    private final List<Tile> tileList;
    private int cost;

    public Path(int x, int y, int x1, int y1, TrafficType trafficType, List<Tile> tileList) {
        this.tileList = tileList;
    }

    public List<Tile> getTileList() {
        return tileList;
    }

    public Path(TrafficType trafficType) {
        this.trafficType = trafficType;
        this.tileList = new ArrayList<>();
        this.cost = 0;
    }

    public Path(TrafficType trafficType, List<Tile> tileList) {
        this.trafficType = trafficType;
        this.tileList = new ArrayList<>();
        this.cost = 0;
        tileList.forEach(this::addTile);
    }

    public void addTile(Tile tile) {
        if (!tileList.isEmpty()) {
            cost += trafficType.getCostPer(tileList.getLast(), tile);
        }
        tileList.add(tile);
    }

    public int getCost() {
        return cost;
    }

    public int getLength() {
        return Math.max(tileList.size() - 1, 0);
    }

    public TrafficType getTrafficType() {
        return trafficType;
    }

    public Path slicePath(Coordinated start, Coordinated end) {
        List<Tile> recorded = new ArrayList<>();
        boolean isRecording = false;
        for (Tile tile : tileList) {
            if (!isRecording && tile.distance(start) == 0) isRecording = true;
            if (isRecording) recorded.add(tile);
            if (tile.distance(end) == 0) isRecording = false;
        }
        if (isRecording || recorded.isEmpty()) throw new RuntimeException("Slice point is not in the path");
        return new Path(trafficType, recorded);
    }

    public void appendPath(Path path) {
        if (trafficType != path.trafficType) throw new RuntimeException(String.format("Path types don't match: %s vs %s", trafficType, path.trafficType));
        for (Tile tile : path.tileList) {
            if (tile.equals(tileList.getLast())) continue;
            addTile(tile);
        }
    }

    public void appendPath(List<Path> pathList) {
        pathList.forEach(this::appendPath);
    }

    public double getCarbon() {
        return getLength() * trafficType.getCarbonPer();
    }

    public static Path mergePath(List<Path> pathList) {
        Path ret = new Path(pathList.getFirst().getTrafficType());
        ret.appendPath(pathList);
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tile tile : tileList) {
            stringBuilder.append(tile.getCoordinateString()).append(", ");
        }
        return String.format("%s: %s cost: %s", trafficType.toString(), stringBuilder, cost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (cost != path.cost) return false;
        if (trafficType != path.trafficType) return false;
        return tileList.equals(path.tileList);
    }

    @Override
    public int hashCode() {
        int result = trafficType.hashCode();
        result = 31 * result + tileList.hashCode();
        result = 31 * result + cost;
        return result;
    }

    public Path copy() {
        return new Path(this.trafficType, this.tileList);
    }
}
