package me.redraskal.survivethenight.listener;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.GameState;
import me.redraskal.survivethenight.game.PlayerRole;
import me.redraskal.survivethenight.manager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaListener implements Listener {

    @Getter private final Arena arena;
    @Getter private final ArenaManager arenaManager;

    public ArenaListener(Arena arena, ArenaManager arenaManager) {
        this.arena = arena;
        this.arenaManager = arenaManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        event.setCancelled(true);
        this.getArena().broadcastMessage(this.getArenaManager().getSurviveTheNight()
                .buildMessage("&9" + event.getPlayer().getName() + " &8» &f" + event.getMessage()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        this.getArena().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        if(this.getArena().getGameRunnable() == null) return;
        if(this.getArena().getPlayerRoles().get(event.getPlayer()) == PlayerRole.KILLER) {
            if(this.getArena().getGameRunnable().getIronGolem().isDead()) return;
            this.getArena().getGameRunnable().getIronGolem().teleport(event.getTo());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!this.getArena().getPlayers().contains((Player) event.getEntity())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(!this.getArena().getPlayers().contains(player)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.WITHER
                || event.getCause() == EntityDamageEvent.DamageCause.POISON
                || event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            event.setCancelled(true);
            return;
        }
        if(this.getArena().getGameState() == GameState.INGAME) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        if(!this.getArena().getPlayers().contains(damager)) return;
        if(this.getArena().getGameState() == GameState.INGAME) {
            if(event.getEntity() instanceof Player) {
                Player entity = (Player) event.getEntity();
                if(this.getArena().getPlayers().contains(entity)) {
                    if(this.getArena().getPlayerRoles().get(entity) == PlayerRole.KILLER) {
                        event.setCancelled(true);
                    } else {
                        if(this.getArena().getPlayerRoles().get(damager) == PlayerRole.SURVIVOR) {
                            event.setCancelled(true);
                        } else {
                            //TODO: Check if player will die with this hit
                        }
                    }
                }
            } else {
                if(event.getEntity() instanceof IronGolem) {
                    final double damage = event.getDamage();
                    event.setDamage(0);
                    if(event.getEntity().hasMetadata("killer")) {
                        Player killer = Bukkit.getPlayer(UUID.fromString(event.getEntity()
                                .getMetadata("killer").get(0).asString()));
                        if(killer != null) {
                            //TODO: Check if player will die with this hit
                            killer.damage(damage, damager);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
            return;
        }
        event.setCancelled(true);
    }
}