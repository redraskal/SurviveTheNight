package me.redraskal.survivethenight.event;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ArenaEndGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Arena arena;

    public ArenaEndGameEvent(Arena arena) {
        this.arena = arena;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}