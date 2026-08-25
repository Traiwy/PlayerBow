package ru.traiwy.playersbow.bow.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.manager.session.BowPullSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BowPullManager {

    private static final int NOFALL_DURATION_TICKS = 100;

    private final JavaPlugin plugin;
    private final NoFallManager noFallManager;
    private final CustomBowFactory bowFactory;
    private final Map<UUID, BowPullSession> byShooter = new HashMap<>();
    private final Map<UUID, UUID> targetToShooter = new HashMap<>();

    public BowPullManager(JavaPlugin plugin, NoFallManager noFallManager, CustomBowFactory bowFactory) {
        this.plugin = plugin;
        this.noFallManager = noFallManager;
        this.bowFactory = bowFactory;
    }

    public boolean hasSession(Player shooter) {
        return byShooter.containsKey(shooter.getUniqueId());
    }

    public boolean isCaptured(Player target) {
        return targetToShooter.containsKey(target.getUniqueId());
    }

    public void start(Player shooter, Player target) {
        BowPullSession session = new BowPullSession(shooter, target);
        byShooter.put(shooter.getUniqueId(), session);
        targetToShooter.put(target.getUniqueId(), shooter.getUniqueId());

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!isValid(session)) {
                end(session, null, true);
                return;
            }
            session.tick();
        }, 0L, 1L);
        session.bind(task);
    }

    public void release(Player shooter, Vector velocity) {
        BowPullSession session = byShooter.get(shooter.getUniqueId());
        if (session == null) return;
        end(session, velocity, true);
    }

    public void stopByShooter(Player shooter) {
        BowPullSession session = byShooter.get(shooter.getUniqueId());
        if (session != null) end(session, null, true);
    }

    public void stopByTarget(Player target) {
        UUID shooterId = targetToShooter.get(target.getUniqueId());
        if (shooterId == null) return;
        BowPullSession session = byShooter.get(shooterId);
        if (session != null) end(session, null, true);
    }

    public void stopAll() {
        for (BowPullSession session : new ArrayList<>(byShooter.values())) {
            end(session, null, false);
        }
    }

    private void end(BowPullSession session, Vector velocity, boolean guardFall) {
        Player shooter = session.getShooter();
        Player target = session.getTarget();

        byShooter.remove(shooter.getUniqueId());
        targetToShooter.remove(target.getUniqueId());

        session.stop();

        if (!target.isOnline()) {
            noFallManager.clear(target.getUniqueId());
            return;
        }
        if (velocity != null) target.setVelocity(velocity);
        if (guardFall && session.getTicks() > 0) noFallManager.grant(target, NOFALL_DURATION_TICKS);
    }

    private boolean isValid(BowPullSession session) {
        Player shooter = session.getShooter();
        Player target = session.getTarget();

        return shooter.isOnline()
                && target.isOnline()
                && !target.isDead()
                && shooter.getWorld().equals(target.getWorld())
                && shooter.isHandRaised()
                && bowFactory.isCustomBow(shooter.getInventory().getItemInMainHand());
    }
}
