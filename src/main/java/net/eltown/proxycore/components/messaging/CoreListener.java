package net.eltown.proxycore.components.messaging;

import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.CoreCalls;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;

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

            }
        }), "ProxyCore/Teleportation/Listener", "proxy.receive");
        this.listener.callback((delivery -> {
            switch (CoreCalls.valueOf(delivery.getKey().toUpperCase())) {
                case REQUEST_GET_ONLINE_PLAYERS:
                    try {
                        final StringBuilder builder = new StringBuilder();

                        for (final ProxiedPlayer s : this.instance.getProxy().getPlayers().values()) {
                            builder.append(s.getName()).append("#");
                        }

                        delivery.answer(CoreCalls.CALLBACK_GET_ONLINE_PLAYERS.name(), builder.toString());
                        break;
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

            }
        }), "ProxyCore/Core/Listener", "proxy.callback");
    }

}
