package ru.traiwy.playersbow.bow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoFallDamageListener implements Listener {
    private final Set<UUID> noFallPlayers = new HashSet<>();

    public void addNoFall(Player player) {
        noFallPlayers.add(player.getUniqueId());
    }

    public void removeNoFall(Player player) {
        noFallPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        System.out.println(2);

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL &&
                noFallPlayers.contains(player.getUniqueId())) {
            System.out.println(1);
            event.setCancelled(true);
        }
    }
}
