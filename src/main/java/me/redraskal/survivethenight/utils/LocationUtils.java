package me.redraskal.survivethenight.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class LocationUtils {

    public static boolean isSameLocation(Location location, Location comparing) {
        return (location.getBlockX() == comparing.getBlockX()
                && location.getBlockY() == comparing.getBlockY()
                && location.getBlockZ() == comparing.getBlockZ());
    }

    public static Location faceLocation(Location location, Location facing) {
        Vector direction = location.toVector().subtract(facing.toVector());
        direction.multiply(-1);
        location.setDirection(direction);
        return location;
    }

    public static Block getTargetBlock(LivingEntity entity, int range) {
        final BlockIterator bit = new BlockIterator(entity, range);
        while(bit.hasNext()) {
            final Block next = bit.next();
            if(next != null && next.getType() != Material.AIR) {
                return next;
            }
        }
        return null;
    }
}