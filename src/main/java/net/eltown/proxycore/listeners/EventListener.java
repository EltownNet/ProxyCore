package net.eltown.proxycore.listeners;

import dev.waterdog.event.defaults.PlayerChatEvent;
import dev.waterdog.event.defaults.PlayerLoginEvent;
import dev.waterdog.player.ProxiedPlayer;
import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.GroupCalls;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class EventListener {

    private final ProxyCore instance;

    public void onLogin(final PlayerLoginEvent event) {
        CompletableFuture.runAsync(() -> {
            final ProxiedPlayer player = event.getPlayer();
            this.instance.getTinyRabbit().sendAndReceive((delivery -> {
                switch (GroupCalls.valueOf(delivery.getKey().toUpperCase())) {
                    case CALLBACK_FULL_GROUP_PLAYER:
                        final String group = delivery.getData()[1];
                        final String prefix = delivery.getData()[2];
                        this.instance.cachedRankedPlayers.put(player.getName(), group);
                        this.instance.cachedGroupPrefix.put(group, prefix);
                        break;
                }
            }), "groups", GroupCalls.REQUEST_FULL_GROUP_PLAYER.name(), player.getName());
        });
    }

    public void onChat(final PlayerChatEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final String message = event.getMessage();

        this.instance.getProxy().getPlayers().values().forEach(e -> {
            e.sendMessage(this.instance.cachedGroupPrefix.get(this.instance.cachedRankedPlayers.get(player.getName())).replace("%p", player.getName()) + " §8» §f" + message);
            this.instance.getLogger().info(this.instance.cachedGroupPrefix.get(this.instance.cachedRankedPlayers.get(player.getName())).replace("%p", player.getName()) + " §8» §f" + message);
        });

        event.setCancelled(true);
    }

}
