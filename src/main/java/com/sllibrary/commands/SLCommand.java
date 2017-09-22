package com.sllibrary.commands;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import org.spigotmc.SpigotConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public abstract class SLCommand extends SLCommandInjector implements SLCommandInjector.SLCommandFormat {

    @Getter private final Plugin plugin;

    private final static HashMap<Command, Object> handlers = new HashMap<>();
    private final static HashMap<Command, Method> methods = new HashMap<>();
    private final static HashMap<String, SubCommand> subCommands = new HashMap<>();
    private final static HashMap<String, Object> subHandlers = new HashMap<>();
    private final static HashMap<String, Method> subMethods = new HashMap<>();

    public SLCommand(Plugin plugin) {
        super("");
        this.registerCommands(plugin, this);
        this.plugin = plugin;
    }

    private void registerCommands(Plugin plugin, Object handler) {
        for (Method method : handler.getClass().getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 3 && CommandSender.class.isAssignableFrom(params[0]) && String[].class.equals(params[1])) {
                if (isCommandProperty(method)) {

                    CommandProperties annotation = method.getAnnotation(CommandProperties.class);
                    this.setName(annotation.name());
                    if (((CraftServer) Bukkit.getServer()).getCommandMap().getCommands().stream().map(Command::getName).noneMatch(annotation.name()::equalsIgnoreCase)) {
                        ((CraftServer) Bukkit.getServer()).getCommandMap().register(plugin.getName(), this);
                        if(!(Arrays.equals(annotation.aliases(), new String[]{""}))) {
                            setAliases(Lists.newArrayList(annotation.aliases()));
                        }
                        if(!annotation.description().equals("")) {
                            setDescription(annotation.description());
                        }
                        if(!annotation.usage().equals("")) {
                            setUsage(annotation.usage());
                        }
                        handlers.put(this, handler);
                        methods.put(this, method);
                    }
                }

                if (isSubCommandHandler(method)) {
                    SubCommandProperties annotation = method.getAnnotation(SubCommandProperties.class);
                    if (((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(annotation.parent()) != null) {
                        ((CraftServer) Bukkit.getServer()).getCommandMap().register(plugin.getName(), this);
                        SubCommand subCommand = new SubCommand(((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(annotation.parent()), annotation.name());
                        subCommand.permission = annotation.permission();
                        subCommand.permissionMessage = annotation.permissionMessage();
                        subCommands.put(subCommand.toString(), subCommand);
                        subHandlers.put(subCommand.toString(), handler);
                        subMethods.put(subCommand.toString(), method);
                    }
                }
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CommandProperties {

        String name();

        String[] aliases() default {""};

        String description() default "";

        String usage() default "";
    }

    /**
     * Tests if a method is a command handler
     */
    private static boolean isCommandProperty(Method method) {
        return method.getAnnotation(CommandProperties.class) != null;
    }

    /**
     * @author AmoebaMan
     *         An annotation interface that may be attached to a method to designate it as a subcommand handler.
     *         When registering a handler with this class, only methods marked with this annotation will be considered for subcommand registration.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SubCommandProperties {
        String parent();

        String name();

        String permission() default "";

        String permissionMessage() default "You do not have permission to use that command";
    }

    /**
     * Tests if a method is a subcommand handler
     */
    private static boolean isSubCommandHandler(Method method) {
        return method.getAnnotation(SubCommandProperties.class) != null;
    }

    /**
     * A class for representing subcommands
     */
    private static class SubCommand {

        public final Command superCommand;
        public final String subCommand;
        public String permission;
        public String permissionMessage;

        public SubCommand(Command superCommand, String subCommand) {
            this.superCommand = superCommand;
            this.subCommand = subCommand.toLowerCase();
        }

        public boolean equals(Object x) {
            return toString().equals(x.toString());
        }

        public String toString() {
            return (superCommand.getName() + " " + subCommand).trim();
        }
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length > 0) {
            /*
             * Get the subcommand given and the handler and method attached to it
             */
            SubCommand subCommand = new SubCommand(this, strings[0]);
            subCommand = subCommands.get(subCommand.toString());
            /*
             * If and only if the subcommand actually exists...
             */
            if (subCommand != null) {
                Object subHandler = subHandlers.get(subCommand.toString());
                Method subMethod = subMethods.get(subCommand.toString());
                /*
                 * If and only if both handler and method exist...
                 */
                if (subHandler != null && subMethod != null) {
                    /*
                     * Reorder the arguments so we don't resend the subcommand
                     */
                    String[] substrings = new String[strings.length - 1];
                    System.arraycopy(strings, 1, substrings, 0, strings.length - 1);
                    /*
                     * If the method requires a player and the subcommand wasn't sent by one, don't continue
                     */
                    if (subMethod.getParameterTypes()[0].equals(Player.class) && !(commandSender instanceof Player)) {
                        commandSender.sendMessage(ChatColor.RED + "This command requires a player commandSender");
                        return true;
                    }
                    /*
                     * If the method requires a console and the subcommand wasn't sent by one, don't continue
                     */
                    if (subMethod.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(commandSender instanceof ConsoleCommandSender)) {
                        commandSender.sendMessage(ChatColor.RED + "This command requires a console commandSender");
                        return true;
                    }
                    /*
                     * If a permission is attached to this subcommand and the commandSender doens't have it, don't continue
                     */
                    if (!subCommand.permission.isEmpty() && !commandSender.hasPermission(subCommand.permission)) {
                        commandSender.sendMessage(ChatColor.RED + subCommand.permissionMessage);
                        return true;
                    }
                    /*
                     * Try to process the command
                     */
                    try {
                        subMethod.invoke(subHandler, commandSender, strings);
                    } catch (Exception e) {
                        commandSender.sendMessage(ChatColor.RED + "An error occurred while trying to process the command");
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        /*
         * If a subcommand was successfully executed, the command will not reach this point
         * Get the handler and method attached to this command
         */
        Object handler = handlers.get(this);
        Method method = methods.get(this);
        /*
         * If and only if both handler and method exist...
         */
        if (handler != null && method != null) {
            /*
             * If the method requires a player and the command wasn't sent by one, don't continue
             */
            if (method.getParameterTypes()[0].equals(Player.class) && !(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "This command requires a player commandSender");
                return true;
            }
            /*
             * If the method requires a console and the command wasn't sent by one, don't continue
             */
            if (method.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(commandSender instanceof ConsoleCommandSender)) {
                commandSender.sendMessage(ChatColor.RED + "This command requires a console commandSender");
                return true;
            }
            /*
             * Try to process the command
             */
            try {
                method.invoke(handler, commandSender, strings);
            } catch (Exception e) {
                commandSender.sendMessage(ChatColor.RED + "An error occurred while trying to process the command");
                e.printStackTrace();
            }
            return true;
        }
        commandSender.sendMessage(SpigotConfig.unknownCommandMessage);
        return true;
    }
}