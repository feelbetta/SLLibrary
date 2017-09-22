package com.sllibrary.util.players;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sllibrary.SLLibrary;
import com.sllibrary.util.blocks.Blocks;
import com.sllibrary.util.objects.Optionals;
import com.sllibrary.util.objects.Predicates;
import com.sllibrary.util.packets.Packets;
import com.sllibrary.util.players.textures.SkinTexture;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Players {

    private Players() {
    }

    public static Map<Player, Double> getPlayersInRange(Player player, int range) {
        Map<Player, Double> players = Players.getPlayersInRange(player.getLocation(), range);
        players.remove(player);
        return players;
    }

    public static Map<Player, Double> getPlayersInRange(Location location, int range) {
        List<Player> players = location.getWorld().getPlayers();
        return players.stream().filter(player -> player.getLocation().distanceSquared(location) <= range * range).collect(Collectors.toMap(Function.identity(), player -> player.getLocation().distanceSquared(location)));
    }

    public static Optional<Player> getClosestPlayer(Player player) {
        return Optionals.getOptionalOf(player.getWorld().getPlayers().stream().filter(Predicates.not(player::equals)).min(Comparator.comparingDouble(player1 -> player1.getLocation().distanceSquared(player.getLocation())))).orElse(null);
    }

    public static Optional<Player> getClosestPlayer(Location location) {
        return Optionals.getOptionalOf(location.getWorld().getPlayers().stream().min(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location))).orElse(null));
    }

    public static boolean isOnGround(Player player) {
        double check = (player.getLocation().getY() - player.getLocation().getBlockY());
        return check % Blocks.getRelativeBlockHeight(player.getLocation().getBlock().getType()) == 0 || check <= 0.002;
    }

    public static Optional<SkinTexture> getSkinTexture(Player player) {
        if (player == null) {
            return Optional.empty();
        }
        if (JavaPlugin.getPlugin(SLLibrary.class).getSkinTextureTracker().isTracked(player.getUniqueId())) {
            return JavaPlugin.getPlugin(SLLibrary.class).getSkinTextureTracker().get(player.getUniqueId());
        }
        Property property = ((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures").iterator().next();
        return Optional.of(new SkinTexture(property.getValue(), property.getSignature()));
    }

    public static void setSkinTexture(Player player, SkinTexture skinTexture) {
        if (player == null) {
            return;
        }
        if (!JavaPlugin.getPlugin(SLLibrary.class).getSkinTextureTracker().isTracked(player.getUniqueId())) {
            JavaPlugin.getPlugin(SLLibrary.class).getSkinTextureTracker().track(player.getUniqueId(), Players.getSkinTexture(player).get());
        }
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        CraftPlayer craftPlayer = ((CraftPlayer) player);
        EntityPlayer entityPlayer = craftPlayer.getHandle();

        GameProfile gameProfile = craftPlayer.getProfile();

        gameProfile.getProperties().get("textures").clear();
        gameProfile.getProperties().put("textures", new Property("textures", skinTexture.getValue(), skinTexture.getSignature()));

        PacketContainer removePlayer = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        PacketContainer addPlayer = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);

        PacketContainer respawnPlayer = protocolManager.createPacket(PacketType.Play.Server.RESPAWN);

        EnumWrappers.NativeGameMode nativeGameMode = EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode());
        World world = player.getWorld();

        List<PlayerInfoData> playerInfoData = Collections.singletonList(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), entityPlayer.ping, nativeGameMode, WrappedChatComponent.fromText(player.getDisplayName())));

        removePlayer.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        removePlayer.getPlayerInfoDataLists().write(0, playerInfoData);

        addPlayer.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        addPlayer.getPlayerInfoDataLists().write(0, playerInfoData);

        Packets.broadcast(removePlayer, addPlayer);
        Bukkit.getOnlinePlayers().forEach(o -> {
            o.hidePlayer(player);
            o.showPlayer(player);
        });
        respawnPlayer.getIntegers().write(0, entityPlayer.dimension);
        respawnPlayer.getDifficulties().write(0, EnumWrappers.Difficulty.valueOf(world.getDifficulty().name()));
        respawnPlayer.getGameModes().write(0, nativeGameMode);
        respawnPlayer.getWorldTypeModifier().write(0, world.getWorldType());

        Packets.broadcast(respawnPlayer);
    }

    public static void resetSkinTexture(Player player) {
        JavaPlugin.getPlugin(SLLibrary.class).getSkinTextureTracker().get(player.getUniqueId()).ifPresent(skinTexture -> Players.setSkinTexture(player, skinTexture));
    }
}
