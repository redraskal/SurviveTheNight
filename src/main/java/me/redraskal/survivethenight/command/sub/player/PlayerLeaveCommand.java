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
public class PlayerLeaveCommand extends SubCommand {

    @Override
    public String name() {
        return "leave";
    }

    @Override
    public String permission() {
        return "survive.player.leave";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ArenaManager arenaManager = surviveTheNight.getArenaManager();
        Arena arena = arenaManager.getArena(player);

        if(arena != null) {
            if(!arena.removePlayer(player)) {
                player.sendMessage(surviveTheNight.buildMessage("<prefix> &cAn error has occurred while removing you from the arena."));
            }
        } else {
            player.sendMessage(surviveTheNight.buildMessage("<prefix> &cYou are not in an arena."));
        }
    }
}