package org.example.landoflustrous.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Every class that has a coordinate must extend from this class.
 */

public class Coordinated {

    public int x;
    public int y;

    public Coordinated(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distance(Coordinated other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public String getCoordinateString() {
        return String.format("(%d, %d)", x, y);
    }

    public <T extends Coordinated> List<T> getNearestN(List<T> coordinatedList, int n) {
        List<T> sortedList = new ArrayList<>(coordinatedList);
        sortedList.sort((c1, c2) -> Integer.compare(distance(c1), distance(c2)));
        return sortedList.subList(0, Math.min(n, sortedList.size()));
    }


}