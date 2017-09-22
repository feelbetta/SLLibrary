package com.sllibrary.util.players.textures.tracker;

import com.sllibrary.util.players.textures.SkinTexture;
import com.sllibrary.util.trackers.DataTracker;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SkinTextureTracker extends DataTracker<UUID, SkinTexture> {

    public SkinTextureTracker(Plugin plugin) {
        super(plugin);
    }
}
