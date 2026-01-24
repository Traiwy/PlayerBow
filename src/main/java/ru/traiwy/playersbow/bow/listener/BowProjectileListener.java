package ru.traiwy.playersbow.bow.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BowProjectileListener implements Listener {
    private final JavaPlugin plugin;

    public BowProjectileListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Player shooter)) return;

        NamespacedKey key = new NamespacedKey(plugin, "custom_arrow");
        if (!projectile.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        event.setCancelled(true);
        event.setDamage(0.0);
    }
}
