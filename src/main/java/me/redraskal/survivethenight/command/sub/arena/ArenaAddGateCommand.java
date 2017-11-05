package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.utils.Cuboid;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaAddGateCommand extends SubCommand {

    @Override
    public String name() {
        return "arena addgate";
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
                    Cuboid region = surviveTheNight.getWandListener().getSelection(player.getUniqueId());

                    if(region != null) {
                        List<Cuboid> gates = new ArrayList<>();
                        if(arena.getGates() != null) gates = arena.getGates();
                        gates.add(region);
                        arena.setGates(gates);
                        arenaManager.saveArena(id);
                        player.sendMessage(surviveTheNight.buildMessage("<prefix> &aGate has been saved."));
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
            player.sendMessage(surviveTheNight.buildMessage("<prefix> &cPlease provide an arena id and an area name."));
        }
    }
}