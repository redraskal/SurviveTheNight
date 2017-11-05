package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.utils.Cuboid;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaSetBoundsCommand extends SubCommand {

    @Override
    public String name() {
        return "arena setbounds";
    }

    @Override
    public String permission() {
        return "survive.arena.create";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ArenaManager arenaManager = surviveTheNight.getArenaManager();

        if(args.length > 2) {
            try {
                int id = Integer.parseInt(args[2]);
                if(arenaManager.getArenaMap().containsKey(id)) {
                    Arena arena = arenaManager.getArenaMap().get(id);
                    Cuboid bounds = surviveTheNight.getWandListener().getSelection(player.getUniqueId());

                    if(bounds != null) {
                        arena.setBounds(bounds);
                        arenaManager.saveArena(id);
                        player.sendMessage(surviveTheNight.buildMessage("<prefix> &aMap boundaries has been set to ("
                                + bounds.getPos1().getBlockX() + ", " + bounds.getPos1().getBlockY() + ", "
                                + bounds.getPos1().getBlockZ() + ") ("
                                + bounds.getPos2().getBlockX() + ", " + bounds.getPos2().getBlockY() + ", "
                                + bounds.getPos2().getBlockZ() + ")."));
                    } else {
                        player.sendMessage(surviveTheNight.buildMessage("<prefix> &cPlease select a region first with /" + label + " arena wand."));
                    }
                } else {
                    player.sendMessage(surviveTheNight.buildMessage("<prefix> &cThe specified arena does not exist."));
                }
            } catch (Exception e) {
                player.sendMessage(surviveTheNight.buildMessage("<prefix> &cAn error has occurred while parsing the arena id."));
            }
        } else {
            player.sendMessage(surviveTheNight.buildMessage("<prefix> &cPlease provide an arena id."));
        }
    }
}