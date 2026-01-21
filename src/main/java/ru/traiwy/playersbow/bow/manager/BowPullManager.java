package ru.traiwy.playersbow.bow.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.traiwy.playersbow.bow.listener.NoFallDamageListener;
import ru.traiwy.playersbow.bow.manager.session.BowPullSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BowPullManager {

    private final JavaPlugin plugin;
    private final NoFallDamageListener  noFallPlayers;
    private final Map<UUID, BowPullSession> sessions = new HashMap<>();

    public BowPullManager(JavaPlugin plugin, NoFallDamageListener noFallPlayers) {
        this.plugin = plugin;
        this.noFallPlayers = noFallPlayers;
    }

    public boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public void start(Player shooter, Player target) {
        noFallPlayers.addNoFall(target);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            BowPullSession session = sessions.get(shooter.getUniqueId());
            if (session == null || !isValid(shooter)) {
                stop(shooter);
                return;
            }
            session.tick();
        }, 0L, 1L);

        BowPullSession session = new BowPullSession(shooter, target, task);
        sessions.put(shooter.getUniqueId(), session);
    }

    public void release(Player shooter, Vector velocity) {
        BowPullSession session = sessions.remove(shooter.getUniqueId());
        if (session != null) {
            session.release(velocity);


            Player target = session.getTarget();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                noFallPlayers.removeNoFall(target);
            }, 100L);
        }
    }

    public void stop(Player shooter) {
        BowPullSession session = sessions.remove(shooter.getUniqueId());
        if (session != null) {
            session.stop();
            noFallPlayers.removeNoFall(session.getTarget());
        }
    }

    private boolean isValid(Player player) {
        return player.isOnline()
                && player.isHandRaised()
                && player.getInventory().getItemInMainHand().getType().name().endsWith("BOW");
    }
}
