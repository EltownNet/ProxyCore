package net.eltown.proxycore.commands.administration;

import net.eltown.proxycore.ProxyCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {

    private final ProxyCore proxyCore;

    public KickCommand(final ProxyCore proxyCore) {
        super("kick", "proxy.core.command.kick");
        this.proxyCore = proxyCore;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.getPermission())) return;
        if (args.length == 1) {
            final ProxiedPlayer target = this.proxyCore.getProxy().getPlayer(args[0]);
            if (target != null) {
                target.disconnect("[Proxy] Du wurdest gekickt.");
            } else sender.sendMessage("Dieser Spieler konnte nicht gefunden werden.");
        }
    }
}
