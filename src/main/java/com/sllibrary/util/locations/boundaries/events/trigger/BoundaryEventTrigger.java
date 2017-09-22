package com.sllibrary.util.locations.boundaries.events.trigger;

import com.sllibrary.SLLibrary;
import com.sllibrary.util.locations.boundaries.Boundary;
import com.sllibrary.util.locations.boundaries.events.BoundaryEnterEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoundaryEventTrigger implements Listener {

    private final SLLibrary slLibrary;

    public BoundaryEventTrigger(SLLibrary slLibrary) {
        this.slLibrary = slLibrary;
        Bukkit.getPluginManager().registerEvents(this, slLibrary);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() || from.getY() == to.getY() || from.getZ() == to.getZ()) {
            return;
        }
        if (!this.slLibrary.getBoundaryTracker().isInBoundary(to)) {
            return;
        }
        List<Boundary> boundaries = new ArrayList<>(this.slLibrary.getBoundaryTracker().getBoundary(to));
        boundaries.removeAll(this.slLibrary.getBoundaryTracker().getBoundary(from));
        if (boundaries.equals(this.slLibrary.getBoundaryTracker().getBoundary(to))) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new BoundaryEnterEvent(player, new HashSet<>(boundaries)));
    }
}
