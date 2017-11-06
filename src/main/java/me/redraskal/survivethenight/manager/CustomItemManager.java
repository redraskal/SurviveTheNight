package me.redraskal.survivethenight.manager;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.utils.SkullProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class CustomItemManager {

    @Getter private final SurviveTheNight surviveTheNight;

    @Getter private final ItemStack fuelCanItemStack;
    @Getter private final ItemStack lightItemStack;
    @Getter private final ItemStack bandageItemStack;

    public CustomItemManager(SurviveTheNight surviveTheNight) {
        this.surviveTheNight = surviveTheNight;

        this.fuelCanItemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        this.applyDisplayName(this.fuelCanItemStack, "&7&lFuel Can");
        new SkullProfile(this.getSurviveTheNight().getConfig().getString("fuel-can-hash"))
                .applyTextures(this.fuelCanItemStack);

        this.lightItemStack = new ItemStack(Material.FLINT_AND_STEEL, 1);
        this.applyDisplayName(this.lightItemStack, "&6&lLight");

        this.bandageItemStack = new ItemStack(Material.PAPER, 1);
        this.applyDisplayName(this.bandageItemStack, "&c&lBandage &7(Self-heal)");
    }

    public boolean isSameItem(ItemStack itemStack, ItemStack comparing) {
        if(itemStack == null || comparing == null) return false;
        if(itemStack.getType() == comparing.getType()) {
            if(itemStack.hasItemMeta() && comparing.hasItemMeta()) {
                if(itemStack.getItemMeta().getDisplayName().equals(comparing.getItemMeta().getDisplayName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyDisplayName(ItemStack itemStack, String displayName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        itemStack.setItemMeta(itemMeta);
    }
}