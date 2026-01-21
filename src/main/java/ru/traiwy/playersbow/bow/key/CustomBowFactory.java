package ru.traiwy.playersbow.bow.key;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomBowFactory {

    private final NamespacedKey customBowKey;

    public CustomBowFactory(JavaPlugin plugin) {
        this.customBowKey = new NamespacedKey(plugin, "custom_bow");
    }

    public ItemStack create() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();

        meta.setDisplayName("§d§lPlayer Bow");
        meta.getPersistentDataContainer().set(
                customBowKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        bow.setItemMeta(meta);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        bow.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return bow;
    }

    public boolean isCustomBow(ItemStack item) {
        if (item == null || item.getType() != Material.BOW) return false;
        if (!item.hasItemMeta()) return false;

        return item.getItemMeta()
                .getPersistentDataContainer()
                .has(customBowKey, PersistentDataType.BYTE);
    }
}
