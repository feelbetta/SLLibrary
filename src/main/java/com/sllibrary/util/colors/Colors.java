package com.sllibrary.util.colors;

import com.google.common.collect.ImmutableBiMap;
import org.bukkit.ChatColor;

import java.awt.*;

public class Colors {

    private static final int MAX_CLOSEST = 255 * 255 * 3;

    public static final ImmutableBiMap<Color, ChatColor> COLOR_MAP = ImmutableBiMap.<Color, ChatColor> builder()
            .put(Color.BLACK, ChatColor.BLACK)
            .put(new Color(0, 0, 170), ChatColor.DARK_BLUE)
            .put(new Color(0, 170, 0), ChatColor.DARK_GREEN)
            .put(new Color(0, 170, 170), ChatColor.DARK_AQUA)
            .put(new Color(170, 0, 0), ChatColor.DARK_RED)
            .put(new Color(170, 0, 170), ChatColor.DARK_PURPLE)
            .put(new Color(255, 170, 0), ChatColor.GOLD)
            .put(new Color(170, 170, 170), ChatColor.GRAY)
            .put(new Color(85, 85, 85), ChatColor.DARK_GRAY)
            .put(new Color(85, 85, 255), ChatColor.BLUE)
            .put(new Color(85, 255, 85), ChatColor.GREEN)
            .put(new Color(85, 255, 255), ChatColor.AQUA)
            .put(new Color(255, 85, 85), ChatColor.RED)
            .put(new Color(255, 85, 255), ChatColor.LIGHT_PURPLE)
            .put(new Color(255, 255, 85), ChatColor.YELLOW)
            .put(Color.WHITE, ChatColor.WHITE)
            .build();

    private Colors() {

    }

    public static Color closestColorMatch(Color color, Iterable<Color> colors) {
        if (color.getAlpha() < 1) return null;

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int closest = MAX_CLOSEST;
        Color best = null;

        for (Color key : colors) {
            int rDist = Math.abs(r - key.getRed());
            int gDist = Math.abs(g - key.getGreen());
            int bDist = Math.abs(b - key.getBlue());
            int dist = rDist * rDist + gDist * gDist + bDist * bDist;
            if (dist < closest) {
                best = key;
                closest = dist;
            }
        }
        return best;
    }
}
