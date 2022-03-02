package net.eltown.proxycore.components.data.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Player {

    private final UUID UUID;
    private String name;
    private long firstLogin;
    private long lastLogin;

}
