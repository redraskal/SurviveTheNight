package me.redraskal.survivethenight.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ConfigUtils {

    public static Location decodeLocation(String object) {
        if(object.isEmpty()) return null;
        String[] parts = object.split("@");
        return new Location(Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]), Float.parseFloat(parts[4]));
    }

    public static String encodeLocation(Location object) {
        if(object == null) return "";
        return object.getWorld().getName() + "@" + object.getX() + "@" + object.getY() + "@" + object.getZ()
                + "@" + object.getYaw() + "@" + object.getPitch();
    }

    public static Cuboid decodeCuboid(String object) {
        if(object.isEmpty()) return null;
        String pos1 = object.split(";")[0];
        String pos2 = object.split(";")[1];
        return new Cuboid(decodeLocation(pos1), decodeLocation(pos2));
    }

    public static String encodeCuboid(Cuboid object) {
        if(object == null) return "";
        return encodeLocation(object.getPos1()) + ";" + encodeLocation(object.getPos2());
    }

    public static List<String> encodeLocationList(List<Location> object) {
        if(object == null) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        object.forEach(location -> result.add(encodeLocation(location)));

        return result;
    }

    public static List<Location> decodeLocationList(List<String> object) {
        if(object.isEmpty()) return null;
        List<Location> result = new ArrayList<>();

        object.forEach(location -> result.add(decodeLocation(location)));

        return result;
    }

    public static List<String> encodeBlockList(List<Block> object) {
        if(object == null) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        object.forEach(block -> result.add(encodeLocation(LocationUtils.center(block.getLocation()))));

        return result;
    }

    public static List<Block> decodeBlockList(List<String> object) {
        if(object.isEmpty()) return null;
        List<Block> result = new ArrayList<>();

        object.forEach(block -> result.add(decodeLocation(block).getBlock()));

        return result;
    }

    public static List<String> encodeCuboidList(List<Cuboid> object) {
        if(object == null) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        object.forEach(cuboid -> result.add(encodeCuboid(cuboid)));

        return result;
    }

    public static List<Cuboid> decodeCuboidList(List<String> object) {
        if(object.isEmpty()) return null;
        List<Cuboid> result = new ArrayList<>();

        object.forEach(cuboid -> result.add(decodeCuboid(cuboid)));

        return result;
    }

    public static List<String> encodeAreaMap(Map<String, Cuboid> object) {
        if(object == null) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        for(Map.Entry<String, Cuboid> entry : object.entrySet()) {
            result.add(entry.getKey() + "&" + encodeCuboid(entry.getValue()));
        }

        return result;
    }

    public static Map<String, Cuboid> decodeAreaMap(List<String> object) {
        if(object.isEmpty()) return null;
        Map<String, Cuboid> result = new HashMap<>();

        object.forEach(cuboid -> result.put(cuboid.split("&")[0], decodeCuboid(cuboid.split("&")[1])));

        return result;
    }
}