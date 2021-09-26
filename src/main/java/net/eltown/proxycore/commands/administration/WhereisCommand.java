package net.eltown.proxycore.commands.administration;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;

public class WhereisCommand  extends Command {

    private final ProxyCore proxyCore;

    public WhereisCommand(final ProxyCore proxyCore) {
        super("whereis", CommandSettings.builder()
                .setDescription("Finde einen Spieler auf einem Unterserver.")
                .setPermission("proxycore.command.whereis")
                .setUsageMessage("whereis <Spieler>")
                .build());
        this.proxyCore = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                final ProxiedPlayer player = this.proxyCore.getProxy().getPlayer(args[0]);
                if (player != null) {
                    final ServerInfo serverInfo = player.getServerInfo();
                    sender.sendMessage(Language.get("whereis.info", args[0], serverInfo.getServerName()));
                } else sender.sendMessage(Language.get("player.not.found"));
                return true;
            }
        }
        return false;
    }
}