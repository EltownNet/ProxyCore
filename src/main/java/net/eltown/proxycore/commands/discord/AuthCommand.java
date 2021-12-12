package net.eltown.proxycore.commands.discord;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.auth.AuthCalls;
import net.eltown.proxycore.components.language.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AuthCommand extends Command {

    private final ProxyCore proxyCore;

    public AuthCommand(final ProxyCore proxyCore) {
        super("auth");
        this.proxyCore = proxyCore;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 1) {
                final String token = args[0];
                this.proxyCore.getTinyRabbit().sendAndReceive((delivery -> {
                    switch (AuthCalls.valueOf(delivery.getKey().toUpperCase())) {
                        case CALLBACK_TOKEN_INVALID:
                            player.sendMessage(Language.get("token.invalid"));
                            break;
                        case CALLBACK_ALREADY_AUTH:
                            player.sendMessage(Language.get("token.already.auth"));
                            break;
                        case CALLBACK_NULL:
                            player.sendMessage(Language.get("token.success"));
                            break;
                    }
                }), "discord.bot.auth", AuthCalls.REQUEST_SOLVE_AUTH.name(), token, player.getName());
            } else player.sendMessage(Language.get("token.usage"));
        }
    }
}
