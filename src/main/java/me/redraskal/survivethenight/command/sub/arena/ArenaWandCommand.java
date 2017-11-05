package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaWandCommand extends SubCommand {

    @Override
    public String name() {
        return "arena wand";
    }

    @Override
    public String permission() {
        return "survive.arena.create";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ItemStack itemStack = new ItemStack(Material.IRON_AXE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                "&6&l&nPosition Wand"));
        itemStack.setItemMeta(itemMeta);

        player.getInventory().addItem(itemStack);
        player.sendMessage(surviveTheNight.buildMessage("<prefix> &aYou have been given the &6&l&nPosition Wand&a."));
    }
}