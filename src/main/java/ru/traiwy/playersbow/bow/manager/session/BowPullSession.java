package ru.traiwy.playersbow.bow.manager.session;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

@Getter
public class BowPullSession {

    private static final double FORWARD_OFFSET = 2.0;
    private static final double VERTICAL_OFFSET = -0.6;
    private static final double OFFSET_STEP = 0.5;

    private final Player shooter;
    private final Player target;
    private int ticks;

    public BowPullSession(Player shooter, Player target) {
        this.shooter = shooter;
        this.target = target;

        target.eject();
        if (target.isInsideVehicle()) target.leaveVehicle();
        target.setGravity(false);
        target.setCollidable(false);
    }

    public void tick() {
        ticks++;

        Location hold = holdPoint();
        Location current = target.getLocation();
        hold.setYaw(current.getYaw());
        hold.setPitch(current.getPitch());

        target.setFallDistance(0.0f);
        target.teleport(hold, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void stop() {
        target.setGravity(true);
        target.setCollidable(true);
    }

    private Location holdPoint() {
        Location eye = shooter.getEyeLocation();
        Vector direction = eye.getDirection();

        for (double offset = FORWARD_OFFSET; offset >= OFFSET_STEP; offset -= OFFSET_STEP) {
            Location point = eye.clone()
                    .add(direction.clone().multiply(offset))
                    .add(0, VERTICAL_OFFSET, 0);
            if (isFree(point)) return point;
        }
        return eye.clone().add(0, VERTICAL_OFFSET, 0);
    }

    private boolean isFree(Location point) {
        return point.getBlock().isPassable()
                && point.clone().add(0, 1, 0).getBlock().isPassable();
    }
}
