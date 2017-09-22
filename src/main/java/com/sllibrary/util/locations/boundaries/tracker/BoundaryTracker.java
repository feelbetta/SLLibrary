package com.sllibrary.util.locations.boundaries.tracker;

import com.sllibrary.util.locations.boundaries.Boundary;
import com.sllibrary.util.locations.boundaries.BoundaryCorner;
import com.sllibrary.util.trackers.DataTracker;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class BoundaryTracker extends DataTracker<String, Boundary> implements Listener {

    public BoundaryTracker(Plugin plugin) {
        super(plugin);
    }

    public boolean isInBoundary(Location location) {
        return this.getData().values().stream().anyMatch(boundary -> this.isInBoundary(boundary, location));
    }

    public boolean isInBoundary(Boundary boundary, Location location) {
        BoundaryCorner firstCorner = boundary.getBoundaryCorners()[0];
        BoundaryCorner secondCorner = boundary.getBoundaryCorners()[1];

        if (firstCorner == null || secondCorner == null) {
            return false;
        }
        Location firstCornerLocation = firstCorner.getLocation();
        Location secondCornerLocation = secondCorner.getLocation();

        double higherBoundX = Math.max(firstCornerLocation.getX(), secondCornerLocation.getX());
        double higherBoundY = Math.max(firstCornerLocation.getY(), secondCornerLocation.getY());
        double higherBoundZ = Math.max(firstCornerLocation.getZ(), secondCornerLocation.getZ());

        double lowerBoundX = Math.min(firstCornerLocation.getX(), secondCornerLocation.getX());
        double lowerBoundY = Math.min(firstCornerLocation.getY(), secondCornerLocation.getY());
        double lowerBoundZ = Math.min(firstCornerLocation.getZ(), secondCornerLocation.getZ());

        return location.getX() >= lowerBoundX && location.getX() <= higherBoundX
                && location.getY() >= lowerBoundY && location.getY() <= higherBoundY
                && location.getZ() >= lowerBoundZ && location.getZ() <= higherBoundZ;
    }

    public List<Boundary> getBoundary(Location location) {
        return this.getData().values().stream().filter(boundary -> this.isInBoundary(location)).collect(Collectors.toList());
    }

    public void construct(Boundary boundary) {
        if (boundary == null) {
            return;
        }
        this.track(boundary.getName(), boundary);
    }
}
