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
            "Unseren §9Discord §7findest du unter §9https://discord.eltown.net§7!",
            "Du benötigst eine bessere §eChestShop-Lizenz§7? Dann vereinbare einen Termin im §eRathaus§7!",
            "§2Farme Items §7oder erledige §2Quests§7, um dein §2Level §7zu steigern.",
            "Bei §6ChestShops §7kannst du auch mit der §6Bankkarte §7bezahlen!",
            "Heute schon deine §dtägliche Belohnung §7bei §dLola §7abgeholt?",
            "Heute schon für §dtolle Belohnungen §7gevotet? §8[§d/vote§8]",
            "Verwalte deine §9Freunde §7auf Eltown mit §9/freunde§7.",
            "Verifiziere deinen §bDiscord-Account §7mit deinem §bMinecraft-Account§7! §8[§b/auth§8]",
            "Bei §cFragen oder Problemen §7steht dir der §cSupport §7zur Verfügung! §8[§c/support§8]",
            "Bei §2Feedback und Vorschlägen §7steht dir der §2Support §7zur Verfügung! §8[§2/support§8]",
            "Jede Woche stehen euch §15 neue Quests §7zur Verfügung.",
            "In den nächsten Wochen erwarten euch viele §gneue Features§7!"
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
                    core.getProxy().getPlayers().forEach((p) -> p.sendMessage(prefix + messages.get(ThreadLocalRandom.current().nextInt(messages.size()))));
                }
            }
        }, 1000 * 300, 1000 * 300);
    }

}
