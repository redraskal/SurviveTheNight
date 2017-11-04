package me.redraskal.survivethenight.manager;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaManager {

    @Getter private final SurviveTheNight surviveTheNight;
    @Getter private final Map<Integer, Arena> arenaMap = new HashMap<>();

    public ArenaManager(SurviveTheNight surviveTheNight) {
        this.surviveTheNight = surviveTheNight;
        this.reloadArenas();
    }

    public void reloadArenas() {
        arenaMap.clear();
        YamlConfiguration arenaConfig = this.getSurviveTheNight().getArenaConfig();
        for(String key : arenaConfig.getKeys(false)) {
            int id = Integer.parseInt(key);

            Arena arena = new Arena(this, id);
            arena.setLobbyPosition(ConfigUtils.decodeLocation(arenaConfig.getString("" + id + ".lobbyPosition")));
            arena.setMainLobbyPosition(ConfigUtils.decodeLocation(arenaConfig.getString("" + id + ".mainLobbyPosition")));
            arena.setSpawnPositions(ConfigUtils.decodeLocationList(arenaConfig.getStringList("" + id + ".spawnPositions")));

            arena.setMinPlayers(arenaConfig.getInt("" + id + ".minPlayers"));
            arena.setMaxPlayers(arenaConfig.getInt("" + id + ".maxPlayers"));
            arena.setGeneratorsNeeded(arenaConfig.getInt("" + id + ".generatorsNeeded"));

            arena.setGenerators(ConfigUtils.decodeBlockList(arenaConfig.getStringList("" + id + ".generators")));
            arena.setBounds(ConfigUtils.decodeCuboid(arenaConfig.getString("" + id + ".bounds")));
            arena.setDoors(ConfigUtils.decodeCuboidList(arenaConfig.getStringList("" + id + ".doors")));
            arena.setAreas(ConfigUtils.decodeAreaMap(arenaConfig.getStringList("" + id + ".areas")));
            arena.setGates(ConfigUtils.decodeCuboidList(arenaConfig.getStringList("" + id + ".gates")));

            arenaMap.put(id, arena);
        }
    }

    public boolean createArena(int id) {
        if(this.getArenaMap().containsKey(id)) return false;
        arenaMap.put(id, new Arena(this, id));
        this.saveArena(id);
        return true;
    }

    public boolean deleteArena(int id) {
        if(!this.getArenaMap().containsKey(id)) return false;

        YamlConfiguration arenaConfig = this.getSurviveTheNight().getArenaConfig();
        arenaConfig.set("" + id, null);

        this.getSurviveTheNight().saveArenaConfig();
        arenaMap.remove(id);
        return true;
    }

    public boolean saveArena(int id) {
        if(!this.getArenaMap().containsKey(id)) return false;

        Arena arena = arenaMap.get(id);
        YamlConfiguration arenaConfig = this.getSurviveTheNight().getArenaConfig();

        arenaConfig.set("" + id + ".lobbyPosition", ConfigUtils.encodeLocation(arena.getLobbyPosition()));
        arenaConfig.set("" + id + ".mainLobbyPosition", ConfigUtils.encodeLocation(arena.getMainLobbyPosition()));
        arenaConfig.set("" + id + ".spawnPositions", ConfigUtils.encodeLocationList(arena.getSpawnPositions()));

        arenaConfig.set("" + id + ".minPlayers", arena.getMinPlayers());
        arenaConfig.set("" + id + ".maxPlayers", arena.getMaxPlayers());
        arenaConfig.set("" + id + ".generatorsNeeded", arena.getGeneratorsNeeded());

        arenaConfig.set("" + id + ".generators", ConfigUtils.encodeBlockList(arena.getGenerators()));
        arenaConfig.set("" + id + ".bounds", ConfigUtils.encodeCuboid(arena.getBounds()));
        arenaConfig.set("" + id + ".doors", ConfigUtils.encodeCuboidList(arena.getDoors()));
        arenaConfig.set("" + id + ".areas", ConfigUtils.encodeAreaMap(arena.getAreas()));
        arenaConfig.set("" + id + ".gates", ConfigUtils.encodeCuboidList(arena.getGates()));

        this.getSurviveTheNight().saveArenaConfig();
        return true;
    }
}