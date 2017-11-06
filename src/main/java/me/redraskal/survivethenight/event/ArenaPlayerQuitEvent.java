package me.redraskal.survivethenight.event;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaPlayerQuitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Arena arena;
    @Getter private final Player player;

    public ArenaPlayerQuitEvent(Arena arena, Player player) {
        this.arena = arena;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}