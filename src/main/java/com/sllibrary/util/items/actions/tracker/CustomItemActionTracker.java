package com.sllibrary.util.items.actions.tracker;

import com.sllibrary.util.items.CustomItem;
import com.sllibrary.util.trackers.DataTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class CustomItemActionTracker extends DataTracker<CustomItem, CustomItem> implements Listener {

    public CustomItemActionTracker(Plugin plugin) {
        super(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Action action = event.getAction();

        if (itemStack == null) {
            return;
        }
        Optional<CustomItem> customItem = this.getCustomItem(new CustomItem(itemStack).withAmount(1));

        customItem.ifPresent(customItem1 -> customItem1.getCustomItemAction().ifPresent(customItemAction -> {
            event.setCancelled(true);
            customItemAction.onInteract(player, customItem1.clone().withAmount(itemStack.getAmount()), event.getClickedBlock(), action);
        }));
    }

    public Optional<CustomItem> getCustomItem(CustomItem customItem) {
        return this.get(customItem);
    }

    @Override
    public void track(CustomItem key, CustomItem value) {
        super.track(key.clone().withAmount(1), value);
    }
}
