package me.redraskal.survivethenight.game;

import lombok.Getter;
import lombok.Setter;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Arena {

    @Getter private final ArenaManager arenaManager;
    @Getter private final int arenaid;

    @Getter @Setter private Location lobbyPosition;
    @Getter @Setter private Location mainLobbyPosition;
    @Getter @Setter private List<Location> spawnPositions;

    @Getter @Setter private int minPlayers = 2;
    @Getter @Setter private int maxPlayers = 17;
    @Getter @Setter private int generatorsNeeded = 6;

    @Getter @Setter private List<Block> generators;
    @Getter @Setter private Cuboid bounds;
    @Getter @Setter private List<Cuboid> doors;
    @Getter @Setter private Map<String, Cuboid> areas = new HashMap<>();
    @Getter @Setter private List<Cuboid> gates;

    public Arena(ArenaManager arenaManager, int arenaid) {
        this.arenaManager = arenaManager;
        this.arenaid = arenaid;
    }

    public boolean canPlay() {
        return (lobbyPosition != null && mainLobbyPosition != null
                && spawnPositions != null && spawnPositions.size() > 0
                && minPlayers > 0 && maxPlayers > 0 && generatorsNeeded > 0
                && generators != null && generators.size() > 0
                && bounds != null && doors != null && doors.size() > 0
                && areas != null && gates != null && gates.size() > 0);
    }
}