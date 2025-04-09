package org.example.landoflustrous.model;

import java.util.*;

public class Route {

    private final List<Path> pathList;

    public Route() {
        this.pathList = new ArrayList<>();
    }

    public Route(List<Path> pathList) {
        this.pathList = pathList;
    }

    public Tile getFirst() {
        return pathList.getFirst().getTileList().getFirst();
    }

    public Tile getLast() {
        return pathList.getLast().getTileList().getLast();
    }

    public void addPath(Path path) {
        pathList.add(path);
    }

    public void mergeRoute(Route route) {
        pathList.addAll(route.pathList);
    }

    public int getTotalCost() {
        int totalCost = 0;
        TrafficType previousTrafficType = null;
        for (Path path : pathList) {
            totalCost += path.getCost();
            if (previousTrafficType != null) {
                totalCost += previousTrafficType.getChangeCost(path.getTrafficType());
            }
            previousTrafficType = path.getTrafficType();
        }
        return totalCost;
    }

    public double getTotalCarbon() {
        return pathList.stream().mapToDouble(Path::getCarbon).sum();
    }

    public Set<TrafficType> getTrafficType() {
        Set<TrafficType> trafficTypes = new HashSet<>();
        for (Path path : pathList) {
            trafficTypes.add(path.getTrafficType());
        }
        return trafficTypes;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
//        pathList.forEach(path -> stringBuilder.append(path.toString()).append('\n'));
        pathList.forEach(path -> {
            stringBuilder.append("Path: ").append(path.toString());
            stringBuilder.append(" (Traffic Type: ").append(path.getTrafficType()).append(")\n");
        });


        stringBuilder.append("Time: ").append(getTotalCost()).append("   Carbon: ").append(getTotalCarbon());
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return Objects.equals(pathList, route.pathList);
    }

    @Override
    public int hashCode() {
        return pathList != null ? pathList.hashCode() : 0;
    }

    public List<Path> getPathList() {
        return pathList;
    }


    public List<Tile> totalTileList() {
        List<Tile> tileList = new LinkedList<Tile>();
        for (int i = 0; i < this.pathList.size(); i++) {
            List<Tile> temp = this.pathList.get(i).getTileList();
            for (int j = 0; j < temp.size(); j++) {
                tileList.add(temp.get(j));
            }
        }
        return tileList;
    }


}
