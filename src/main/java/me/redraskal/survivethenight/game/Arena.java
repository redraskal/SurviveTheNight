package me.redraskal.survivethenight.game;

import lombok.Getter;
import lombok.Setter;
import me.redraskal.survivethenight.listener.ArenaListener;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

    @Getter private ArenaListener arenaListener;
    @Getter private List<Player> players = new ArrayList<>();

    @Getter @Setter private Location lobbyPosition;
    @Getter @Setter private Location mainLobbyPosition;
    @Getter @Setter private List<Location> spawnPositions = new ArrayList<>();

    @Getter @Setter private int minPlayers = 2;
    @Getter @Setter private int maxPlayers = 17;
    @Getter @Setter private int generatorsNeeded = 6;

    @Getter @Setter private List<Block> generators = new ArrayList<>();
    @Getter @Setter private Cuboid bounds;
    @Getter @Setter private Map<Cuboid, Location> doors = new HashMap<>();
    @Getter @Setter private Map<String, Cuboid> areas = new HashMap<>();
    @Getter @Setter private List<Cuboid> gates = new ArrayList<>();

    public Arena(ArenaManager arenaManager, int arenaid) {
        this.arenaManager = arenaManager;
        this.arenaid = arenaid;
        this.arenaListener = new ArenaListener(this, this.getArenaManager());
        this.getArenaManager().getSurviveTheNight().getServer().getPluginManager()
                .registerEvents(this.getArenaListener(), this.getArenaManager().getSurviveTheNight());
    }

    public boolean addPlayer(Player player) {
        if(this.getPlayers().contains(player)) {
            player.sendMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &cYou are already in this arena."));
            return false;
        }
        if(!canPlay()) {
            player.sendMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &cThis arena has not been setup yet."));
            return false;
        }
        this.getPlayers().add(player);
        player.teleport(this.getLobbyPosition());
        //TODO: Some join message thing, and a separate chat channel
        return true;
    }

    public boolean removePlayer(Player player) {
        if(!this.getPlayers().contains(player)) return false;
        this.getPlayers().remove(player);
        player.teleport(this.getMainLobbyPosition());
        //TODO: Some leave message thing, leave separate chat channel
        return true;
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