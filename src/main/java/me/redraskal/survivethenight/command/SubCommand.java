package me.redraskal.survivethenight.command;

import me.redraskal.survivethenight.SurviveTheNight;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public abstract class SubCommand {

    public abstract String name();

    public abstract String permission();

    public abstract void execute(Player player, SurviveTheNight surviveTheNight, String label, String[] args);
}