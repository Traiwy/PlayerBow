package ru.traiwy.playersbow.bow.manager.session;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

@Getter
public class BowPullSession {
    private final Player shooter;
    private final Player target;
    private final BukkitTask task;

    public BowPullSession(Player shooter, Player target, BukkitTask task) {
        this.shooter = shooter;
        this.target = target;
        this.task = task;

        target.setGravity(false);
        target.setCollidable(false);
    }

    public void tick() {
        Location eye = shooter.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Location fixed = eye
                .add(dir.multiply(2.0))
                .add(0, -0.6, 0);

        fixed.setYaw(target.getLocation().getYaw());
        fixed.setPitch(target.getLocation().getPitch());

        target.teleport(fixed);
    }

    public void release(Vector velocity) {
        target.setVelocity(velocity);
        stop();
    }

    public void stop() {
        task.cancel();
        target.setGravity(true);
        target.setCollidable(true);
    }
}
