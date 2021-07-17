package net.eltown.proxycore.components.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.eltown.proxycore.ProxyCore;
import org.bson.Document;

import java.util.function.Consumer;

public class WarnHandler {

    private final ProxyCore proxyCore;
    private final MongoCollection<Document> warnCollection;

    public WarnHandler(final ProxyCore proxyCore, final MongoDatabase database) {
        this.proxyCore = proxyCore;
        this.warnCollection = database.getCollection("warn_collection");
    }

    public void initiateWarn(final String target, final String reason, final String executor, final Consumer<String> consumer) {

    }

}
