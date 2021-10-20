package net.eltown.proxycore.listeners;

import dev.waterdog.waterdogpe.event.defaults.PlayerChatEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerDisconnectEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.groups.GroupCalls;
import net.eltown.proxycore.components.data.guardian.PunishmentDocument;
import net.eltown.proxycore.components.language.Language;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class EventListener {

    private final ProxyCore instance;

    public void onLogin(final PlayerLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        CompletableFuture.runAsync(() -> {
            this.instance.getBanHandler().isActiveBan(player.getName(), isBanned -> {
                if (isBanned) {
                    this.instance.getBanHandler().getActiveBanEntryByTarget(player.getName(), punishmentDocument -> {
                        if (punishmentDocument.getDuration() < System.currentTimeMillis()) {
                            this.instance.getBanHandler().cancelBan(player.getName(), "Ablauf der Bestrafung", "SYSTEM/PROXY", v -> {
                            });
                            this.instance.getPlaytimeHandler().setOnline(player.getName());
                        } else {
                            player.disconnect(Language.getNP("banhandler.disconnect", punishmentDocument.getId(), punishmentDocument.getReason(), this.instance.getRemainingTimeFuture(punishmentDocument.getDuration())));
                        }
                    });
                } else {
                    this.instance.getPlaytimeHandler().setOnline(player.getName());
                    this.instance.getMuteHandler().isActiveMute(player.getName(), e -> {
                        if (e) {
                            this.instance.getMuteHandler().getActiveMuteEntryByTarget(player.getName(), punishmentDocument -> {
                                if (punishmentDocument.getDuration() < System.currentTimeMillis()) {
                                    this.instance.getMuteHandler().cancelMute(player.getName(), "Ablauf der Bestrafung", "SYSTEM/PROXY", v -> {});
                                } else {
                                    this.instance.getMuteHandler().cachedActiveMutes.put(player.getName(), punishmentDocument);
                                }
                            });
                        }
                    });
                    this.instance.getTinyRabbit().sendAndReceive((delivery -> {
                        switch (GroupCalls.valueOf(delivery.getKey().toUpperCase())) {
                            case CALLBACK_FULL_GROUP_PLAYER:
                                final String group = delivery.getData()[1];
                                this.instance.cachedRankedPlayers.put(player.getName(), group);
                                break;
                        }
                    }), "api.groupmanager.main", GroupCalls.REQUEST_FULL_GROUP_PLAYER.name(), player.getName());


                    this.instance.getProxy().getPlayers().values().forEach((p) -> {
                        p.sendMessage(Language.getNP("player.joined", player.getName()));
                    });
                }
            });
        });
    }

    public void onQuit(final PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        this.instance.getPlaytimeHandler().setOffline(event.getPlayer().getName());

        this.instance.getProxy().getPlayers().values().forEach((p) -> {
            p.sendMessage(Language.getNP("player.quit", player.getName()));
        });
    }

    public void onChat(final PlayerChatEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final String message = event.getMessage();

        if (this.instance.getMuteHandler().cachedActiveMutes.containsKey(player.getName())) {
            final PunishmentDocument punishmentDocument = this.instance.getMuteHandler().cachedActiveMutes.get(player.getName());
            if (punishmentDocument.getDuration() < System.currentTimeMillis()) {
                this.instance.getMuteHandler().cancelMute(player.getName(), "Ablauf der Bestrafung", "SYSTEM/PROXY", v -> {
                });
            } else {
                player.sendMessage(Language.getNP("mutehandler.muted", punishmentDocument.getId(), punishmentDocument.getReason(), this.instance.getRemainingTimeFuture(punishmentDocument.getDuration())));
                event.setCancelled(true);
            }
        } else {
            this.instance.getProxy().getPlayers().values().forEach(e -> {
                e.sendMessage(this.instance.cachedGroupPrefix.get(this.instance.cachedRankedPlayers.get(player.getName())).replace("%p", player.getName()) + " §8» §f" + message);
                this.instance.setLastMessage(System.currentTimeMillis());
            });
            this.instance.getLogger().info("[CHAT] [" + player.getName() + "] " + message.replace("§", "&"));

            event.setCancelled(true);
        }
    }

}
