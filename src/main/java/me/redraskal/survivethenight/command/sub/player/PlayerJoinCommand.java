package me.redraskal.survivethenight.command.sub.player;

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
public class PlayerJoinCommand extends SubCommand {

    @Override
    public String name() {
        return "join";
    }

    @Override
    public String permission() {
        return "survive.player.join";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ArenaManager arenaManager = surviveTheNight.getArenaManager();
        Arena arena = arenaManager.getArena(player);

        if(arena != null) {
            player.sendMessage(surviveTheNight.buildMessage("<prefix> &cYou are already in an arena."));
        } else {
            if(args.length > 1) {
                try {
                    int id = Integer.parseInt(args[1]);
                    if(arenaManager.getArenaMap().containsKey(id)) {
                        arenaManager.getArenaMap().get(id).addPlayer(player);
                    } else {
                        player.sendMessage(surviveTheNight.buildMessage("<prefix> &cThe specified arena does not exist."));
                    }
                } catch (Exception e) {
                    player.sendMessage(surviveTheNight.buildMessage("<prefix> &cAn error has occurred while parsing the arena id."));
                }
            } else {
                //TODO: Find arena to join
                player.sendMessage(surviveTheNight.buildMessage("<prefix> &cWe could not find you an available arena."));
            }
        }
    }
}