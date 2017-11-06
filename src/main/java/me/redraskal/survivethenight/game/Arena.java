package me.redraskal.survivethenight.game;

import lombok.Getter;
import lombok.Setter;
import me.redraskal.survivethenight.event.ArenaEndGameEvent;
import me.redraskal.survivethenight.event.ArenaPlayerJoinEvent;
import me.redraskal.survivethenight.event.ArenaPlayerQuitEvent;
import me.redraskal.survivethenight.listener.ArenaListener;
import me.redraskal.survivethenight.manager.ArenaManager;
import me.redraskal.survivethenight.runnable.GamePostStartRunnable;
import me.redraskal.survivethenight.runnable.GameRunnable;
import me.redraskal.survivethenight.runnable.GameStartRunnable;
import me.redraskal.survivethenight.utils.Cuboid;
import me.redraskal.survivethenight.utils.InventoryUtils;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Arena {

    @Getter private final ArenaManager arenaManager;
    @Getter private final int arenaid;

    @Getter private ArenaListener arenaListener;
    @Getter @Setter private GameState gameState = GameState.LOBBY;
    @Getter private List<Player> players = new ArrayList<>();
    @Getter private Map<Player, PlayerRole> playerRoles = new HashMap<>();

    @Getter @Setter private GameStartRunnable gameStartRunnable;
    @Getter @Setter private GamePostStartRunnable gamePostStartRunnable;
    @Getter @Setter private GameRunnable gameRunnable;

    @Getter @Setter private Location lobbyPosition;
    @Getter @Setter private Location mainLobbyPosition;
    @Getter @Setter private List<Location> spawnPositions = new ArrayList<>();

    @Getter @Setter private int minPlayers = 2;
    @Getter @Setter private int maxPlayers = 17;
    @Getter @Setter private int generatorsNeeded = 6;

    @Getter @Setter private List<Block> generators = new ArrayList<>();
    @Getter @Setter private Cuboid bounds;
    @Getter @Setter private Map<Cuboid, Location> doors = new HashMap<>();
    @Getter @Setter private Map<String, Cuboid> areas = new HashMap<>();
    @Getter @Setter private List<Cuboid> gates = new ArrayList<>();

    public Arena(ArenaManager arenaManager, int arenaid) {
        this.arenaManager = arenaManager;
        this.arenaid = arenaid;
        this.arenaListener = new ArenaListener(this, this.getArenaManager());
        this.getArenaManager().getSurviveTheNight().getServer().getPluginManager()
                .registerEvents(this.getArenaListener(), this.getArenaManager().getSurviveTheNight());
    }

    public void broadcastMessage(String message) {
        this.getPlayers().forEach(player -> player.sendMessage(message));
    }

    public boolean addPlayer(Player player) {
        if(this.getPlayers().contains(player)) {
            player.sendMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &cYou are already in this arena."));
            return false;
        }
        if(!canPlay()) {
            player.sendMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &cThis arena has not been setup yet."));
            return false;
        }

        this.getPlayers().add(player);

        if(this.getGameState() != GameState.LOBBY) {
            player.teleport(this.getSpawnPositions().get(new Random().nextInt(this.getSpawnPositions().size())));
            InventoryUtils.resetPlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
            this.getPlayerRoles().put(player, PlayerRole.SURVIVOR);
            return true;
        }

        player.teleport(this.getLobbyPosition());
        InventoryUtils.resetPlayer(player);
        this.getPlayerRoles().put(player, PlayerRole.SURVIVOR);
        this.getArenaManager().getSurviveTheNight().getServer()
                .getPluginManager().callEvent(new ArenaPlayerJoinEvent(this, player));
        this.broadcastMessage(this.getArenaManager().getSurviveTheNight().buildMessage("&9" + player.getName() + " &6wants to survive!"));

        try {
            this.getArenaManager().getSurviveTheNight().getBossBarManager().sendBossBar(player, "&bPlaying &7» &e&lSurvive The Night");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(this.getPlayers().size() == this.getMinPlayers()) this.gameStartRunnable = new GameStartRunnable(this);
        if(this.getGameStartRunnable() != null && this.getCapacity() >= 50D && this.getGameStartRunnable().getCountdown() >= 60) {
            this.getGameStartRunnable().setCountdown(60);
            this.broadcastMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &6The timer has been shortened to 60 seconds."));
        }
        if(this.getGameStartRunnable() != null && this.getCapacity() >= 75D && this.getGameStartRunnable().getCountdown() >= 15) {
            this.getGameStartRunnable().setCountdown(15);
            this.broadcastMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &aWe almost have a full server!"));
            this.broadcastMessage(this.getArenaManager().getSurviveTheNight().buildMessage("<prefix> &6The timer has been shortened to 15 seconds."));
        }

        return true;
    }

    public boolean removePlayer(Player player) {
        if(!this.getPlayers().contains(player)) return false;

        this.getPlayers().remove(player);
        if(this.getPlayerRoles().containsKey(player)) {
            if(this.getPlayerRoles().get(player) == PlayerRole.KILLER) {
                if(this.getGamePostStartRunnable() != null) {
                    if(this.getGamePostStartRunnable().getNpcMap().containsKey(player)) {
                        this.getGamePostStartRunnable().getNpcMap().get(player).despawn();
                        this.getGamePostStartRunnable().getNpcMap().remove(player);
                    }
                    if(this.getGamePostStartRunnable().getArmorStandMap().containsKey(player)) {
                        this.getGamePostStartRunnable().getArmorStandMap().get(player).remove();
                        this.getGamePostStartRunnable().getArmorStandMap().remove(player);
                        try {
                            NMSUtils.sendCameraPacket(player, player.getEntityId(),
                                    this.getArenaManager().getSurviveTheNight());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    this.getGamePostStartRunnable().getIronGolem().remove();
                } else {
                    if(this.getGameRunnable() != null) {
                        this.getGameRunnable().getIronGolem().remove();
                    }
                }
            }
            this.getPlayerRoles().remove(player);
        }
        player.teleport(this.getMainLobbyPosition());
        InventoryUtils.resetPlayer(player);
        this.getArenaManager().getSurviveTheNight().getServer()
                .getPluginManager().callEvent(new ArenaPlayerQuitEvent(this, player));

        try {
            this.getArenaManager().getSurviveTheNight().getBossBarManager().removeBar(player);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(this.getPlayers().size() == (this.getMinPlayers()-1)) {
            if(this.getGameStartRunnable() != null) {
                this.getGameStartRunnable().cancel();
                this.broadcastMessage(this.getArenaManager().getSurviveTheNight()
                        .buildMessage("&cNot enough players are in the arena to start the game."));
                this.setGameStartRunnable(null);
            }
            if(this.getGameState() != GameState.LOBBY) {
                this.setGameState(GameState.LOBBY);
                this.getArenaManager().getSurviveTheNight().getServer()
                        .getPluginManager().callEvent(new ArenaEndGameEvent(this));
                if(this.getGameRunnable() != null) {
                    this.getGameRunnable().getGenerators().forEach(generator -> {
                        if(!generator.getArmorStand().isDead()) generator.getArmorStand().remove();
                        if(!generator.getArmorStand2().isDead()) generator.getArmorStand2().remove();
                    });
                    this.getBounds().getWorld().getEntities().forEach(entity -> {
                        if(this.getBounds().hasEntityInside(entity) && !(entity instanceof Player)) {
                            entity.remove();
                        }
                    });
                    this.setGameRunnable(null);
                }
            }
        }

        return true;
    }

    public boolean canPlay() {
        return (lobbyPosition != null && mainLobbyPosition != null
                && spawnPositions != null && spawnPositions.size() > 0
                && minPlayers > 0 && maxPlayers > 0 && generatorsNeeded > 0
                && generators != null && generators.size() > 0
                && bounds != null && doors != null && doors.size() > 0
                && areas != null && gates != null && gates.size() > 0);
    }

    public double getCapacity() {
        return (((double)this.getPlayers().size()/(double)this.getMaxPlayers())*100D);
    }
}