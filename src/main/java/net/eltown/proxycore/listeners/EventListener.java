package net.eltown.proxycore.listeners;

import dev.waterdog.waterdogpe.event.defaults.PlayerChatEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerDisconnectEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.GroupCalls;
import net.eltown.proxycore.components.language.Language;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class EventListener {

    private final ProxyCore instance;

    public void onLogin(final PlayerLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        this.instance.getProxy().getPlayers().values().forEach((p) -> {
            p.sendMessage(Language.getNP("player.joined", player.getName()));
        });

        CompletableFuture.runAsync(() -> {
            try {
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
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onQuit(final PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        this.instance.getProxy().getPlayers().values().forEach((p) -> {
            p.sendMessage(Language.getNP("player.quit", player.getName()));
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
