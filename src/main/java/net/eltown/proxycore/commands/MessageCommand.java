package net.eltown.proxycore.commands;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;
import net.eltown.proxycore.components.tools.ProxyTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

public class MessageCommand extends Command {

    private final ProxyCore proxyCore;

    public static HashMap<String, String> reply = new HashMap<>();

    public MessageCommand(final ProxyCore proxyCore) {
        super("msg", "", "message", "dm");
        this.proxyCore = proxyCore;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length >= 2) {
                final ProxiedPlayer player = (ProxiedPlayer) sender;
                final ProxiedPlayer target = ProxyTools.findPlayer(args[0]);
                final StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.length; i++) builder.append(args[i]).append(" ");
                final String message = builder.substring(0, builder.length() - 1);

                if (target != null) {
                    if (target.getName().equals(player.getName())) {
                        player.sendMessage("§8» §fMsg §8| §7Du kannst dir selber keine private Nachricht schreiben.");
                    }

                    player.sendMessage("§8» §fMsg §8| §f" + player.getName() + " §7-> §9" + target.getName() + " §8» §f" + message);
                    target.sendMessage("§8» §fMsg §8| §9" + player.getName() + " §7-> §f" + target.getName() + " §8» §f" + message);
                    reply.remove(player.getName());
                    reply.remove(target.getName());
                    reply.put(player.getName(), target.getName());
                    reply.put(target.getName(), player.getName());
                } else player.sendMessage(Language.get("player.not.found", args[0]));
            } else sender.sendMessage("§8» §fMsg §8| §7Nutze: /msg <Spieler> <Nachricht>");
        }
    }
}
