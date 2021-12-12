package net.eltown.proxycore.commands.administration;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.tools.ProxyTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CheckPlaytimeCommand extends Command {

    private final ProxyCore core;

    public CheckPlaytimeCommand(final ProxyCore proxyCore) {
        super("checkplaytime", "proxycore.command.checkplaytime");
        this.core = proxyCore;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length > 0) {
                String target = args[0];
                final ProxiedPlayer preTarget = ProxyTools.findPlayer(target);
                if (preTarget != null) target = preTarget.getName();

                final long[] playTime = this.core.getPlaytimeHandler().getPlaytime(target);
                sender.sendMessage("§7Spielzeit von §9" + target + "§r:");
                sender.sendMessage("§0Gesamt: §r" + playTime[0] + "ms");
                sender.sendMessage("§0Heute: §r" + playTime[1] + "ms");
            }
        }
    }
}
