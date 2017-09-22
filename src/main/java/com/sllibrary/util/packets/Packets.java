package com.sllibrary.util.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.sllibrary.util.objects.Streams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Packets {

    private Packets() {
    }

    public static void send(Player player, PacketContainer... packetContainers) {
        Streams.stream(packetContainers).collect(Collectors.toCollection(LinkedList::new)).forEach(packetContainer -> {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void broadcast(PacketContainer... packetContainers) {
        Bukkit.getOnlinePlayers().forEach(o -> Packets.send(o, packetContainers));
    }
}
