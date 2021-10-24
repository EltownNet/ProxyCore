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
            "Lust auf §eKryptowährungen§7? Dann probier doch mal §e/wallet§7!",
            "§2Farme Items §7oder erledige §2Quests§7, um dein §2Level §7zu steigern.",
            "Natürlich kann man bei §6ChestShops §7auch mit der §6Bankkarte §7bezahlen!",
            "Heute schon deine §dtägliche Belohnung §7bei §dLola §7abgeholt?",
            "Bei §cFragen oder Problemen §7steht dir der §cSupport §7zur verfügung. §8[§c/support§8]"
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
        }, 1000 * 300, 1000 * 300);
    }

}
