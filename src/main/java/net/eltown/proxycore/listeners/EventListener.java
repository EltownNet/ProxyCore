package net.eltown.proxycore.listeners;

import dev.waterdog.event.defaults.PlayerChatEvent;
import dev.waterdog.event.defaults.PlayerLoginEvent;
import dev.waterdog.player.ProxiedPlayer;
import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class EventListener {

    private final ProxyCore instance;

    public void onLogin(final PlayerLoginEvent event) {
        CompletableFuture.runAsync(() -> {
            final ProxiedPlayer player = event.getPlayer();
        });
    }

    public void onChat(final PlayerChatEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final String message = event.getMessage();

        event.setCancelled(true);
    }

}
