package ru.traiwy.playersbow.bow.effect;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FallLandingEffect {

    private static final float EXPLOSION_POWER = 1.0f;
    private static final int FLAME_PARTICLES = 5;
    private static final int SMOKE_PARTICLES = 2;
    private static final double RADIUS_PER_POWER = 2.0;
    private static final double KNOCKBACK_PER_POWER = 0.5;
    private static final double KNOCKBACK_LIFT = 0.4;
    private static final double MIN_LENGTH_SQUARED = 1.0E-6;

    public void play(Player player) {
        final Location location = player.getLocation();
        final World world = location.getWorld();

        world.createExplosion(
                location.getX(),
                location.getY(),
                location.getZ(),
                EXPLOSION_POWER,
                false,
                false
        );

        world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
        world.spawnParticle(Particle.FLAME, location, FLAME_PARTICLES, 1, 1, 1, 0.1);
        world.spawnParticle(Particle.SMOKE_LARGE, location, SMOKE_PARTICLES, 1, 1, 1, 0.05);

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.5f, 1.2f);

        final double radius = EXPLOSION_POWER * RADIUS_PER_POWER;
        for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living) || living.equals(player)) continue;

            Vector direction = living.getLocation().toVector().subtract(location.toVector());
            if (direction.lengthSquared() < MIN_LENGTH_SQUARED) continue;

            living.setVelocity(direction.normalize()
                    .setY(KNOCKBACK_LIFT)
                    .multiply(EXPLOSION_POWER * KNOCKBACK_PER_POWER));
        }

        player.playEffect(EntityEffect.TOTEM_RESURRECT);
    }
}
