package net.eltown.proxycore.components.handlers;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.player.Player;
import net.eltown.proxycore.components.data.player.PlayerCalls;
import net.eltown.proxycore.components.tinyrabbit.Queue;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PlayerHandler {

    private final ProxyCore proxyCore;
    private final HashMap<UUID, Player> cachedPlayers = new HashMap<>();

    public PlayerHandler(final ProxyCore proxyCore) {
        this.proxyCore = proxyCore;
    }

    public void handlePlayerJoin(final ProxiedPlayer proxiedPlayer) {
        this.proxyCore.getTinyRabbit().sendAndReceive(delivery -> {
            final String[] d = delivery.getData();
            switch (PlayerCalls.valueOf(delivery.getKey().toUpperCase())) {
                case CALLBACK_GET_PLAYER: {
                    final Player player = new Player(UUID.fromString(d[1]), proxiedPlayer.getName(), Long.parseLong(d[3]), System.currentTimeMillis());
                    this.cachedPlayers.put(proxiedPlayer.getUniqueId(), player);

                    this.proxyCore.getTinyRabbit().send(Queue.PLAYER_RECEIVE, PlayerCalls.REQUEST_UPDATE_LAST_LOGIN.name(), proxiedPlayer.getUniqueId().toString());

                    if (!player.getName().equals(proxiedPlayer.getName())) {
                        this.proxyCore.getTinyRabbit().send(Queue.PLAYER_RECEIVE, PlayerCalls.REQUEST_UPDATE_NAME.name(), proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());
                    }
                }
                case CALLBACK_NULL: {
                    this.proxyCore.getTinyRabbit().send(Queue.PLAYER_RECEIVE, PlayerCalls.REUEST_CREATE_PLAYER.name(), proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getName());
                }
            }
        }, Queue.PLAYER_CALLBACK, PlayerCalls.REQUEST_GET_PLAYER.name(), proxiedPlayer.getUniqueId().toString());
    }

}
