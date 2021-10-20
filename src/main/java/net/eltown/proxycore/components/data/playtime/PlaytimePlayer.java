package net.eltown.proxycore.components.data.playtime;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlaytimePlayer {

    private final String player;
    private long total, today;
    private boolean changed;

    public void add(final long ms) {
        total += ms;
        today += ms;
        changed = true;
    }

    public void nextDay() {
        if (today > 0) {
            today = 0;
            changed = true;
        }
    }

}
