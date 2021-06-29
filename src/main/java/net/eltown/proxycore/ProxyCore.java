package net.eltown.proxycore;

import dev.waterdog.waterdogpe.event.defaults.PlayerChatEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import lombok.Getter;
import net.eltown.proxycore.commands.AnnounceCommand;
import net.eltown.proxycore.commands.BringmeCommand;
import net.eltown.proxycore.commands.JumptoCommand;
import net.eltown.proxycore.commands.WhereisCommand;
import net.eltown.proxycore.commands.discord.AuthCommand;
import net.eltown.proxycore.components.language.Language;
import net.eltown.proxycore.components.messaging.CoreListener;
import net.eltown.proxycore.components.messaging.GroupListener;
import net.eltown.proxycore.components.messaging.TeleportationListener;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbit;
import net.eltown.proxycore.listeners.EventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@Getter
public class ProxyCore extends Plugin {

    private EventListener listener;

    private TinyRabbit tinyRabbit;
    private GroupListener groupListener;
    private TeleportationListener teleportationListener;
    private CoreListener coreListener;

    public final HashMap<String, String> cachedRankedPlayers = new HashMap<>();
    public final HashMap<String, String> cachedGroupPrefix = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            this.tinyRabbit = new TinyRabbit("localhost", "ProxyCore");
            this.groupListener = new GroupListener(this);
            this.teleportationListener = new TeleportationListener(this);
            this.coreListener = new CoreListener(this);
            this.loadPlugin();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlugin() {
        Language.init(this);

        this.listener = new EventListener(this);
        this.getProxy().getEventManager().subscribe(PlayerLoginEvent.class, this.listener::onLogin);
        this.getProxy().getEventManager().subscribe(PlayerChatEvent.class, this.listener::onChat);

        this.getProxy().getCommandMap().registerCommand("jumpto", new JumptoCommand(this));
        this.getProxy().getCommandMap().registerCommand("bringme", new BringmeCommand(this));
        this.getProxy().getCommandMap().registerCommand("whereis", new WhereisCommand(this));
        this.getProxy().getCommandMap().registerCommand("announce", new AnnounceCommand(this));

        this.getProxy().getCommandMap().registerCommand("auth", new AuthCommand(this));
    }

    public String createId(final int i) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder stringBuilder = new StringBuilder();
        final Random rnd = new Random();
        while (stringBuilder.length() < i) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public String createId(final int i, final String prefix) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder stringBuilder = new StringBuilder(prefix + "-");
        final Random rnd = new Random();
        while (stringBuilder.length() < i) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

}
