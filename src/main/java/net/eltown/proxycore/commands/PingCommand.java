package net.eltown.proxycore.commands;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;

public class PingCommand extends Command {

    private final ProxyCore proxyCore;

    public PingCommand(final ProxyCore proxyCore) {
        super("ping", CommandSettings.builder()
                .setDescription("Überprüfe deinen derzeitigen Ping zum Server.")
                .setUsageMessage("ping")
                .build()
        );
        this.proxyCore = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender commandSender, String s, String[] strings) {

        if (commandSender.isPlayer()) {
            final ProxiedPlayer player = (ProxiedPlayer) commandSender;
            final long ping = player.getPing();
            player.sendMessage(Language.get("player.ping", this.color(ping), ping));
        } else commandSender.sendMessage("Sehr witzig. Dein Ping ist 0ms.");

        return true;
    }


    private String color(final long ms) {
        return ms > 499 ? "§4" : ms > 299 ? "§c" : ms > 199 ? "§6" : ms > 99 ? "§e" : ms > 49 ? "§a" : "§2";
    }
}
