package ru.traiwy.playersbow.bow.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoFallManager {

    private final JavaPlugin plugin;
    private final Map<UUID, BukkitTask> guarded = new HashMap<>();

    public NoFallManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void grant(Player player, int durationTicks) {
        UUID id = player.getUniqueId();
        clear(id);
        guarded.put(id, Bukkit.getScheduler().runTaskLater(plugin, () -> guarded.remove(id), durationTicks));
    }

    public boolean has(Player player) {
        return guarded.containsKey(player.getUniqueId());
    }

    public void clear(Player player) {
        clear(player.getUniqueId());
    }

    public void clear(UUID id) {
        BukkitTask task = guarded.remove(id);
        if (task != null) task.cancel();
    }

    public void clearAll() {
        guarded.values().forEach(BukkitTask::cancel);
        guarded.clear();
    }
}
