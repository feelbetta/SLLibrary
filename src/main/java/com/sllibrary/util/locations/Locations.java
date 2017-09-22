package com.sllibrary.util.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Optional;

public class Locations {

    private Locations() {
    }

    public static String serialize(Location location) {
        return location.getWorld() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Optional<Location> deserialize(String string) {
        String[] parts = string.split(";");
        return parts.length == 6 ? Optional.of(new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]))) : Optional.empty();
    }

    public static Location getRandom(Location min, Location max) {
        Location range = new Location(min.getWorld(), Math.abs(max.getX() - min.getX()), min.getY(), Math.abs(max.getZ() - min.getZ()));
        return new Location(min.getWorld(), (Math.random() * range.getX()) + (min.getX() <= max.getX() ? min.getX() : max.getX()), range.getY(), (Math.random() * range.getZ()) + (min.getZ() <= max.getZ() ? min.getZ() : max.getZ()));
    }
}
