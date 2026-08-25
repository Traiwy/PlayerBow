package ru.traiwy.playersbow.bow.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.manager.session.BowPullSession;
import ru.traiwy.playersbow.bow.target.TargetFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BowPullManager {

    private static final int NOFALL_DURATION_TICKS = 100;

    private final JavaPlugin plugin;
    private final NoFallManager noFallManager;
    private final CustomBowFactory bowFactory;
    private final TargetFinder targetFinder;
    private final Map<UUID, BukkitTask> aimTasks = new HashMap<>();
    private final Map<UUID, BowPullSession> byShooter = new HashMap<>();
    private final Map<UUID, UUID> targetToShooter = new HashMap<>();

    public BowPullManager(JavaPlugin plugin, NoFallManager noFallManager,
                          CustomBowFactory bowFactory, TargetFinder targetFinder) {
        this.plugin = plugin;
        this.noFallManager = noFallManager;
        this.bowFactory = bowFactory;
        this.targetFinder = targetFinder;
    }

    public boolean isCaptured(Player target) {
        return targetToShooter.containsKey(target.getUniqueId());
    }

    public void beginAim(Player shooter) {
        UUID id = shooter.getUniqueId();
        if (aimTasks.containsKey(id)) return;

        aimTasks.put(id, Bukkit.getScheduler().runTaskTimer(plugin, () -> aimTick(shooter), 0L, 1L));
    }

    public void release(Player shooter, Vector velocity) {
        cancelAim(shooter.getUniqueId());

        BowPullSession session = byShooter.get(shooter.getUniqueId());
        if (session == null) return;
        end(session, velocity, true);
    }

    public void stopByShooter(Player shooter) {
        cancelAim(shooter.getUniqueId());

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
        for (UUID id : new ArrayList<>(aimTasks.keySet())) {
            cancelAim(id);
        }
        for (BowPullSession session : new ArrayList<>(byShooter.values())) {
            end(session, null, false);
        }
    }

    private void aimTick(Player shooter) {
        if (!canHold(shooter)) {
            stopByShooter(shooter);
            return;
        }

        BowPullSession session = byShooter.get(shooter.getUniqueId());
        if (session == null) {
            Player target = targetFinder.find(shooter);
            if (target == null || isCaptured(target)) return;

            session = new BowPullSession(shooter, target);
            byShooter.put(shooter.getUniqueId(), session);
            targetToShooter.put(target.getUniqueId(), shooter.getUniqueId());
        } else if (!isTargetValid(session)) {
            end(session, null, true);
            return;
        }

        session.tick();
    }

    private void cancelAim(UUID shooterId) {
        BukkitTask task = aimTasks.remove(shooterId);
        if (task != null) task.cancel();
    }

    private void end(BowPullSession session, Vector velocity, boolean guardFall) {
        Player target = session.getTarget();

        byShooter.remove(session.getShooter().getUniqueId());
        targetToShooter.remove(target.getUniqueId());

        session.stop();

        if (!target.isOnline()) {
            noFallManager.clear(target.getUniqueId());
            return;
        }
        if (velocity != null) target.setVelocity(velocity);
        if (guardFall && session.getTicks() > 0) noFallManager.grant(target, NOFALL_DURATION_TICKS);
    }

    private boolean canHold(Player shooter) {
        return shooter.isOnline()
                && shooter.isHandRaised()
                && bowFactory.isCustomBow(shooter.getInventory().getItemInMainHand());
    }

    private boolean isTargetValid(BowPullSession session) {
        Player target = session.getTarget();

        return target.isOnline()
                && !target.isDead()
                && session.getShooter().getWorld().equals(target.getWorld());
    }
}
