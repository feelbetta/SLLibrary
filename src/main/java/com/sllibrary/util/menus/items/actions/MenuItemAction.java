package com.sllibrary.util.menus.items.actions;

import com.sllibrary.util.menus.items.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface MenuItemAction {

    void onClick(Player player, MenuItem menuItem, ClickType clickType);
}
