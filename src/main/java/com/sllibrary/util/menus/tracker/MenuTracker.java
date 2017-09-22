package com.sllibrary.util.menus.tracker;

import com.sllibrary.SLLibrary;
import com.sllibrary.util.menus.Menu;
import com.sllibrary.util.menus.items.MenuItem;
import com.sllibrary.util.trackers.DataTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class MenuTracker extends DataTracker<String, Menu> implements Listener {

    public MenuTracker(SLLibrary slLibrary) {
        super(slLibrary);
        Bukkit.getPluginManager().registerEvents(this, slLibrary);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        Optional<Menu> menu = this.getMenu(inventory);

        if (event.getAction() == InventoryAction.NOTHING || !menu.isPresent()) {
            return;
        }
        int clickedSlot = event.getSlot();
        event.setResult(Event.Result.DENY);
        if (!menu.get().getMenuItems().containsKey(clickedSlot)) {
            return;
        }
        MenuItem menuItem = menu.get().getMenuItems().get(clickedSlot);
        menuItem.getMenuItemAction().ifPresent(menuItemAction -> menuItemAction.onClick(player, menuItem, event.getClick()));
    }

    public boolean isMenu(Inventory inventory) {
        return this.getData().values().stream().map(Inventory::getTitle).anyMatch(inventory.getTitle()::equals);
    }

    public Optional<Menu> getMenu(Inventory inventory) {
        return this.get(inventory.getTitle().trim());
    }
}
