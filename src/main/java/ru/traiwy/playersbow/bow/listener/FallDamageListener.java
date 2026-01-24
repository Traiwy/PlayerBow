package ru.traiwy.playersbow.bow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.traiwy.playersbow.bow.effect.FallLandingEffect;
import ru.traiwy.playersbow.bow.manager.NoFallManager;

public class FallDamageListener implements Listener {

    private final NoFallManager noFallManager;
    private final FallLandingEffect landingEffect;

    public FallDamageListener(NoFallManager noFallManager,
                              FallLandingEffect landingEffect) {
        this.noFallManager = noFallManager;
        this.landingEffect = landingEffect;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!noFallManager.has(player)) return;

        event.setCancelled(true);
        noFallManager.remove(player);

        landingEffect.play(player);
    }
}
