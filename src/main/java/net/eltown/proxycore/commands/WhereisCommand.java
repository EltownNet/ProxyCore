package net.eltown.proxycore.commands;

import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.network.ServerInfo;
import dev.waterdog.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;

public class WhereisCommand  extends Command {

    private final ProxyCore proxyCore;

    public WhereisCommand(final ProxyCore proxyCore) {
        super("whereis", CommandSettings.builder()
                .setDescription("Finde einen Spieler auf einem Unterserver.")
                .setPermission("proxycore.command.whereis")
                .build());
        this.proxyCore = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission()) && sender instanceof ProxiedPlayer) {
            if (args.length == 1) {
                final ProxiedPlayer player = this.proxyCore.getProxy().getPlayer(args[0]);
                if (player != null) {
                    final ServerInfo serverInfo = player.getServerInfo();
                    sender.sendMessage(Language.get("whereis.info", args[0], serverInfo.getServerName()));
                } else sender.sendMessage(Language.get("player.not.found"));
            }
        }
        return false;
    }
}