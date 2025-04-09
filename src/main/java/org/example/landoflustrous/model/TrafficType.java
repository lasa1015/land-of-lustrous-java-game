package org.example.landoflustrous.model;

public enum TrafficType {
    WALK, BIKE, BUS, CAR, TRAIN;

    @Override
    public String toString() {
        return switch (this) {
            case WALK -> "walk";
            case BIKE -> "bike";
            case BUS -> "bus";
            case CAR -> "car";
            case TRAIN -> "luas";
        };
    }

    // Carbon emission on move to an adjacent tile
    public double getCarbonPer() {
        return switch (this) {
            case WALK, BIKE -> 1.0;
            case BUS, TRAIN -> 5.0;
            case CAR -> 10.0;
        };
    }

    // Time spent on move to an adjacent tile, tile types considered
    public int getCostPer(Tile tile1, Tile tile2) {
        switch (this) {
            case WALK:
                if (tile1.isRoad && tile2.isRoad) {
                    return 25;
                } else if (tile1.isRail || tile2.isRail || tile1.isForbidden || tile2.isForbidden) {
                    return Integer.MAX_VALUE;
                } else {
                    return 50;
                }
            case BIKE:
                return (tile1.isRoad && tile2.isRoad) ? 12 : Integer.MAX_VALUE;
            case BUS:
                return (tile1.isRoad && tile2.isRoad) ? 4 : Integer.MAX_VALUE;
            case CAR:
                return (tile1.isRoad && tile2.isRoad) ? 3 : Integer.MAX_VALUE;
            case TRAIN:
                return (tile1.isRail && tile2.isRail) ? 2 : Integer.MAX_VALUE;
            default:
                return Integer.MAX_VALUE;
        }
    }

    // Time spent on changing between traffic types
    public int getChangeCost(TrafficType next) {
        if (next.equals(TrafficType.WALK)) {
            if (this.equals(TrafficType.WALK) || this.equals(TrafficType.CAR)) return 1;
            else return 10;
        } else if (next.equals(TrafficType.BIKE)) return 10;  // Rent a bike
        else if (next.equals(TrafficType.CAR)) return 50;  // Wait for a taxi
        else return 100;  // Wait for a bus or train
    }
}
