package net.eltown.proxycore.components.tools;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProxyTools {

    public static ProxiedPlayer findPlayer(final String name) {
        ProxiedPlayer startsWith = null;

        for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return player;
            if (player.getName().toLowerCase().startsWith(name.toLowerCase())) startsWith = player;
        }

        return startsWith;
    }

}
