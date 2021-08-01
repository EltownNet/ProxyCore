package net.eltown.proxycore.components.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.guardian.GuardianCalls;
import net.eltown.proxycore.components.data.guardian.PunishmentDocument;
import net.eltown.proxycore.components.data.guardian.PunishmentLogDocument;
import net.eltown.proxycore.components.language.Language;
import org.bson.Document;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BanHandler {

    private final ProxyCore proxyCore;
    private final MongoCollection<Document> banCollection, banBehaviorCollection;

    public BanHandler(final ProxyCore proxyCore, final MongoDatabase database) {
        this.proxyCore = proxyCore;
        this.banCollection = database.getCollection("ban_collection");
        this.banBehaviorCollection = database.getCollection("ban_behavior_collection");
    }

    public void initiateBan(final String target, final String reason, final String executor, final long duration, final Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            final String id = this.proxyCore.createId(7, "B");

            final Document document = new Document("_id", id)
                    .append("target", target)
                    .append("reason", reason)
                    .append("executor", executor)
                    .append("date", this.proxyCore.getDate())
                    .append("duration", duration);
            this.banCollection.insertOne(document);

            this.initiateBanLog(new PunishmentDocument(id, target, reason, executor, this.proxyCore.getDate(), duration));

            final ProxiedPlayer player = this.proxyCore.getProxy().getPlayer(target);
            if (player != null) {
                player.disconnect(Language.getNP("banhandler.disconnect", id, reason, this.proxyCore.getRemainingTimeFuture(duration)));
            }

            this.proxyCore.getTinyRabbit().send("guardian_discord", GuardianCalls.REQUEST_INITIATE_BAN.name(), id, target, reason, executor, String.valueOf(duration));
            consumer.accept(id);
        });
    }

    public void initiateBanLog(final PunishmentDocument log) {
        CompletableFuture.runAsync(() -> {
            final String setId = this.proxyCore.createId(10, "BL");
            final Document document = new Document("_id", setId)
                    .append("id", log.getId())
                    .append("target", log.getTarget())
                    .append("reason", log.getReason())
                    .append("executor", log.getExecutor())
                    .append("date", log.getDate())
                    .append("time_end", log.getDuration())
                    .append("time_start", System.currentTimeMillis());
            this.banBehaviorCollection.insertOne(document);
        });
    }

    public void initiateUnbanLog(final String target, final String id, final String reason, final String executor, final String setId) {
        CompletableFuture.runAsync(() -> {
            final Document document = new Document("_id", setId)
                    .append("id", id)
                    .append("target", target)
                    .append("reason", reason)
                    .append("executor", executor)
                    .append("date", this.proxyCore.getDate());
            this.banBehaviorCollection.insertOne(document);
        });
    }

    public void cancelBan(final String target, final String reason, final String executor, final Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            final String setId = this.proxyCore.createId(7, "CL");
            final Document document = this.banCollection.findOneAndDelete(new Document("target", target));
            assert document != null;
            consumer.accept(setId);

            this.proxyCore.getTinyRabbit().send("guardian_discord", GuardianCalls.REQUEST_CANCEL_BAN.name(), setId, document.getString("_id"), target, reason, executor);
            this.initiateUnbanLog(target, document.getString("_id"), reason, executor, setId);
        });
    }

    public void isActiveBan(final String target, final Consumer<Boolean> booleanConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.banCollection.find(new Document("target", target)).first();
            booleanConsumer.accept(document != null);
        });
    }

    public void isActiveBanId(final String id, final Consumer<Boolean> booleanConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.banCollection.find(new Document("_id", id)).first();
            booleanConsumer.accept(document != null);
        });
    }

    public void getActiveBanEntryByTarget(final String target, final Consumer<PunishmentDocument> entryConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.banCollection.find(new Document("target", target)).first();
            if (document != null) {
                entryConsumer.accept(new PunishmentDocument(
                        document.getString("_id"),
                        document.getString("target"),
                        document.getString("reason"),
                        document.getString("executor"),
                        document.getString("date"),
                        document.getLong("duration")
                ));
            } else entryConsumer.accept(null);
        });
    }

    public void getActiveBanEntryById(final String id, final Consumer<PunishmentDocument> entryConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.banCollection.find(new Document("_id", id)).first();
            if (document != null) {
                entryConsumer.accept(new PunishmentDocument(
                        document.getString("_id"),
                        document.getString("target"),
                        document.getString("reason"),
                        document.getString("executor"),
                        document.getString("date"),
                        document.getLong("duration")
                ));
            } else entryConsumer.accept(null);
        });
    }

    public void getBanBehaviorEntries(final String target, final String prefix, final Consumer<Set<PunishmentLogDocument>> entriesConsumer) {
        CompletableFuture.runAsync(() -> {
            final LinkedHashSet<PunishmentLogDocument> logDocuments = new LinkedHashSet<>();
            for (final Document document : this.banBehaviorCollection.find(new Document("target", target))) {
                if (document.getString("_id").startsWith(prefix)) {
                    if (prefix.equalsIgnoreCase("CL")) {
                        logDocuments.add(new PunishmentLogDocument(
                                document.getString("_id"),
                                document.getString("id"),
                                document.getString("target"),
                                document.getString("reason"),
                                document.getString("executor"),
                                document.getString("date"),
                                0,
                                0
                        ));
                    } else {
                        logDocuments.add(new PunishmentLogDocument(
                                document.getString("_id"),
                                document.getString("id"),
                                document.getString("target"),
                                document.getString("reason"),
                                document.getString("executor"),
                                document.getString("date"),
                                document.getLong("time_end"),
                                document.getLong("time_start")
                        ));
                    }
                }
            }
            entriesConsumer.accept(logDocuments);
        });
    }

    public void getBanBehaviorEntry(final String id, final Consumer<PunishmentLogDocument> entryConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.banBehaviorCollection.find(new Document("_id", id)).first();
            if (document != null) {
                entryConsumer.accept(new PunishmentLogDocument(
                        document.getString("_id"),
                        document.getString("id"),
                        document.getString("target"),
                        document.getString("reason"),
                        document.getString("executor"),
                        document.getString("date"),
                        document.getLong("time_end"),
                        document.getLong("time_start")
                ));
            } else entryConsumer.accept(null);
        });
    }

    public void deleteBanBehaviorEntry(final String id) {
        CompletableFuture.runAsync(() -> {
            this.banBehaviorCollection.findOneAndDelete(new Document("_id", id));
        });
    }

}
