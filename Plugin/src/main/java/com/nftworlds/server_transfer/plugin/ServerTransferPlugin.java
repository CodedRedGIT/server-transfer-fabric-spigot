package com.nftworlds.server_transfer.plugin;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.papermc.lib.PaperLib;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;

public final class ServerTransferPlugin extends JavaPlugin {
    private static final String WQL_CHANNEL = "server-transfer-mod:wql_channel";

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        if (!getServer().getMessenger().getOutgoingChannels().contains(WQL_CHANNEL)) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, WQL_CHANNEL);
        }
    }

    @Override
    public void onDisable() {
    }

    /**
     * @param ip_address IP Address to connect to
     * @param player     Player to send connect packets to
     */
    public static void sendTransferPacket(@NotNull String ip_address, @NotNull Player player) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(ip_address);
        Preconditions.checkArgument(player.isOnline(), "Player must be online to send a transfer packet.");

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(ip_address);

        if (!player.getListeningPluginChannels().contains(WQL_CHANNEL)) {
            try {
                Method method = player.getClass().getDeclaredMethod("addChannel", String.class);
                method.invoke(player, WQL_CHANNEL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        player.sendPluginMessage(ServerTransferPlugin.getPlugin(ServerTransferPlugin.class), WQL_CHANNEL, out.toByteArray());
    }

    /**
     * @param ip_address IP Address to connect to
     * @param players    Players to send connect packets to
     */
    public static void sendTransferPacket(@NotNull String ip_address, Player @NotNull ... players) {
        for (Player player : players) {
            sendTransferPacket(ip_address, player);
        }
    }
}
