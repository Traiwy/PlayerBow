package ru.traiwy.playersbow.bow.key;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import ru.traiwy.playersbow.config.BowConfig;

public class CustomBowFactory {

    private final NamespacedKey customBowKey;
    private final NamespacedKey customArrowKey;
    private final BowConfig config;

    public CustomBowFactory(JavaPlugin plugin, BowConfig config) {
        this.customBowKey = new NamespacedKey(plugin, "custom_bow");
        this.customArrowKey = new NamespacedKey(plugin, "custom_arrow");
        this.config = config;
    }

    public ItemStack create() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();

        meta.setDisplayName(config.getBowName());
        meta.setLore(config.getBowLore());
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(
                customBowKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        bow.setItemMeta(meta);
        return bow;
    }

    public boolean isCustomBow(ItemStack item) {
        if (item == null || item.getType() != Material.BOW) return false;
        if (!item.hasItemMeta()) return false;

        return item.getItemMeta()
                .getPersistentDataContainer()
                .has(customBowKey, PersistentDataType.BYTE);
    }

    public void markArrow(Entity arrow) {
        arrow.getPersistentDataContainer().set(customArrowKey, PersistentDataType.BYTE, (byte) 1);
    }

    public boolean isCustomArrow(Entity arrow) {
        return arrow.getPersistentDataContainer().has(customArrowKey, PersistentDataType.BYTE);
    }
}
