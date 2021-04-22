package net.eltown.proxycore;

import dev.waterdog.event.defaults.PlayerChatEvent;
import dev.waterdog.event.defaults.PlayerLoginEvent;
import dev.waterdog.plugin.Plugin;
import lombok.Getter;
import net.eltown.proxycore.components.language.Language;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbit;
import net.eltown.proxycore.listeners.EventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Getter
public class ProxyCore extends Plugin {

    private EventListener listener;
    private TinyRabbit tinyRabbit;

    @Override
    public void onEnable() {
        try {
            this.tinyRabbit = new TinyRabbit("localhost", "ProxyCore");
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
