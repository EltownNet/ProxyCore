package net.eltown.proxycore.components.messaging;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.GroupCalls;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GroupListener {

    private final ProxyCore instance;
    private final TinyRabbitListener listener;

    public GroupListener(final ProxyCore instance) {
        this.instance = instance;
        this.listener = new TinyRabbitListener("localhost");
        this.listener.throwExceptions(true);

        this.getGroups().forEach(e -> {
            this.instance.getTinyRabbit().sendAndReceive(prefix -> {
                switch (GroupCalls.valueOf(prefix.getKey().toUpperCase())) {
                    case CALLBACK_GET_PREFIX:
                        this.instance.cachedGroupPrefix.put(e, prefix.getData()[1]);
                        break;
                }
            }, "groups", GroupCalls.REQUEST_GET_PREFIX.name(), e);
        });

        this.startListening();
    }

    public void startListening() {
        this.listener.receive((delivery -> {
            switch (GroupCalls.valueOf(delivery.getKey().toUpperCase())) {
                case REQUEST_CHANGE_PLAYER_PREFIX:
                    this.instance.cachedRankedPlayers.put(delivery.getData()[1], delivery.getData()[2]);
                    break;
                case REQUEST_CHANGE_PREFIX:
                    this.instance.cachedGroupPrefix.remove(delivery.getData()[1]);
                    this.instance.cachedGroupPrefix.put(delivery.getData()[1], delivery.getData()[2]);
                    break;
            }
        }), "ProxyCore/GroupManager/Listener", "groups.extern");
    }

    public List<String> getGroups() {
        final AtomicReference<List<String>> list = new AtomicReference<>();
        this.instance.getTinyRabbit().sendAndReceive((delivery -> {
            switch (GroupCalls.valueOf(delivery.getKey().toUpperCase())) {
                case CALLBACK_GROUPS:
                    list.set(Arrays.asList(delivery.getData()[1].split("#")));
                    break;
            }
        }), "groups", GroupCalls.REQUEST_GROUPS.name());
        return list.get();
    }

}
