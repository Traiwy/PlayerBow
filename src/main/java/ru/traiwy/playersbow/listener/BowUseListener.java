package ru.traiwy.playersbow.listener;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class BowUseListener implements Listener {
    private final JavaPlugin plugin;

    private final Map<UUID, BukkitTask> bowTasks = new HashMap<>();

    @EventHandler
    public void onBowUse(PlayerInteractEvent event){
        System.out.println(1);
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final var player = event.getPlayer();
        final ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.BOW) return;
        System.out.println(2);

        if (bowTasks.containsKey(player.getUniqueId())) return;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            System.out.println(3);
            if(!player.isOnline() ||  player.getInventory().getItemInMainHand().getType() != Material.BOW){
                System.out.println(4);
                BukkitTask taskToCancel = bowTasks.get(player.getUniqueId());
                if(taskToCancel != null){
                    taskToCancel.cancel();
                }
                bowTasks.remove(player.getUniqueId());
                return;
            };
            final var target = getTargetPlayer(player, 10);
            if(target != null){
                player.sendMessage("You bow in  " + target.getName());
            }else{
                System.out.println("Player is null");
            }
        }, 0L, 20L);
        bowTasks.put(player.getUniqueId(), task);
    }

    private Player getTargetPlayer(Player player, double distance){
        var result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                distance,
                entity -> entity instanceof Player && entity != player
        );

        if(result == null){
            System.out.println("Player is null");
            return null;
        }else {
            System.out.println(result);
        }
        return (Player) result.getHitEntity();
    }
}
