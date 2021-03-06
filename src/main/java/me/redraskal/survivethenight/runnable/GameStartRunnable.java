package me.redraskal.survivethenight.runnable;

import lombok.Getter;
import lombok.Setter;
import me.redraskal.survivethenight.event.ArenaStartGameEvent;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.GameState;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GameStartRunnable extends BukkitRunnable {

    @Getter private final Arena arena;
    @Getter @Setter private int countdown = 120;

    public GameStartRunnable(Arena arena) {
        this.arena = arena;
        this.runTaskTimer(arena.getArenaManager().getSurviveTheNight(), 0, 20L);
    }

    @Override
    public void run() {
        if(countdown <= 1) {
            this.cancel();
            this.getArena().setGameState(GameState.INGAME);
            this.getArena().broadcastMessage(this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("<prefix> &e&lThe game is starting! &7You will be teleported in a few seconds..."));
            this.getArena().getPlayers().forEach(player -> {
                try {
                    this.getArena().getArenaManager()
                            .getSurviveTheNight().getBossBarManager().removeBar(player);
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
            });
            this.getArena().setGamePostStartRunnable(new GamePostStartRunnable(this.getArena()));
            this.getArena().setGameStartRunnable(null);
            this.getArena().getArenaManager().getSurviveTheNight().getServer().getPluginManager()
                    .callEvent(new ArenaStartGameEvent(this.getArena()));
        }
        countdown--;
        this.getArena().getPlayers().forEach(player -> player.setLevel(countdown));
    }
}