package net.eltown.proxycore.commands.administration;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.tools.ProxyTools;

public class CheckPlaytimeCommand extends Command {

    private final ProxyCore core;

    public CheckPlaytimeCommand(final ProxyCore proxyCore) {
        super("checkplaytime", CommandSettings.builder()
                .setDescription("Überprüfe die Spielzeit eines Spielers.")
                .setPermission("proxycore.command.checkplaytime")
                .setUsageMessage("checkplaytime <Spieler>")
                .build());
        this.core = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length > 0) {
                String target = args[0];
                final ProxiedPlayer preTarget = ProxyTools.findPlayer(target);
                if (preTarget != null) target = preTarget.getName();

                final long[] playTime = this.core.getPlaytimeHandler().getPlaytime(target);
                sender.sendMessage("§7Spielzeit von §9" + target + "§r:");
                sender.sendMessage("§0Gesamt: §r" + playTime[0] + "ms");
                sender.sendMessage("§0Heute: §r" + playTime[1] + "ms");
            } else sender.sendMessage(this.getUsageMessage());
        }
        return true;
    }

}
