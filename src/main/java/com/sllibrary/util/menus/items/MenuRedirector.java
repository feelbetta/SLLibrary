package com.sllibrary.util.menus.items;

import com.sllibrary.util.items.CustomItem;
import com.sllibrary.util.menus.Menu;
import lombok.Getter;

public class MenuRedirector extends MenuItem {

    @Getter private final Menu menu;

    public MenuRedirector(CustomItem customItem, Menu menu) {
        super(customItem.glowing());
        this.menu = menu;
        this.setMenuItemAction((player, menuItem, clickType) -> this.menu.to(player));
    }
}
