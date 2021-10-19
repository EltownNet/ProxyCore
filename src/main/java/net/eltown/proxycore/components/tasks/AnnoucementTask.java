package net.eltown.proxycore.components.tasks;

import net.eltown.proxycore.ProxyCore;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class AnnoucementTask {

    private final ProxyCore core;
    private final String prefix = "§8» §fInfo §8| §7";
    private final List<String> messages = Arrays.asList(
            "Unseren §9Discord §7findest du unter §7www.§9eltown.net/discord§7!",
            "Du benötigst eine bessere §eChestShop-Lizenz§7? Dann vereinbare einen Termin im §eRathaus§7 mit §eFrau Bärwald§7!",
            "Lust auf §eKryptowährungen§7? Dann probier doch mal §e/wallet§7!"
    );

    public AnnoucementTask(final ProxyCore core) {
        this.core = core;
        this.core.setLastMessage(0);
    }

    public void start() {
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (core.getProxy().getPlayers().size() > 0 && core.getLastMessage() + 60000 < System.currentTimeMillis()) {
                    core.getProxy().getPlayers().values().forEach((p) -> p.sendMessage(prefix + messages.get(ThreadLocalRandom.current().nextInt(messages.size()))));
                }
            }
        }, 1000 * 120, 1000 * 120);
    }

}
