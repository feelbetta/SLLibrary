package com.sllibrary;

import com.sllibrary.util.authentication.UUIDFetcher;
import com.sllibrary.util.items.actions.tracker.CustomItemActionTracker;
import com.sllibrary.util.locations.boundaries.tracker.BoundaryTracker;
import com.sllibrary.util.menus.tracker.MenuTracker;
import com.sllibrary.util.players.textures.tracker.SkinTextureTracker;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SLLibrary extends JavaPlugin {

    /**
     * Plugin development library
     *
     * @author slees
     */

    private UUIDFetcher uuidFetcher;

    private CustomItemActionTracker customItemActionTracker;
    private MenuTracker menuTracker;

    private BoundaryTracker boundaryTracker;

    private SkinTextureTracker skinTextureTracker;

    @Override
    public void onEnable() {
        this.registerTrackers();

        this.uuidFetcher = new UUIDFetcher(this);
    }
    @Override
    public void onDisable() {
        this.purgeTrackers();
    }

    private void registerTrackers() {
        this.customItemActionTracker = new CustomItemActionTracker(this);
        this.menuTracker = new MenuTracker(this);
        this.boundaryTracker = new BoundaryTracker(this);
        this.skinTextureTracker = new SkinTextureTracker(this);
    }

    private void purgeTrackers() {
        this.uuidFetcher.purge();
        this.skinTextureTracker.purge();
    }
}
