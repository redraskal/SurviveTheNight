package me.redraskal.survivethenight.manager;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaManager {

    @Getter private final SurviveTheNight surviveTheNight;

    public ArenaManager(SurviveTheNight surviveTheNight) {
        this.surviveTheNight = surviveTheNight;
    }

    public boolean createArena(int id) {
        YamlConfiguration arenaConfig = this.getSurviveTheNight().getArenaConfig();
        //TODO
    }
}