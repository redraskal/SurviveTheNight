package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import me.redraskal.survivethenight.manager.ArenaManager;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaDeleteCommand extends SubCommand {

    @Override
    public String name() {
        return "arena delete";
    }

    @Override
    public String permission() {
        return "survive.arena.delete";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ArenaManager arenaManager = surviveTheNight.getArenaManager();
        int arenaid = 0;

        if(args.length > 2) {
            try {
                arenaid = Integer.parseInt(args[2]);
                if(arenaManager.deleteArena(arenaid)) {
                    player.sendMessage(surviveTheNight.buildMessage("&aArena has been successfully deleted."));
                } else {
                    player.sendMessage(surviveTheNight.buildMessage("&cThe specified arena id does not exist."));
                }
            } catch (Exception e) {
                player.sendMessage(surviveTheNight.buildMessage("&cAn error has occurred while parsing the arena id."));
            }
        } else {
            player.sendMessage(surviveTheNight.buildMessage("&cPlease provide an arena id."));
        }
    }
}