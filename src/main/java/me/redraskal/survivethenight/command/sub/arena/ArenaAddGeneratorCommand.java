package me.redraskal.survivethenight.command.sub.arena;

import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.SubCommand;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.utils.LocationUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaAddGeneratorCommand extends SubCommand {

    @Override
    public String name() {
        return "arena addgenerator";
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
                    List<Block> generatorBlocks = new ArrayList<>();
                    if(arena.getGenerators() != null) generatorBlocks = arena.getGenerators();

                    Block generatorBlock = LocationUtils.getTargetBlock(player, 6);
                    if(generatorBlock != null) {
                        generatorBlocks.add(generatorBlock);
                        arena.setGenerators(generatorBlocks);
                        arenaManager.saveArena(id);
                        player.sendMessage(surviveTheNight.buildMessage("<prefix> &aGenerator block has been saved."));
                    } else {
                        player.sendMessage(surviveTheNight.buildMessage("<prefix> &cTarget block not found."));
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