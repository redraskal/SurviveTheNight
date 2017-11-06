package me.redraskal.survivethenight.game;

import lombok.Getter;
import org.bukkit.block.Sign;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SignInfo {

    @Getter private final Sign sign;
    @Getter private final int arenaID;
    @Getter private final SignType signType;

    public SignInfo(Sign sign, int arenaID, SignType signType) {
        this.sign = sign;
        this.arenaID = arenaID;
        this.signType = signType;
    }

    public enum SignType {

        JOIN, LEAVE
    }
}