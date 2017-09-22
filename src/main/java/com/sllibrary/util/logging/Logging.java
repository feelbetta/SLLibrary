package com.sllibrary.util.logging;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Logging {

    private Logging() {
    }

    public static void info(Plugin plugin, String message) {
        Bukkit.getLogger().info(plugin.getLogger().getName() + message);
    }

    public static void warn(Plugin plugin, String message) {
        Bukkit.getLogger().warning(plugin.getLogger().getName() + message);
    }
}
