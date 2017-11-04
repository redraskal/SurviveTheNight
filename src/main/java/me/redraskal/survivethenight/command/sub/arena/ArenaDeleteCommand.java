package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
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
        player.sendMessage(surviveTheNight.buildMessage("TODO"));
    }
}