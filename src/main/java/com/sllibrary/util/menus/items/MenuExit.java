package com.sllibrary.util.menus.items;

import com.sllibrary.util.items.CustomItem;
import org.bukkit.Material;

public class MenuExit extends MenuItem {

    public MenuExit() {
        super(new CustomItem(Material.BARRIER).withName("$cExit"));
        this.setMenuItemAction((player, menuItem, clickType) -> player.closeInventory());
    }
}
