package me.redraskal.survivethenight.listener;

import lombok.Getter;
import me.redraskal.survivethenight.game.Arena;
import me.redraskal.survivethenight.manager.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
        String message = this.getArenaManager().getSurviveTheNight()
                .buildMessage("&9" + event.getPlayer().getName() + " &8» &f" + event.getMessage());
        this.getArena().getPlayers().forEach(player -> player.sendMessage(message));
    }
}