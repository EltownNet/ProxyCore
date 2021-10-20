package net.eltown.proxycore.components.tasks;

import dev.waterdog.waterdogpe.ProxyServer;
import lombok.RequiredArgsConstructor;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.handlers.PlaytimeHandler;

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
                    ProxyServer.getInstance().getPlayers().values().forEach((e) -> handler.needChange(e.getName()));
                    handler.save();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 60000, 60000);
    }

}
