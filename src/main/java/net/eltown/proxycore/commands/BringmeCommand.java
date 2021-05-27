package net.eltown.proxycore.commands;

import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.network.ServerInfo;
import dev.waterdog.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;

public class BringmeCommand extends Command {

    private final ProxyCore proxyCore;

    public BringmeCommand(final ProxyCore proxyCore) {
        super("bringme", CommandSettings.builder()
                .setDescription("Bringe einen Spieler von einem anderen Unterservern zu dir")
                .setPermission("proxycore.command.bringme")
                .setUsageMessage("bringme <Spieler>")
                .build());
        this.proxyCore = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission()) && sender instanceof ProxiedPlayer) {
            if (args.length == 1) {
                final ProxiedPlayer player = this.proxyCore.getProxy().getPlayer(args[0]);
                if (player != null) {
                    final ServerInfo targetServerInfo = player.getServerInfo();
                    final ServerInfo senderServerInfo = ((ProxiedPlayer) sender).getServerInfo();
                    if (targetServerInfo != senderServerInfo) {
                        player.redirectServer(senderServerInfo);
                    } else sender.sendMessage(Language.get("player.same.server", args[0]));
                } else sender.sendMessage(Language.get("player.not.found"));
                return true;
            }
        }
        return false;
    }
}
