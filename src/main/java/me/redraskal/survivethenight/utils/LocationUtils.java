package me.redraskal.survivethenight.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

    public static Location center(Location location) {
        String x = "" + location.getX();
        String z = "" + location.getZ();
        if(x.contains(".")) x = x.substring(0, x.indexOf("."));
        if(z.contains(".")) z = z.substring(0, z.indexOf("."));
        x+=".5";
        z+=".5";
        location.setX(Double.parseDouble(x));
        location.setZ(Double.parseDouble(z));
        return resetRotation(location);
    }

    public static Location resetRotation(Location location) {
        location.setYaw(0f);
        location.setPitch(0f);
        return location;
    }

    public static Location faceEntity(Location location, Entity entity) {
        return faceLocation(location, entity.getLocation());
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