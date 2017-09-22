package com.sllibrary.util.menus;

import com.sllibrary.SLLibrary;
import com.sllibrary.util.items.CustomItem;
import com.sllibrary.util.menus.designs.MenuDesign;
import com.sllibrary.util.menus.items.MenuItem;
import com.sllibrary.util.menus.rows.Row;
import com.sllibrary.util.objects.Streams;
import com.sllibrary.util.trackers.Sendable;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Getter
public class Menu extends CraftInventoryCustom implements Sendable {

    @Getter(AccessLevel.NONE)
    public static final CustomItem HOLDER = new CustomItem(Material.STAINED_GLASS_PANE).withName(" ").withData(7);

    private final String name;

    private final Row rows;

    private final Map<Integer, MenuItem> menuItems = new HashMap<>();

    public Menu(SLLibrary slLibrary, String name) {
        this(slLibrary, name, Row.ONE);
    }

    public Menu(SLLibrary slLibrary, String name, Row rows) {
        this(slLibrary, name, rows, null);
    }

    public Menu(SLLibrary slLibrary, String name, Row rows, MenuDesign menuDesign) {
        super(null, rows.getSlotAmount(), StringUtils.repeat(" ", ((30 - name.length()) / 2) + 2) + name);
        slLibrary.getMenuTracker().track(name, this);
        this.name = name;
        this.rows = rows;
        if (menuDesign == null) {
            return;
        }
        this.applyDesign(menuDesign);
    }

    private void applyDesign(MenuDesign menuDesign) {
        IntStream rightSide = IntStream.iterate(8, i -> i + 9).limit(this.getRows().getSlotAmount() / 9);
        IntStream leftSide = IntStream.range(0, this.rows.getSlotAmount()).filter(value -> value % 9 == 0);
        IntStream top = IntStream.range(0, 9);
        IntStream bottom = IntStream.range(this.rows.getSlotAmount() - 9, this.rows.getSlotAmount());

        switch (menuDesign) {
            case CHECKER:
                IntStream.range(0, this.rows.getSlotAmount() - 1).filter(n -> n % 2 == 0).forEach(this::applyHolder);
                break;
            case BORDER:
                Streams.concat(rightSide, leftSide, top, bottom).forEach(this::applyHolder);
                break;
            case TOP:
                top.forEach(this::applyHolder);
                break;
            case BOTTOM:
                bottom.forEach(this::applyHolder);
                break;
            case SIDES:
                IntStream.concat(rightSide, leftSide).forEach(this::applyHolder);
                break;
            case TOP_AND_BOTTOM:
                IntStream.concat(top, bottom).forEach(this::applyHolder);
                break;
            case LEFT:
                leftSide.forEach(this::applyHolder);
                break;
            case RIGHT:
                rightSide.forEach(this::applyHolder);
                break;
            case FULL:
                IntStream.range(0, this.rows.getSlotAmount()).forEach(this::applyHolder);
                break;
            case SPLIT:
                IntStream.iterate(4, i -> i + 9).limit(this.getRows().getSlotAmount() / 9).forEach(this::applyHolder);
        }
    }

    private void applyHolder(int slot) {
        this.setItem(slot, Menu.HOLDER);
    }

    public Menu setMenuItem(MenuItem menuItem, Row row, int slot) {
        this.setItem(row.getRelativeSlot(slot), menuItem);
        this.menuItems.put(row.getRelativeSlot(slot), menuItem);
        return this;
    }

    public Menu addMenuItem(MenuItem menuItem) {
        int slot = this.firstEmpty();
        this.setItem(slot, menuItem);
        this.menuItems.put(slot, menuItem);
        return this;
    }

    @Override
    public void to(Player player) {
        player.openInventory(this);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Menu && this.getTitle().equals(((Menu) object).getTitle());
    }
}
