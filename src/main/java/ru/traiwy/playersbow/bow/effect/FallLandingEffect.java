package ru.traiwy.playersbow.bow.effect;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FallLandingEffect {

    private final float explosionPower;
    private final int particleCount;

    public FallLandingEffect(float explosionPower, int particleCount) {
        this.explosionPower = explosionPower;
        this.particleCount = particleCount;
    }

    public void play(Player player) {
        final Location location = player.getLocation();
        final World world = location.getWorld();

        world.createExplosion(
                location.getX(),
                location.getY(),
                location.getZ(),
                explosionPower,
                false,
                false
        );

        world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
        world.spawnParticle(Particle.FLAME, location, particleCount, 1, 1, 1, 0.1);
        world.spawnParticle(Particle.SMOKE_LARGE, location, particleCount / 2, 1, 1, 1, 0.05);

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.5f, 1.2f);

        world.getNearbyEntities(location,
                        explosionPower * 2,
                        explosionPower * 2,
                        explosionPower * 2)
                .stream()
                .filter(e -> e instanceof LivingEntity && e != player)
                .forEach(entity -> {
                    final Vector dir = entity.getLocation().toVector()
                            .subtract(location.toVector())
                            .normalize()
                            .multiply(explosionPower * 0.5);
                    entity.setVelocity(dir);
                });

        player.playEffect(EntityEffect.TOTEM_RESURRECT);
    }
}

