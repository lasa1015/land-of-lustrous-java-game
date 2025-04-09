package org.example.landoflustrous.service.strategy;

import org.example.landoflustrous.model.*;

import java.util.List;

public class WalkStrategy extends TrafficStrategy {

    public WalkStrategy(GameMap gameMap) {
        super(gameMap);
    }

    public Route navigate(Coordinated start, Coordinated end) {
        return navigate(start, end, findPathRoaming(start, end, TrafficType.WALK));
    }

    public Route navigate(Coordinated start, Coordinated end, Path walkPath) {
        if (walkPath != null) return new Route(List.of(walkPath));
        else return null;
    }
}
