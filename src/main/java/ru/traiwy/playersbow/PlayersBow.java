package ru.traiwy.playersbow;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.traiwy.playersbow.bow.effect.FallLandingEffect;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.listener.BowProjectileListener;
import ru.traiwy.playersbow.bow.listener.BowUseListener;
import ru.traiwy.playersbow.bow.listener.FallDamageListener;
import ru.traiwy.playersbow.bow.listener.PlayerStateListener;
import ru.traiwy.playersbow.bow.manager.BowPullManager;
import ru.traiwy.playersbow.bow.manager.NoFallManager;
import ru.traiwy.playersbow.bow.target.TargetFinder;
import ru.traiwy.playersbow.command.GiveBowCommand;
import ru.traiwy.playersbow.config.BowConfig;

public final class PlayersBow extends JavaPlugin {

    private BowPullManager bowPullManager;
    private NoFallManager noFallManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        BowConfig config = new BowConfig(getConfig());
        CustomBowFactory customBowFactory = new CustomBowFactory(this, config);
        TargetFinder targetFinder = new TargetFinder();
        FallLandingEffect fallLandingEffect = new FallLandingEffect();

        noFallManager = new NoFallManager(this);
        bowPullManager = new BowPullManager(this, noFallManager, customBowFactory);

        getServer().getPluginManager().registerEvents(new FallDamageListener(noFallManager, fallLandingEffect), this);
        getServer().getPluginManager().registerEvents(new BowUseListener(bowPullManager, customBowFactory, targetFinder, config), this);
        getServer().getPluginManager().registerEvents(new BowProjectileListener(customBowFactory), this);
        getServer().getPluginManager().registerEvents(new PlayerStateListener(bowPullManager, noFallManager), this);

        PluginCommand command = getCommand("custombow");
        GiveBowCommand giveBowCommand = new GiveBowCommand(customBowFactory, config);
        command.setExecutor(giveBowCommand);
        command.setTabCompleter(giveBowCommand);
    }

    @Override
    public void onDisable() {
        bowPullManager.stopAll();
        noFallManager.clearAll();
    }
}
