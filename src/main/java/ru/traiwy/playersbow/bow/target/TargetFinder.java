package ru.traiwy.playersbow.bow.target;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TargetFinder {

    private static final String EXEMPT_PERMISSION = "playersbow.exempt";
    private static final double MAX_DISTANCE = 15.0;
    private static final double MAX_ANGLE = 10.0;
    private static final double MIN_LENGTH_SQUARED = 1.0E-6;

    public Player find(Player shooter) {
        Location eye = shooter.getEyeLocation();
        Vector direction = eye.getDirection();

        Player best = null;
        double bestAngle = MAX_ANGLE;

        for (Entity entity : shooter.getNearbyEntities(MAX_DISTANCE, MAX_DISTANCE, MAX_DISTANCE)) {
            if (!(entity instanceof Player candidate)) continue;
            if (!isGrabbable(shooter, candidate)) continue;

            Vector to = candidate.getEyeLocation().toVector().subtract(eye.toVector());
            if (to.lengthSquared() > MAX_DISTANCE * MAX_DISTANCE) continue;
            if (to.lengthSquared() < MIN_LENGTH_SQUARED) continue;

            double angle = Math.toDegrees(direction.angle(to));
            if (angle >= bestAngle) continue;
            if (!shooter.hasLineOfSight(candidate)) continue;

            best = candidate;
            bestAngle = angle;
        }
        return best;
    }

    private boolean isGrabbable(Player shooter, Player candidate) {
        return candidate != shooter
                && !candidate.isDead()
                && candidate.getGameMode() != GameMode.SPECTATOR
                && !candidate.hasPermission(EXEMPT_PERMISSION)
                && shooter.canSee(candidate);
    }
}
