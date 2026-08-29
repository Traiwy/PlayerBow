package ru.traiwy.playersbow.bow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.traiwy.playersbow.bow.manager.BowPullManager;
import ru.traiwy.playersbow.bow.manager.NoFallManager;

public class PlayerStateListener implements Listener {

    private final BowPullManager manager;
    private final NoFallManager noFallManager;

    public PlayerStateListener(BowPullManager manager, NoFallManager noFallManager) {
        this.manager = manager;
        this.noFallManager = noFallManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        release(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        release(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        release(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasGravity()) player.setGravity(true);
    }

    private void release(Player player) {
        manager.stopByShooter(player);
        manager.stopByTarget(player);
        noFallManager.clear(player);
    }
}
