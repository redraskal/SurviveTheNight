package me.redraskal.survivethenight;

import lombok.Getter;
import me.redraskal.survivethenight.command.MainCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SurviveTheNight extends JavaPlugin {

    private File f_messageConfig;
    @Getter private YamlConfiguration messageConfig;

    public void onEnable() {
        this.getDataFolder().mkdirs();

        this.f_messageConfig = new File(this.getDataFolder(), "messages.yml");
        if(!f_messageConfig.exists()) {
            this.saveResource("messages.yml", false);
        }
        this.messageConfig = YamlConfiguration.loadConfiguration(f_messageConfig);

        this.getCommand("survive").setExecutor(new MainCommand(this));
    }

    public String buildMessage(String message) {
        String prefix = this.getMessageConfig().getString("prefix");
        return ChatColor.translateAlternateColorCodes('&',
                message.replace("<prefix>", prefix));
    }

    public String buildConfigMessage(String key) {
        return this.buildMessage(this.getMessageConfig().getString(key));
    }
}
