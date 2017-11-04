package me.redraskal.survivethenight.command;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.command.sub.ArenaHelpCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class MainCommand implements CommandExecutor {

    @Getter private final SurviveTheNight surviveTheNight;
    private final List<SubCommand> subCommands;

    public MainCommand(SurviveTheNight surviveTheNight) {
        this.surviveTheNight = surviveTheNight;

        this.subCommands = new ArrayList<>();
        this.subCommands.add(new ArenaHelpCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return false;
        }
        Player player = (Player) sender;
        if(args.length > 0) {
            for(SubCommand subCommand : subCommands) {
                String[] subArgs = subCommand.name().split(" ");
                int check = subArgs.length;
                if(args.length < check) continue;
                int correct = 0;
                for(int i=0; i<args.length; i++) {
                    if(args[i].equals(subArgs[i])) correct++;
                    if(correct == check) {
                        if(!subCommand.permission().isEmpty()
                                && !player.hasPermission(subCommand.permission())) {
                            player.sendMessage(this.getSurviveTheNight().buildConfigMessage("no-permission"));
                            return false;
                        }
                        subCommand.execute(player, surviveTheNight, label, args);
                        return false;
                    }
                }
            }
            player.sendMessage(this.getSurviveTheNight().buildConfigMessage("command-not-found")
                    .replace("<label>", label));
        } else {
            if(player.hasPermission("survive.player.help")) {
                player.sendMessage(this.getSurviveTheNight().buildMessage("<prefix> Player commands:"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7/" + label + " &fjoin &b(arena id)&3: Joins an arena"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7/" + label + " &fleave&3: Leaves the arena"));
            } else {
                player.sendMessage(this.getSurviveTheNight().buildConfigMessage("no-permission"));
            }
        }
        return false;
    }
}