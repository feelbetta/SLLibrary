package com.sllibrary.util.locations.boundaries.events;

import com.sllibrary.util.locations.boundaries.Boundary;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

@Getter
public class BoundaryEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Set<Boundary> boundary;

    public BoundaryEnterEvent(Player player, Set<Boundary> boundary) {
        this.player = player;
        this.boundary = boundary;
    }

    @Override
    public HandlerList getHandlers() {
        return BoundaryEnterEvent.handlers;
    }
}
