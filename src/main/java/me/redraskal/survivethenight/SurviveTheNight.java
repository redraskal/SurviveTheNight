package me.redraskal.survivethenight;

import lombok.Getter;
import me.redraskal.survivethenight.command.MainCommand;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.listener.WandListener;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.manager.BossBarManager;
import me.redraskal.survivethenight.manager.CustomItemManager;
import me.redraskal.survivethenight.manager.SignManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
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

    private File f_signConfig;
    @Getter private YamlConfiguration signConfig;

    @Getter private BossBarManager bossBarManager;
    @Getter private ArenaManager arenaManager;
    @Getter private CustomItemManager customItemManager;
    @Getter private SignManager signManager;
    @Getter private WandListener wandListener;

    @Getter private NPCRegistry registry;

    public void onEnable() {
        this.getDataFolder().mkdirs();
        this.saveDefaultConfig();

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

        this.f_signConfig = new File(this.getDataFolder(), "signs.yml");
        if(!f_signConfig.exists()) {
            try {
                f_signConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        this.signConfig = YamlConfiguration.loadConfiguration(f_signConfig);

        this.registry = CitizensAPI.getNPCRegistry();

        try {
            this.bossBarManager = new BossBarManager(this);
        } catch (Exception e) {
            e.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.arenaManager = new ArenaManager(this);
        this.customItemManager = new CustomItemManager(this);
        this.signManager = new SignManager(this);
        this.wandListener = new WandListener(this);

        this.getServer().getPluginManager().registerEvents(wandListener, this);

        this.getCommand("survive").setExecutor(new MainCommand(this));
    }

    public void onDisable() {
        this.getRegistry().deregisterAll();
        Bukkit.getOnlinePlayers().forEach(player -> {
            Arena arena = this.getArenaManager().getArena(player);
            if(arena != null) arena.removePlayer(player);
        });
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

    public void saveSignConfig() {
        try {
            this.getSignConfig().save(this.f_signConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
