package org.example.landoflustrous.model;

import java.util.Objects;

public class Tile extends Coordinated {

    public boolean isRail;
    public boolean isRoad;
    public boolean isForbidden;

    public Tile(int x, int y, boolean isRail, boolean isRoad, boolean isForbidden) {
        super(x, y);
        this.isRail = isRail;
        this.isRoad = isRoad;
        this.isForbidden = isForbidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;
        return this.x == tile.x && this.y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static Tile createTile(int index, int x, int y) {
        return switch (index) {
            case 0 -> new Tile(x, y, false, false, true);
            case 1, 7, 8 -> new Tile(x, y, false, true, false);
            case 2 -> new Tile(x, y, true, false, false);
            case 3, 9 -> new Tile(x, y, true, true, false);
            case 4 -> new Tile(x, y, false, false, false);
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    // Getter for x coordinate
    public int getX() {
        return x;
    }

    // Getter for y coordinate
    public int getY() {
        return y;
    }
}
