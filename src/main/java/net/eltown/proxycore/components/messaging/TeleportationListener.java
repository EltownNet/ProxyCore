package net.eltown.proxycore.components.messaging;

import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.teleportation.TeleportationCalls;
import net.eltown.proxycore.components.language.Language;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;

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
                case REQUEST_TELEPORT:
                    final String[] d = delivery.getData();
                    final ProxiedPlayer player = this.instance.getProxy().getPlayer(d[1]);
                    if (!d[2].startsWith("to##")) {
                        player.connect(this.instance.getProxy().getServerInfo(d[2]));
                    } else {
                        final String[] p = d[2].split("##");
                        final ProxiedPlayer target = this.instance.getProxy().getPlayer(p[1]);
                        player.connect(target.getServerInfo());
                    }
                    break;
                case REQUEST_TELEPORT_TPA:
                    final String[] g = delivery.getData();
                    final ProxiedPlayer target = this.instance.getProxy().getPlayer(g[1]);
                    final ProxiedPlayer player1 = this.instance.getProxy().getPlayer(g[2]);
                    player1.connect(target.getServerInfo());
                    break;
                case REQUEST_TPA_NOTIFICATION:
                    final ProxiedPlayer player2 = this.instance.getProxy().getPlayer(delivery.getData()[2]);
                    player2.sendMessage(Language.get("tpa.notification", delivery.getData()[1]));
                    break;
            }
        }), "Core/Proxy/Teleportation[Receive]", "core.proxy.teleportation.receive");
    }

}
