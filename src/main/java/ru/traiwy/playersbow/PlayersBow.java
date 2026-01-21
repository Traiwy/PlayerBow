package ru.traiwy.playersbow;

import org.bukkit.plugin.java.JavaPlugin;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.bow.listener.BowUseListener;
import ru.traiwy.playersbow.bow.listener.NoFallDamageListener;
import ru.traiwy.playersbow.command.GiveBowCommand;

public final class PlayersBow extends JavaPlugin {

    @Override
    public void onEnable() {
        CustomBowFactory customBowFactory = new CustomBowFactory(this);
        NoFallDamageListener noFallDamageListener = new NoFallDamageListener();
        getServer().getPluginManager().registerEvents(noFallDamageListener, this);
        getServer().getPluginManager().registerEvents(new BowUseListener(this, customBowFactory, noFallDamageListener), this);
        getCommand("custombow").setExecutor(new GiveBowCommand(customBowFactory));

    }

}
