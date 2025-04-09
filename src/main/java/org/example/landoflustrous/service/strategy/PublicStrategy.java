package org.example.landoflustrous.service.strategy;

import org.example.landoflustrous.model.*;
import org.example.landoflustrous.util.ListGrouper;
import org.example.landoflustrous.util.Pair;

import java.util.*;

public class PublicStrategy extends TrafficStrategy {

    private final Map<Station, Map<Station, Edge>> matrix = new HashMap<>();

    public PublicStrategy(GameMap gameMap) {
        super(gameMap);
        List<Station> busStationsList = new ArrayList<>();
        gameMap.getBusLines().forEach(busLine -> {
            addTransLine(busLine);
            busStationsList.addAll(busLine.getStations());
        });

        List<List<Station>> busShiftingStations = ListGrouper.groupAndFilter(busStationsList, busStation -> Objects.hash(busStation.x, busStation.y));
        busShiftingStations.forEach(sameStaionList ->
                Pair.generateUnorderedPairs(sameStaionList).forEach(pair ->
                        addEdge(pair.getLeft(), pair.getRight(), new Edge(new Path(TrafficType.WALK, List.of(gameMap.getTile(pair.getLeft())))))));

        List<List<Station>> stationLists = new ArrayList<>();
        gameMap.getRailLines().forEach(railLine -> {
            addTransLine(railLine);
            stationLists.add(railLine.getStations());
        });
        stationLists.add(busStationsList);

        Pair.generateUnorderedPairs(stationLists).forEach(listPair -> Pair.generatePairs(listPair.getLeft(), listPair.getRight()).forEach(pair -> {
            if (pair.getLeft().distance(pair.getRight()) < WALK_TOLERANCE * 2) {
                addEdge(pair.getLeft(), pair.getRight(), new Edge(findPathRoaming(pair.getLeft(), pair.getRight(), TrafficType.WALK)));
            }
        }));
    }

    public Route navigate(Coordinated start, Coordinated end, boolean enableTrain) {
        return navigate(start, end, findPathRoaming(start, end, TrafficType.WALK), enableTrain);
    }

    public Route navigate(Coordinated start, Coordinated end, Path walkPath, boolean enableTrain) {
        if (walkPath == null) return null;
        List<Station> startStations = enableTrain? start.getNearestN(gameMap.getPublicStations(), Integer.MAX_VALUE) : start.getNearestN(gameMap.getBusStations(), Integer.MAX_VALUE);
        List<Station> endStations = enableTrain? end.getNearestN(gameMap.getPublicStations(), Integer.MAX_VALUE) : end.getNearestN(gameMap.getBusStations(), Integer.MAX_VALUE);

        int bestCost = Integer.MAX_VALUE;
        Route bestRoute = null;
        for (Pair<Station, Station> pair : Pair.generatePairs(startStations, endStations)) {
            Route currentRoute = new Route();
            currentRoute.addPath(findPathRoaming(start, pair.getLeft(), TrafficType.WALK));
            currentRoute.mergeRoute(constructRoute(navigateStations(pair.getLeft(), pair.getRight(), enableTrain)));
            currentRoute.addPath(findPathRoaming(pair.getRight(), end, TrafficType.WALK));
            int currentCost = currentRoute.getTotalCost();
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestRoute = currentRoute;
            }
        }
        if (bestCost > walkPath.getCost()) return null;
        return bestRoute;
    }

    private Route constructRoute(List<Station> stationList) {
        List<Path> pathList = new ArrayList<>();
        Path currentPath = null;
        for (int i = 0;i < stationList.size() - 1;i++) {
            Path thisPath = matrix.get(stationList.get(i)).get(stationList.get(i + 1)).getPath().copy();
            if (currentPath == null) currentPath = thisPath;
            else if (currentPath.getTrafficType() == thisPath.getTrafficType()) currentPath.appendPath(thisPath);
            else {
                pathList.add(currentPath);
                currentPath = thisPath;
            }
        }
        if (currentPath != null) pathList.add(currentPath);
        return new Route(pathList);
    }

    private List<Station> navigateStations(Station station1, Station station2, boolean enableTrain) {
        if (!hasStation(station1) || !hasStation(station2)) return null;
        Map<Station, Integer> costTable = new HashMap<>();
        matrix.keySet().forEach(station -> {
            if (station.equals(station1)) costTable.put(station, 0);
            else costTable.put(station, Integer.MAX_VALUE);
        });

        Map<Station, Station> confirmedStations = new HashMap<>();
        Map<Station, Station> queuedStations = new HashMap<>();
        queuedStations.put(station1, null);
        for (Station currentStation = station1;!confirmedStations.containsKey(station2) && !queuedStations.isEmpty();) {
            Station finalCurrentStation = currentStation;
            matrix.get(currentStation).forEach((station, edge) -> {
                if ((enableTrain || edge.getTrafficType() != TrafficType.TRAIN) && edge.getCost() + costTable.get(finalCurrentStation) < costTable.get(station)) {
                    costTable.put(station, edge.getCost() + costTable.get(finalCurrentStation));
                    queuedStations.put(station, finalCurrentStation);
                }
            });
            currentStation = queuedStations.keySet().stream().min(Comparator.comparingInt(costTable::get)).get();
            confirmedStations.put(currentStation, queuedStations.get(currentStation));
            queuedStations.remove(currentStation);
        }

        List<Station> ret = new ArrayList<>();
        for (Station station = station2;station != null;station = confirmedStations.get(station)) ret.add(station);

        return ret.reversed();
    }

    private boolean hasStation(Station station) {
        return matrix.containsKey(station);
    }

    private void addEdge(Station station1, Station station2, Edge edge) {

        Edge edge1, edge2;
        if (edge.getFirst().distance(station1) == 0) {
            edge1 = edge;
            edge2 = edge.createReversed();
        } else {
            edge2 = edge;
            edge1 = edge.createReversed();
        }

        if (matrix.containsKey(station1)) matrix.get(station1).put(station2, edge1);
        else matrix.put(station1, new HashMap<>(Map.of(station2, edge1)));

        if (matrix.containsKey(station2)) matrix.get(station2).put(station1, edge2);
        else matrix.put(station2, new HashMap<>(Map.of(station1, edge2)));
    }

    public void addTransLine(PublicTransLine publicTransLine) {
        final List<Station> s = publicTransLine.getStations();
        for (int i = 0;i < s.size() - 1;i++) {
            addEdge(s.get(i), s.get(i + 1), new Edge(publicTransLine.slicePath(s.get(i), s.get(i + 1))));
        }
    }

}

class Edge {

    private final Path path;

    public Edge(Path path) {
        this.path = path;
    }

    public int getCost() {
        return path.getCost() + ((path.getTrafficType() == TrafficType.WALK)? TrafficType.WALK.getChangeCost(TrafficType.BUS) : 0);
    }

    public TrafficType getTrafficType() {
        return path.getTrafficType();
    }

    public Path getPath() {
        return path;
    }

    public Tile getFirst() {
        return path.getTileList().getFirst();
    }

    public Edge createReversed() {
        return new Edge(new Path(getTrafficType(), new ArrayList<>(path.getTileList()).reversed()));
    }
}
