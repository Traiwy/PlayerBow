package ru.traiwy.playersbow.bow.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoFallManager {
    private final Set<UUID> noFallPlayers = new HashSet<>();

    public void add(Player player) {
        noFallPlayers.add(player.getUniqueId());
    }

    public void remove(Player player) {
        noFallPlayers.remove(player.getUniqueId());
    }

    public boolean has(Player player) {
        return noFallPlayers.contains(player.getUniqueId());
    }
}

