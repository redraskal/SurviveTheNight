package me.redraskal.survivethenight.runnable;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.game.GameState;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ChestRefillRunnable extends BukkitRunnable {

    @Getter private final Arena arena;
    @Getter private final Block chest;
    private int seconds = 0;

    public ChestRefillRunnable(Arena arena, Block chest) {
        this.arena = arena;
        this.chest = chest;
        this.getChest().setMetadata("chest-refilling",
                new FixedMetadataValue(this.getArena()
                        .getArenaManager().getSurviveTheNight(), true));
        this.runTaskTimer(arena.getArenaManager()
                .getSurviveTheNight(), 0, 20L);
    }

    @Override
    public void run() {
        if(this.getArena().getGameState() != GameState.INGAME) {
            this.cancel();
            this.resetChest();
            return;
        }

        seconds++;

        if(seconds >= 30) {
            this.cancel();
            this.resetChest();
        }
    }

    private void resetChest() {
        this.getChest().removeMetadata("chest-refilling", this.getArena()
                .getArenaManager().getSurviveTheNight());
        try {
            NMSUtils.forceChestState(this.getChest(), false);
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
}