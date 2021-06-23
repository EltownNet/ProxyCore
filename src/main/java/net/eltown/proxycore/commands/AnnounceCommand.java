package net.eltown.proxycore.commands;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import net.eltown.proxycore.ProxyCore;

public class AnnounceCommand extends Command {

    private final ProxyCore proxyCore;

    public AnnounceCommand(final ProxyCore proxyCore) {
        super("announce", CommandSettings.builder()
                .setDescription("Sende eine serverweite Informations-Nachricht")
                .setPermission("proxycore.command.announce")
                .setUsageMessage("announce <Nachricht>")
                .build());
        this.proxyCore = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {

        }
        return false;
    }
}