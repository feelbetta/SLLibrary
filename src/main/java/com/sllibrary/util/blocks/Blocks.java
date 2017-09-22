package com.sllibrary.util.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Blocks {

    private Blocks() {
    }

    public static boolean isHarmful(Material material) {
        switch(material) {
            case LAVA:
            case CACTUS:
            case FIRE:
            case STATIONARY_LAVA:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLiquid(Material material) {
        switch(material) {
            case LAVA:
            case STATIONARY_LAVA:
            case WATER:
            case STATIONARY_WATER:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDangerousFromAbove(Material material) {
        switch(material) {
            case LAVA:
            case STATIONARY_LAVA:
            case SAND:
            case GRAVEL:
            case ANVIL:
                return true;
            default:
                return false;
        }
    }

    public static Double getRelativeBlockHeight(Material material) {
        switch (material) {
            case ACACIA_FENCE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE:
            case DARK_OAK_FENCE_GATE:
            case FENCE:
            case FENCE_GATE:
            case IRON_FENCE:
            case JUNGLE_FENCE:
            case JUNGLE_FENCE_GATE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case SPRUCE_FENCE_GATE:
            case COBBLE_WALL:
                return 0.5;
            case GRASS_PATH:
            case SOIL:
            case CACTUS:
                return 0.9375;
            case SOUL_SAND:
            case CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
                return 0.875;
            case CHORUS_PLANT:
                return 0.8125;
            case ENCHANTMENT_TABLE:
                return 0.75;
            case BED_BLOCK:
                return 0.5625;
            case SKULL:
                return 0.25;
            case WATER_LILY:
                return 0.09375;
            default:
                return 0.0625;
        }
    }

    public static void placeItems(Block block, int slot, ItemStack itemStack) {
        if (!Blocks.hasInventory(block)) {
            return;
        }
        ((InventoryHolder) block.getState()).getInventory().setItem(slot, itemStack);
    }

    public static void placeItems(Block block, Map<Integer, ItemStack> itemStacks) {
        itemStacks.forEach((integer, itemStack) -> Blocks.placeItems(block, integer, itemStack));
    }

    public static void placeItems(Block block, boolean randomly, ItemStack... itemStacks) {
        if (!Blocks.hasInventory(block)) {
            return;
        }
        IntStream.range(0, itemStacks.length).filter(value -> Blocks.getInventory(block).get().getItem(value) == null).forEach(value -> Blocks.placeItems(block, randomly ? ThreadLocalRandom.current().nextInt(0, Blocks.getInventory(block).get().getSize() - 1) : value, itemStacks[value]));
    }

    public static boolean hasInventory(Block block) {
        return block.getState() instanceof InventoryHolder;
    }

    public static Optional<Inventory> getInventory(Block block) {
        return Blocks.hasInventory(block) ? Optional.of(((InventoryHolder) block.getState()).getInventory()) : Optional.empty();
    }
}
