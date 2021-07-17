package net.eltown.proxycore.components.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.GuardianCalls;
import net.eltown.proxycore.components.data.PunishmentDocument;
import net.eltown.proxycore.components.data.PunishmentLogDocument;
import org.bson.Document;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MuteHandler {

    private final ProxyCore proxyCore;
    private final MongoCollection<Document> muteCollection, muteBehaviorCollection;

    public final HashMap<String, PunishmentDocument> cachedActiveMutes = new HashMap<>();

    public MuteHandler(final ProxyCore proxyCore, final MongoDatabase database) {
        this.proxyCore = proxyCore;
        this.muteCollection = database.getCollection("mute_collection");
        this.muteBehaviorCollection = database.getCollection("mute_behavior_collection");
    }

    public void initiateMute(final String target, final String reason, final String executor, final long duration, final Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            final String id = this.proxyCore.createId(7, "M");

            final Document document = new Document("_id", id)
                    .append("target", target)
                    .append("reason", reason)
                    .append("executor", executor)
                    .append("date", this.proxyCore.getDate())
                    .append("duration", duration);
            this.muteCollection.insertOne(document);

            this.initiateMuteLog(new PunishmentDocument(id, target, reason, executor, this.proxyCore.getDate(), duration));

            final ProxiedPlayer player = this.proxyCore.getProxy().getPlayer(target);
            if (player != null) {
                this.cachedActiveMutes.put(player.getName(), new PunishmentDocument(id, target, reason, executor, this.proxyCore.getDate(), duration));
            }

            this.proxyCore.getTinyRabbit().send("guardian_discord", GuardianCalls.REQUEST_INITIATE_MUTE.name(), id, target, reason, executor, String.valueOf(duration));
            consumer.accept(id);
        });
    }

    public void initiateMuteLog(final PunishmentDocument log) {
        CompletableFuture.runAsync(() -> {
            final String setId = this.proxyCore.createId(10, "ML");
            final Document document = new Document("_id", setId)
                    .append("id", log.getId())
                    .append("target", log.getTarget())
                    .append("reason", log.getReason())
                    .append("executor", log.getExecutor())
                    .append("date", log.getDate())
                    .append("time_end", log.getDuration())
                    .append("time_start", System.currentTimeMillis());
            this.muteBehaviorCollection.insertOne(document);
        });
    }

    public void initiateUnmuteLog(final String target, final String id, final String reason, final String executor, final String setId) {
        CompletableFuture.runAsync(() -> {
            final Document document = new Document("_id", setId)
                    .append("id", id)
                    .append("target", target)
                    .append("reason", reason)
                    .append("executor", executor)
                    .append("date", this.proxyCore.getDate());
            this.muteBehaviorCollection.insertOne(document);
        });
    }

    public void cancelMute(final String target, final String reason, final String executor, final Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            final String setId = this.proxyCore.createId(7, "CL");
            final Document document = this.muteCollection.findOneAndDelete(new Document("target", target));
            assert document != null;
            consumer.accept(setId);

            final ProxiedPlayer player = this.proxyCore.getProxy().getPlayer(target);
            if (player != null) {
                this.cachedActiveMutes.remove(player.getName());
            }

            this.proxyCore.getTinyRabbit().send("guardian_discord", GuardianCalls.REQUEST_CANCEL_MUTE.name(), setId, document.getString("_id"), target, reason, executor);
            this.initiateUnmuteLog(target, document.getString("_id"), reason, executor, setId);
        });
    }

    public void isActiveMute(final String target, final Consumer<Boolean> booleanConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.muteCollection.find(new Document("target", target)).first();
            booleanConsumer.accept(document != null);
        });
    }

    public void isActiveMuteId(final String id, final Consumer<Boolean> booleanConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.muteCollection.find(new Document("_id", id)).first();
            booleanConsumer.accept(document != null);
        });
    }

    public void getActiveMuteEntryByTarget(final String target, final Consumer<PunishmentDocument> entryConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.muteCollection.find(new Document("target", target)).first();
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

    public void getActiveMuteEntryById(final String id, final Consumer<PunishmentDocument> entryConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.muteCollection.find(new Document("_id", id)).first();
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

    public void getMuteBehaviorEntries(final String target, final String prefix, final Consumer<Set<PunishmentLogDocument>> entriesConsumer) {
        CompletableFuture.runAsync(() -> {
            final LinkedHashSet<PunishmentLogDocument> logDocuments = new LinkedHashSet<>();
            for (final Document document : this.muteBehaviorCollection.find(new Document("target", target))) {
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

    public void getMuteBehaviorEntry(final String id, final Consumer<PunishmentLogDocument> entryConsumer) {
        CompletableFuture.runAsync(() -> {
            final Document document = this.muteBehaviorCollection.find(new Document("_id", id)).first();
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

    public void deleteMuteBehaviorEntry(final String id) {
        CompletableFuture.runAsync(() -> {
            this.muteBehaviorCollection.findOneAndDelete(new Document("_id", id));
        });
    }

}

