package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.manager.ArenaManager;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaSetMaxPlayersCommand extends SubCommand {

    @Override
    public String name() {
        return "arena setmaxplayers";
    }

    @Override
    public String permission() {
        return "survive.arena.create";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ArenaManager arenaManager = surviveTheNight.getArenaManager();

        if(args.length > 3) {
            try {
                int id = Integer.parseInt(args[2]);
                int argument = Integer.parseInt(args[3]);
                if(arenaManager.getArenaMap().containsKey(id)) {
                    Arena arena = arenaManager.getArenaMap().get(id);
                    arena.setMaxPlayers(argument);
                    arenaManager.saveArena(id);
                    player.sendMessage(surviveTheNight.buildMessage("<prefix> &aMax players has been set to " + argument + "."));
                } else {
                    player.sendMessage(surviveTheNight.buildMessage("<prefix> &cThe specified arena does not exist."));
                }
            } catch (Exception e) {
                player.sendMessage(surviveTheNight.buildMessage("<prefix> &cAn error has occurred while parsing the command arguments."));
            }
        } else {
            player.sendMessage(surviveTheNight.buildMessage("<prefix> &cPlease provide an arena id and a number."));
        }
    }
}