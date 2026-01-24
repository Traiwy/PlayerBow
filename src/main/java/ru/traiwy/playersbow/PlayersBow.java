package ru.traiwy.playersbow;

import org.bukkit.plugin.java.JavaPlugin;
import ru.traiwy.playersbow.bow.effect.FallLandingEffect;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.listener.BowProjectileListener;
import ru.traiwy.playersbow.bow.listener.BowUseListener;
import ru.traiwy.playersbow.bow.listener.FallDamageListener;
import ru.traiwy.playersbow.bow.manager.NoFallManager;
import ru.traiwy.playersbow.command.GiveBowCommand;

public final class PlayersBow extends JavaPlugin {

    @Override
    public void onEnable() {
        CustomBowFactory customBowFactory = new CustomBowFactory(this);
        NoFallManager noFallManager = new NoFallManager();
        FallLandingEffect fallLandingEffect = new FallLandingEffect(1,5);
        getServer().getPluginManager().registerEvents(new FallDamageListener(noFallManager, fallLandingEffect), this);
        getServer().getPluginManager().registerEvents(new BowUseListener(this, customBowFactory, noFallManager), this);
        getServer().getPluginManager().registerEvents(new BowProjectileListener(this), this);
        getCommand("custombow").setExecutor(new GiveBowCommand(customBowFactory));

    }

}
