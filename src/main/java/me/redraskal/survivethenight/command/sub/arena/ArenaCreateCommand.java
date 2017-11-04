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
public class ArenaCreateCommand extends SubCommand {

    @Override
    public String name() {
        return "arena create";
    }

    @Override
    public String permission() {
        return "survive.arena.create";
    }

    @Override
    public void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args) {
        ArenaManager arenaManager = surviveTheNight.getArenaManager();
        int arenaid = 0;

        if(args.length > 2) {
            try {
                arenaid = Integer.parseInt(args[2]);
                if(arenaManager.createArena(arenaid)) {
                    player.sendMessage(surviveTheNight.buildMessage("&aArena " + arenaid + " has been successfully created."));
                } else {
                    player.sendMessage(surviveTheNight.buildMessage("&cThe specified arena id already exists."));
                }
            } catch (Exception e) {
                player.sendMessage(surviveTheNight.buildMessage("&cAn error has occurred while parsing the arena id."));
            }
        } else {
            for(Integer arena : arenaManager.getArenaMap().keySet()) {
                if(arena > arenaid) arenaid = arena;
            }
            arenaid++;
            if(arenaManager.createArena(arenaid)) {
                player.sendMessage(surviveTheNight.buildMessage("&aArena " + arenaid + " has been successfully created."));
            } else {
                player.sendMessage(surviveTheNight.buildMessage("&cThe specified arena id already exists."));
            }
        }
    }
}