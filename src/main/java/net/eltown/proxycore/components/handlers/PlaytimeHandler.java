package net.eltown.proxycore.components.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.config.Config;
import net.eltown.proxycore.components.data.playtime.PlaytimePlayer;
import net.eltown.proxycore.components.tasks.PlaytimeTask;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaytimeHandler {

    private final MongoCollection<Document> coll;
    private final HashMap<String, PlaytimePlayer> cache = new HashMap<>();
    public final HashMap<String, Long> onlineSince = new HashMap<>();
    @Getter
    private int lastSaveDay;
    private final Config config;
    private final Timer timer;

    public PlaytimeHandler(final ProxyCore core, final MongoDatabase database) {
        this.timer = new Timer("PlaytimeHandlerTimer #1");
        this.config = new Config(core.getDataFolder() + "/playtime.yml");
        if (this.config.exists("lastSaveDay")) {
            this.lastSaveDay = this.config.getInt("lastSaveDay");
        } else {
            final Calendar calendarNow = new GregorianCalendar();
            calendarNow.setTime(new Date(System.currentTimeMillis()));
            calendarNow.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            this.config.set("lastSaveDay", calendarNow.get(Calendar.DAY_OF_YEAR));
            this.config.save();
            this.config.reload();
        }

        this.coll = database.getCollection("playtime");

        for (final Document doc : this.coll.find()) {
            this.cache.put(doc.getString("_id"),
                    new PlaytimePlayer(doc.getString("_id"), doc.getLong("total"), doc.getLong("today"), false));
        }

        new PlaytimeTask(this).start();
    }

    public void needChange(final String player) {
        if (this.cache.containsKey(player)) this.cache.get(player).setChanged(true);
    }

    public void nextDay(final int day) {
        this.config.set("lastSaveDay", day);
        this.config.save();
        this.config.reload();
        this.lastSaveDay = day;

        this.cache.values().forEach(PlaytimePlayer::nextDay);
        this.save();
    }

    public long[] getPlaytime(final String player) {
        final PlaytimePlayer ptp = this.cache.getOrDefault(player, null);
        if (ptp == null) return new long[]{0L, 0L};

        final long currentPlaytime = onlineSince.containsKey(player) ? System.currentTimeMillis() - onlineSince.get(player) : 0L;
        return new long[]{ptp.getTotal() + currentPlaytime, ptp.getToday() + currentPlaytime};
    }

    public void setOnline(final String player) {
        if (!this.cache.containsKey(player)) this.create(player);
        this.onlineSince.put(player, System.currentTimeMillis());
    }

    public void setOffline(final String player) {
        final long now = System.currentTimeMillis();
        final long since = this.onlineSince.get(player);

        final long time = now - since;
        this.onlineSince.remove(player);

        this.cache.get(player).add(time);
    }

    public void create(final String player) {
        this.cache.put(player, new PlaytimePlayer(player, 0L, 0L, false));
        CompletableFuture.runAsync(() -> {
            this.coll.insertOne(new Document("_id", player)
                    .append("total", 0L)
                    .append("today", 0L)
            );
        });
    }

    public void save() {
        final Calendar calendarNow = new GregorianCalendar();
        calendarNow.setTime(new Date(System.currentTimeMillis()));
        calendarNow.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        final int day = calendarNow.get(Calendar.DAY_OF_YEAR);

        if (day != lastSaveDay) {
            this.nextDay(day);
            return;
        }

        final AtomicInteger updated = new AtomicInteger();

        this.cache.forEach((user, ptp) -> {
            if (ptp.isChanged()) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CompletableFuture.runAsync(() -> {
                            final long[] pt = getPlaytime(user);

                            coll.updateOne(new Document("_id", user),
                                    new Document("$set",
                                            new Document("total", pt[0])
                                                    .append("today", pt[1])
                                    ));
                        });
                    }
                }, updated.getAndIncrement() * 50L);
                ptp.setChanged(false);
            }
        });
    }


}
