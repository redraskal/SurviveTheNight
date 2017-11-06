package me.redraskal.survivethenight.runnable;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.GameState;
import me.redraskal.survivethenight.game.PlayerRole;
import me.redraskal.survivethenight.utils.LocationUtils;
import me.redraskal.survivethenight.utils.NBTUtils;
import me.redraskal.survivethenight.utils.NMSUtils;
import me.redraskal.survivethenight.utils.Sounds;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GamePostStartRunnable extends BukkitRunnable {

    @Getter private final Arena arena;
    private int ticks = 0;
    private int totalTicks = 20*10;

    @Getter private Map<Player, ArmorStand> armorStandMap = new HashMap<>();
    @Getter private Map<Player, NPC> npcMap = new HashMap<>();
    @Getter private IronGolem ironGolem;

    public GamePostStartRunnable(Arena arena) {
        this.arena = arena;
        this.getArena().getBounds().getWorld().setTime(11616L);
        this.getArena().getBounds().getWorld().setStorm(false);
        arena.getPlayerRoles().put(arena.getPlayers()
                .get(new Random().nextInt(arena.getPlayers().size())), PlayerRole.KILLER);
        Collections.shuffle(arena.getSpawnPositions());

        int currentSpawnpoint = -1;

        for(Player player : arena.getPlayers()) {
            currentSpawnpoint++;
            if(currentSpawnpoint >= arena.getSpawnPositions().size()) currentSpawnpoint = 0;
            final Location spawnpoint = LocationUtils.faceLocation(arena.getSpawnPositions().get(currentSpawnpoint).clone().add(10D, 1D, 0),
                    arena.getSpawnPositions().get(currentSpawnpoint));
            player.teleport(spawnpoint);

            if(arena.getPlayerRoles().get(player) == PlayerRole.SURVIVOR) {
                player.setHealthScale(4D);
                player.setFoodLevel(0);

                NPC npc = arena.getArenaManager().getSurviveTheNight().getRegistry()
                        .createNPC(EntityType.PLAYER, player.getName());
                npc.spawn(arena.getSpawnPositions().get(currentSpawnpoint).clone());
                npc.faceLocation(arena.getSpawnPositions().get(currentSpawnpoint).clone().subtract(3D, 0, 0));
                npcMap.put(player, npc);
            } else {
                player.setHealthScale(2D);
                player.setFoodLevel(7);

                ironGolem = spawnpoint.getWorld().spawn(arena.getSpawnPositions().get(currentSpawnpoint).clone(), IronGolem.class);
                ironGolem.setCanPickupItems(false);
                ironGolem.setMetadata("killer", new FixedMetadataValue(this.getArena().getArenaManager().getSurviveTheNight(), player.getUniqueId().toString()));
                try {
                    NBTUtils.setInt(ironGolem, "NoAI", 1);
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
            }

            ArmorStand armorStand = spawnpoint.getWorld().spawn(spawnpoint, ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setBasePlate(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);

            player.playSound(player.getLocation(), Sounds.AMBIENCE_CAVE.spigot(), 1.6f, 0.49f);

            player.setGameMode(GameMode.SPECTATOR);
            try {
                NMSUtils.sendCameraPacket(player, armorStand.getEntityId(), arena.getArenaManager().getSurviveTheNight());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            armorStandMap.put(player, armorStand);
        }

        this.runTaskTimer(arena.getArenaManager().getSurviveTheNight(), 0, 1L);
    }

    @Override
    public void run() {
        if(this.getArena().getGameState() != GameState.INGAME) {
            this.cancel();
            return;
        }
        if(ticks >= totalTicks) {
            this.cancel();

            for(Map.Entry<Player, ArmorStand> entry : armorStandMap.entrySet()) {
                entry.getKey().setGameMode(GameMode.SURVIVAL);
                try {
                    if(this.getArena().getPlayerRoles().get(entry.getKey()) == PlayerRole.SURVIVOR) {
                        entry.getKey().teleport(npcMap.get(entry.getKey()).getStoredLocation());
                        npcMap.get(entry.getKey()).despawn();
                        npcMap.remove(entry.getKey());

                        entry.getKey().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 0, false, false), true);
                        entry.getKey().addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 0, false, false), true);
                        entry.getKey().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false), true);
                    } else {
                        entry.getKey().teleport(ironGolem.getLocation());

                        entry.getKey().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false), true);
                        entry.getKey().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), true);
                        entry.getKey().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0, false, false), true);

                        try {
                            NMSUtils.removeEntityClientSide(entry.getKey(), ironGolem);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    NMSUtils.sendCameraPacket(entry.getKey(), entry.getKey().getEntityId(),
                            this.getArena().getArenaManager().getSurviveTheNight());
                    this.getArena().setGameRunnable(new GameRunnable(this.getArena(), this.ironGolem));
                    this.getArena().setGamePostStartRunnable(null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                entry.getValue().remove();
            }

            return;
        }

        ticks++;
        this.getArena().getBounds().getWorld().setTime(this.getArena().getBounds().getWorld().getTime()+32L);
        this.armorStandMap.values().forEach(armorStand -> armorStand
                .teleport(armorStand.getLocation().clone().subtract(0.05D, 0, 0)));

        if(ticks == 20) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("A Killer is on the loose");
            String message2 = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("People have entered your property");
            this.getArena().getPlayers().forEach(player -> {
                try {
                    NMSUtils.clearTitle(player);
                    if(this.getArena().getPlayerRoles().get(player) == PlayerRole.SURVIVOR) {
                        NMSUtils.sendTitleSubtitle(player, "", message, 20, 40, 20);
                    } else {
                        NMSUtils.sendTitleSubtitle(player, "", message2, 20, 40, 20);
                    }
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
            });
        }

        if(ticks == 80) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("&eUse your &e&lSound &eand &e&lMap &eto help you...");
            String message2 = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("&c&lKill them before they escape or...");
            this.getArena().getPlayers().forEach(player -> {
                try {
                    NMSUtils.clearTitle(player);
                    if(this.getArena().getPlayerRoles().get(player) == PlayerRole.SURVIVOR) {
                        NMSUtils.sendTitleSubtitle(player, "", message, 20, 40, 20);
                    } else {
                        NMSUtils.sendTitleSubtitle(player, "", message2, 20, 40, 20);
                    }
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
            });
        }

        if(ticks == 140) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("&9&lSur&d&lv&c&li&6&lv&e&le &eThe Night");
            this.getArena().getPlayers().forEach(player -> {
                try {
                    NMSUtils.clearTitle(player);
                    NMSUtils.sendTitleSubtitle(player, "", message, 20, 20, 20);
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
            });
        }
    }
}