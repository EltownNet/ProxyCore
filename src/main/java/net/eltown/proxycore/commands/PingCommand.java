package net.eltown.proxycore.commands;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {

    private final ProxyCore proxyCore;

    public PingCommand(final ProxyCore proxyCore) {
        super("ping");
        this.proxyCore = proxyCore;
    }

    private String color(final long ms) {
        return ms > 499 ? "§4" : ms > 299 ? "§c" : ms > 199 ? "§6" : ms > 99 ? "§e" : ms > 49 ? "§a" : "§2";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer player = (ProxiedPlayer) sender;
            final long ping = player.getPing();
            player.sendMessage(Language.get("player.ping", this.color(ping), ping));
        } else sender.sendMessage("Sehr witzig. Dein Ping ist 0ms.");

    }
}
