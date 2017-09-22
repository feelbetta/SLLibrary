package com.sllibrary.util.authentication.trackers;

import com.sllibrary.util.trackers.DataTracker;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class NameTracker extends DataTracker<UUID, String> {

    public NameTracker(Plugin plugin) {
        super(plugin);
    }
}
