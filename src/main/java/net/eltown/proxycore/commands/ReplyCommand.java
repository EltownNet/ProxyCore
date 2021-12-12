package net.eltown.proxycore.commands;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command {

    private final ProxyCore proxyCore;

    public ReplyCommand(final ProxyCore proxyCore) {
        super("reply", "", "r");
        this.proxyCore = proxyCore;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer player = (ProxiedPlayer) sender;
            if (MessageCommand.reply.containsKey(player.getName())) {
                final ProxiedPlayer target = this.proxyCore.getProxy().getPlayer(MessageCommand.reply.get(player.getName()));

                final StringBuilder builder = new StringBuilder();
                for (final String arg : args) builder.append(arg).append(" ");
                final String message = builder.substring(0, builder.length() - 1);
                if (target != null) {
                    player.sendMessage("§8» §fMsg §8| §f" + player.getName() + " §7-> §9" + target.getName() + " §8» §f" + message);
                    target.sendMessage("§8» §fMsg §8| §9" + player.getName() + " §7-> §f" + target.getName() + " §8» §f" + message);
                } else player.sendMessage(Language.get("player.not.found", MessageCommand.reply.get(player.getName())));
            } else player.sendMessage("§8» §fMsg §8| §7Du kannst niemandem antworten.");
        }
    }
}
