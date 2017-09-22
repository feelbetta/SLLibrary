package com.sllibrary.util.locations.boundaries;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class Boundary {

    private final String name;

    private final BoundaryCorner[] boundaryCorners = new BoundaryCorner[2];

    public Boundary(String name) {
        this.name = name.toLowerCase();
    }

    public Boundary withBoundaryCorner(int corner, Location location) {
        this.boundaryCorners[corner - 1] = new BoundaryCorner(location);
        return this;
    }
}
