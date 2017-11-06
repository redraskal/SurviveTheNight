package me.redraskal.survivethenight.game;

import lombok.Getter;
import me.redraskal.survivethenight.utils.Cuboid;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Closet {

    @Getter private final Arena arena;
    @Getter private final Location insidePosition;
    @Getter private final Cuboid doorBounds;

    @Getter private Player hider;
    @Getter private Location previousPosition;

    public Closet(Arena arena, Location insidePosition, Cuboid doorBounds) {
        this.arena = arena;
        this.insidePosition = insidePosition;
        this.doorBounds = doorBounds;
    }

    public void hide(Player player) {
        if(this.getArena().getPlayerRoles().get(player) == PlayerRole.KILLER) {
            if(this.getHider() != null) {
                this.hider = null;
                player.teleport(this.getPreviousPosition());
                this.previousPosition = null;
            }
            return;
        }
        if(this.getHider() != null) {
            if(this.getHider().getUniqueId().equals(player.getUniqueId())) {
                this.hider = null;
                player.teleport(this.getPreviousPosition());
                this.previousPosition = null;
            } else {
                try {
                    NMSUtils.clearTitle(player);
                    NMSUtils.sendTitleSubtitle(player,
                            "&cA player is already hiding", "&cin this closet",
                            0, 20, 10);
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
        } else {
            this.hider = player;
            this.previousPosition = player.getLocation().clone();
            player.teleport(this.getInsidePosition());

            try {
                NMSUtils.clearTitle(player);
                NMSUtils.sendTitleSubtitle(player,
                        "&aHidden", "&fYou are now hidden to everyone else",
                        0, 20, 10);
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
}