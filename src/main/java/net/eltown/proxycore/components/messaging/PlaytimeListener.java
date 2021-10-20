package net.eltown.proxycore.components.messaging;

import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.playtime.PlaytimeCalls;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;

public class PlaytimeListener {

    private final ProxyCore core;
    private final TinyRabbitListener listener;

    public PlaytimeListener(final ProxyCore core) {
        this.core = core;
        this.listener = new TinyRabbitListener("localhost");
        this.listener.throwExceptions(true);

        this.startListening();
    }

    private void startListening() {
        this.listener.callback(r -> {
            switch (PlaytimeCalls.valueOf(r.getKey().toUpperCase())) {
                case REQUEST_PLAYTIME:
                    final long[] playtime = this.core.getPlaytimeHandler().getPlaytime(r.getData()[1]);
                    r.answer(PlaytimeCalls.REQUEST_PLAYTIME.name(), "" + playtime[0], "" + playtime[1]);
                    break;
            }
        }, "Core/Proxy/Playtime[Main]", "core.proxy.playtime.main");
    }

}
