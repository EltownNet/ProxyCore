package net.eltown.proxycore.components.data;

import lombok.Data;

@Data
public class PunishmentDocument {

    private final String id;
    private final String target;
    private final String reason;
    private final String executor;
    private final String date;
    private final long duration;

}
