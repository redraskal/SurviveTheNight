package me.redraskal.survivethenight.listener;

import lombok.Getter;
import me.redraskal.survivethenight.event.ArenaEndGameEvent;
import me.redraskal.survivethenight.event.ArenaPlayerJoinEvent;
import me.redraskal.survivethenight.event.ArenaPlayerQuitEvent;
import me.redraskal.survivethenight.event.ArenaStartGameEvent;
import me.redraskal.survivethenight.game.SignInfo;
import me.redraskal.survivethenight.manager.SignManager;
import me.redraskal.survivethenight.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SignListener implements Listener {

    @Getter private final SignManager signManager;
    private Map<Player, Long> signCooldowns = new HashMap<>();

    public SignListener(SignManager signManager) {
        this.signManager = signManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(signCooldowns.containsKey(event.getPlayer())) signCooldowns.remove(event.getPlayer());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(!event.getPlayer().hasPermission("survive.sign.create")) return;
        if(!event.getLine(0).equalsIgnoreCase("[survive]")) return;

        Sign sign = (Sign) event.getBlock().getState();

        if(event.getLine(1).equalsIgnoreCase("leave")) {
            event.setCancelled(true);
            this.getSignManager().createSign(
                    new SignInfo(sign, -1, SignInfo.SignType.LEAVE));
            event.getPlayer().sendMessage(this.getSignManager().getSurviveTheNight()
                    .buildMessage("&aSign has been created."));
        } else {
            try {
                int id = Integer.parseInt(event.getLine(1));
                if(!this.getSignManager().getSurviveTheNight()
                        .getArenaManager().getArenaMap().containsKey(id)) {
                    event.setLine(1, ChatColor.RED + "Arena not found.");
                } else {
                    event.setCancelled(true);
                    this.getSignManager().createSign(
                            new SignInfo(sign, id, SignInfo.SignType.JOIN));
                    event.getPlayer().sendMessage(this.getSignManager().getSurviveTheNight()
                            .buildMessage("&aSign has been created."));
                }
            } catch (Exception e) {
                event.setLine(1, ChatColor.RED + "Invalid arena id.");
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!event.getClickedBlock().getType().toString().contains("SIGN")) return;
        if(signCooldowns.containsKey(event.getPlayer())) {
            if((System.currentTimeMillis()
                    - signCooldowns.get(event.getPlayer())) >= 1000L) {
                signCooldowns.remove(event.getPlayer());
            } else {
                return;
            }
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        for(SignInfo signInfo : this.getSignManager().getSignInfoList()) {
            if(LocationUtils.isSameLocation(sign.getLocation(), signInfo.getSign().getLocation())) {
                event.setCancelled(true);
                signCooldowns.put(event.getPlayer(), System.currentTimeMillis());
                if(signInfo.getSignType() == SignInfo.SignType.JOIN) {
                    Bukkit.getServer().dispatchCommand(event.getPlayer(),
                            "survive join " + signInfo.getArenaID());
                } else {
                    Bukkit.getServer().dispatchCommand(event.getPlayer(),
                            "survive leave");
                }
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().toString().contains("SIGN")) return;
        Sign sign = (Sign) event.getBlock().getState();
        for(SignInfo signInfo : this.getSignManager().getSignInfoList()) {
            if(LocationUtils.isSameLocation(sign.getLocation(), signInfo.getSign().getLocation())) {
                if(!event.getPlayer().hasPermission("survive.sign.delete")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(this.getSignManager().getSurviveTheNight()
                            .buildMessage("&cYou do not have permission to delete this sign."));
                } else {
                    this.getSignManager().deleteSign(signInfo);
                    event.getPlayer().sendMessage(this.getSignManager().getSurviveTheNight()
                            .buildMessage("&aSign has been deleted."));
                }
                return;
            }
        }
    }

    @EventHandler
    public void onArenaPlayerJoin(ArenaPlayerJoinEvent event) {
        this.getSignManager().updateSigns(event.getArena());
    }

    @EventHandler
    public void onArenaPlayerQuit(ArenaPlayerQuitEvent event) {
        this.getSignManager().updateSigns(event.getArena());
    }

    @EventHandler
    public void onArenaStartGame(ArenaStartGameEvent event) {
        this.getSignManager().updateSigns(event.getArena());
    }

    @EventHandler
    public void onArenaEndGame(ArenaEndGameEvent event) {
        this.getSignManager().updateSigns(event.getArena());
    }
}