package ru.traiwy.playersbow.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BowConfig {

    private final String bowName;
    private final List<String> bowLore;
    private final Messages messages;

    public BowConfig(FileConfiguration config) {
        this.bowName = Messages.color(config.getString("bow.name"));
        this.bowLore = config.getStringList("bow.lore").stream()
                .map(Messages::color)
                .collect(Collectors.toList());
        this.messages = new Messages(config.getConfigurationSection("messages"));
    }
}
