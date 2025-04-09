package org.example.landoflustrous.model;

import java.util.ArrayList;
import java.util.List;

public class PublicTransLine extends Path {

    private final List<Station> stations;
    private final String lineCode;

    public PublicTransLine(TrafficType trafficType, int id) {
        super(trafficType);
        this.stations = new ArrayList<>();
        this.lineCode = trafficType.toString() + id;
    }

    public String getLineCode() {
        return lineCode;
    }

    public List<Station> getStations() {
        return stations;
    }

}
