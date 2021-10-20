package net.eltown.proxycore;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import dev.waterdog.waterdogpe.event.defaults.PlayerChatEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerDisconnectEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.eltown.proxycore.commands.*;
import net.eltown.proxycore.commands.administration.BringmeCommand;
import net.eltown.proxycore.commands.administration.CheckPlaytimeCommand;
import net.eltown.proxycore.commands.administration.JumptoCommand;
import net.eltown.proxycore.commands.administration.WhereisCommand;
import net.eltown.proxycore.commands.discord.AuthCommand;
import net.eltown.proxycore.components.handlers.BanHandler;
import net.eltown.proxycore.components.handlers.MuteHandler;
import net.eltown.proxycore.components.handlers.PlaytimeHandler;
import net.eltown.proxycore.components.handlers.WarnHandler;
import net.eltown.proxycore.components.language.Language;
import net.eltown.proxycore.components.messaging.*;
import net.eltown.proxycore.components.tasks.AnnoucementTask;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbit;
import net.eltown.proxycore.listeners.EventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class ProxyCore extends Plugin {

    private MongoClient databaseClient;
    private MongoDatabase database;

    private EventListener listener;

    private TinyRabbit tinyRabbit;
    private GroupListener groupListener;
    private TeleportationListener teleportationListener;
    private CoreListener coreListener;
    private GuardianListener guardianListener;
    private PlaytimeListener playtimeListener;

    private BanHandler banHandler;
    private MuteHandler muteHandler;
    private WarnHandler warnHandler;
    private PlaytimeHandler playtimeHandler;

    public final HashMap<String, String> cachedRankedPlayers = new HashMap<>();
    public final HashMap<String, String> cachedGroupPrefix = new HashMap<>();

    @Setter
    private long lastMessage;


    @Override
    public void onEnable() {
        try {
            this.loadPlugin();
            this.getLogger().info("§aProxyCore erfolgreich initialisiert.");
        } catch (final Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Fehler beim initialisieren des ProxyCores.");
        }
    }

    @SneakyThrows
    private void loadPlugin() {
        this.connectDatabase();
        Language.init(this);

        this.tinyRabbit = new TinyRabbit("localhost", "Core/Proxy/System[Main]");
        this.groupListener = new GroupListener(this);
        this.teleportationListener = new TeleportationListener(this);
        this.coreListener = new CoreListener(this);
        this.guardianListener = new GuardianListener(this);
        this.playtimeListener = new PlaytimeListener(this);

        this.banHandler = new BanHandler(this, this.database);
        this.muteHandler = new MuteHandler(this, this.database);
        this.warnHandler = new WarnHandler(this, this.database);
        this.playtimeHandler = new PlaytimeHandler(this, this.database);

        this.listener = new EventListener(this);
        this.getProxy().getEventManager().subscribe(PlayerLoginEvent.class, this.listener::onLogin);
        this.getProxy().getEventManager().subscribe(PlayerChatEvent.class, this.listener::onChat);
        this.getProxy().getEventManager().subscribe(PlayerDisconnectEvent.class, this.listener::onQuit);

        this.getProxy().getCommandMap().registerCommand("jumpto", new JumptoCommand(this));
        this.getProxy().getCommandMap().registerCommand("bringme", new BringmeCommand(this));
        this.getProxy().getCommandMap().registerCommand("whereis", new WhereisCommand(this));
        this.getProxy().getCommandMap().registerCommand("ping", new PingCommand(this));
        this.getProxy().getCommandMap().registerCommand(new CheckPlaytimeCommand(this));

        this.getProxy().getCommandMap().registerCommand("auth", new AuthCommand(this));

        new AnnoucementTask(this).start();
    }

    private void connectDatabase() {
        final MongoClientURI clientURI = new MongoClientURI("mongodb://root:Qco7TDqoYq3RXq4pA3y7ETQTK6AgqzmTtRGLsgbN@45.138.50.23:27017/admin?authSource=admin");
        this.databaseClient = new MongoClient(clientURI);
        this.database = databaseClient.getDatabase("eltown");
        final Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
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

    public String getRemainingTimeFuture(final long duration) {
        if (duration == -1L) {
            return "Permanent";
        } else {
            final SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            final SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            final long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            final int days = (int) (time / 86400000L);
            final int hours = (int) (time / 3600000L % 24L);
            final int minutes = (int) (time / 60000L % 60L);
            String day = "Tage";
            if (days == 1) {
                day = "Tag";
            }

            String hour = "Stunden";
            if (hours == 1) {
                hour = "Stunde";
            }

            String minute = "Minuten";
            if (minutes == 1) {
                minute = "Minute";
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return "Wenige Augenblicke";
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }

}
