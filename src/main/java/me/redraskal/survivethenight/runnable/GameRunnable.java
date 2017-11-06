package me.redraskal.survivethenight.runnable;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.GameState;
import me.redraskal.survivethenight.game.PlayerRole;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.entity.IronGolem;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GameRunnable extends BukkitRunnable {

    @Getter private final Arena arena;
    @Getter private final IronGolem ironGolem;

    private int ticks = 0;

    public GameRunnable(Arena arena, IronGolem ironGolem) {
        this.arena = arena;
        this.ironGolem = ironGolem;
    }

    @Override
    public void run() {
        if(this.getArena().getGameState() != GameState.INGAME) {
            this.cancel();
            return;
        }

        ticks++;

        if(ticks == 0) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Find &f&lFuel Cans &fand carry them to &f&lGenerators");
            String message2 = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Attack survivors with your &f&lRusty Axe");
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

        if(ticks == 60) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Right-click to refuel them");
            String message2 = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Pull them towards you with your &f&lFishing Hook");
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

        if(ticks == 120) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("&f&l4 Fuel Cans &fwill power a Generator");
            String message2 = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Right-click &f&lClosets &fto check for hiding Survivors");
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

        if(ticks == 180) {
            String message = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Power &f&l6 Generators &fto unlock the gate");
            String message2 = this.getArena().getArenaManager().getSurviveTheNight()
                    .buildMessage("Stop survivors from powering the &f&lGenerators");
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
    }
}