package net.eltown.proxycore.components.tasks;

import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.components.handlers.PlaytimeHandler;
import net.md_5.bungee.api.ProxyServer;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class PlaytimeTask {

    private final PlaytimeHandler handler;

    public void start() {
        final Timer timer = new Timer("PlaytimeTask #1");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ProxyServer.getInstance().getPlayers().forEach((e) -> handler.needChange(e.getName()));
                    handler.save();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 60000, 60000);
    }

}
