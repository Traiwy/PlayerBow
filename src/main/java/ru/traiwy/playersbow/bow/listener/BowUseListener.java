package ru.traiwy.playersbow.bow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.manager.BowPullManager;
import ru.traiwy.playersbow.bow.target.TargetFinder;
import ru.traiwy.playersbow.config.BowConfig;

public class BowUseListener implements Listener {

    private static final String USE_PERMISSION = "playersbow.use";

    private final BowPullManager manager;
    private final CustomBowFactory bowFactory;
    private final TargetFinder targetFinder;
    private final BowConfig config;

    public BowUseListener(BowPullManager manager, CustomBowFactory bowFactory,
                          TargetFinder targetFinder, BowConfig config) {
        this.manager = manager;
        this.bowFactory = bowFactory;
        this.targetFinder = targetFinder;
        this.config = config;
    }

    @EventHandler
    public void onPull(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!bowFactory.isCustomBow(event.getItem())) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(USE_PERMISSION)) {
            config.getMessages().send(player, "no-permission");
            return;
        }
        if (manager.hasSession(player)) return;

        Player target = targetFinder.find(player);
        if (target == null || manager.isCaptured(target)) return;

        manager.start(player, target);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!bowFactory.isCustomBow(event.getBow())) return;
        if (!player.hasPermission(USE_PERMISSION)) return;

        manager.release(player, event.getProjectile().getVelocity());
        bowFactory.markArrow(event.getProjectile());
    }
}
