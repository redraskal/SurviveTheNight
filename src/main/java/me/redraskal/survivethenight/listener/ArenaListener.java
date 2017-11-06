package me.redraskal.survivethenight.listener;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.Closet;
import me.redraskal.survivethenight.game.GameState;
import me.redraskal.survivethenight.game.PlayerRole;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.runnable.ChestRefillRunnable;
import me.redraskal.survivethenight.utils.Cuboid;
import me.redraskal.survivethenight.utils.InventoryUtils;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;
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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMoveLowPriority(PlayerMoveEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        if(this.getArena().getGameRunnable() == null) return;
        String areaName = "";

        for(Map.Entry<String, Cuboid> entry : this.getArena().getAreas().entrySet()) {
            if(entry.getValue().hasBlockInside(event.getTo().getBlock())) {
                areaName = entry.getKey();
                break;
            }
        }

        if(!areaName.isEmpty()) {
            try {
                NMSUtils.sendActionBar(event.getPlayer(), areaName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        if(this.getArena().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        } else {
            if(event.getItemDrop().getItemStack().getType() == Material.WATCH) {
                event.setCancelled(true);
            }
            if(this.getArena().getPlayerRoles().get(event.getPlayer()) == PlayerRole.KILLER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        if(this.getArena().getGameState() != GameState.INGAME) return;
        if(this.getArena().getPlayerRoles().get(event.getPlayer()) == PlayerRole.KILLER) {
            event.setCancelled(true);
            return;
        }
        event.getPlayer().getInventory().forEach(itemStack -> {
            if(this.getArenaManager().getSurviveTheNight().getCustomItemManager()
                    .isSameItem(itemStack, event.getItem().getItemStack())) {
                event.setCancelled(true);
                return;
            }
        });
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        if(event.getAction().toString().contains("BLOCK")) {
            event.setCancelled(true);
            if(this.getArena().getGameRunnable() != null) {
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(event.getClickedBlock().getType() == Material.CHEST) {
                        if(!event.getClickedBlock().hasMetadata("chest-refilling")) {
                            try {
                                NMSUtils.forceChestState(event.getClickedBlock(), true);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            }

                            new ChestRefillRunnable(this.getArena(), event.getClickedBlock());
                            event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(),
                                    Sound.ZOMBIE_REMEDY, 0.5f, 0.79f);
                            event.getClickedBlock().getWorld().spigot().playEffect(event.getClickedBlock().getLocation(),
                                    Effect.CLOUD, 0, 0, 1, 1, 1, 0,
                                    15, 16);

                            Item item = event.getClickedBlock().getWorld().dropItem(
                                    event.getClickedBlock().getRelative(BlockFace.UP).getLocation(),
                                    this.getArenaManager().getSurviveTheNight().getCustomItemManager().getFuelCanItemStack());
                            item.setPickupDelay(20);
                            item.setVelocity(new Vector(0.0D, 0.25D, 0.0D));

                            Item item2 = event.getClickedBlock().getWorld().dropItem(
                                    event.getClickedBlock().getRelative(BlockFace.UP).getLocation(),
                                    this.getArenaManager().getSurviveTheNight().getCustomItemManager().getBandageItemStack());
                            item2.setPickupDelay(20);
                            item2.setVelocity(new Vector(0.0D, 0.25D, 0.0D));

                            if(new Random().nextBoolean()) {
                                Item item3 = event.getClickedBlock().getWorld().dropItem(
                                        event.getClickedBlock().getRelative(BlockFace.UP).getLocation(),
                                        this.getArenaManager().getSurviveTheNight().getCustomItemManager().getLightItemStack());
                                item3.setPickupDelay(20);
                                item3.setVelocity(new Vector(0.0D, 0.25D, 0.0D));
                            }
                        }

                        return;
                    }

                    for(Closet closet : this.getArena().getGameRunnable().getClosets()) {
                        if(closet.getDoorBounds().hasBlockInside(event.getClickedBlock())) {
                            closet.hide(event.getPlayer());
                            return;
                        }
                    }

                    this.getArena().getGameRunnable().getGenerators().forEach(generator -> {
                        if(generator.getBlock().getLocation()
                                .equals(event.getClickedBlock().getLocation())) {
                            generator.fill(event.getPlayer());
                        }
                    });
                }
            }
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
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(!this.getArena().getPlayers().contains(event.getPlayer())) return;
        if(this.getArena().getGameRunnable() == null) return;
        if(this.getArena().getPlayerRoles().get(event.getPlayer()) == PlayerRole.KILLER) {
            try {
                NMSUtils.removeEntityClientSide(event.getPlayer(),
                        this.getArena().getGameRunnable().getIronGolem());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof IronGolem
                && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if(event.getEntity().hasMetadata("killer")) {
                event.setCancelled(true);
            }
        }
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
                            if((entity.getHealth()-event.getDamage()) <= 0D) {
                                event.setDamage(0);
                                InventoryUtils.resetPlayer(entity);
                                entity.setGameMode(GameMode.SPECTATOR);

                                //TODO: Player has died message
                            }
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
                            if((killer.getHealth()-event.getDamage()) <= 0D) {
                                event.setDamage(0);
                                InventoryUtils.resetPlayer(killer);
                                killer.setGameMode(GameMode.SPECTATOR);

                                //TODO: Killer has died message
                            } else {
                                killer.damage(damage, damager);
                            }
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