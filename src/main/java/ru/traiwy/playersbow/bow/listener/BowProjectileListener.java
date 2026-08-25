package ru.traiwy.playersbow.bow.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;

public class BowProjectileListener implements Listener {

    private final CustomBowFactory bowFactory;

    public BowProjectileListener(CustomBowFactory bowFactory) {
        this.bowFactory = bowFactory;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!bowFactory.isCustomArrow(projectile)) return;

        event.setCancelled(true);
    }
}
