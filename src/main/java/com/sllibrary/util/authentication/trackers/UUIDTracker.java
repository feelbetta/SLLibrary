package com.sllibrary.util.authentication.trackers;

import com.sllibrary.util.trackers.DataTracker;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class UUIDTracker extends DataTracker<String, UUID> {

    public UUIDTracker(Plugin plugin) {
        super(plugin);
    }
}
