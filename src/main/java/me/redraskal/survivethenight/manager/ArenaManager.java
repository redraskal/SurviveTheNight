package me.redraskal.survivethenight.manager;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

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
            if(!arenaConfig.getString("" + id + ".lobbyPosition").isEmpty())
                arena.setLobbyPosition(ConfigUtils.decodeLocation(arenaConfig.getString("" + id + ".lobbyPosition")));
            if(!arenaConfig.getString("" + id + ".mainLobbyPosition").isEmpty())
                arena.setMainLobbyPosition(ConfigUtils.decodeLocation(arenaConfig.getString("" + id + ".mainLobbyPosition")));
            if(!arenaConfig.getString("" + id + ".spawnPositions").isEmpty())
                arena.setSpawnPositions(ConfigUtils.decodeLocationList(arenaConfig.getStringList("" + id + ".spawnPositions")));

            arena.setMinPlayers(arenaConfig.getInt("" + id + ".minPlayers"));
            arena.setMaxPlayers(arenaConfig.getInt("" + id + ".maxPlayers"));
            arena.setGeneratorsNeeded(arenaConfig.getInt("" + id + ".generatorsNeeded"));

            if(!arenaConfig.getString("" + id + ".generators").isEmpty())
                arena.setGenerators(ConfigUtils.decodeBlockList(arenaConfig.getStringList("" + id + ".generators")));
            if(arenaConfig.get("" + id + ".bounds") != null)
                arena.setBounds(ConfigUtils.decodeCuboid(arenaConfig.getString("" + id + ".bounds")));
            if(!arenaConfig.getString("" + id + ".doors").isEmpty())
                arena.setDoors(ConfigUtils.decodeCuboidMap(arenaConfig.getStringList("" + id + ".doors")));
            if(!arenaConfig.getString("" + id + ".areas").isEmpty())
                arena.setAreas(ConfigUtils.decodeAreaMap(arenaConfig.getStringList("" + id + ".areas")));
            if(!arenaConfig.getString("" + id + ".gates").isEmpty())
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

    public Arena getArena(Player player) {
        for(Arena arena : arenaMap.values()) {
            if(arena.getPlayers().contains(player)) return arena;
        }
        return null;
    }

    public boolean deleteArena(int id) {
        if(!this.getArenaMap().containsKey(id)) return false;

        YamlConfiguration arenaConfig = this.getSurviveTheNight().getArenaConfig();
        arenaConfig.set("" + id, null);

        this.getSurviveTheNight().saveArenaConfig();
        HandlerList.unregisterAll(this.getArenaMap().get(id).getArenaListener());
        arenaMap.remove(id);
        return true;
    }

    public boolean saveArena(int id) {
        if(!this.getArenaMap().containsKey(id)) return false;

        Arena arena = arenaMap.get(id);
        YamlConfiguration arenaConfig = this.getSurviveTheNight().getArenaConfig();

        if(arena.getLobbyPosition() != null) arenaConfig.set("" + id + ".lobbyPosition", ConfigUtils.encodeLocation(arena.getLobbyPosition()));
        if(arena.getMainLobbyPosition() != null) arenaConfig.set("" + id + ".mainLobbyPosition", ConfigUtils.encodeLocation(arena.getMainLobbyPosition()));
        if(arena.getSpawnPositions() != null) arenaConfig.set("" + id + ".spawnPositions", ConfigUtils.encodeLocationList(arena.getSpawnPositions()));

        arenaConfig.set("" + id + ".minPlayers", arena.getMinPlayers());
        arenaConfig.set("" + id + ".maxPlayers", arena.getMaxPlayers());
        arenaConfig.set("" + id + ".generatorsNeeded", arena.getGeneratorsNeeded());

        if(arena.getGenerators() != null) arenaConfig.set("" + id + ".generators", ConfigUtils.encodeBlockList(arena.getGenerators()));
        if(arena.getBounds() != null) arenaConfig.set("" + id + ".bounds", ConfigUtils.encodeCuboid(arena.getBounds()));
        if(arena.getDoors() != null) arenaConfig.set("" + id + ".doors", ConfigUtils.encodeCuboidMap(arena.getDoors()));
        if(arena.getAreas() != null) arenaConfig.set("" + id + ".areas", ConfigUtils.encodeAreaMap(arena.getAreas()));
        if(arena.getGates() != null) arenaConfig.set("" + id + ".gates", ConfigUtils.encodeCuboidList(arena.getGates()));

        this.getSurviveTheNight().saveArenaConfig();
        return true;
    }
}