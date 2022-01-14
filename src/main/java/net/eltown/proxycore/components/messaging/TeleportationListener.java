package net.eltown.proxycore.components.messaging;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.teleportation.TeleportationCalls;
import net.eltown.proxycore.components.language.Language;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;
import net.eltown.proxycore.components.tools.ProxyTools;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportationListener {

    private final ProxyCore instance;
    private final TinyRabbitListener listener;

    public TeleportationListener(final ProxyCore instance) {
        this.instance = instance;
        this.listener = new TinyRabbitListener("localhost");
        this.listener.throwExceptions(true);

        this.startListening();
    }

    public void startListening() {
        this.listener.receive((delivery -> {
            switch (TeleportationCalls.valueOf(delivery.getKey().toUpperCase())) {
                case REQUEST_TELEPORT_SERVER:
                    final String[] d = delivery.getData();
                    final ProxiedPlayer player = ProxyTools.findPlayer(d[1]);
                    final ServerInfo serverInfo = this.instance.getProxy().getServerInfo(d[2]);
                    player.connect(serverInfo);
                    break;
                case REQUEST_TELEPORT_PLAYER:
                    final String[] g = delivery.getData();
                    final ProxiedPlayer target = ProxyTools.findPlayer(g[2]);
                    final ProxiedPlayer player1 = ProxyTools.findPlayer(g[1]);
                    player1.connect(target.getServer().getInfo());
                    break;
                case REQUEST_TPA_NOTIFICATION:
                    final ProxiedPlayer player2 = ProxyTools.findPlayer(delivery.getData()[2]);
                    player2.sendMessage(Language.get("tpa.notification", delivery.getData()[1]));
                    break;
            }
        }), "Core/Proxy/Teleportation[Receive]", "core.proxy.teleportation.receive");
    }

}
