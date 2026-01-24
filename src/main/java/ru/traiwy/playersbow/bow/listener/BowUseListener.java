package ru.traiwy.playersbow.bow.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.manager.BowPullManager;
import ru.traiwy.playersbow.bow.manager.NoFallManager;

public class BowUseListener implements Listener {

    private final BowPullManager manager;
    private final CustomBowFactory bowFactory;

    public BowUseListener(JavaPlugin plugin, CustomBowFactory bowFactory, NoFallManager noFallManager) {
        this.manager = new BowPullManager(plugin, noFallManager);
        this.bowFactory = bowFactory;
    }

    @EventHandler
    public void onPull(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!bowFactory.isCustomBow(item)) return;
        if (manager.hasSession(player)) return;

        Player target = findTarget(player, 15);
        if (target != null) {
            manager.start(player, target);
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack bow = event.getBow();
        if (!bowFactory.isCustomBow(bow)) return;

        manager.release(player, event.getProjectile().getVelocity());
    }

    private Player findTarget(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        return player.getNearbyEntities(distance, distance, distance).stream()
                .filter(e -> e instanceof Player p && p != player)
                .map(e -> (Player) e)
                .filter(p -> {
                    Vector to = p.getLocation().add(0, 1, 0)
                            .toVector()
                            .subtract(eye.toVector())
                            .normalize();
                    return Math.toDegrees(Math.acos(dir.dot(to))) < 10;
                })
                .findFirst()
                .orElse(null);
    }
}
