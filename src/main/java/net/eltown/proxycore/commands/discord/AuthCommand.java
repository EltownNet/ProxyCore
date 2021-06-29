package net.eltown.proxycore.commands.discord;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.AuthCalls;
import net.eltown.proxycore.components.language.Language;

public class AuthCommand extends Command {

    private final ProxyCore proxyCore;

    public AuthCommand(final ProxyCore proxyCore) {
        super("announce", CommandSettings.builder()
                .setDescription("Verifiziere dich mit unserem Discord-Server")
                .setUsageMessage("auth <Token>")
                .build());
        this.proxyCore = proxyCore;
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
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
            return true;
        }
        return false;
    }

}
