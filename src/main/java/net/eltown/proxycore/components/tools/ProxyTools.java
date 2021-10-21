package net.eltown.proxycore.components.tools;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class ProxyTools {

    public static ProxiedPlayer findPlayer(final String name) {
        ProxiedPlayer startsWith = null;

        for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
            if (player.getName().equalsIgnoreCase(name)) return player;
            if (player.getName().toLowerCase().startsWith(name.toLowerCase())) startsWith = player;
        }

        return startsWith;
    }

}
