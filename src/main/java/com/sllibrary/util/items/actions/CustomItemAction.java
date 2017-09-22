package com.sllibrary.util.items.actions;

import com.sllibrary.util.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public interface CustomItemAction {

    void onInteract(Player player, CustomItem customItem, Block clickedBlock, Action action);
}
