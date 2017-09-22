package com.sllibrary.util.items;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class Items {

    private Items() {
    }

    public static boolean isCorrectTool(ItemStack itemStack, Block block) {
        return !block.getDrops(itemStack).isEmpty();
    }

    public static Optional<Material> getCorrectTool(Block block) {
        return block.getDrops().stream().map(ItemStack::getType).findFirst();
    }
}
