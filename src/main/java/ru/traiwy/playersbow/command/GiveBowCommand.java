package ru.traiwy.playersbow.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;
import ru.traiwy.playersbow.config.BowConfig;

import java.util.Collections;
import java.util.List;

public class GiveBowCommand implements CommandExecutor, TabCompleter {

    private static final String USE_PERMISSION = "playersbow.use";

    private final CustomBowFactory factory;
    private final BowConfig config;

    public GiveBowCommand(CustomBowFactory factory, BowConfig config) {
        this.factory = factory;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            config.getMessages().send(sender, "players-only");
            return true;
        }
        if (!player.hasPermission(USE_PERMISSION)) {
            config.getMessages().send(player, "no-permission");
            return true;
        }

        final ItemStack bow = factory.create();
        if (!player.getInventory().addItem(bow).isEmpty()) {
            config.getMessages().send(player, "inventory-full");
            return true;
        }

        config.getMessages().send(player, "bow-received");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
