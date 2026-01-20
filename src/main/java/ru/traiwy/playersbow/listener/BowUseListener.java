package ru.traiwy.playersbow.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BowUseListener implements Listener {

    private final JavaPlugin plugin;

    private final Map<UUID, BukkitTask> pullTasks = new HashMap<>();
    private final Map<UUID, Player> targets = new HashMap<>();

    public BowUseListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBowPull(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.BOW) return;
        if (pullTasks.containsKey(player.getUniqueId())) return;

        Player target = getTargetPlayer(player, 15);
        if (target == null) return;

        targets.put(player.getUniqueId(), target);

        target.setGravity(false);
        target.setCollidable(false);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!player.isOnline()
                    || !player.isHandRaised()
                    || player.getInventory().getItemInMainHand().getType() != Material.BOW) {

                stopPull(player);
                return;
            }

            Player t = targets.get(player.getUniqueId());
            if (t == null || !t.isOnline()) return;

            Location eye = player.getEyeLocation();
            Vector dir = eye.getDirection().normalize();

            Location fixed = eye.add(dir.multiply(2.0)).add(0, -0.6, 0);
            fixed.setYaw(t.getLocation().getYaw());
            fixed.setPitch(t.getLocation().getPitch());

            t.teleport(fixed);

        }, 0L, 1L);

        pullTasks.put(player.getUniqueId(), task);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Player target = targets.get(player.getUniqueId());
        if (target == null) return;

        Vector arrowVelocity = event.getProjectile().getVelocity();
        target.setVelocity(arrowVelocity);

        stopPull(player);
    }

    private void stopPull(Player player) {
        BukkitTask task = pullTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();

        Player target = targets.remove(player.getUniqueId());
        if (target != null) {
            target.setGravity(true);
            target.setCollidable(true);
        }
    }

    private Player getTargetPlayer(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        for (var e : player.getNearbyEntities(distance, distance, distance)) {
            if (!(e instanceof Player target) || target == player) continue;

            Vector toTarget = target.getLocation()
                    .add(0, 1, 0)
                    .toVector()
                    .subtract(eye.toVector())
                    .normalize();

            double angle = Math.toDegrees(Math.acos(dir.dot(toTarget)));
            if (angle < 10) return target;
        }
        return null;
    }
}
