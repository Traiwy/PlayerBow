package ru.traiwy.playersbow.config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class Messages {

    private final Map<String, String> values = new HashMap<>();

    public Messages(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            values.put(key, color(section.getString(key)));
        }
    }

    public void send(CommandSender receiver, String key) {
        String message = values.get(key);
        if (message == null || message.isEmpty()) return;
        receiver.sendMessage(message);
    }

    public static String color(String raw) {
        return ChatColor.translateAlternateColorCodes('&', raw);
    }
}
