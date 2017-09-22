package com.sllibrary.util.menus.items;

import com.sllibrary.util.items.CustomItem;
import com.sllibrary.util.menus.items.actions.MenuItemAction;
import com.sllibrary.util.objects.Optionals;

import java.util.Optional;

public class MenuItem extends CustomItem {

    private MenuItemAction menuItemAction;

    public MenuItem(CustomItem customItem) {
        super(customItem);
    }

    public MenuItem setMenuItemAction(MenuItemAction menuItemAction) {
        this.menuItemAction = menuItemAction;
        return this;
    }

    public Optional<MenuItemAction> getMenuItemAction() {
        return Optionals.getOptionalOf(this.menuItemAction);
    }
}
