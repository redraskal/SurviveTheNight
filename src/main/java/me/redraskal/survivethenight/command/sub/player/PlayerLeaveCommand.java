package me.redraskal.survivethenight.command.sub.player;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
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
        player.sendMessage(surviveTheNight.buildMessage("TODO"));
    }
}