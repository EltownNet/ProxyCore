package net.eltown.proxycore.listeners;

import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.groups.GroupCalls;
import net.eltown.proxycore.components.data.guardian.PunishmentDocument;
import net.eltown.proxycore.components.language.Language;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class EventListener implements Listener {

    private final ProxyCore instance;

    @EventHandler
    public void onLogin(final PostLoginEvent event) {
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


                    this.instance.getProxy().getPlayers().forEach((p) -> {
                        p.sendMessage(Language.getNP("player.joined", player.getName()));
                    });
                }
            });

            /*final Scoreboard scoreboard = player.getScoreboard();
            final Team team = scoreboard.getTeam(player.getName());
            team.setPrefix(this.instance.cachedGroupPrefix.get(this.instance.cachedRankedPlayers.get(player.getName())).replace("%p", ""));
            team.addPlayer(player.getName());
            player.getScoreboard().addTeam(team);*/
        });
    }

    @EventHandler
    public void onQuit(final PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        this.instance.getPlaytimeHandler().setOffline(event.getPlayer().getName());

        this.instance.getProxy().getPlayers().forEach((p) -> {
            p.sendMessage(Language.getNP("player.quit", player.getName()));
        });
    }

    @EventHandler
    public void onChat(final ChatEvent event) {
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        final String message = event.getMessage();

        if (message.startsWith("/")) return;
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
            this.instance.getProxy().getPlayers().forEach(e -> {
                e.sendMessage(this.instance.cachedGroupPrefix.get(this.instance.cachedRankedPlayers.get(player.getName())).replace("%p", player.getName()) + " §8» §f" + message);
                this.instance.setLastMessage(System.currentTimeMillis());
            });
            this.instance.getLogger().info("[CHAT] [" + player.getName() + "] " + message.replace("§", "&"));

            event.setCancelled(true);
        }
    }

}
