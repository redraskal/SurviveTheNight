package me.redraskal.survivethenight;

import lombok.Getter;
import me.redraskal.survivethenight.command.MainCommand;
import me.redraskal.survivethenight.manager.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SurviveTheNight extends JavaPlugin {

    private File f_messageConfig;
    @Getter private YamlConfiguration messageConfig;

    private File f_arenaConfig;
    @Getter private YamlConfiguration arenaConfig;

    @Getter private ArenaManager arenaManager;

    public void onEnable() {
        this.getDataFolder().mkdirs();

        this.f_messageConfig = new File(this.getDataFolder(), "messages.yml");
        if(!f_messageConfig.exists()) {
            this.saveResource("messages.yml", false);
        }
        this.messageConfig = YamlConfiguration.loadConfiguration(f_messageConfig);

        this.f_arenaConfig = new File(this.getDataFolder(), "arenas.yml");
        if(!f_arenaConfig.exists()) {
            try {
                f_arenaConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        this.arenaConfig = YamlConfiguration.loadConfiguration(f_arenaConfig);

        this.arenaManager = new ArenaManager(this);

        this.getCommand("survive").setExecutor(new MainCommand(this));
    }

    public String buildMessage(String message) {
        String prefix = this.getMessageConfig().getString("prefix");
        return ChatColor.translateAlternateColorCodes('&',
                message.replace("<prefix>", prefix)
                .replace("<pipe>", "⏐")
                .replace("<doublepipe>", "▎"));
    }

    public String buildConfigMessage(String key) {
        return this.buildMessage(this.getMessageConfig().getString(key));
    }

    public void saveArenaConfig() {
        try {
            this.getArenaConfig().save(this.f_arenaConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
