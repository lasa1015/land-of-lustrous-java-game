package org.example.landoflustrous.service.strategy;

import org.example.landoflustrous.model.*;
import org.example.landoflustrous.util.Pair;

import java.util.List;

import java.util.*;

public class BikeStrategy extends TrafficStrategy {

    public BikeStrategy(GameMap gameMap) {
        super(gameMap);
    }

    public Route navigate(Coordinated start, Coordinated end) {
        return navigate(start, end, findPathRoaming(start, end, TrafficType.WALK));
    }

    public Route navigate(Coordinated start, Coordinated end, Path walkPath) {

        if (walkPath == null) {
            return null;
        }

        List<Station> sortedStartStations, sortedEndStations;
        if ((sortedStartStations = getSortedStationsByDistance(start, walkPath.getLength())).isEmpty()
                || (sortedEndStations = getSortedStationsByDistance(end, walkPath.getLength())).isEmpty()) return null;

        List<Pair<Station, Station>> stationPairs = Pair.generateFirstNPairs(sortedStartStations, sortedEndStations, 6);

        Route bestRoute = null;
        int bestCost = Integer.MAX_VALUE;
        for (Pair<Station, Station> pair : stationPairs) {
            Path toFirstStation = findPathRoaming(start, pair.getLeft(), TrafficType.WALK);
            Path bikeSegment = findPathRoaming(pair.getLeft(), pair.getRight(), TrafficType.BIKE);
            Path fromLastStation = findPathRoaming(pair.getRight(), end, TrafficType.WALK);

            Route currentRoute = new Route(Arrays.asList(toFirstStation, bikeSegment, fromLastStation));
            int currentCost = currentRoute.getTotalCost();
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestRoute = currentRoute;
            }
        }

        return bestRoute;
    }

    private List<Station> getSortedStationsByDistance(Coordinated point, int pathLength) {
        return gameMap.getBikeStations().stream()
                .filter(station -> point.distance(station) < pathLength)
                .sorted(Comparator.comparingInt(point::distance))
                .toList();
    }
}


