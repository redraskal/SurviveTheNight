package me.redraskal.survivethenight.game;

import lombok.Getter;
import lombok.Setter;
import me.redraskal.survivethenight.utils.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Generator {

    @Getter private final Block block;
    @Getter @Setter private boolean running = false;
    @Getter @Setter private int fuelPercentage = 0;

    @Getter private final ArmorStand armorStand;
    @Getter private final ArmorStand armorStand2;

    public Generator(Block block) {
        this.block = block;
        this.armorStand = block.getWorld().spawn(LocationUtils.center(block.getLocation().clone().add(0, 1D, 0)), ArmorStand.class);
        this.armorStand2 = block.getWorld().spawn(armorStand.getLocation().clone().subtract(0, 0.3D, 0), ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);

        armorStand2.setVisible(false);
        armorStand2.setBasePlate(false);
        armorStand2.setGravity(false);
        armorStand2.setSmall(true);

        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&l✖ &cGenerator &c&l✖"));
        armorStand.setCustomNameVisible(true);

        armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', "&8▍▍▍▍▍▍▍▍▍▍▍▍▍▍"));
        armorStand2.setCustomNameVisible(true);
    }
}