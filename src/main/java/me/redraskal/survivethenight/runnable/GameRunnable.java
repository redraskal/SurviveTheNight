package me.redraskal.survivethenight.runnable;

import lombok.Getter;
import me.redraskal.survivethenight.game.*;
import me.redraskal.survivethenight.utils.Cuboid;
import me.redraskal.survivethenight.utils.NMSUtils;
import me.redraskal.survivethenight.utils.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class GameRunnable extends BukkitRunnable {

    @Getter private final Arena arena;
    @Getter private final IronGolem ironGolem;
    @Getter private final List<Generator> generators = new ArrayList<>();
    @Getter private final List<Closet> closets = new ArrayList<>();

    private int ticks = 0;
    private boolean morning = false;
    private int hour = 8;
    private int seconds = 0;

    private int minutesLeft = 15;
    private int secondsLeft = 0;

    public GameRunnable(Arena arena, IronGolem ironGolem) {
        this.arena = arena;
        this.ironGolem = ironGolem;
        this.getArena().getGenerators().forEach(block -> generators.add(
                new Generator(this.getArena().getArenaManager().getSurviveTheNight(), this.getArena(), block)));
        for(Map.Entry<Cuboid, Location> entry : this.getArena().getDoors().entrySet()) {
            closets.add(new Closet(this.getArena(), entry.getValue(), entry.getKey()));
        }
        this.runTaskTimer(this.getArena().getArenaManager().getSurviveTheNight(), 0, 1L);
    }

    public int getGeneratorsFilled() {
        int filled = 0;

        for(Generator generator : this.getGenerators()) {
            if(generator.isRunning()) filled++;
        }

        return filled;
    }

    @Override
    public void run() {
        if(this.getArena().getGameState() != GameState.INGAME) {
            this.cancel();
            return;
        }

        ticks++;

        if(ticks % 10 == 0) {
            this.getGenerators().forEach(generator -> {
                generator.getBlock().getWorld().playSound(generator.getBlock().getLocation(),
                        Sounds.ZOMBIE_INFECT.spigot(), 0.6f, 0.79f);
                if(generator.isRunning()) {
                    generator.getBlock().getWorld().playSound(generator.getBlock().getLocation(),
                            Sounds.MINECART_BASE.spigot(), 0.4f, 0.09f);
                }
            });
        }

        if(ticks % 20 == 0) {
            this.getArena().getBounds().getWorld().setTime(
                    this.getArena().getBounds().getWorld().getTime()+6L);

            if(ticks % 40 == 0) {
                if(seconds >= 59) {
                    seconds = 0;
                    if(hour == 12) {
                        hour = 1;
                    } else {
                        hour++;
                    }
                    if(hour == 12) morning = true;
                } else {
                    seconds++;
                }
            }

            String f_seconds = "" + seconds;
            if(seconds < 10) f_seconds = "0" + f_seconds;
            final String ff_seconds = f_seconds;
            String timeMarker = "PM";
            if(morning) timeMarker = "AM";
            final String f_timeMarker = timeMarker;

            if(secondsLeft <= 0) {
                if(minutesLeft <= 0) {
                    this.cancel();
                    this.getArena().setGameState(GameState.FINISHED);
                    this.getArena().setGameRunnable(null);

                    //TODO: Fireworks and all :D

                    List<Player> players = new ArrayList<>();
                    players.addAll(this.getArena().getPlayers());

                    players.forEach(player -> this.getArena().removePlayer(player));
                } else {
                    minutesLeft--;
                    secondsLeft = 59;
                }
            } else {
                secondsLeft--;
            }

            String f_minutesLeft = "" + minutesLeft;
            String f_secondsLeft = "" + secondsLeft;

            if(minutesLeft < 10) f_minutesLeft = "0" + f_minutesLeft;
            if(secondsLeft < 10) f_secondsLeft = "0" + f_secondsLeft;

            final String ff_minutesLeft = f_minutesLeft;
            final String ff_secondsLeft = f_secondsLeft;

            this.getArena().getScoreboardMap().values().forEach(scoreboard -> {
                scoreboard.setTitle("&9Sur&dv&ci&6v&ee &8» &7" + ff_minutesLeft + ":" + ff_secondsLeft);
            });

            this.getArena().getPlayers().forEach(player -> {
                try {
                    this.getArena().getArenaManager().getSurviveTheNight()
                            .getBossBarManager().sendBossBar(player, "" + hour + ":" + ff_seconds
                            + " " + f_timeMarker + " &8⏐ &e"
                            + this.getGeneratorsFilled() + "&7/&e"
                            + this.getArena().getGeneratorsNeeded() + " &fGenerators powered");
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
        }

        if(ticks == 1) {
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