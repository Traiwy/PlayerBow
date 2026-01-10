package ru.traiwy.playersbow;

import org.bukkit.plugin.java.JavaPlugin;
import ru.traiwy.playersbow.listener.BowUseListener;

public final class PlayersBow extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BowUseListener(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
