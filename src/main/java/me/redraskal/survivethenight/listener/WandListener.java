package me.redraskal.survivethenight.listener;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.utils.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class WandListener implements Listener {

    @Getter private final SurviveTheNight surviveTheNight;
    private Map<UUID, Location> pos1 = new HashMap<>();
    private Map<UUID, Location> pos2 = new HashMap<>();

    public WandListener(SurviveTheNight surviveTheNight) {
        this.surviveTheNight = surviveTheNight;
    }

    public Cuboid getSelection(UUID uuid) {
        if(!pos1.containsKey(uuid) || !pos2.containsKey(uuid)) return null;
        return new Cuboid(pos1.get(uuid), pos2.get(uuid));
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getItem().getType() == Material.IRON_AXE
                && event.getItem().hasItemMeta()
                && event.getItem().getItemMeta().hasDisplayName()
                && event.getItem().getItemMeta().getDisplayName().equals(
                        ChatColor.translateAlternateColorCodes('&',
                "&6&l&nPosition Wand"))) {
            if(event.getPlayer().hasPermission("survive.arena.create")) {
                event.setCancelled(true);
                if(event.getClickedBlock() != null && event.getAction().toString().contains("BLOCK")) {
                    if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        pos1.put(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
                        event.getPlayer().sendMessage(surviveTheNight.buildMessage("&aPosition 1 has been set to ("
                                + pos1.get(event.getPlayer().getUniqueId()).getBlockX() + ", " + pos1.get(event.getPlayer().getUniqueId()).getBlockY() + ", "
                                + pos1.get(event.getPlayer().getUniqueId()).getBlockZ() + ")."));
                    } else {
                        pos2.put(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
                        event.getPlayer().sendMessage(surviveTheNight.buildMessage("&aPosition 2 has been set to ("
                                + pos2.get(event.getPlayer().getUniqueId()).getBlockX() + ", " + pos2.get(event.getPlayer().getUniqueId()).getBlockY() + ", "
                                + pos2.get(event.getPlayer().getUniqueId()).getBlockZ() + ")."));
                    }
                }
            } else {
                event.getPlayer().sendMessage(surviveTheNight.buildMessage("&cYou do not have permission to use this item."));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(pos1.containsKey(event.getPlayer().getUniqueId())) pos1.remove(event.getPlayer().getUniqueId());
        if(pos2.containsKey(event.getPlayer().getUniqueId())) pos2.remove(event.getPlayer().getUniqueId());
    }
}