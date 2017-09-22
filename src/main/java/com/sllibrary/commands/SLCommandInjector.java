package com.sllibrary.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

abstract class SLCommandInjector extends Command {

    public interface SLCommandFormat {

        void perform(Player player, String[] args);
    }

    protected SLCommandInjector(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
