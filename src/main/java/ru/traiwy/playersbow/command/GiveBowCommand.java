package ru.traiwy.playersbow.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.traiwy.playersbow.bow.key.CustomBowFactory;

public class GiveBowCommand implements CommandExecutor {
    private final CustomBowFactory factory;

    public GiveBowCommand(CustomBowFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;
        final ItemStack bow = factory.create();
        player.getInventory().addItem(bow);
        return true;
    }

}
