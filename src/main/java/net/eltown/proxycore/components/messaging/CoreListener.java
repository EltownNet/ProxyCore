package net.eltown.proxycore.components.messaging;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.CoreCalls;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;
import net.eltown.proxycore.components.tools.ProxyTools;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CoreListener {

    private final ProxyCore instance;
    private final TinyRabbitListener listener;

    public CoreListener(final ProxyCore instance) {
        this.instance = instance;
        this.listener = new TinyRabbitListener("localhost");
        this.listener.throwExceptions(true);

        this.startListening();
    }

    public void startListening() {
        this.listener.receive((delivery -> {
            switch (CoreCalls.valueOf(delivery.getKey().toUpperCase())) {
                case REQUEST_BROADCAST_PROXY_MESSAGE:
                    this.instance.getProxy().getPlayers().forEach(e -> {
                        e.sendMessage(delivery.getData()[1]);
                    });
                    break;
                case REQUEST_SEND_PLAYER_MESSAGE:
                    final ProxiedPlayer player = this.instance.getProxy().getPlayer(delivery.getData()[1]);
                    if (player != null) player.sendMessage(delivery.getData()[2]);
                    break;
            }
        }), "Core/Proxy/Core[Receive]", "core.proxy.core.receive");
        this.listener.callback((delivery -> {
            switch (CoreCalls.valueOf(delivery.getKey().toUpperCase())) {
                case REQUEST_GET_ONLINE_PLAYERS:
                    try {
                        final StringBuilder builder = new StringBuilder();

                        for (final ProxiedPlayer s : this.instance.getProxy().getPlayers()) {
                            builder.append(s.getName()).append("#");
                        }

                        delivery.answer(CoreCalls.CALLBACK_GET_ONLINE_PLAYERS.name(), builder.toString());
                        break;
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_IS_PLAYER_ONLINE:
                    final String target = delivery.getData()[1];
                    final ProxiedPlayer player = ProxyTools.findPlayer(target);
                    if (player == null) delivery.answer(CoreCalls.CALLBACK_NULL.name(), "null");
                    else delivery.answer(CoreCalls.CALLBACK_ONLINE.name(), "null");
                    break;

            }
        }), "Core/Proxy/Core[Callback]", "core.proxy.core.callback");
    }

}
