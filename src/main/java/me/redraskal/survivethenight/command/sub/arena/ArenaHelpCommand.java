package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaHelpCommand extends SubCommand {

    @Override
    public String name() {
        return "arena";
    }

    @Override
    public String permission() {
        return "survive.arena.help";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        player.sendMessage("");
        player.sendMessage(surviveTheNight.buildMessage("<prefix> Arena commands:"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena create &b<arena id>&3: Creates an arena"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena delete &b<arena id>&3: Deletes an arena"));
        player.sendMessage("");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena setlobby &b<arena id>&3: Sets the game lobby position"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena setmainlobby &b<arena id>&3: Sets the main lobby position"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena addspawn &b<arena id>&3: Adds a spawn position"));
        player.sendMessage("");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena setminplayers &b<arena id> <#>"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena setmaxplayers &b<arena id> <#>"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena setgeneratorsneeded &b<arena id> <#>"));
        player.sendMessage("");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena addgenerator &b<arena id>&3: Adds a generator (look at the block)"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena wand&3: Gives you a position wand"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena setbounds&3: Sets the arena bounds"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena adddoor &b<arena id>&3: Adds a door (go inside the door)"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena addarea &b<arena id> <name>&3: Adds an area"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7 /" + label + " &farena addgate &b<arena id>&3: Adds an exit gate"));
    }
}