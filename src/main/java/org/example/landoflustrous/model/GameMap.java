package org.example.landoflustrous.model;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

//加载游戏地图和公共交通线路的信息，并提供方法用于查询地图和交通线路的相关信息。
public class GameMap {

    private final List<List<Tile>> tileGrid;
    private final Set<Station> bikeStations;
    private final List<BusLine> busLines;
    private final List<RailLine> railLines;

    public GameMap(String filePath, List<String> railLineFilePaths, List<String> busLineFilePaths) throws IOException {
        this.tileGrid = new ArrayList<>();
        this.bikeStations = new HashSet<>();
        this.busLines = new ArrayList<>();
        this.railLines = new ArrayList<>();

        parseTileGrid(filePath);
        parseRailLines(railLineFilePaths);
        parseBusLines(busLineFilePaths);
    }

    // TileGrid is transposed! Use getTile(x, y) or getTile(coord) to get the tile!
    public Tile getTile(int x, int y) {
        return tileGrid.get(y).get(x);
    }

    public Tile getTile(Coordinated coordinated) {
        return tileGrid.get(coordinated.y).get(coordinated.x);
    }

    public int getWidth() {
        return tileGrid.getFirst().size();
    }

    public int getHeight() {
        return tileGrid.size();
    }

    public Set<Station> getBikeStations() {
        return bikeStations;
    }

    public List<BusLine> getBusLines() {
        return busLines;
    }

    public List<Station> getBusStations() {
        return busLines.stream().map(PublicTransLine::getStations).flatMap(List::stream).collect(Collectors.toList());
    }

    public List<RailLine> getRailLines() {
        return railLines;
    }

    public List<Station> getRailStations() {
        return railLines.stream().map(PublicTransLine::getStations).flatMap(List::stream).collect(Collectors.toList());
    }

    public List<Station> getPublicStations() {
        List<Station> stations = new ArrayList<>(getBusStations());
        stations.addAll(getRailStations());
        return stations;
    }

    private void parseTileGrid(String filePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new FileNotFoundException("Cannot find resource " + filePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int y = 0;
                while ((line = br.readLine()) != null) {
                    List<Tile> row = new ArrayList<>();
                    for (int x = 0; x < line.length(); x++) {
                        int index = Character.getNumericValue(line.charAt(x));
                        Tile tile = Tile.createTile(index, x, y);
                        row.add(tile);
                        if (index == 7) {
                            bikeStations.add(new Station(x, y, "CityBikes"));
                        }
                    }
                    tileGrid.add(row);
                    y++;
                }
            }
        }
    }


    private void parseBusLines(List<String> filePaths) throws IOException {
        int id = 0;
        for (String filePath : filePaths) {
            BusLine busLine = new BusLine(id++);
            parseTransLine(filePath, busLine);
            busLines.add(busLine);
        }
    }

    private void parseRailLines(List<String> filePaths) throws IOException {
        int id = 0;
        for (String filePath : filePaths) {
            RailLine railLine = new RailLine(id++);
            parseTransLine(filePath, railLine);
            railLines.add(railLine);
        }
    }

//    private void parseTransLine(String filePath, PublicTransLine line) throws IOException {
//        try (InputStream is = getClass().getResourceAsStream(filePath)) {
//            if (is == null) {
//                throw new FileNotFoundException("Cannot find resource " + filePath);
//            }
//            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
//                String lineContent;
//                int y = 0;
//                while ((lineContent = br.readLine()) != null) {
//                    for (int x = 0; x < lineContent.length(); x++) {
//                        int index = Character.getNumericValue(lineContent.charAt(x));
//                        if (index != 0) {
//                            line.addTile(getTile(x, y));
//                            if (index == 8 || index == 9) {
//                                line.getStations().add(new Station(x, y, line.getLineCode()));
//                            }
//                        }
//                    }
//                    y++;
//                }
//            }
//        }
//    }

    private void parseTransLine(String filePath, PublicTransLine line) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new FileNotFoundException("Cannot find resource " + filePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String lineStr;
                List<List<Integer>> grid = new ArrayList<>();
                int y = 0;
                while ((lineStr = br.readLine()) != null) {
                    List<Integer> row = new ArrayList<>();
                    for (int x = 0; x < lineStr.length(); x++) {
                        int index = Character.getNumericValue(lineStr.charAt(x));
                        row.add(index);
                    }
                    grid.add(row);
                    y++;
                }
                findAndParseRoute(grid, line);
            }
        }
    }

    private void findAndParseRoute(List<List<Integer>> grid, PublicTransLine line) {
        Coordinated start = findStartCoordinate(grid);
        if (start != null) {
            followRoute(grid, start, line, null);
        }
    }

    private Coordinated findStartCoordinate(List<List<Integer>> grid) {
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(y).size(); x++) {
                if (grid.get(y).get(x) != 0 && isEndCoordinate(grid, x, y)) {
                    return new Coordinated(x, y);
                }
            }
        }
        return null;
    }

    private boolean isEndCoordinate(List<List<Integer>> grid, int x, int y) {
        int count = 0;
        if (x > 0 && grid.get(y).get(x - 1) != 0) count++;
        if (x < grid.get(y).size() - 1 && grid.get(y).get(x + 1) != 0) count++;
        if (y > 0 && grid.get(y - 1).get(x) != 0) count++;
        if (y < grid.size() - 1 && grid.get(y + 1).get(x) != 0) count++;
        return count == 1;
    }

    private void followRoute(List<List<Integer>> grid, Coordinated current, PublicTransLine line, Coordinated prev) {
        while (current != null) {
            int x = current.x;
            int y = current.y;
            int tileIndex = grid.get(y).get(x);
            line.addTile(getTile(x, y));
            if (tileIndex == 8 || tileIndex == 9) {
                line.getStations().add(new Station(x, y, line.getLineCode()));
            }
            Coordinated next = getNextCoordinated(grid, x, y, prev);
            prev = current;
            current = next;
        }
    }

    private Coordinated getNextCoordinated(List<List<Integer>> grid, int x, int y, Coordinated prev) {
        List<Coordinated> possibleDirections = Arrays.asList(
                new Coordinated(x - 1, y), new Coordinated(x + 1, y),
                new Coordinated(x, y - 1), new Coordinated(x, y + 1)
        );

        for (Coordinated next : possibleDirections) {
            if (isValidCoordinated(grid, next.x, next.y, prev)) {
                if (grid.get(next.y).get(next.x) != 0) {
                    return next;
                }
            }
        }
        return null;
    }

    private boolean isValidCoordinated(List<List<Integer>> grid, int x, int y, Coordinated prev) {
        return x >= 0 && y >= 0 && x < grid.get(0).size() && y < grid.size() && (prev == null || (x != prev.x || y != prev.y));
    }


    public void print() {
        for (List<Tile> row : tileGrid) {
            for (Tile tile : row) {
                if (tile.isForbidden) {
                    System.out.print("X  ");
                } else if (tile.isRail) {
                    System.out.print("R  ");
                } else if (tile.isRoad) {
                    System.out.print("B  ");
                } else {
                    System.out.print(".  ");
                }
            }
            System.out.println();
        }
    }

//    public List<Tile> getPassableTiles() {
//        List<Tile> passableTiles = new ArrayList<>();
//        for (List<Tile> row : tileGrid) {
//            for (Tile tile : row) {
//                if (tile.isPassable()) {
//                    passableTiles.add(tile);
//                }
//            }
//        }
//        return passableTiles;
//    }


}
