package net.eltown.proxycore.components.messaging;

import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.GroupCalls;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;

public class GroupListener {

    private final ProxyCore instance;
    private final TinyRabbitListener listener;

    public GroupListener(final ProxyCore instance) {
        this.instance = instance;
        this.listener = new TinyRabbitListener("localhost");
        this.listener.throwExceptions(true);

        this.startListening();
    }

    public void startListening() {
        this.listener.receive((delivery -> {
            switch (GroupCalls.valueOf(delivery.getKey().toUpperCase())) {
                case REQUEST_CHANGE_PLAYER_PREFIX:
                    this.instance.cachedRankedPlayers.remove(delivery.getData()[1]);
                    this.instance.cachedRankedPlayers.put(delivery.getData()[1], delivery.getData()[2]);
                    break;
            }
        }), "ProxyCore/GroupManager/Listener", "groups.extern");
    }

}
