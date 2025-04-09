package org.example.landoflustrous.model;

import java.util.Objects;

public class Station extends Coordinated {

    private final String lineCode;

    public Station(int x, int y, String lineCode) {
        super(x, y);
        this.lineCode = lineCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;
        return this.distance(station) == 0 && this.lineCode.equals(((Station) o).lineCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, lineCode);
    }
}
